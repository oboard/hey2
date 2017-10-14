package omark.hey;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import android.animation.AnimatorSet;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.AlphaAnimation;

public class Main extends Activity {
    static Main me;
    static int webindex = 0;
    static View popn, back_icon, forward_icon;
    static HeyClipboard clipboard;
    static ScrollText dock;
    static FrameLayout desktop;
    static RelativeLayout root;
    static ProgressBar progressbar;
    static HeyWeb web;//now webview
    static ArrayList<HeyWeb> pages = new ArrayList<HeyWeb>();
    static LinearLayout multi_box, multi_scroll;
    static HorizontalScrollView multi_scroll_box;
    static ArrayList<LinearLayout> multi = new ArrayList<LinearLayout>();
    static ArrayList<ImageView> multiimage = new ArrayList<ImageView>();
    static ArrayList<TextView> multitext = new ArrayList<TextView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //创建剪辑版
        clipboard = new HeyClipboard(this);

        S.init(this, "main");
        if (S.get("first", true)) {
            S.put("first", false);
            S.put("home", "https://www.bing.com");
            S.ok();
        }

        me = this;
        popn = findViewById(R.id.main_popn);
        dock = (ScrollText)findViewById(R.id.main_dock);
        desktop = (FrameLayout)findViewById(R.id.main_desktop);
        progressbar = (ProgressBar)findViewById(R.id.main_progress);
        back_icon = findViewById(R.id.main_back_icon);
        back_icon.setBackground(new BitmapDrawable(ColoBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.back), Color.WHITE)));
        forward_icon = findViewById(R.id.main_forward_icon);
        forward_icon.setBackground(new BitmapDrawable(ColoBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.forward), Color.WHITE)));
        multi_scroll_box = (HorizontalScrollView)findViewById(R.id.main_multi_scroll_box);
        multi_scroll = (LinearLayout)findViewById(R.id.main_multi_scroll);
        multi_box = (LinearLayout)findViewById(R.id.main_multi_box);
        addMulti();//

        root = (RelativeLayout)findViewById(R.id.main_root);
        root.setBackgroundColor(0xff707070);
        multi_box.setBackgroundColor(0xffeeeeee);
        
        Intent i = getIntent();
        if (i != null) onNewIntent(i);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getDataString() == null) {
            web = addPage("");
            webindex += 1;
        } else {
            web = addPage(intent.getDataString());
        }
    }

    public void onMultiClick(View v) {
        if (v.getTag() == null) return;
        webindex = v.getTag();
        web = pages.get(webindex - 1);
        dock.setText(web.getTitle());
        
        onDockClick(null);
    } public void onDockClick(View v) {
        if (multi_scroll_box.getVisibility() == View.GONE) {

            multiimage.get(webindex - 1).setImageBitmap(getWebDrawing());
            multiimage.get(webindex - 1).setTag(webindex);
            multitext.get(webindex - 1).setText(web.getTitle());

            hidePage();
            AlphaAnimation alphaA = new AlphaAnimation(0, 1);
            alphaA.setZAdjustment(AnimationSet.ZORDER_BOTTOM);
            alphaA.setDuration(320);
            multi_scroll_box.setAnimation(alphaA);
            multi_scroll_box.setVisibility(View.VISIBLE);
        } else {
            AlphaAnimation alphaA = new AlphaAnimation(0, 1);
            alphaA.setZAdjustment(AnimationSet.ZORDER_BOTTOM);
            alphaA.setDuration(320);
            web.setAnimation(alphaA);
            
            multi_scroll_box.setVisibility(View.GONE);
            web.setVisibility(View.VISIBLE);
        }

        multi_box.setVisibility(multi_scroll_box.getVisibility());
    } public void onAddClick(View v) {
        addPage("");
        webindex += 1;
        if (webindex >= multi.size() * 2 ) addMulti();
        multi_scroll_box.setVisibility(View.GONE);
        multi_box.setVisibility(View.GONE);
    }

    public HeyWeb addPage(String uri) {
        String link = uri.equals("") ? S.get("home", "https://www.bing.com") : uri;
        HeyWeb new_web = new HeyWeb(this);
        new_web.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        new_web.loadUrl(link);
        new_web.setVisibility(View.VISIBLE);
        new_web.setWebChromeClient(new HeyWebChrome());
        new_web.setOnTouchListener(HeyWebTouch(new_web));

        desktop.addView(new_web);
        pages.add(new_web);

        webindex += uri.equals("") ? 0 : 1;

        return new_web;
    } public void hidePage() {
        for (int i = 0;i < pages.size();i++) {
            pages.get(i).setVisibility(View.GONE);
        }
    } public void addMulti() {
        int count = multi.size();
        multi.add((LinearLayout)LayoutInflater.from(this).inflate(R.layout.multi, null));
        multi_scroll.addView(multi.get(count));
        multi.get(count).findViewById(R.id.multi_root).setLayoutParams(new LinearLayout.LayoutParams(getWindowManager().getDefaultDisplay().getWidth() / 2, LinearLayout.LayoutParams.MATCH_PARENT));
        multitext.add((TextView)multi.get(count).findViewById(R.id.multi_text0));
        multitext.add((TextView)multi.get(count).findViewById(R.id.multi_text1));
        //multitext.add((TextView)multi.get(count).findViewById(R.id.multi_text2));
        //multitext.add((TextView)multi.get(count).findViewById(R.id.multi_text3));
        multiimage.add((ImageView)multi.get(count).findViewById(R.id.multi_image0));
        multiimage.add((ImageView)multi.get(count).findViewById(R.id.multi_image1));
        //multiimage.add((ImageView)multi.get(count).findViewById(R.id.multi_image2));
        //multiimage.add((ImageView)multi.get(count).findViewById(R.id.multi_image3));
    }

    public View.OnTouchListener HeyWebTouch(final HeyWeb w) {
        return (new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent moe) {
                popn.setX(w.getLeft() + moe.getX());
                popn.setY(w.getTop() + moe.getY());
                return false;
            }
        });
    }

    public Bitmap getWebDrawing() {
        View view = getWindow().getDecorView();
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return Bitmap.createBitmap(bitmap, 0, desktop.getTop(), view.getWidth(), view.getHeight() - dock.getHeight() - desktop.getTop());
    } public void ripple_version(View view_children) {
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
    } public static float dip2px(Context context, float dipValue) {
        return (dipValue * context.getResources().getDisplayMetrics().density + 0.5f) ;
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
                Main.progressbar.setVisibility(View.GONE);
                //关闭刷新状态
                //  MainActivity.webrefreshlayout.setRefreshing(false);
                if (!(v.getTitle().equals("about:blank"))) {
                    //int c = GetWebTopColor();
                    //webcolor.set(webholderarray.indexOf(webholder), c);
                    //StatusColor(c);
                }
            }
        } else {
            if (v == Main.web) {
                if (View.GONE == Main.progressbar.getVisibility()) Main.progressbar.setVisibility(View.VISIBLE);
                Main.progressbar.setProgress(newProgress);
            }
        } super.onProgressChanged(v, newProgress);

    }
}
    
