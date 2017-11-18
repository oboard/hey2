package omark.hey.control;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.MotionEvent;
import android.os.Build;

public class HeySetting extends RelativeLayout {
    
    public TextView mTextView;
    public HeySwitch mSwitch;

    public HeySetting(final Context context) {
        super(context);
        init(context);
    } public HeySetting(final Context context, AttributeSet attr) {
        super(context, attr);
        init(context);
    } public void init(Context c) {
        if (getTag() == null) return;
        setClipChildren(false);
        setClickable(true);
        View mView = new View(c);
        mView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mSwitch.setChecked(!mSwitch.isChecked());
                mSwitch.change();
                performClick();
            }
        });
       
        if (getBackground() == null && Build.VERSION.SDK_INT > 14) {
            int[] attrsArray = { android.R.attr.selectableItemBackground };
            TypedArray typedArray = c.obtainStyledAttributes(attrsArray);
            mView.setBackgroundResource(typedArray.getResourceId(0, attrsArray[0]));
            typedArray.recycle();
        }
        
        mTextView = new TextView(c);
        mTextView.setMaxLines(1);
        mTextView.setText(getTag().toString());
        mTextView.setTextColor(Color.BLACK);
        mTextView.setVisibility(View.VISIBLE);
        mTextView.setEllipsize(TextUtils.TruncateAt.END);
        
        mSwitch = new HeySwitch(c);
        //mSwitch.setClickable(false);
        if (getTranslationX() > 1)
            mSwitch.setVisibility(View.VISIBLE);
        else
            mSwitch.setVisibility(View.GONE);
            
        setTranslationX(1);
        
        MarginLayoutParams lpm = new MarginLayoutParams(MarginLayoutParams.WRAP_CONTENT, MarginLayoutParams.WRAP_CONTENT);
        lpm.setMargins(10, 0, 24, 0);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(lpm);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        addView(mTextView, lp);
        
        MarginLayoutParams lpm2 = new MarginLayoutParams((int)dip2px(c, 40), (int)dip2px(c, 16));
        lpm2.setMargins(0, 0, 10, 0);
        
        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(lpm2);
        lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp2.addRule(RelativeLayout.CENTER_VERTICAL);
        addView(mSwitch, lp2);
        addView(mView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT));
        
        postInvalidate();
    }
    public void setChecked(boolean checked) {
        mSwitch.changeTo(checked);
    }
    public boolean isChecked() {
        return mSwitch.isChecked();
    }

    public static float dip2px(Context context, float dipValue) {
        return (dipValue * context.getResources().getDisplayMetrics().density + 0.5f) ;
    }
}
