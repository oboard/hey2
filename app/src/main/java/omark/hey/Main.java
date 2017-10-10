package omark.hey;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import android.widget.ProgressBar;

public class Main extends Activity {
    static Main me;
    static View popn;
    static ScrollText dock;
    static FrameLayout desktop;
    static FrameLayout root;
    static ProgressBar progressbar;
    static HeyWeb web;//now webview
    static ArrayList<HeyWeb> pages = new ArrayList<HeyWeb>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        S.init(this, "main");
        if (S.get("first", true)) {
            S.put("first", false);
            S.put("home", "https://www.bing.com");
            S.ok();
        }

        me = this;
        dock = (ScrollText)findViewById(R.id.main_dock);
        desktop = (FrameLayout)findViewById(R.id.main_desktop);
        progressbar = (ProgressBar)findViewById(R.id.main_progress);
        root = (FrameLayout)findViewById(R.id.main_root);
        root.setBackgroundColor(0xff66ccff);

        web = addPage("");
    }

    public void onDockClick(View v) {

    }

    public HeyWeb addPage(String uri) {
        String link = uri.equals("") ? S.get("home", "https://www.bing.com") : uri;
        HeyWeb new_web = new HeyWeb(this);
        new_web.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        new_web.loadUrl(link);
        new_web.setVisibility(View.VISIBLE);
        new_web.setWebChromeClient(new HeyWebChrome());
        desktop.addView(new_web);
        pages.add(new_web);

        return new_web;
    }









    public void ripple_version(View view_children) {
        if (android.os.Build.VERSION.SDK_INT >= 21) {//版本兼容
            int[] attrsArray = { android.R.attr.selectableItemBackgroundBorderless };
            // TypedArray typedArray = this.obtainStyledAttributes(attrsArray); //Activity中使用
            TypedArray typedArray = obtainStyledAttributes(attrsArray);//Fragment中
            int selector = typedArray.getResourceId(0, attrsArray[0]);
            view_children.setBackgroundResource(selector);
            // don't forget the resource recycling
            typedArray.recycle();
        } else {
            int[] attrsArray = { android.R.attr.selectableItemBackground };
            // TypedArray typedArray = this.obtainStyledAttributes(attrsArray); //Activity中使用
            TypedArray typedArray = obtainStyledAttributes(attrsArray);//Fragment中
            int selector = typedArray.getResourceId(0, attrsArray[0]);
            view_children.setBackgroundResource(selector);
            // don't forget the resource recycling
            typedArray.recycle();
        }
    } public static int dip2px(Context context, float dipValue) {
        return (int)(dipValue * context.getResources().getDisplayMetrics().density + 0.5f) ;
    } @Override public void onConfigurationChanged(Configuration newConfig) {  
        super.onConfigurationChanged(newConfig);
        //weborientation = (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT);
        //if (!weborientation) {
        //    if (floatblack.getVisibility() == View.GONE) webtcard.setY(0);
        //    webmultilayout.setPadding(0, webtcard.getHeight(), 0, 0);
        //}
    }
    public Bitmap ColoBitmap(Bitmap bitmap, int color) {
        Bitmap b = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        for (int x = 0; x < b.getWidth(); x++) {
            for (int y = 0; y < b.getHeight(); y++) {
                if (b.getPixel(x, y) != Color.TRANSPARENT) b.setPixel(x, y, color);
            }
        }
        return b;
    } public boolean isLightColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        if (darkness < 0.5) {
            return true; // It's a light color
        } else {
            return false; // It's a dark color
        }
    } public int getNavigationBarHeight(Context context) {
        if (hasSoftKeys(getWindowManager())) return context.getResources().getDimensionPixelSize(context.getResources().getIdentifier("navigation_bar_height", "dimen", "android"));
        return 0;
    } private boolean hasSoftKeys(WindowManager windowManager) {
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);
        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }
    }


 class HeyWebChrome extends WebChromeClient {
    //private View mCustomView;
    /* private CustomViewCallback mCustomViewCallback;
     @Override
     public void onShowCustomView(View view, CustomViewCallback callback) {
     super.onShowCustomView(view, callback);
     if (mCustomView != null) {
     callback.onCustomViewHidden();
     return;
     } mCustomView = view;
     webmultilayout.setPadding(0, 0, 0, 0);
     webmultilayout.addView(mCustomView);
     webtcard.setVisibility(View.GONE);
     mCustomViewCallback = callback;
     webholder.setVisibility(View.GONE);
     setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
     }

     public void onHideCustomView() {
     webholder.setVisibility(View.VISIBLE);
     if (mCustomView == null) return;
     mCustomView.setVisibility(View.GONE);
     webmultilayout.removeView(mCustomView);
     webmultilayout.setPadding(0, 0, 0, ContextHeight);
     webtcard.setVisibility(View.VISIBLE);
     mCustomViewCallback.onCustomViewHidden();
     mCustomView = null;
     setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
     super.onHideCustomView();
     } */
    public void onReceivedTitle(WebView v, String title) {
        //((TextView)v.getTag()).setText(title);
        if ((v == Main.web) && (!(title.equals("about:blank")))) Main.dock.setText(title);
    } 
    @Override public void onProgressChanged(final WebView v, int newProgress) {
        if (newProgress == 100) {

            if (v == Main.web) {
                Main.progressbar.setVisibility(View.INVISIBLE);
                //关闭刷新状态
                //  MainActivity.webrefreshlayout.setRefreshing(false);
                if (!(v.getTitle().equals("about:blank"))) {
                    //int c = GetWebTopColor();
                    //webcolor.set(webholderarray.indexOf(webholder), c);
                    //StatusColor(c);

                }
            } else {
                if (v == Main.web) {
                    if (View.INVISIBLE == Main.progressbar.getVisibility()) Main.progressbar.setVisibility(View.VISIBLE);
                    Main.progressbar.setProgress(newProgress); 
                }
            } super.onProgressChanged(v, newProgress);
        }
    }
}
    
