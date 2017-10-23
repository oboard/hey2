package omark.hey;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
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
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
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
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ValueCallback;
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
import android.graphics.Rect;

public class Main extends Activity {
    static Main me;
    static int webindex = - 1;
    static View popn, manager_tab, blacker, back_icon, forward_icon;
    static HeyClipboard clipboard;
    static ScrollText dock;
    static EditText text;
    static LinearLayout manager;
    static ImageButton manager_back;
    static FrameLayout desktop;
    static RelativeLayout root, ground;
    static ProgressBar progressbar;
    static HeyWeb web;//now webview
    static ImageView aniimage;
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }

        //创建剪辑版
        clipboard = new HeyClipboard(this);

        S.init(this, "main");
        if (S.get("first", true)) {
            S.put("first", false);
            S.put("home", "https://www.bing.com");
            S.put("search" , HeySearch.DEFAULT);
            S.put("pagecolor" , true);
            /*
             //兼容H5？？
             S.put("h5_app_ui_pgbar", "#dedede");
             S.put("h5_app_ui_btnbg", "#aadedede");
             S.put("h5_app_ui_btntxt", "#000000");
             S.put("h5_app_ui_stbar", "#888888");
             S.put("h5_app_ui_bg", "#ffffff");
             */
            S.ok();
        }

        me = this;
        popn = findViewById(R.id.main_popn);
        blacker = findViewById(R.id.main_blacker);
        dock = (ScrollText)findViewById(R.id.main_dock);
        root = (RelativeLayout)findViewById(R.id.main_root);
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
                                    url = HeySearch.getSearch(url);
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
        manager_tab = findViewById(R.id.main_manager_tab);
        manager_back = (ImageButton)findViewById(R.id.main_manager_back);
        ripple_version(manager_back);

        add = (ImageButton)findViewById(R.id.main_add);
        add.setImageBitmap(ColoBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.add), S.getColor(R.color.colorBackground)));
        clear = (ImageButton)findViewById(R.id.main_clear);
        clear.setImageBitmap(ColoBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.clear), S.getColor(R.color.colorBackground)));

        back_icon = findViewById(R.id.main_back_icon);
        back_icon.setBackground(new BitmapDrawable(ColoBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.back), S.getColor(R.color.colorBackground))));
        forward_icon = findViewById(R.id.main_forward_icon);
        forward_icon.setBackground(new BitmapDrawable(ColoBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.forward), S.getColor(R.color.colorBackground))));
        multi_scroll_box = (HorizontalScrollView)findViewById(R.id.main_multi_scroll_box);
        multi_scroll = (LinearLayout)findViewById(R.id.main_multi_scroll);
        multi_box = (LinearLayout)findViewById(R.id.main_multi_box);
        aniimage = (ImageView)findViewById(R.id.main_aniimage);
        addMulti();//

        multi_box.setBackgroundColor(S.getColor(R.color.colorPrimary));

        onNewIntent(getIntent());

        onChangeBackground(Color.TRANSPARENT, new ColorDrawable(S.getColor(R.color.colorPrimary)));
    }

    public static void onChangeBackground(Integer f, Drawable b) {
        if (f == Color.TRANSPARENT)
            Main.root.setBackground(b);
        else
            Main.root.setBackgroundColor(f);

        int c = f;
        if (f == Color.TRANSPARENT) {
            if (b instanceof ColorDrawable)
                c = ((ColorDrawable)b).getColor();
            else if (b instanceof BitmapDrawable)
                c = ((BitmapDrawable)b).getBitmap().getPixel(0, 0);
        }
        if (Main.me.isLightColor(c))
            blacker.setVisibility(View.VISIBLE);
        //dock.setTextColor(Color.BLACK);
        else
            blacker.setVisibility(View.GONE);
        //dock.setTextColor(Color.WHITE);

        Main.root.invalidate();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent == null) {
            web = addPage("");
            return;
        }
        String action = intent.getAction();
        if (Intent.ACTION_SEND.equals(action)) {
            web = addPage(HeySearch.getSearch(intent.getStringExtra(Intent.EXTRA_TEXT)));
        } else if (Intent.ACTION_VIEW.equals(action)) {
            web = addPage(intent.getData().toString());
        } else if (Intent.ACTION_WEB_SEARCH.equals(action)) {
            web = addPage(HeySearch.getSearch(intent.getStringExtra("query")));
        } else {
            web = addPage("");
        }

    }

    Boolean isExit = false;
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    public void onBackPressed() {
        if (multi_box.getVisibility() == View.VISIBLE) {
            onDockClick(null);
            return;
        } else if (manager.getVisibility() == View.VISIBLE) {
            onManagerBackClick(null);
            return;
        } else {
            if (web.canGoBack()) {
                web.goBack();

                web.loadUrl("javascript:document.title = " + web.getTitle());
                return;
            } else {
                if (pages.size() != 1) {
                    if (!isExit) {
                        isExit = true;
                        handler.sendEmptyMessageDelayed(0, 1500);
                        Toast.makeText(this, "再按一次关闭标签页", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        removePage(webindex);
                        freshMulti();
                        return;
                    }

                } else {
                    if (!isExit) {
                        isExit = true;
                        handler.sendEmptyMessageDelayed(0, 1500);
                        Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        finish();
                    }
                }
            }
        }
    } 

    public void onMultiClick(View v) {
        if (v.getTag() == null) return;
        webindex = v.getTag();
        web = pages.get(webindex);
        dock.setText(web.getTitle());
        onDockClick(null);
    } public void onDockClick(View v) {
        if (multi_scroll_box.getVisibility() == View.GONE) {

            multiimages.set(webindex, getWebDrawing());
            multiimage.get(webindex).setImageBitmap(multiimages.get(webindex));
            multiimage.get(webindex).setTag(webindex);
            multitext.get(webindex).setText(web.getTitle());
            multiimage.get(webindex).invalidate();

            hidePage();
            scaleAni(false);
        } else {
            scaleAni(true);
        }

    } 
    @TargetApi(11)
    private void scaleAni(boolean open) {
        if (Build.VERSION.SDK_INT < 11) {
            //老版本老动画～
            if (open) {
                AlphaAnimation alphaA = new AlphaAnimation(0, 1);
                alphaA.setZAdjustment(AnimationSet.ZORDER_BOTTOM);
                alphaA.setDuration(320);
                web.setAnimation(alphaA);

                multi_scroll_box.setVisibility(View.GONE);
                web.setVisibility(View.VISIBLE);

                onChangeBackground(Main.multibottom.get(Main.webindex), Main.multitop.get(Main.webindex));
            } else {
                AlphaAnimation alphaA = new AlphaAnimation(0, 1);
                alphaA.setZAdjustment(AnimationSet.ZORDER_BOTTOM);
                alphaA.setDuration(320);

                multitext.get(webindex).setAnimation(alphaA);
                multiimage.get(webindex).setAnimation(alphaA);

                multi_scroll_box.setVisibility(View.VISIBLE);
                multi_box.setVisibility(View.VISIBLE);
                root.setBackgroundColor(S.getColor(R.color.colorPrimary));
            }
        } else {
            Rect l = new Rect(), k = new Rect();
            web.getGlobalVisibleRect(k);
            multiimage.get(webindex).getGlobalVisibleRect(l);

            ValueAnimator mAni1, mAni2, mAni3, mAni4;

            if (open) {
                mAni1 = ValueAnimator.ofInt(l.left, k.left);
                mAni2 = ValueAnimator.ofInt(l.top, k.top);
                mAni3 = ValueAnimator.ofInt(l.width(), k.width());
                mAni4 = ValueAnimator.ofInt(l.height(), k.height());

                AnimationSet aniA = new AnimationSet(true);
                aniA.addAnimation(new AlphaAnimation(1, 0));
                aniA.addAnimation(new ScaleAnimation(1f, 0.9f, 1f, 0.9f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
                aniA.setZAdjustment(AnimationSet.ZORDER_BOTTOM);
                aniA.setDuration(320);
                multi_scroll_box.setAnimation(aniA);


                multi_box.setVisibility(View.GONE);
                multi_scroll_box.setVisibility(View.GONE);
                onChangeBackground(Main.multibottom.get(Main.webindex), Main.multitop.get(Main.webindex));

                mAni1.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator ani) {
                            web.setVisibility(View.VISIBLE);
                            aniimage.setVisibility(View.GONE);
                        }
                    });
            } else {
                mAni1 = ValueAnimator.ofInt(k.left, l.left);
                mAni2 = ValueAnimator.ofInt(k.top, l.top);
                mAni3 = ValueAnimator.ofInt(k.width(), l.width());
                mAni4 = ValueAnimator.ofInt(k.height(), l.height());

                AnimationSet aniA = new AnimationSet(true);
                aniA.addAnimation(new AlphaAnimation(0, 1));
                aniA.addAnimation(new ScaleAnimation(0.9f, 1f, 0.9f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
                aniA.setZAdjustment(AnimationSet.ZORDER_BOTTOM);
                aniA.setDuration(320);
                multi_scroll_box.setAnimation(aniA);

                multi_box.setVisibility(View.VISIBLE);
                multi_scroll_box.setVisibility(View.VISIBLE);
                root.setBackgroundColor(S.getColor(R.color.colorPrimary));

                mAni1.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator ani) {
                            aniimage.setVisibility(View.GONE);
                        }
                    });
            }

            mAni1.setDuration(320).setInterpolator(new DecelerateInterpolator());
            mAni2.setDuration(320).setInterpolator(new DecelerateInterpolator());
            mAni3.setDuration(320).setInterpolator(new DecelerateInterpolator());
            mAni4.setDuration(320).setInterpolator(new DecelerateInterpolator());

            aniimage.setImageBitmap(multiimages.get(webindex));
            aniimage.setVisibility(View.VISIBLE);

            mAni1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {  
                    @Override
                    public void onAnimationUpdate(ValueAnimator ani) {  
                        int curValue = ani.getAnimatedValue();
                        aniimage.setX(curValue);
                        aniimage.invalidate();
                    }
                });
            mAni2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {  
                    @Override
                    public void onAnimationUpdate(ValueAnimator ani) {  
                        int curValue = ani.getAnimatedValue();
                        aniimage.setY(curValue);
                        aniimage.invalidate();
                    }
                });
            mAni3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {  
                    @Override
                    public void onAnimationUpdate(ValueAnimator ani) {  
                        int curValue = ani.getAnimatedValue();
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)aniimage.getLayoutParams();  
                        params.width = curValue;
                        aniimage.setLayoutParams(params);
                    }
                });
            mAni4.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {  
                    @Override
                    public void onAnimationUpdate(ValueAnimator ani) {  
                        int curValue = ani.getAnimatedValue();
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)aniimage.getLayoutParams();  
                        params.height = curValue;
                        aniimage.setLayoutParams(params);
                    }
                });

            mAni1.start();
            mAni2.start();
            mAni3.start();
            mAni4.start();
        }
    } public void onDockLongClick(View v) {
        View view = getWindow().getDecorView();

        TranslateAnimation tranA = new TranslateAnimation(0, 0, view.getHeight(), 0);
        tranA.setZAdjustment(AnimationSet.ZORDER_TOP);
        tranA.setDuration(320);

        text.setText(web.getUrl());
        text.selectAll();
        text.requestFocus();
        keyboardState(true);
        manager.setAnimation(tranA);
        manager.setVisibility(View.VISIBLE);
    } public void onManagerBackClick(View v) {
        keyboardState(false);
        if (manager.getVisibility() == View.GONE) return;

        View view = getWindow().getDecorView();
        ScaleAnimation scaleA = new ScaleAnimation(1, 0.9f, 1, 0.9f);
        scaleA.setDuration(320);
        //web.setAnimation(scaleA);

        TranslateAnimation tranA = new TranslateAnimation(0, 0, 0, view.getHeight());
        tranA.setZAdjustment(AnimationSet.ZORDER_TOP);
        tranA.setDuration(320);

        //tranA.setFillAfter(true);
        manager.setAnimation(tranA);

        new Handler(){
            @Override public void handleMessage(Message msg) {
                super.handleMessage(msg);
                manager.setVisibility(View.GONE);
            }
        }.sendEmptyMessageDelayed(0, 320);
    } public void onManagerClick(View v) {
        TranslateAnimation tranA = new TranslateAnimation(manager_tab.getTranslationX(), v.getX(), 0, 0);
        tranA.setDuration(320);
        tranA.setFillAfter(true);
        manager_tab.setAnimation(tranA);
        manager_tab.invalidate();
        manager.requestLayout();
        
        switch (v.getId()) {
            case R.id.main_manager_t0:
                
                break;
            case R.id.main_manager_t1:

                break;
            case R.id.main_manager_t2:

                break;
        }
    } public void onAddClick(View v) {
        web = addPage("");
        if (webindex + 1 > multi.size() * 2) addMulti();
        multi_scroll_box.setVisibility(View.GONE);
        multi_box.setVisibility(View.GONE);
    } public void onRemoveClick(View v) {
        if (pages.size() == 1) return;
        removePage((int)v.getTag());
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

        multitop.add(new ColorDrawable(S.getColor(R.color.colorPrimary)));
        multibottom.add(0x01000000);
        multiimages.add(null);

        webindex = pages.size() - 1;

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
        webindex = - 1;
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
        if (webindex == index) {
            webindex = index + ((index == 0) ? 0 : -1);
            web = pages.get(webindex);
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
            multiimage.get(i).setTag(i);
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
                popn.setX(w.getX() + moe.getX());
                popn.setY(w.getY() + moe.getY());
                return false;
            }
        });
    }

    public Bitmap getWebDrawing() {
        View view = getWindow().getDecorView();
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return Bitmap.createBitmap(bitmap, (int)desktop.getScrollX(), (int)desktop.getY(), view.getWidth() - (int)desktop.getScrollX(), view.getHeight() - dock.getHeight() - desktop.getTop());
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
    } public static void keyboardState(boolean open) {
        InputMethodManager i = (InputMethodManager)Main.me.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (open) {
            i.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
        } else {
            View view = Main.me.getWindow().peekDecorView();
            if (view != null) i.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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



    ValueCallback<Uri> mUploadMessage;
    ValueCallback<Uri[]> mUploadMessageForAndroid5;

    public void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), 333);
    }

    public void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
        mUploadMessageForAndroid5 = uploadMsg;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");

        startActivityForResult(chooserIntent, 335);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 333) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null: intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        } else if (requestCode == 335) {
            if (null == mUploadMessageForAndroid5)
                return;
            Uri result = (intent == null || resultCode != RESULT_OK) ? null: intent.getData();
            if (result != null) {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            } else {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            }
            mUploadMessageForAndroid5 = null;
        }
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
        try {
            if (title.equals("about:blank")) return;
            if (Main.web == v) Main.dock.setText(title);

            int webi = Main.pages.indexOf(v);
            Main.multitext.get(webi).setText(title);
            Main.multibottom.set(webi, 0x01000000);
            v.loadUrl("javascript:void((function(){" +
                      "try {" +
                      "Context.onReceivedThemeColor(document.querySelector(\"meta[name='theme-color']\").getAttribute(\"content\")," + webi + ");" +
                      "} catch (e) {" +
                      "Context.onReceivedThemeColor(\"\"," + webi + ");" +
                      "}" +
                      "})())");
        } catch (Exception e) {
            e.printStackTrace();
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
                    //if (v.getUrl().contains("coolapk.com"))
                    Main.multitop.set(Main.webindex, new BitmapDrawable(Bitmap.createBitmap(b , 0, 0, b.getWidth(), 1)));
                    //else
                    //    Main.multitop.set(Main.webindex, new ColorDrawable(Bitmap.createBitmap(b, 0, 0, b.getWidth(), 1).getPixel(0, 0)));
                    Main.onChangeBackground(Main.multibottom.get(Main.webindex), Main.multitop.get(Main.webindex));
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




    //扩展浏览器上传文件
    //3.0++版本
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        Main.me.openFileChooserImpl(uploadMsg);
    }

    //3.0--版本
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        Main.me.openFileChooserImpl(uploadMsg);
    }

    //4.x
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        Main.me.openFileChooserImpl(uploadMsg);
    }

    // For Android > 5.0
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> uploadMsg, WebChromeClient.FileChooserParams fileChooserParams) {
        Main.me.openFileChooserImplForAndroid5(uploadMsg);
        return true;
    }


}
