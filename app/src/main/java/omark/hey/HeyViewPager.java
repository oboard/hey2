package omark.hey;
import android.view.ViewGroup;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class HeyViewPager extends ViewGroup {
    
    public HeyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean p1, int p2, int p3, int p4, int p5) {
        
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        
        return true;//super.onTouchEvent(event);
    }
    
    
}
