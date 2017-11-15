package omark.hey.control;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.CheckBox;
import android.graphics.Xfermode;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.RectF;

public class HeySwitch extends CheckBox {
    float fx = 0, fy = 0;
    int w = 0, h = 0;
    static int color = Color.BLACK;

    public HeySwitch(final Context context) {
        super(context);
        init(context);
    } public HeySwitch(final Context context, AttributeSet attr) {
        super(context, attr);
        init(context);
    } public void init(Context c) {
        setBackgroundDrawable(null);
        //w = (int)dip2px(c, 40);
        //h = (int)dip2px(c, 16);
    }

    public void changeTo(boolean v) {
        setChecked(v);
        fy = 0;
        Animation ani = new BarAnimation();
        ani.setDuration(1);
        ani.setInterpolator(new AccelerateDecelerateInterpolator());
        startAnimation(ani);
    }
    public void change() {
        fy = 0;
        Animation ani = new BarAnimation();
        ani.setDuration(196);
        ani.setInterpolator(new AccelerateDecelerateInterpolator());
        startAnimation(ani);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case event.ACTION_DOWN:
                fy = isChecked() ? -0.2f : 0.2f;
                postInvalidate();
                break;
            case event.ACTION_UP:
                change();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);

        //Animation防鬼畜
        //if (isChecked() && fx == 0) fx = 1;
        //if (!isChecked() && fx == 1) fx = 0;
        w = getWidth();//(int)dip2px(c, 40);
        h = getHeight();//(int)dip2px(c, 16);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
     
        RectF[] r = {
            new RectF(0, 0, h, h),
            new RectF(w - h, 0, w, h)
        };
        
        //画外部
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawArc(r[0], 90, 180, false, paint);
        canvas.drawArc(r[1], -90, 180, false, paint);
        canvas.drawLine(h / 2, 0, w - h / 2, 0, paint);
        canvas.drawLine(h / 2, h, w - h / 2, h, paint);
        

        //画内部
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(blendColor(color, Color.TRANSPARENT, fx + fy));
        canvas.drawArc(r[0], 90, 180, false, paint);
        canvas.drawArc(r[1], -90, 180, false, paint);
        canvas.drawRect(h / 2, 0, w - h / 2, h, paint);

        //画状态球
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(blendColor(Color.WHITE, color, fx));
        canvas.drawCircle(fx * (w - h) + h / 2, h / 2, h / 4, paint);

    }

    public int blendColor(int colorA, int colorB, float ratio) {  
        final float inverseRatio = 1f - ratio;
        float a = (Color.alpha(colorA) * ratio) + (Color.alpha(colorB) * inverseRatio);
        float r = (Color.red(colorA) * ratio) + (Color.red(colorB) * inverseRatio);
        float g = (Color.green(colorA) * ratio) + (Color.green(colorB) * inverseRatio);
        float b = (Color.blue(colorA) * ratio) + (Color.blue(colorB) * inverseRatio);
        return Color.argb((int) a, (int) r, (int) g, (int) b);
    }

    public class BarAnimation extends Animation {
        public BarAnimation() {} 
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            if (isChecked())
                fx = interpolatedTime;
            else
                fx = 1 - interpolatedTime;
            postInvalidate();
        }
    }

}
