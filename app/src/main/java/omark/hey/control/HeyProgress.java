package omark.hey.control;
import android.widget.ProgressBar;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.content.Context;
import android.util.AttributeSet;

public class HeyProgress extends ProgressBar {

    static int color = Color.BLACK;

    public HeyProgress(final Context context) {
        super(context);
    } public HeyProgress(final Context context, AttributeSet attr) {
        super(context, attr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        int h = getHeight(), w = getWidth();
        Paint p = new Paint(color);
        canvas.drawRect(w - w * getProgress() / 100, 0, w * getProgress() / 100, h, p);
    }

}
