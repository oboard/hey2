package omark.hey;
import android.view.View;
import android.view.MotionEvent;
import android.widget.TextView;
import android.util.AttributeSet;
import android.widget.Scroller;
import android.content.Context;
import android.view.Gravity;
import android.graphics.Rect;

public class ScrollText extends TextView {
    //定义两个变量用于存储按下view时所处的坐标
    int lastX = 0, lastY = 0;

    //滑动~
    Scroller scroller;

    public ScrollText(Context context, AttributeSet attrs) {
        super(context, attrs);
        scroller = new Scroller(context);

        setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Main.web.reload();
                    return true;
                }
            });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //检测到触摸事件后
        //第一时间得到相对于父控件的触摸点坐标 并赋值给x,y
        View viewGroup = (View)getParent();
        int x = (int)event.getX(), y = (int)event.getY();

        switch (event.getAction()) {
                //触摸事件中绕不开的第一步，必然执行，将按下时的触摸点坐标赋值给
                //lastX 和 last Y
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                break;
                //触摸事件的第二步，这时候的x,y已经随着滑动操作产生了变化，用变化后的坐标减去首次触摸时的坐标得到相对的偏移量
            case MotionEvent.ACTION_MOVE:
                int offsetx = viewGroup.getScrollX() + lastX - x;
                int max = Main.dip2px(this.getContext(), 60);
                //this.setText("" + offsetx + "max:" + max);
                if (offsetx > max) {
                    offsetx = max;
                } else if (offsetx < -max) {
                    offsetx = -max;
                }
                viewGroup.scrollTo(offsetx, 0);
                break;
                //触摸事件的第三步，必然执行，手指抬起时候触发，这里会将移动过的view还原到原来的位置，并且有过度效果不是突然移动
            case MotionEvent.ACTION_UP:
                //因为下面要使用父视图的引用来得到偏移量
                //调用 startScroll 方法 ，参数为 起始X坐标，起始Y坐标，目的X坐标，目的Y坐标，过度动画持续时间
                //这里使用了viewGroup.getScrollX() 和 viewGroup.getScrollY() 作为起始坐标，ScrollY 和 ScrollX 记录了使用 scrollBy 进行偏移的量
                //所以使用他们就等于是使用了现在的坐标作为起始坐标，目的坐标为他们的负数，就是偏移量为0的位置，也是view在没有移动之前的位置

                if (Main.web != null) {
                    if (viewGroup.getScrollX() > Main.dip2px(this.getContext(), 50)) 
                        Main.web.goForward();
                    else if (viewGroup.getScrollX() < -Main.dip2px(this.getContext(), 50)) 
                        Main.web.goBack();
                }
                if (Math.abs(viewGroup.getScrollX()) < Main.dip2px(this.getContext(), 1)) Main.me.onDockClick(null);
                scroller.startScroll(viewGroup.getScrollX(), viewGroup.getScrollY(), -viewGroup.getScrollX(), -viewGroup.getScrollY(), 800);
                //刷新view，这里很重要，如果不执行，下面的
                //  computeScroll 方法就不会执行 computeScroll 方法是由 onDraw 方法调用的，而刷新 View 会调用 onDraw。
                invalidate();
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        //在上面尝试刷新视图之后被调用，并且执行了
        //computeScrollOffset 方法，
        //此方法根据上面传进来的起始坐标和目的坐标还有动画时间，进行计算每次移动的偏移量
        //如果到达目的坐标
        //false ，如果不为零 说明没有到达目的坐标
        if (scroller.computeScrollOffset()) {
            //使用 scrollTo 方法进行移动，参数是从 scroller 的 getCurrX 以及 getCurrY 方法得到的，这两个参数每次在执行 computeScrollOffset 之后都会改变，会越来越接近目的坐标。
            ((View)getParent()).scrollTo(scroller.getCurrX(), scroller.getCurrY());
            // 再次刷新 view 也等于是在循环执行此方法 直到 computeScrollOffset 判断到达目的坐标为止，
            // 循环次数和每次移动的坐标距离相关，每次移动的坐标距离又跟目的坐标的距离和动画时长有关
            //通常距离越长，动画时间越长，循环次数越多
            invalidate();
        }
    }
}
