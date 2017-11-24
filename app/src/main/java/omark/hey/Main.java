package omark.hey;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.os.Looper;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import omark.hey.control.HeyProgress;
import omark.hey.control.HeySetting;
import android.widget.ScrollView;
import android.view.animation.TranslateAnimation;

public class Main extends Activity {
    static Main me;
    static int webindex = - 1;
    static boolean vmode = false;
    static View popn, manager_tab, blacker, night, desktop_float;
    static HeyClipboard clipboard;
    static ScrollText dock;
    static EditText text;
    static GridView menu;
    static FrameLayout desktop, simulation, multi_box;
    static RelativeLayout root, ground, manager, manager_ground;
    static HeyProgress progressbar;
    static HeyWeb web;
    static ImageView background;
    static Spinner simulation_u, simulation_a;
    static TextView multi_text, back_icon, forward_icon, manager_back, button_left, button_right, button_number, manager_th;
    static TextView simulation_back, nomarkbook, multi_box_add, multi_box_remove, button_back;
    static TextView[] manager_tab_button = new TextView[2];
    static ListView bookmark_list, history_list;
    static LinearLayout multi_scroll;
    static HorizontalScrollView multi_scroll_box;
    static ScrollView settings;
    static ArrayList<HeyWeb> pages = new ArrayList<HeyWeb>();
    static ArrayList<LinearLayout> multi = new ArrayList<LinearLayout>();
    static ArrayList<ImageView> multiimage = new ArrayList<ImageView>();
    static ArrayList<Bitmap> multiimages = new ArrayList<Bitmap>();
    static ArrayList<Drawable> multitop = new ArrayList<Drawable>();
    static ArrayList<Integer> multibottom = new ArrayList<Integer>();
    static ArrayList<TextView> multitext = new ArrayList<TextView>();
    static ArrayList<HeyMenu> menus = new ArrayList<HeyMenu>();
    static HeyBookmark bookmark;
    static HeyHistory history;
    static Bitmap lastimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        S.init(this, "main");

        if (S.get("first", true)) {
            S.put("first", false)
                .put("home", HeyHelper.DEFAULT_HOME)
                .put("search" , HeyHelper.DEFAULT_SEARCH)
                .put("searchindex" , 0)
                .put("pagecolor", true)
                .put("style", 0)
                .ok();
            /*
             //兼容H5？？
             S.put("h5_app_ui_pgbar", "#dedede");
             S.put("h5_app_ui_btnbg", "#aadedede");
             S.put("h5_app_ui_btntxt", "#000000");
             S.put("h5_app_ui_stbar", "#888888");
             S.put("h5_app_ui_bg", "#ffffff");
             */
        }

        setContentView(R.layout.main);




        me = this;
        popn = findViewById(R.id.main_popn);
        night = findViewById(R.id.main_night);
        blacker = findViewById(R.id.main_blacker);
        menu = (GridView)findViewById(R.id.main_menu);
        text = (EditText)findViewById(R.id.main_text);
        dock = (ScrollText)findViewById(R.id.main_dock);
        manager_tab = findViewById(R.id.main_manager_tab);
        root = (RelativeLayout)findViewById(R.id.main_root);
        desktop_float = findViewById(R.id.main_desktop_float);
        desktop = (FrameLayout)findViewById(R.id.main_desktop);
        ground = (RelativeLayout)findViewById(R.id.main_ground);
        settings = (ScrollView)findViewById(R.id.main_settings);
        manager = (RelativeLayout)findViewById(R.id.main_manager);
        background = (ImageView)findViewById(R.id.main_background);
        progressbar = (HeyProgress)findViewById(R.id.main_progress);
        manager_ground = (RelativeLayout)findViewById(R.id.main_manager_ground);
        ((HeySetting)findViewById(R.id.setting_2)).setChecked(S.get("pagecolor", true));
        night.setTag(false);

        //4.4以上透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
            ground.setFitsSystemWindows(true);
            ground.setClipToPadding(true);
        }

        //创建
        clipboard = new HeyClipboard(this);
        bookmark = new HeyBookmark();
        history = new HeyHistory();
        HeyWindowManager.init();

        final AlphaAnimation alphaA = new AlphaAnimation(1, 0);
        alphaA.setDuration(225);
        alphaA.setFillAfter(true);
        night.startAnimation(alphaA);

        menu.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView view, final View v, final int i, long l) {
                    //点击菜单后，滚回去～
                    View viewGroup = (View)dock.getParent();
                    viewGroup.scrollTo(0, 0);
                    switch (i) {
                        case 0:
                            web = addPage("");
                            break;
                        case 1:
                            web.reload();
                            break;
                        case 2:
                            try {
                                //分享文字
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_TEXT, web.getUrl());
                                shareIntent.setType("text/plain");
                                //设置分享列表的标题，并且每次都显示分享列表
                                startActivity(Intent.createChooser(shareIntent, getString(R.string.lang37))); 
                            } catch (Exception e) {
                                Toast.makeText(Main.this, getString(R.string.lang21), Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case 3:
                            web.loadUrl(S.get("home", HeyHelper.DEFAULT_HOME));
                            break;
                        case 4:
                            LinearLayout dl = (LinearLayout)LayoutInflater.from(Main.this).inflate(R.layout.diglog_bookmark, null);
                            final EditText t1 = (EditText)dl.findViewById(R.id.diglog_bookmark_1), t2 = (EditText)dl.findViewById(R.id.diglog_bookmark_2);
                            t1.setText(web.getTitle());
                            t2.setText(web.getUrl());
                            new AlertDialog.Builder(Main.this).setView(dl).setTitle(R.string.lang17)
                                .setNegativeButton(R.string.lang4, null).setPositiveButton(R.string.lang3, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int i) {
                                        S.addIndexX("bm" , new String[] {"b", "bn", "bt"}, new String[] {t2.getText().toString(), t1.getText().toString(), "" + System.currentTimeMillis()});

                                        bookmark_list.setAdapter(bookmark.getAdapter());
                                        Toast.makeText(Main.this, R.string.lang20, Toast.LENGTH_SHORT).show();
                                    }
                                }).show();
                            break;
                        case 7:
                            HeyWindowManager.addWindow(Main.this.getApplicationContext());
                            HeyWindowManager.web_x.loadUrl(web.getUrl());
                            break;
                        case 6:
                            //真空模式
                            setVmode(!menus.get(webindex).getState(i));
                            break;
                        case 5:
                            //夜间模式
                            if (!menus.get(webindex).getState(i)) {
                                final AlphaAnimation alphaA = new AlphaAnimation(0, 1);
                                alphaA.setDuration(225);
                                alphaA.setFillAfter(true);
                                night.startAnimation(alphaA);
                                night.setTag(true);
                                for (int k = 0; k < pages.size(); k++) {
                                    menus.get(k).setState(i, true);
                                }
                            } else {
                                final AlphaAnimation alphaA = new AlphaAnimation(1, 0);
                                alphaA.setDuration(225);
                                alphaA.setFillAfter(true);
                                night.startAnimation(alphaA);
                                night.setTag(false);
                                for (int k = 0; k < pages.size(); k++) {
                                    menus.get(k).setState(i, false);
                                }
                            }
                            break;
                        case 8:
                            //开发者模式
                            if (!menus.get(webindex).getState(i)) {
                                web.loadUrl("javascript:(function(){var script=document.createElement('script');script.src='http://eruda.liriliri.io/eruda.min.js';document.body.appendChild(script);script.onload=function(){eruda.init();eruda.show();};})()");
                                menus.get(webindex).setState(i, true);
                            } else {
                                web.loadUrl("javascript:(function(){eruda.destroy()})()");
                                menus.get(webindex).setState(i, false);
                            }
                            break;
                        case 9:
                            //仿真
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    public void run() {
                                        freshSimulation();

                                        final AlphaAnimation tranA = new AlphaAnimation(0, 1);
                                        tranA.setDuration(225);
                                        simulation.startAnimation(tranA);
                                        simulation.setVisibility(View.VISIBLE);

                                        AnimationSet aniA = new AnimationSet(true);
                                        aniA.addAnimation(new ScaleAnimation(1, 0.5f, 1f, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.2f));
                                        aniA.setInterpolator(new DecelerateInterpolator());
                                        aniA.setFillAfter(true);
                                        aniA.setDuration(225);
                                        desktop.startAnimation(aniA);

                                    }
                                });
                            break;
                        case 10:
                            //设置
                            AnimationSet aniB = new AnimationSet(true);
                            aniB.addAnimation(new TranslateAnimation(0, 0, dip2px(Main.this, 8), 0));
                            aniB.addAnimation(new AlphaAnimation(0, 1));
                            aniB.setInterpolator(new DecelerateInterpolator());
                            aniB.setDuration(225);
                            settings.startAnimation(aniB);
                            settings.setVisibility(View.VISIBLE);

                            AnimationSet aniA = new AnimationSet(true);
                            aniA.addAnimation(new ScaleAnimation(1, 0.9f, 1f, 0.9f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.2f));
                            aniA.addAnimation(new AlphaAnimation(1, 0));
                            aniA.setInterpolator(new DecelerateInterpolator());
                            aniA.setDuration(225);
                            aniA.setAnimationListener(new Animation.AnimationListener() {
                                    public void onAnimationStart(Animation ani) {}
                                    public void onAnimationRepeat(Animation ani) {}
                                    public void onAnimationEnd(Animation ani) {
                                        menu.setVisibility(View.GONE);
                                    }
                                });
                            menu.startAnimation(aniA);
                            viewGroup.scrollTo(0, 288);

                            button_back.setVisibility(View.VISIBLE);
                            dock.setVisibility(View.GONE);
                            return;
                        default:
                            Toast.makeText(Main.this, "不存在的操作", Toast.LENGTH_SHORT).show();
                            break;
                    }

                    onMenu(false);
                }
            });

        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {  
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event)  {
                    if (actionId == EditorInfo.IME_ACTION_GO || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                        String url = v.getText().toString();
                        if (!url.equals("")) {
                            url = HeyHelper.toWeb(url);
                            //转到页面
                            web.loadUrl(url);
                            onManagerBackClick(null);
                            return true;
                        }
                    }
                    return false;
                }    
            });

        bookmark_list = (ListView)findViewById(R.id.main_manager_bookmark_list);
        history_list = (ListView)findViewById(R.id.main_manager_history_list);

        manager_back = (TextView)findViewById(R.id.main_manager_back);
        back_icon = (TextView)findViewById(R.id.main_back_icon);
        forward_icon = (TextView)findViewById(R.id.main_forward_icon);
        button_left = (TextView)findViewById(R.id.main_button_left);
        button_right = (TextView)findViewById(R.id.main_button_right);
        button_number = (TextView)findViewById(R.id.main_button_number);
        button_back = (TextView)findViewById(R.id.main_button_back);

        multi_box_add = (TextView)findViewById(R.id.main_multi_box_add);
        multi_box_remove = (TextView)findViewById(R.id.main_multi_box_remove);

        manager_back.setTextColor(S.getColor(R.color.colorPrimary));
        back_icon.setTextColor(S.getColor(R.color.colorBackground));
        forward_icon.setTextColor(S.getColor(R.color.colorBackground));
        button_left.setText(String.valueOf((char)((Integer)0xE6DD).intValue()));
        button_right.setText(String.valueOf((char)((Integer)0xE3FA).intValue()));

        HeyHelper.setFont(back_icon, "m");
        HeyHelper.setFont(button_back, "m");
        HeyHelper.setFont(button_left, "m");
        HeyHelper.setFont(button_right, "m");
        HeyHelper.setFont(forward_icon, "m");
        HeyHelper.setFont(manager_back, "m");
        HeyHelper.setFont(multi_box_add, "m");
        HeyHelper.setFont(multi_box_remove, "m");
        ripple_version(manager_back);
        ripple_version(button_back);

        multi_scroll_box = (HorizontalScrollView)findViewById(R.id.main_multi_scroll_box);
        multi_scroll = (LinearLayout)findViewById(R.id.main_multi_scroll);
        multi_box = (FrameLayout)findViewById(R.id.main_multi_box);
        multi_text = (TextView)findViewById(R.id.main_multi_box_text);
        addMulti();

        nomarkbook = (TextView)findViewById(R.id.main_nomarkbook);
        manager_tab_button[0] = (TextView)findViewById(R.id.main_manager_t0);
        manager_tab_button[1] = (TextView)findViewById(R.id.main_manager_t1);
        for (TextView tvv : manager_tab_button) {
            HeyHelper.setFont(tvv, "m");
        }

        manager_th = (TextView)findViewById(R.id.main_manager_th);
        HeyHelper.setFont(manager_th, "m");

        onIntent(getIntent());
        if (webindex < 1)
            onIntent(null);

        onChangeBackground(Color.TRANSPARENT, getHeyBackground());
        freshDock();

        bookmark_list.setAdapter(bookmark.getAdapter());
        bookmark_list.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView view, View v, int index, long l) {
                    String url = bookmark.getData().get(index).get("url").toString();
                    web.loadUrl(url);
                    onManagerBackClick(null);
                }
            });
        bookmark_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView view, View v, final int index, long l) {
                    final LinearLayout dl = (LinearLayout)LayoutInflater.from(Main.this).inflate(R.layout.diglog_bookmark, null);
                    final EditText t1 = (EditText)dl.findViewById(R.id.diglog_bookmark_1), t2 = (EditText)dl.findViewById(R.id.diglog_bookmark_2);;   
                    final List<Map<String, Object>> hbm = bookmark.data_list;
                    t1.setText(hbm.get(index).get("title").toString());
                    t2.setText(hbm.get(index).get("url").toString());
                    new AlertDialog.Builder(Main.this).setView(dl).setTitle(R.string.lang11)
                        .setNegativeButton(R.string.lang4, null).setPositiveButton(R.string.lang3, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int i) {
                                S.put("bn" + index, t1.getText().toString())
                                    .put("b" + index, t2.getText().toString())
                                    .put("bt" + index, "" + System.currentTimeMillis())
                                    .ok();
                                bookmark_list.setAdapter(bookmark.getAdapter());
                            }
                        }).setNeutralButton(R.string.lang10, new DialogInterface.OnClickListener() {   
                            public void onClick(DialogInterface dialog, int i) {
                                S.delIndexX("bm", new String[] {"b", "bn", "bt"}, index);
                                bookmark_list.setAdapter(bookmark.getAdapter());
                            }
                        }).show();
                    return true;
                }
            });

        history_list.setAdapter(history.getAdapter());
        history_list.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView view, View v, int index, long l) {
                    String url = history.getData().get(index).get("url").toString();
                    web.loadUrl(url);
                    onManagerBackClick(null);
                }
            });
        history_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView view, View v, final int i, long l) {
                    final int index = history.data_list.size() - i - 1;
                    final LinearLayout dl = (LinearLayout)LayoutInflater.from(Main.this).inflate(R.layout.diglog_bookmark, null);
                    final EditText t1 = (EditText)dl.findViewById(R.id.diglog_bookmark_1), t2 = (EditText)dl.findViewById(R.id.diglog_bookmark_2);;   
                    final List<Map<String, Object>> hbm = history.data_list;
                    t1.setText(hbm.get(i).get("title").toString());
                    t2.setText(hbm.get(i).get("url").toString());
                    new AlertDialog.Builder(Main.this).setView(dl).setTitle(R.string.lang11)
                        .setNegativeButton(R.string.lang4, null).setPositiveButton(R.string.lang3, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int i) {
                                S.put("hn" + index, t1.getText().toString())
                                    .put("h" + index, t2.getText().toString())
                                    .put("ht" + index, "" + System.currentTimeMillis())
                                    .ok();
                                history_list.setAdapter(history.getAdapter());
                            }
                        }).setNeutralButton(R.string.lang10, new DialogInterface.OnClickListener() {   
                            public void onClick(DialogInterface dialog, int i) {
                                S.delIndexX("hm", new String[] {"h", "hn", "ht"}, index);
                                history_list.setAdapter(history.getAdapter());
                            }
                        }).show();
                    return true;
                }
            });

        simulation = (FrameLayout)findViewById(R.id.main_simulation);
        simulation_u = (Spinner)findViewById(R.id.simulation_u);
        simulation_u.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, UserAgent.s));
        simulation_u.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    simulation_a.setAdapter(new ArrayAdapter<String>(Main.this, android.R.layout.simple_spinner_item, UserAgent.ss[pos]));
                    setUA(UserAgent.ssr()[pos][0]);
                    web.reload();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        simulation_a = (Spinner)findViewById(R.id.simulation_a);
        simulation_a.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, UserAgent.ss[0]));
        simulation_a.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    setUA(UserAgent.ssr()[(int)simulation_u.getSelectedItemPosition()][pos]);
                    web.reload();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });/*
         simulation_d = (Spinner)findViewById(R.id.simulation_d);
         simulation_d.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
         @Override
         public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
         Toast.makeText(Main.this, "你点击的是:" + pos, 2000).show();
         }
         @Override
         public void onNothingSelected(AdapterView<?> parent) {}
         });*/
        simulation_back = (TextView)findViewById(R.id.simulation_back);
        ripple_version(simulation_back);
        HeyHelper.setFont(simulation_back, "m");

    }

    public static void onMenu(boolean open) {
        ScrollText.isMenu = open;
        if (open) {
            button_right.setVisibility(View.GONE);
            button_number.setVisibility(View.GONE);
            desktop_float.setVisibility(View.VISIBLE);
        } else {
            freshDock();
            desktop_float.setVisibility(View.GONE);
        }
    }

    public void setVmode(boolean b) {
        //关掉缓存和Cookies...
        for (int k = 0; k < pages.size(); k++) {
            HeyWeb page = pages.get(k);
            page.getSettings().setAppCacheEnabled(!b);
            page.getSettings().setDatabaseEnabled(!b);
            page.getSettings().setDomStorageEnabled(!b);
            menus.get(k).setState(6, b);
        }
        CookieManager.getInstance().setAcceptCookie(!b);
        vmode = b;
    }

    public void onSimulationBack(View v) {
        final AlphaAnimation tranA = new AlphaAnimation(1, 0);
        tranA.setDuration(225);
        tranA.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation ani) {}
                public void onAnimationRepeat(Animation ani) {}
                public void onAnimationEnd(Animation ani) {
                    simulation.setVisibility(View.GONE);
                }
            });
        simulation.startAnimation(tranA);

        AnimationSet aniA = new AnimationSet(true);
        aniA.addAnimation(new ScaleAnimation(0.5f, 1f, 0.5f, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.2f));
        aniA.setInterpolator(new DecelerateInterpolator());
        aniA.setFillAfter(true);
        aniA.setDuration(225);
        desktop.startAnimation(aniA);
    }

    public void onBackWeb(View v) {
        onDockClick(null);
    }

    public void onBarClick(View v) {
        switch (v.getId()) {
            case R.id.main_button_left:
                onDockClick(v);
                if (Math.abs(ground.getScrollY()) <= dip2px(this, 48)) {
                    dock.scroller.startScroll(ground.getScrollX(), ground.getScrollY(), -ground.getScrollX(), (int)dip2px(this, 160) - ground.getScrollY(), 195);
                    onMenu(true);
                } else {
                    dock.scroller.startScroll(ground.getScrollX(), ground.getScrollY(), -ground.getScrollX(), -ground.getScrollY(), 195);
                    onMenu(false);
                }
                dock.invalidate();
                break;
            case R.id.main_button_right:
                onDockClick(null);
                break;
            case R.id.main_button_back:
                button_back.setVisibility(View.GONE);
                //设置
                AnimationSet aniB = new AnimationSet(true);
                aniB.addAnimation( new TranslateAnimation(0, 0, 0, dip2px(Main.this, 8)));
                aniB.addAnimation(new AlphaAnimation(1, 0));
                aniB.setInterpolator(new DecelerateInterpolator());
                aniB.setDuration(225);
                aniB.setAnimationListener(new Animation.AnimationListener() {
                        public void onAnimationStart(Animation ani) {}
                        public void onAnimationRepeat(Animation ani) {}
                        public void onAnimationEnd(Animation ani) {
                            settings.setVisibility(View.GONE);
                        }
                    });
                settings.startAnimation(aniB);

                AnimationSet aniA = new AnimationSet(true);
                aniA.addAnimation(new ScaleAnimation(0.9f, 1f, 0.9f, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.2f));
                aniA.addAnimation(new AlphaAnimation(0, 1));
                aniA.setInterpolator(new DecelerateInterpolator());
                aniA.setDuration(225);
                menu.startAnimation(aniA);
                menu.setVisibility(View.VISIBLE);
                View viewGroup = (View)dock.getParent();
                viewGroup.scrollTo(0, 160);
                dock.setVisibility(View.VISIBLE);
                break;
            case R.id.main_desktop_float:
                button_back.setVisibility(View.GONE);
                menu.setVisibility(View.VISIBLE);
                settings.setVisibility(View.GONE);
                onBarClick(button_left);
                break;
        }
    }

    public void onSettingClick(View v) {
        switch (v.getId()) {
            case R.id.setting_1:
                final EditText et = new EditText(this);
                et.setText(S.get("home", HeyHelper.DEFAULT_HOME));
                new AlertDialog.Builder(this).setView(et).setTitle(v.getTag().toString())
                    .setNegativeButton(R.string.lang4, null).setPositiveButton(R.string.lang3, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int i) {
                            S.put("home", et.getText().toString())
                                .ok();
                        }
                    }).setNeutralButton(R.string.lang1, new DialogInterface.OnClickListener() {   
                        public void onClick(DialogInterface dialog, int i) {
                            S.put("home", HeyHelper.DEFAULT_HOME)
                                .ok();
                        }
                    }).show();
                break;
            case R.id.setting_2:
                S.put("pagecolor", ((HeySetting)v).isChecked())
                    .ok();
                break;
            case R.id.setting_3:
                final String[] se = {
                    "Bing",
                    "Google",
                    "Baidu",
                    "Sogou",
                    "Yandex",
                    "Yahoo",
                    "360"
                };
                final String[] su = {
                    HeyHelper.DEFAULT_SEARCH,
                    "https://google.com.hk/search?q=",
                    "https://baidu.com/s?word=",
                    "https://sogo.com/web?query=",
                    "https://yandex.com/search/?text=",
                    "https://search.yahoo.com/search?p=",
                    "https://so.com/s?q="
                };
                new AlertDialog.Builder(this).setTitle(v.getTag().toString()).setSingleChoiceItems(se, S.get("searchindex", 0), new DialogInterface.OnClickListener() { 
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            S.put("search", su[which])
                                .put("searchindex", which)
                                .ok();

                            dialog.dismiss();
                        } 
                    }).show();
                break;
            case R.id.setting_4:
                new AlertDialog.Builder(this).setTitle(v.getTag().toString())
                    .setItems(new String[] {
                        S.getString(R.string.lang1),
                        S.getString(R.string.lang13)
                    }, new DialogInterface.OnClickListener() { 
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            S.put("background", which).ok();
                            if (which == 0) return;
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent, 1);
                        }
                    }).show();
                break;
            case R.id.setting_5:
                new AlertDialog.Builder(this).setTitle(v.getTag().toString())
                    .setSingleChoiceItems(new String[] {
                        S.getString(R.string.lang14),
                        S.getString(R.string.lang15)
                    }, S.get("style", 0), new DialogInterface.OnClickListener() { 
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            S.put("style", which).ok();
                            freshDock();

                            dialog.dismiss();
                        }
                    }).show();
                break;
            case R.id.setting_6:
                startActivity(new Intent(this, About.class));
                break;
        }
    }

    public static void freshSimulation() {
        simulation.invalidate();
        try {
            Bitmap bitmap = Bitmap.createBitmap(lastimage, 0, (int)simulation.getY(), lastimage.getWidth(), (int)(dip2px(Main.me, 240)));
            simulation.setBackgroundDrawable(new BitmapDrawable(FastBlur.rsBlur(Main.me, bitmap, 25)));
        } catch (Exception e) {
            simulation.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }
    }

    public static void freshDock() {
        switch (S.get("style", 0)) {
            case 0:
                button_left.setVisibility(View.VISIBLE);
                button_right.setVisibility(View.VISIBLE);
                button_number.setVisibility(View.VISIBLE);
                break;
            case 1:
                button_left.setVisibility(View.GONE);
                button_right.setVisibility(View.GONE);
                button_number.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        dock.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)desktop.getLayoutParams();
        lp.setMargins(0, 0, 0, (int)dip2px(Main.me, 48));
        desktop.setLayoutParams(lp);
        dock.getLayoutParams().height = (int)dip2px(Main.me, 48);
    }

    public static void setUA(String s) {
        for (Object p : pages.toArray()) {
            HeyWeb page = (HeyWeb)p;
            if (page != null) {
                page.getSettings().setUserAgentString(s);
            }
        }
    }

    public static void onChangeBackground(Integer f, Drawable b) {
        if (f == Color.TRANSPARENT)
            Main.background.setImageDrawable(b);
        else
            Main.background.setImageDrawable(new ColorDrawable(f));

        int c = f;
        if (f == Color.TRANSPARENT) {
            if (b instanceof ColorDrawable)
                c = ((ColorDrawable)b).getColor();
            else if (b instanceof BitmapDrawable) {
                //Bitmap hb = ((BitmapDrawable)b).getBitmap();
                //c = hb.getPixel(hb.getWidth() / 2, 0);
                blacker.setVisibility(View.VISIBLE);
                return;
            }
        }
        if (HeyHelper.isLightColor(c))
            blacker.setVisibility(View.VISIBLE);
        //dock.setTextColor(Color.BLACK);
        else
            blacker.setVisibility(View.GONE);
        //dock.setTextColor(Color.WHITE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        onIntent(intent);
    }

    public void onIntent(Intent intent) {
        if (intent == null) {
            web = addPage("");
            return;
        }
        if (intent.getAction() == null) return;
        try {
            switch (intent.getAction()) {
                case Intent.ACTION_SEND:
                    web = addPage(HeyHelper.getSearch(intent.getStringExtra(Intent.EXTRA_TEXT)));
                    break;
                case Intent.ACTION_VIEW:
                    web = addPage(intent.getData().toString());
                    break;
                case Intent.ACTION_WEB_SEARCH:
                    web = addPage(HeyHelper.getSearch(intent.getStringExtra("query")));
                    break;
                case "omark.hey.addbookmark":
                    LinearLayout dl = (LinearLayout)LayoutInflater.from(Main.this).inflate(R.layout.diglog_bookmark, null);
                    final EditText t1 = (EditText)dl.findViewById(R.id.diglog_bookmark_1), t2 = (EditText)dl.findViewById(R.id.diglog_bookmark_2);
                    new AlertDialog.Builder(Main.this).setView(dl).setTitle(R.string.lang17)
                        .setNegativeButton(R.string.lang4, null).setPositiveButton(R.string.lang3, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int i) {
                                S.addIndexX("bm" , new String[] {"b", "bn", "bt"}, new String[] {t2.getText().toString(), t1.getText().toString(), "" + System.currentTimeMillis()});
                                finish();
                            }
                        }).show();
                    break;
                case "omark.hey.bookmark":
                    onDockLongClick(null);
                    break;
                case "omark.hey.add2":
                    setVmode(true);
                    break;
                case "omark.hey.add":
                    web = addPage("");
                    break;
            }
        } catch (Exception e) {}

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
        if (ScrollText.isMenu) {
            if (settings.getVisibility() == View.VISIBLE)
                onBarClick(button_back);
            else
                onBarClick(button_left);
            return;
        }
        
        if (pages.size() == 0) {
            web = addPage("");
            freshDock();
            return;
        } else {
            final HeyWebChrome hWC = (HeyWebChrome)web.getWebChromeClient();
            if (hWC.mCustomView != null)
                if (hWC.mCustomView.getVisibility() == View.VISIBLE) {
                    hWC.onHideCustomView();
                    return;
                }
        }

        if (multi_scroll_box.getVisibility() == View.VISIBLE)
            onDockClick(null);
        else if (manager.getVisibility() == View.VISIBLE)
            onManagerBackClick(null);
        else if (simulation.getVisibility() == View.VISIBLE) {
            onSimulationBack(null);
        } else {
            if (web.canGoBack()) {
                web.goBack();

                web.loadUrl("javascript:document.title = " + web.getTitle());
            } else {
                if (pages.size() != 1)
                    if (!isExit) {
                        isExit = true;
                        handler.sendEmptyMessageDelayed(0, 1500);
                        Toast.makeText(this, "再按一次关闭标签页", Toast.LENGTH_SHORT).show();
                    } else {
                        removePage(webindex);
                        freshMulti();
                    }
                else {
                    if (!isExit) {
                        isExit = true;
                        handler.sendEmptyMessageDelayed(0, 1500);
                        Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                    } else
                        finish();
                }
            }
        }
    } 

    public void onMultiClick(View v) {
        if (v.getTag() == null) return;
        if ((int)v.getTag() >= pages.size()) {
            v.setTag(null);
            return;
        }
        webindex = v.getTag();
        web = pages.get(webindex);
        if (web.getProgress() >= 100) 
            progressbar.setVisibility(View.GONE);
        else
            progressbar.setProgress(web.getProgress());
        menu.setAdapter(menus.get(webindex).getAdapter());

        dock.setText(web.getTitle());
        onDockClick(null);
    } public void onDockClick(View v) {
        if (multi_scroll_box.getVisibility() == View.GONE || v != null) {
            freshMulti();
            try {
                Bitmap l = getWebDrawing();
                multiimages.set(webindex, l);
                multiimage.get(webindex).setImageBitmap(HeyHelper.getRoundedCornerBitmap(l, dip2px(this, 2)));
                multiimage.get(webindex).setTag(webindex);
                multitext.get(webindex).setText(web.getTitle());
            } catch (Exception e) {
                //手速过快
            }
            if (v != null) return;

            hidePage();
            dock.setVisibility(View.GONE);
            button_left.setVisibility(View.GONE);
            button_right.setVisibility(View.GONE);
            button_number.setVisibility(View.GONE);
            scaleAni(false);
        } else {
            freshDock();
            scaleAni(true);
        }

    }

    private void scaleAni(boolean open) {
        if (open) {
            AnimationSet aniA = new AnimationSet(true);
            aniA.addAnimation(new ScaleAnimation(0.9f, 1f, 0.9f, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f));
            aniA.addAnimation(new AlphaAnimation(0f, 1f));
            aniA.setInterpolator(new DecelerateInterpolator());
            aniA.setDuration(225);
            web.startAnimation(aniA);
            web.setVisibility(View.VISIBLE);

            if (pages.size() > webindex && S.get("pagecolor", true))
                onChangeBackground(Main.multibottom.get(Main.webindex), Main.multitop.get(Main.webindex));

            AnimationSet aniB = new AnimationSet(true);
            aniB.addAnimation(new ScaleAnimation(1f, 1.2f, 1f, 1.2f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f));
            aniB.addAnimation(new AlphaAnimation(1f, 0f));
            aniB.setInterpolator(new DecelerateInterpolator());
            aniB.setZAdjustment(AnimationSet.ZORDER_BOTTOM);
            aniB.setDuration(225);
            aniB.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationStart(Animation ani) {}
                    public void onAnimationRepeat(Animation ani) {}
                    public void onAnimationEnd(Animation ani) {
                        multi_scroll_box.setVisibility(View.GONE);
                    }
                });
            multi_scroll_box.startAnimation(aniB);

            multi_scroll_box.setVisibility(View.VISIBLE);
            multi_box.setVisibility(View.GONE);

        } else {
            if (web != null) {
                AnimationSet aniA = new AnimationSet(true);
                aniA.addAnimation(new ScaleAnimation(1, 0.9f, 1f, 0.9f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f));
                aniA.addAnimation(new AlphaAnimation(1f, 0f));
                aniA.setInterpolator(new DecelerateInterpolator());
                aniA.setDuration(225);
                web.startAnimation(aniA);
                web.setVisibility(View.VISIBLE);
                aniA.setAnimationListener(new Animation.AnimationListener() {
                        public void onAnimationStart(Animation ani) {}
                        public void onAnimationRepeat(Animation ani) {}
                        public void onAnimationEnd(Animation ani) {
                            web.setVisibility(View.GONE);
                        }
                    });
            }
            AnimationSet aniB = new AnimationSet(true);
            aniB.addAnimation(new ScaleAnimation(1.2f, 1f, 1.2f, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f));
            aniB.addAnimation(new AlphaAnimation(0f, 1f));
            aniB.setInterpolator(new DecelerateInterpolator());
            aniB.setZAdjustment(AnimationSet.ZORDER_BOTTOM);
            aniB.setDuration(225);
            multi_scroll_box.startAnimation(aniB);

            multi_scroll_box.setVisibility(View.VISIBLE);
            multi_box.setVisibility(View.VISIBLE);
            root.setBackgroundDrawable(getHeyBackground());
        }
    } public void onDockLongClick(View v) {
        text.setText(web.getUrl());
        text.selectAll();
        text.requestFocus();
        keyboardState(true);
        history_list.setAdapter(history.getAdapter());
        if (bookmark.getData().size() > 0)
            nomarkbook.setVisibility(View.GONE);
        else
            nomarkbook.setVisibility(View.VISIBLE);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    Bitmap bitmap = lastimage;
                    bitmap = FastBlur.rsBlur(Main.this, bitmap, 25);
                    manager.setBackgroundDrawable(new BitmapDrawable(bitmap));

                    final AlphaAnimation tranA = new AlphaAnimation(0, 1);
                    tranA.setDuration(225);
                    manager.startAnimation(tranA);
                    manager.setVisibility(View.VISIBLE);
                }
            });
    } public void onManagerBackClick(View v) {
        keyboardState(false);
        if (manager.getVisibility() == View.GONE) return;

        final AlphaAnimation tranA = new AlphaAnimation(1, 0);
        tranA.setDuration(225);
        tranA.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation ani) {}
                public void onAnimationRepeat(Animation ani) {}
                public void onAnimationEnd(Animation ani) {
                    manager.setVisibility(View.GONE);
                    if (S.get("pagecolor", true))
                        onChangeBackground(Main.multibottom.get(Main.webindex), Main.multitop.get(Main.webindex));
                }
            });

        manager.startAnimation(tranA);
        /*
         new Handler(){
         @Override public void handleMessage(Message msg) {
         super.handleMessage(msg);
         manager.setVisibility(View.GONE);
         }
         }.sendEmptyMessageDelayed(0, 225);*/
    } public void onManagerClick(View v) {
        final FrameLayout[] page = {
            (FrameLayout)findViewById(R.id.main_manager_p1),
            (FrameLayout)findViewById(R.id.main_manager_p2)
        };

        final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)manager_tab.getLayoutParams();  
        params.setMargins((int)v.getX(), 0, 0, 0);
        manager_tab.setLayoutParams(params);

        for (FrameLayout l : page) {
            l.setVisibility(View.GONE);
        }

        manager_th.setVisibility(View.GONE);
        switch (v.getId()) {
            case R.id.main_manager_t0:
                page[0].setVisibility(View.VISIBLE);
                break;
            case R.id.main_manager_t1:
                page[1].setVisibility(View.VISIBLE);
                manager_th.setVisibility(View.VISIBLE);
                break;

            case R.id.main_manager_th:
                page[1].setVisibility(View.VISIBLE);
                manager_th.setVisibility(View.VISIBLE);
                new AlertDialog.Builder(this).setTitle(R.string.lang39)
                    .setNegativeButton(R.string.lang4, null).setPositiveButton(R.string.lang3, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int i) {
                            S.put("hm", 0).ok();
                            history_list.setAdapter(history.getAdapter());
                        }
                    }).show();
                break;
        }
    } public void onRemoveAllClick(View v) {
        onDockClick(null);
        removeAllPage();
    } public void onAddPage(View v) {
        onDockClick(null);
        web = addPage("");
    }

    public HeyWeb addPage(String uri) {
        final String link = uri.equals("") ? S.get("home", "https://www.bing.com") : uri;
        final HeyWeb new_web = new HeyWeb(getApplicationContext());
        new_web.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));
        new_web.loadUrl(link);
        new_web.setVisibility(View.VISIBLE);
        new_web.setWebChromeClient(new HeyWebChrome());
        new_web.setOnTouchListener(HeyWebTouch(new_web));

        desktop.addView(new_web);
        pages.add(new_web);

        webindex = pages.size() - 1;

        multitop.add(getHeyBackground());
        multibottom.add(Color.TRANSPARENT);
        multiimages.add(getWebDrawingB());
        menus.add(new HeyMenu(menu));
        menu.setAdapter(menus.get(webindex).getAdapter());
        pages.get(webindex).addJavascriptInterface(menus.get(webindex), "HEYMENU");

        if (pages.size() > multi.size() * 2) addMulti();
        multi_scroll_box.setVisibility(View.GONE);
        multi_box.setVisibility(View.GONE);

        AnimationSet aniA = new AnimationSet(true);
        aniA.addAnimation(new ScaleAnimation(0.9f, 1f, 0.9f, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f));
        aniA.addAnimation(new AlphaAnimation(0f, 1f));
        aniA.setDuration(225);
        new_web.startAnimation(aniA);

        multi_text.setText("" + pages.size());
        button_number.setText(multi_text.getText());
        return new_web;
    }  public HeyWeb addPageB(String uri) {
        String link = uri.equals("") ? S.get("home", "https://www.bing.com") : uri;
        HeyWeb new_web = new HeyWeb(getApplicationContext());
        new_web.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));
        new_web.loadUrl(link);
        new_web.setVisibility(View.GONE);
        new_web.setWebChromeClient(new HeyWebChrome());
        new_web.setOnTouchListener(HeyWebTouch(new_web));

        desktop.addView(new_web);
        pages.add(new_web);

        multitop.add(getHeyBackground());
        multibottom.add(Color.TRANSPARENT);
        multiimages.add(getWebDrawingB());
        menus.add(new HeyMenu(menu));

        if (pages.size() > multi.size() * 2) addMulti();
        multi_scroll_box.setVisibility(View.GONE);
        multi_box.setVisibility(View.GONE);

        multiimage.get(pages.size() - 1).setImageBitmap(HeyHelper.getRoundedCornerBitmap(multiimages.get(pages.size() - 1), dip2px(this, 2)));
        multiimage.get(pages.size() - 1).setTag(pages.size() - 1);

        multi_text.setText("" + pages.size());
        button_number.setText(multi_text.getText());
        return new_web;
    } public void hidePage() {
        for (Object page : pages.toArray()) {
            ((HeyWeb)page).setVisibility(View.GONE);
        }
    } public void removeAllPage() {
        for (Object p : pages.toArray()) {
            HeyWeb page = (HeyWeb)p;
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
        menus = new ArrayList<HeyMenu>();
        multi_scroll.removeAllViews();
        addMulti();
        webindex = - 1;

        multi_text.setText("" + pages.size());
        button_number.setText(multi_text.getText());
        onDockClick(dock);
        web = addPage("");
    } public void removePage(int index) {
        HeyWeb page = pages.get(index);
        if (page != null) {
            desktop.removeView(page);
            page.removeAllViews();
            page.loadUrl("about:blank");
            page = null;
        }

        pages.remove(index);
        multitext.remove(index);
        multiimage.remove(index);
        multiimages.remove(index);
        multibottom.remove(index);
        multitop.remove(index);
        menus.remove(index);

        multi_text.setText("" + pages.size());
        button_number.setText(multi_text.getText());

        if (pages.size() < 1) {
            onDockClick(null);
            web = addPage("");
        } else if (webindex == index) {
            webindex = index + ((index == 0) ? 0 : -1);
            web = pages.get(webindex);
            if (multi_scroll_box.getVisibility() != View.VISIBLE)
                web.setVisibility(View.VISIBLE);
        }
    } public void addMulti() {
        int count = multi.size();
        int count2 = multi.size() + 1;
        multi.add((LinearLayout)LayoutInflater.from(this).inflate(R.layout.multi, null));
        multi.add((LinearLayout)LayoutInflater.from(this).inflate(R.layout.multi, null));
        multi_scroll.addView(multi.get(count));
        multi_scroll.addView(multi.get(count2));
        multi.get(count).findViewById(R.id.multi_root).setLayoutParams(new LinearLayout.LayoutParams(getWindowManager().getDefaultDisplay().getWidth() / 2, LinearLayout.LayoutParams.FILL_PARENT));
        multi.get(count2).findViewById(R.id.multi_root).setLayoutParams(new LinearLayout.LayoutParams(getWindowManager().getDefaultDisplay().getWidth() / 2, LinearLayout.LayoutParams.FILL_PARENT));
        multitext.add((TextView)multi.get(count).findViewById(R.id.multi_text0));
        multitext.add((TextView)multi.get(count2).findViewById(R.id.multi_text0));
        multitext.add((TextView)multi.get(count).findViewById(R.id.multi_text1));
        multitext.add((TextView)multi.get(count2).findViewById(R.id.multi_text1));
        multiimage.add((ImageView)multi.get(count).findViewById(R.id.multi_image0));
        multiimage.add((ImageView)multi.get(count2).findViewById(R.id.multi_image0));
        multiimage.add((ImageView)multi.get(count).findViewById(R.id.multi_image1));
        multiimage.add((ImageView)multi.get(count2).findViewById(R.id.multi_image1));
    } public void freshMulti() {
        multi.clear();
        multiimage.clear();
        multitext.clear();

        multi_scroll.removeAllViews();
        addMulti();
        for (int i = 4; i < pages.size(); i += 4) {
            addMulti();
        }

        for (ImageView image : multiimage) {
            image.setVisibility(View.GONE);
        }

        for (int i = 0; i < pages.size(); i++) {
            multiimage.get(i).setTag(i);
            multiimage.get(i).setVisibility(View.VISIBLE);
            multitext.get(i).setText(pages.get(i).getTitle());
            multiimage.get(i).setImageBitmap(HeyHelper.getRoundedCornerBitmap(multiimages.get(i), dip2px(this, 2)));
        }
    } public void aniMulti(int fristindex) {
        final AnimationSet aniA = new AnimationSet(true);
        aniA.addAnimation(new ScaleAnimation(0.9f, 1f, 0.9f, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f));
        aniA.addAnimation(new AlphaAnimation(0f, 1f));
        aniA.setDuration(225);

        for (int i = fristindex; i < pages.size(); i++) {
            ((View)multitext.get(i).getParent()).setAnimation(aniA);
        }
        aniA.startNow();
    }

    @Override
    protected void onDestroy() {
        desktop.removeAllViews();
        for (int i = 0; i < pages.size(); i++) {
            HeyWeb page = pages.get(i);
            if (page != null) {
                page.removeAllViews();
                page.clearHistory();
                page.clearCache(true);
                page.loadUrl("about:blank");
                page.freeMemory(); 
                page.pauseTimers();
                page = null;
            }
        }
        super.onDestroy();
    }

    public Drawable getHeyBackground() {
        Drawable d = new ColorDrawable(S.getColor(R.color.colorPrimary));
        if (S.get("background", 0) == 1) d = new BitmapDrawable(S.getStorePic("background"));
        return d;
    }

    long yt = 0;
    float yy = 0;
    public View.OnTouchListener HeyWebTouch(final HeyWeb w) {
        final float h1 = dip2px(Main.this, 48), h2 = dip2px(Main.this, 24);
        return (new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent moe) {
                if (moe.getAction() != MotionEvent.ACTION_DOWN && !ScrollText.isMenu) {
                    if (Math.abs(yy - moe.getY()) > h2) {
                        if (yt < System.currentTimeMillis()) {
                            ValueAnimation valueA;
                            if (yy > moe.getY()) {
                                valueA = ValueAnimation.ofFloat(dock.getLayoutParams().height, h2);

                                if (button_left.getVisibility() == View.VISIBLE && S.get("style", 0) == 0) {
                                    button_left.setVisibility(View.GONE);
                                    button_right.setVisibility(View.GONE);
                                }
                            } else {
                                valueA = ValueAnimation.ofFloat(dock.getLayoutParams().height, h1);

                                if (button_left.getVisibility() == View.GONE && S.get("style", 0) == 0) {
                                    final AlphaAnimation aniA = new AlphaAnimation(0f, 1f);
                                    button_left.setAnimation(aniA);
                                    button_right.setAnimation(aniA);
                                    aniA.setDuration(195);
                                    aniA.startNow();

                                    button_left.setVisibility(View.VISIBLE);
                                    button_right.setVisibility(View.VISIBLE);
                                }
                            }
                            dock.clearAnimation();
                            valueA.setDuration(195);
                            valueA.addUpdateListener(new ValueAnimation.OnAnimatorUpdateListener() {
                                    public void onAnimationUpdate(ValueAnimation animaion) {
                                        final RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)desktop.getLayoutParams();
                                        final int a = (int) animaion.getAnimatedValue();
                                        lp.setMargins(0, 0, 0, a);
                                        desktop.setLayoutParams(lp);
                                        dock.getLayoutParams().height = a;
                                    }
                                });
                            dock.startAnimation(valueA);
                            yt = 195 + System.currentTimeMillis();
                        }
                        yy = moe.getY();
                    }

                } else {
                    yy = moe.getY();
                }
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

        lastimage = Bitmap.createBitmap(bitmap, 0, (int)desktop.getY(), view.getWidth(), view.getHeight() - getNavigationBarHeight(this) - (int)desktop.getY());

        return Bitmap.createBitmap(bitmap, (int)desktop.getScrollX(), (int)desktop.getY(), view.getWidth() - (int)desktop.getScrollX(), view.getHeight() - dock.getHeight() - (int)desktop.getY() - getNavigationBarHeight(this));
    } public Bitmap getWebDrawingB() {
        Display view = getWindowManager().getDefaultDisplay();
        ColorDrawable drawable = new ColorDrawable(Color.WHITE);
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap); drawable.draw(canvas); //拿到这个bitmap了
        return bitmap;
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
        if (open)
            i.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
        else {
            View view = Main.me.getWindow().peekDecorView();
            if (view != null) i.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    } 
    public int getNavigationBarHeight(Context context) {
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
        startActivityForResult(Intent.createChooser(i, "File Chooser"), 3);
    }

    public void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
        mUploadMessageForAndroid5 = uploadMsg;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");

        startActivityForResult(chooserIntent, 5);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 3) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null: intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        } else if (requestCode == 5) {
            if (null == mUploadMessageForAndroid5)
                return;
            Uri result = (intent == null || resultCode != RESULT_OK) ? null: intent.getData();
            if (result != null)
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            else
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});

            mUploadMessageForAndroid5 = null;
        } else if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri uri = intent.getData();
                ContentResolver cr = this.getContentResolver();
                try {
                    S.storePic("background", BitmapFactory.decodeStream(cr.openInputStream(uri)));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}


class HeyWebChrome extends WebChromeClient {

    //全屏视频
    public static View mCustomView;
    public CustomViewCallback mCustomViewCallback;
    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        super.onShowCustomView(view, callback);

        Main.me.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        if (mCustomView != null) {
            callback.onCustomViewHidden();
            return;
        }

        mCustomView = view;
        mCustomViewCallback = callback;

        Main.web.setVisibility(View.GONE);
        Main.root.addView(mCustomView);

        Main.me.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    public void onHideCustomView() {

        Main.me.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        if (mCustomView == null) return;

        mCustomViewCallback.onCustomViewHidden();
        mCustomView.setVisibility(View.GONE);
        mCustomView = null;

        Main.web.setVisibility(View.VISIBLE);
        Main.root.removeView(mCustomView);

        Main.me.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        super.onHideCustomView();
    }

    public void onReceivedTitle(WebView v, String title) {
        //((TextView)v.getTag()).setText(title);
        try {
            if (title.equals("about:blank")) return;
            if (Main.web == v) Main.dock.setText(title);

            int webi = Main.pages.indexOf(v);
            Main.multitext.get(webi).setText(title);

            if (!S.get("pagecolor", true)) return;
            Main.multibottom.set(webi, 0x01000000);
            v.loadUrl("javascript:(function(){" +
                      "try{" +
                      "window.hey.onReceivedThemeColor(document.querySelector(\"meta[name='theme-color']\").getAttribute(\"content\")," + webi + ");" +
                      "}catch(e){" +
                      "window.hey.onReceivedThemeColor(\"\"," + webi + ");" +
                      "}" +
                      "})()");
            //v.loadUrl("javascript:(function(){var script=document.createElement('script');script.src='https://cdn.bootcss.com/eruda/1.2.6/eruda.min.js'; document.body.appendChild(script);})()");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override 
    public void onProgressChanged(final WebView v, int newProgress) {
        if (newProgress == 100) {
            if (v == Main.web) {

                Animation aniA = AnimationUtils.loadAnimation(Main.me, R.anim.finish);
                Main.progressbar.startAnimation(aniA);

                new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        Main.progressbar.setScaleX(1);
                        Main.progressbar.setVisibility(View.GONE);
                    }
                }.sendEmptyMessageDelayed(0, 1000);

                if (!(!S.get("pagecolor", true) ||
                    v.getTitle().equals("about:blank") ||
                    Main.manager.getVisibility() == View.VISIBLE ||
                    Main.multi_scroll_box.getVisibility() == View.VISIBLE)) {

                    //v.scrollTo(0, 0);
                    Bitmap b = Main.me.getWebDrawing();
                    b = FastBlur.rsBlur(Main.me, b, 20);
                    Main.multitop.set(Main.webindex, new BitmapDrawable(Bitmap.createBitmap(b , 0, 0, b.getWidth(), 1)));
                    Main.onChangeBackground(Main.multibottom.get(Main.webindex), Main.multitop.get(Main.webindex));
                }
            }
        } else {
            if (v == Main.web) {

                if (View.GONE == Main.progressbar.getVisibility()) {
                    AlphaAnimation alphaA = new AlphaAnimation(0, 1);
                    alphaA.setDuration(25);
                    Main.progressbar.startAnimation(alphaA);

                    Main.progressbar.setVisibility(View.VISIBLE);
                }
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
