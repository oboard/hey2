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
        setBackground(null);
        w = (int)dip2px(c, 40);
        h = (int)dip2px(c, 16);
    }

    public void change() {
        fy = 0;
        Animation ani = new BarAnimation();
        ani.setDuration(320);
        ani.setInterpolator(new AccelerateDecelerateInterpolator());
        startAnimation(ani);
    }
    
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = 40;
        int desiredHeight = 16;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
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

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
     
        //画外部
        canvas.drawCircle(h / 2, h / 2, h / 2, paint);
        canvas.drawCircle(w - h / 2, h / 2, h / 2, paint);
        canvas.drawRect(h / 2, 0, w - h / 2, h, paint);

        //画内部
        int border = 2;
        paint.setColor(blendColor(color, Color.WHITE, fx + fy));
        canvas.drawCircle(h / 2, h / 2, h / 2 - border, paint);
        canvas.drawCircle(w - h / 2, h / 2, h / 2 - border, paint);
        canvas.drawRect(h / 2, border , w - h / 2 - border, h - border, paint);

        //画状态球
        paint.setColor(blendColor(Color.WHITE, color, fx));
        canvas.drawCircle(fx * (w - h) + h / 2, h / 2, h / 4, paint);

    }

    public int blendColor(int colorA, int colorB, float ratio) {  
        final float inverseRatio = 1f - ratio;
        float r = (Color.red(colorA) * ratio) + (Color.red(colorB) * inverseRatio);
        float g = (Color.green(colorA) * ratio) + (Color.green(colorB) * inverseRatio);
        float b = (Color.blue(colorA) * ratio) + (Color.blue(colorB) * inverseRatio);
        return Color.argb(255, (int) r, (int) g, (int) b);
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

    public static float dip2px(Context context, float dipValue) {
        return (dipValue * context.getResources().getDisplayMetrics().density + 0.5f) ;
    }

}
