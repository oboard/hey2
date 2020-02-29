package omark.hey.control;
import android.widget.ProgressBar;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Color;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class HeyProgress extends View {

    static int color = 0x55ffffff;
    int progress = 0;
    
    public HeyProgress(final Context context) {
        super(context);
    } public HeyProgress(final Context context, AttributeSet attr) {
        super(context, attr);
    }
    
    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        int h = getHeight(), w = getWidth();
        Paint p = new Paint();
        p.setColor(color);
        canvas.drawRect(0, 0, w * progress / 100, h, p);
    }

}
