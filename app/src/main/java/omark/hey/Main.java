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
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.ShapeDrawable;
import java.net.URL;
import android.net.Uri;

public class Main extends Activity {
    static Main me;
    static int webindex = 0;
    static View popn, back_icon, forward_icon;
    static HeyClipboard clipboard;
    static ScrollText dock;
    static EditText text;
    static LinearLayout manager;
    static ImageButton manager_back;
    static FrameLayout desktop;
    static RelativeLayout root, ground;
    static ProgressBar progressbar;
    static HeyWeb web;//now webview
    static ImageButton add, clear;
    static LinearLayout multi_box, multi_scroll;
    static HorizontalScrollView multi_scroll_box;
    static ArrayList<HeyWeb> pages = new ArrayList<HeyWeb>();
    static ArrayList<LinearLayout> multi = new ArrayList<LinearLayout>();
    static ArrayList<ImageView> multiimage = new ArrayList<ImageView>();
    static ArrayList<Bitmap> multiimages = new ArrayList<Bitmap>();
    static ArrayList<Drawable> multitop = new ArrayList<Drawable>();
    static ArrayList<Integer> multibottom = new ArrayList<Integer>();
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
            S.put("search" , HeySearch.DEFAULT);
            S.put("pagecolor" , true);
            S.ok();
        }

        me = this;
        popn = findViewById(R.id.main_popn);
        dock = (ScrollText)findViewById(R.id.main_dock);
        desktop = (FrameLayout)findViewById(R.id.main_desktop);
        ground = (RelativeLayout)findViewById(R.id.main_ground);
        progressbar = (ProgressBar)findViewById(R.id.main_progress);

        text = (EditText)findViewById(R.id.main_text);
        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {  
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event)  {
                    if (actionId == EditorInfo.IME_ACTION_GO || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                        String url = v.getText().toString();
                        if (!url.equals("")) {
                            if (!HeyWeb.isUri(url))  {
                                if (url.indexOf(".") != -1) 
                                    url = "http://" + url;
                                else
                                    url = S.get("search" , HeySearch.DEFAULT) + url;
                            }
                            //转到页面
                            web.loadUrl(url);
                            onManagerBackClick(null);
                            return true;
                        }
                    }    
                    return false;
                }    
            }); 
        manager = (LinearLayout)findViewById(R.id.main_manager);
        manager_back = (ImageButton)findViewById(R.id.main_manager_back);
        ripple_version(manager_back);

        add = (ImageButton)findViewById(R.id.main_add);
        add.setImageBitmap(ColoBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.add), S.getColor(R.color.colorAccent)));
        clear = (ImageButton)findViewById(R.id.main_clear);
        clear.setImageBitmap(ColoBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.clear), S.getColor(R.color.colorAccent)));

        back_icon = findViewById(R.id.main_back_icon);
        back_icon.setBackground(new BitmapDrawable(ColoBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.back), S.getColor(R.color.colorAccent))));
        forward_icon = findViewById(R.id.main_forward_icon);
        forward_icon.setBackground(new BitmapDrawable(ColoBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.forward), S.getColor(R.color.colorAccent))));
        multi_scroll_box = (HorizontalScrollView)findViewById(R.id.main_multi_scroll_box);
        multi_scroll = (LinearLayout)findViewById(R.id.main_multi_scroll);
        multi_box = (LinearLayout)findViewById(R.id.main_multi_box);
        addMulti();//

        root = (RelativeLayout)findViewById(R.id.main_root);
        onChangeBackground(Color.TRANSPARENT, new ColorDrawable(S.getColor(R.color.colorBackground)));
        multi_box.setBackgroundColor(S.getColor(R.color.colorBackground));

        Intent i = getIntent();
        if (i != null) onNewIntent(i);
    }

    public static void onChangeBackground(Integer f, Drawable b) {
        Main.root.setBackground(b);
        Main.ground.setBackgroundColor(f);
        int c = f;
        if (f == Color.TRANSPARENT) {
            if (b instanceof ColorDrawable)
                c = ((ColorDrawable)b).getColor();
            else if (b instanceof BitmapDrawable)
                c = ((BitmapDrawable)b).getBitmap().getPixel(0,0);
        }
        if (Main.me.isLightColor(c))
            dock.setTextColor(Color.BLACK);
        else
            dock.setTextColor(Color.WHITE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getDataString() == null) {
            web = addPage("");
        } else {
            web = addPage(intent.getDataString());
        }
    }

    Boolean isExit = false;
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    }; @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        //拦截返回键
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (multi_box.getVisibility() == View.VISIBLE) {
                onDockClick(null);
                return true;
            } else if (manager.getVisibility() == View.VISIBLE) {
                onManagerBackClick(null);
                return true;    
            } else {
                if (web.canGoBack()) {
                    web.goBack();
                    return true;
                } else {
                    if (pages.size() != 1) {
                        if (!isExit) {
                            isExit = true;
                            handler.sendEmptyMessageDelayed(0, 1500);
                            Toast.makeText(this, "再按一次关闭标签页", Toast.LENGTH_SHORT).show();
                            return false;
                        } else {
                            removePage(webindex - 1);
                            freshMulti();
                            return true;
                        }

                    } else {
                        if (!isExit) {
                            isExit = true;
                            handler.sendEmptyMessageDelayed(0, 1500);
                            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                            return false;
                        } else {
                            finish();
                        }
                    }
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    } public void onMultiClick(View v) {
        if (v.getTag() == null) return;
        webindex = v.getTag();
        web = pages.get(webindex - 1);
        dock.setText(web.getTitle());
        onDockClick(null);
    } public void onDockClick(View v) {
        if (multi_scroll_box.getVisibility() == View.GONE) {

            multiimages.set(webindex - 1, getWebDrawing());
            multiimage.get(webindex - 1).setImageBitmap(multiimages.get(webindex - 1));
            multiimage.get(webindex - 1).setTag(webindex);
            multitext.get(webindex - 1).setText(web.getTitle());

            hidePage();
            AlphaAnimation alphaA = new AlphaAnimation(0, 1);
            alphaA.setZAdjustment(AnimationSet.ZORDER_BOTTOM);
            alphaA.setDuration(320);

            multitext.get(webindex - 1).setAnimation(alphaA);
            multiimage.get(webindex - 1).setAnimation(alphaA);

            multi_scroll_box.setVisibility(View.VISIBLE);
            root.setBackgroundColor(S.getColor(R.color.colorBackground));
        } else {
            AlphaAnimation alphaA = new AlphaAnimation(0, 1);
            alphaA.setZAdjustment(AnimationSet.ZORDER_BOTTOM);
            alphaA.setDuration(320);
            web.setAnimation(alphaA);

            onChangeBackground(Main.multibottom.get(Main.webindex - 1), Main.multitop.get(Main.webindex - 1));
            multi_scroll_box.setVisibility(View.GONE);
            web.setVisibility(View.VISIBLE);
        }

        multi_box.setVisibility(multi_scroll_box.getVisibility());
    } public void onDockLongClick(View v) {
        View view = getWindow().getDecorView();
        TranslateAnimation tranA = new TranslateAnimation(0, 0, view.getHeight(), desktop.getTop());
        tranA.setZAdjustment(AnimationSet.ZORDER_TOP);
        tranA.setDuration(320);
        text.setText(web.getUrl());
        manager.setAnimation(tranA);
        manager.setVisibility(View.VISIBLE);
    } public void onManagerBackClick(View v) {
        if (manager.getVisibility() == View.GONE) return;
        
        View view = getWindow().getDecorView();
        TranslateAnimation tranA = new TranslateAnimation(0, 0, desktop.getTop(), view.getHeight());
        tranA.setZAdjustment(AnimationSet.ZORDER_TOP);
        tranA.setDuration(320);
        tranA.setFillAfter(true);
        manager.setAnimation(tranA);
        
        new Handler(){
            @Override public void handleMessage(Message msg) {
                super.handleMessage(msg);
                manager.setVisibility(View.GONE);
            }
        }.sendEmptyMessageDelayed(0, 320);
    } public void onAddClick(View v) {
        web = addPage("");
        if (webindex > multi.size() * 2) addMulti();
        multi_scroll_box.setVisibility(View.GONE);
        multi_box.setVisibility(View.GONE);
    } public void onRemoveClick(View v) {
        if (pages.size() == 1) return;
        removePage((int)v.getTag() - 1);
        freshMulti();
    } public void onRemoveAllClick(View v) {
        removeAllPage();
        onDockClick(null);
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

        multitop.add(new ColorDrawable(S.getColor(R.color.colorBackground)));
        multibottom.add(0x01000000);
        multiimages.add(null);

        webindex = pages.size();

        return new_web;
    } public void hidePage() {
        for (int i = 0;i < pages.size();i++) {
            pages.get(i).setVisibility(View.GONE);
        }
    } public void removeAllPage() {
        for (int i = 0; i < pages.size(); i++) {
            HeyWeb page = pages.get(i);
            if (page != null) {
                desktop.removeView(page);
                page.loadUrl("about:blank");
                page = null;
            }
        }
        pages = new ArrayList<HeyWeb>();
        multi = new ArrayList<LinearLayout>();
        multiimage = new ArrayList<ImageView>();
        multiimages = new ArrayList<Bitmap>();
        multitop = new ArrayList<Drawable>();
        multibottom = new ArrayList<Integer>();
        multitext = new ArrayList<TextView>();
        multi_scroll.removeAllViews();
        addMulti();
        webindex = 0;
        web = addPage("");
    } public void removePage(int index) {
        HeyWeb page = pages.get(index);
        if (page != null) {
            desktop.removeView(page);
            page.loadUrl("about:blank");
            page = null;
        }
        pages.remove(index);
        multitext.remove(index);
        multiimage.remove(index);
        multiimages.remove(index);
        multibottom.remove(index);
        multitop.remove(index);
        if (webindex == index + 1) {
            webindex = index + ((index == 0) ? 1 : 0);
            web = pages.get(webindex - 1);
            web.setVisibility(View.VISIBLE);
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
    } public void freshMulti() {
        multi = new ArrayList<LinearLayout>();
        multiimage = new ArrayList<ImageView>();
        multitext = new ArrayList<TextView>();

        multi_scroll.removeAllViews();
        addMulti();
        for (int i = 0; i < pages.size() / 2 ; i++) {
            addMulti();
        }

        for (int i = 0; i < pages.size(); i++) {
            multitext.get(i).setText(pages.get(i).getTitle());
            multiimage.get(i).setImageBitmap(multiimages.get(i));
            multiimage.get(i).setTag(i + 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (int i = 0; i < pages.size(); i++) {
            HeyWeb page = pages.get(i);
            if (page != null) {
                desktop.removeView(page);
                page.clearHistory();
                page.clearCache(true);
                page.loadUrl("about:blank");
                page.freeMemory(); 
                page.pauseTimers();
                page = null;
            }
        }
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
        //if (isLightColor(bitmap.getPixel(1, 1))) canvas.drawRect(0, 0, view.getWidth(), 1, new Paint(0x11000000));
        return Bitmap.createBitmap(bitmap, 0, desktop.getTop(), view.getWidth(), view.getHeight() - dock.getHeight() - desktop.getTop());
    } public void ripple_version(View view_children) {
        //版本兼容
        int[] attrsArray = { 
            (android.os.Build.VERSION.SDK_INT >= 21) ?
            android.R.attr.selectableItemBackgroundBorderless :
            android.R.attr.selectableItemBackground
        };

        TypedArray typedArray = obtainStyledAttributes(attrsArray);
        int selector = typedArray.getResourceId(0, attrsArray[0]);
        view_children.setBackgroundResource(selector);
        typedArray.recycle();
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
        return (darkness < 0.5);
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
        if ((v == Main.web) && (!(title.equals("about:blank")))) {
            if (Main.dock.getText().equals(title)) return;
            Main.dock.setText(title);
            Main.multibottom.set(Main.webindex - 1, Color.TRANSPARENT);
            v.loadUrl("javascript:void((function(){" +
                      "try {" +
                      "Context.onReceivedThemeColor(document.querySelector(\"meta[name='theme-color']\").getAttribute(\"content\")," + Main.webindex + ");" +
                      "} catch (e) {" +
                      "Context.onReceivedThemeColor(\"\"," + Main.webindex + ");" +
                      "}" +
                      "})())");
        }
    }

    @Override 
    public void onProgressChanged(final WebView v, int newProgress) {
        if (newProgress == 100) {
            if (v == Main.web) {

                Animation aniA = AnimationUtils.loadAnimation(Main.me, R.anim.finish);
                Main.progressbar.setAnimation(aniA);

                new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        Main.progressbar.setScaleX(1);
                        Main.progressbar.setVisibility(View.GONE);
                    }
                }.sendEmptyMessageDelayed(0, 1000);

                if (!(v.getTitle().equals("about:blank") || Main.manager.getVisibility() == View.VISIBLE || Main.multi_scroll_box.getVisibility() == View.VISIBLE)) {
                    Bitmap b = Main.me.getWebDrawing();
                    Main.multitop.set(Main.webindex - 1, new BitmapDrawable(Bitmap.createBitmap(b, 0, 0, b.getWidth(), 1)));
                    Main.onChangeBackground(Main.multibottom.get(Main.webindex - 1), Main.multitop.get(Main.webindex - 1));
                }
            }
        } else {
            if (v == Main.web) {
                AlphaAnimation alphaA = new AlphaAnimation(0, 1);
                alphaA.setDuration(320);
                Main.progressbar.setAnimation(alphaA);
                if (View.GONE == Main.progressbar.getVisibility()) Main.progressbar.setVisibility(View.VISIBLE);
                Main.progressbar.setProgress(newProgress);
            }
        } super.onProgressChanged(v, newProgress);

    }
}
