package omark.hey;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Scroller;
import android.view.animation.DecelerateInterpolator;

public class ScrollImage extends ImageView {

    float lastX = 0, lastY = 0;

    //滑动~
    Scroller scroller;
    Context mContext;
    public ScrollImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        scroller = new Scroller(context, new DecelerateInterpolator());
        mContext = context;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        View viewGroup = (View)getParent();

        float x = event.getX(), y = event.getY();
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;

                break;
            case MotionEvent.ACTION_MOVE:
                //float offsetx = viewGroup.getScrollX() + lastX - x;
                float offsety = viewGroup.getScrollY() + lastY - y;
                if (Math.abs(offsety) > 20) {
                    getParent().requestDisallowInterceptTouchEvent(true);//通知父控件勿拦截本控件touch事件
                    float max = dip2px(mContext, viewGroup.getHeight() / 1.5f);
                    if (offsety > max)
                        offsety = max;
                    else if (offsety < -max)
                        offsety = -max;
                    
                    viewGroup.setAlpha(0.5f);
                    viewGroup.scrollTo(0, (int)offsety);
                    invalidate();
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);//通知父控件拦截本控件touch事件  
                    viewGroup.scrollTo(0, 0);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(viewGroup.getScrollY()) > viewGroup.getHeight() / 2) {
                    int i = Main.multiimage.indexOf(this);
                    Main.me.removePage(i);
                    Main.me.freshMulti();
                    Main.me.aniMulti(i);
                } else if (Math.abs(viewGroup.getScrollY()) > dip2px(mContext, 20)) {
                    scroller.abortAnimation();
                    scroller.startScroll(viewGroup.getScrollX(), viewGroup.getScrollY(), -viewGroup.getScrollX(), -viewGroup.getScrollY(), 320);
                    invalidate();
                } else if (viewGroup.getAlpha() != 0.5f){
                    performClick();
                }
                viewGroup.setAlpha(1);
                break;
            case MotionEvent.ACTION_CANCEL:
                viewGroup.setAlpha(1);
                break;
        }
        return true;
    } public static float dip2px(Context context, float dipValue) {
        return (dipValue * context.getResources().getDisplayMetrics().density + 0.5f) ;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            ((View)getParent()).scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }
}
