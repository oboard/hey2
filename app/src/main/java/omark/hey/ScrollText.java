package omark.hey;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.animation.BounceInterpolator;

public class ScrollText extends TextView {

    public static boolean isMenu = false;
    
    boolean isUper = false;

    //按下view时所处的坐标
    int lastX = 0, lastY = 0, lastS = 0, dip10 = 10;

    //滑动~
    Scroller scroller;

    public ScrollText(Context context, AttributeSet attrs) {
        super(context, attrs);
        scroller = new Scroller(context, new BounceInterpolator());
        dip10 = (int)Main.dip2px(context, 10);
    }

    Boolean isUp = true;
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isUp) {
                Main.me.onDockClick(null);
                Main.back_icon.setVisibility(View.GONE);
                Main.forward_icon.setVisibility(View.GONE);
                lastS = 3;
            }
            isUp = false;
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        View viewGroup = (View)getParent();
        int x = (int)event.getX(), y = (int)event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                Main.me.onDockClick(this);

                if (!isUper) {
                    isUp = true;
                    handler.sendEmptyMessageDelayed(0, 500);
                }
                if (Main.web.canGoBack())
                    Main.back_icon.setVisibility(View.VISIBLE);
                if (Main.web.canGoForward())
                    Main.forward_icon.setVisibility(View.VISIBLE);
                break;
            case MotionEvent.ACTION_MOVE:
                if (lastS == 3) break;
                int offsetx = viewGroup.getScrollX() + lastX - x;
                int offsety = viewGroup.getScrollY() + lastY - y;
                if (Math.abs(offsetx) > dip10 || Math.abs(offsety) > dip10) {
                    isUp = false;
                    if (lastS == 0) {
                        if (Math.abs(offsetx) >= Math.abs(offsety))
                            lastS = 1;
                        else
                            lastS = 2;
                    }
                    if (lastS == 1) {
                        int max = (int)Main.dip2px(this.getContext(), 60);
                        //this.setText("" + offsetx + "max:" + max);
                        if (offsetx > max)
                            offsetx = max;
                        else if (offsetx < -max) 
                            offsetx = -max;
                        viewGroup.scrollTo(offsetx, 0);
                    } else {
                        int max = (int)Main.dip2px(this.getContext(), 160);
                        if (offsety > max) offsety = max;
                        viewGroup.scrollTo(0, offsety);
                    }
                } else 
                    viewGroup.scrollTo(0, 0);
                break;
            case MotionEvent.ACTION_UP:
                HeyWeb web = Main.web;
                if (web != null) {
                    if (viewGroup.getScrollX() > Main.dip2px(this.getContext(), 50)) {
                        web.goForward();

                        web.loadUrl("javascript:document.title = " + web.getTitle());
                    } else if (viewGroup.getScrollX() < -Main.dip2px(this.getContext(), 50)) {
                        web.goBack();

                        web.loadUrl("javascript:document.title = " + web.getTitle());
                    }
                }
                if (Math.abs(viewGroup.getScrollX()) < 10 && Math.abs(viewGroup.getScrollY()) < 10 && isUp) Main.me.onDockLongClick(null);
                if (viewGroup.getScrollY() >= (int)Main.dip2px(this.getContext(), 100)) {
                    isUper = true;
                    scroller.startScroll(viewGroup.getScrollX(), viewGroup.getScrollY(), -viewGroup.getScrollX(), (int)Main.dip2px(getContext(), 160) - viewGroup.getScrollY(), 225);
                    
                    Main.button_right.setVisibility(View.GONE);
                    Main.button_number.setVisibility(View.GONE);
                } else {
                    isUper = false;
                    scroller.startScroll(viewGroup.getScrollX(), viewGroup.getScrollY(), -viewGroup.getScrollX(), -viewGroup.getScrollY(), 225);
                    if (lastS != 3) Main.freshDock();
                }
                isMenu = isUper;

                Main.back_icon.setVisibility(View.GONE);
                Main.forward_icon.setVisibility(View.GONE);

                lastS = 0;
                isUp = false;
                //刷新view
                invalidate();
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            ((View)getParent()).scrollTo(scroller.getCurrX(), scroller.getCurrY());
            // 再次刷新 view 也等于是在循环执行此方法 直到 computeScrollOffset 判断到达目的坐标为止，
            invalidate();
        }
    }
}
