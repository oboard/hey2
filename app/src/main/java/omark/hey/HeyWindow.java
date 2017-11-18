package omark.hey;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.Display;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.os.Handler;
import android.os.Looper;
import android.graphics.drawable.BitmapDrawable;

public class HeyWindow extends LinearLayout {

    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    HeyWindowManager mHeyWindowManager;
    WindowManager mWindowManager;
    Context mContext;
    FrameLayout root, bar, view;
    TextView close, back, title;
    View size;

    float mX, mY, sX, sY, x, y, w, h;
    boolean firstTouchDown = false;

    public HeyWindow(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.window, this);

        mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        mHeyWindowManager = HeyWindowManager.me;
        bar = (FrameLayout)findViewById(R.id.window_bar);
        root = (FrameLayout)findViewById(R.id.window_root);
        view = (FrameLayout)findViewById(R.id.window_frame);
        close = (TextView)findViewById(R.id.window_close);
        back = (TextView)findViewById(R.id.window_back);
        title = (TextView)findViewById(R.id.window_title);
        size = findViewById(R.id.window_size);
        HeyHelper.setFont(back, "m");
        HeyHelper.setFont(close, "m");

        mContext = context;
        initLayoutParams();
        initEvent();
    }

    //初始化参数
    private void initLayoutParams() {
        //屏幕宽高
        Display display = mWindowManager.getDefaultDisplay();
        int sw = display.getWidth(), sh = display.getHeight();

        //总是出现在应用程序窗口之上。
        lp.type = WindowManager.LayoutParams.TYPE_PHONE;

        lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

        //悬浮窗默认显示的位置
        lp.gravity = Gravity.START | Gravity.TOP;

        //悬浮窗的宽高
        lp.width = (int)(sw / 1.5);
        lp.height = (int)(sh / 1.5);

        //指定位置
        lp.x = (sw + view.getLayoutParams().width) / 2;
        lp.y = (sh + view.getLayoutParams().height) / 2;

        lp.format = PixelFormat.RGBA_8888;
        mWindowManager.addView(this, lp);
    }

    private void initEvent() {
        root.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            //获得焦点
                            /*new Handler().post(new Runnable() {
                             public void run() {
                             if (root.getParent() != null) {
                             lp.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
                             mWindowManager.updateViewLayout(HeyWindow.this, lp);
                             }
                             }
                             });*/

                            if (!firstTouchDown) {
                                firstTouchDown = true;
                                //获取初始位置
                                mX += event.getRawX() - lp.x;
                                mY += event.getRawY() - lp.y;
                            } else {
                                //根据上次手指离开的位置与此次点击的位置进行初始位置微调
                                mX += event.getRawX() - x;
                                mY += event.getRawY() - y;
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            // 获取相对屏幕的坐标，以屏幕左上角为原点
                            x = event.getRawX();
                            y = event.getRawY();
                            updateViewPosition();
                            break;
                    }
                    return true;
                }
            });

        close.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mHeyWindowManager.removeWindow(mContext, HeyWindow.this);
                }
            });

        size.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            sX = event.getRawX();
                            sY = event.getRawY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            // 获取相对屏幕的坐标，以屏幕右下角为原点
                            w = lp.width + event.getRawX() - sX;
                            h = lp.height + event.getRawY() - sY;
                            updateViewSize();
                            break;
                    }
                    sX = event.getRawX();
                    sY = event.getRawY();
                    return true;
                }
            });
    }

    //更新浮动窗口位置
    private void updateViewSize() {
        int min = 128;
        if (w < min) w = min;
        if (h < min) h = min;
        
        lp.width = (int) w;
        lp.height = (int) h;
        mWindowManager.updateViewLayout(this, lp);
    }
    private void updateViewPosition() {
        lp.x = (int) (x - mX);
        lp.y = (int) (y - mY);
        mWindowManager.updateViewLayout(this, lp);
    }
}
