package omark.hey;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import omark.hey.control.HeySetting;

public class Main extends Activity {
    static Main me;
    static int webindex = - 1;
    static View popn, manager_tab, aniimage, blacker, back_icon, forward_icon;
    static HeyClipboard clipboard;
    static ScrollText dock;
    static EditText text;
    static GridView menu;
    static ImageButton manager_back;
    static FrameLayout desktop;
    static RelativeLayout root, ground;
    static ProgressBar progressbar;
    static HeyWeb web;
    static TextView multi_text;
    static ListView bookmark_list, history_list;
    static LinearLayout multi_box, multi_scroll, manager;
    static HorizontalScrollView multi_scroll_box;
    static ArrayList<HeyWeb> pages = new ArrayList<HeyWeb>();
    static ArrayList<LinearLayout> multi = new ArrayList<LinearLayout>();
    static ArrayList<ImageView> multiimage = new ArrayList<ImageView>();
    static ArrayList<Bitmap> multiimages = new ArrayList<Bitmap>();
    static ArrayList<Drawable> multitop = new ArrayList<Drawable>();
    static ArrayList<Integer> multibottom = new ArrayList<Integer>();
    static ArrayList<TextView> multitext = new ArrayList<TextView>();

    static HeyBookmark bookmark;
    static HeyHistory history;

    @Override
    @TargetApi(19)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //创建
        clipboard = new HeyClipboard(this);
        bookmark = new HeyBookmark();
        history = new HeyHistory();

        S.init(this, "main");
        if (S.get("first", true)) {
            S.put("first", false);
            S.put("home", HeyHelper.DEFAULT_HOME);
            S.put("search" , HeyHelper.DEFAULT_SEARCH);
            S.put("pagecolor", true);

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
        
        ((HeySetting)findViewById(R.id.setting_2)).setChecked(S.get("pagecolor", true));
        
        me = this;
        popn = findViewById(R.id.main_popn);
        blacker = findViewById(R.id.main_blacker);
        dock = (ScrollText)findViewById(R.id.main_dock);
        menu = (GridView)findViewById(R.id.main_menu);
        root = (RelativeLayout)findViewById(R.id.main_root);
        desktop = (FrameLayout)findViewById(R.id.main_desktop);
        ground = (RelativeLayout)findViewById(R.id.main_ground);
        progressbar = (ProgressBar)findViewById(R.id.main_progress);

        //4.4以上透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
            ground.setFitsSystemWindows(true);
            ground.setClipToPadding(true);
        }

        menu.setAdapter(new HeyMenu().getAdapter());
        menu.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView view, final View v, final int i, long l) {

                    //点击菜单后，滚回去～
                    View viewGroup = (View)dock.getParent();
                    dock.scroller.startScroll(viewGroup.getScrollX(), viewGroup.getScrollY(), -viewGroup.getScrollX(), -viewGroup.getScrollY(), 320);

                    switch (i) {
                        case 0:
                            onDockClick(v);
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
                            final EditText t1 = (EditText)dl.findViewById(R.id.diglog_bookmark_1), t2 = (EditText)dl.findViewById(R.id.diglog_bookmark_2);;   
                            t1.setText(web.getTitle());
                            t2.setText(web.getUrl());
                            new AlertDialog.Builder(Main.this).setView(dl).setTitle(R.string.lang17)
                                .setNegativeButton(R.string.lang4, null).setPositiveButton(R.string.lang3, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int i) {
                                        S.addIndex("bnm" , "bn", t1.getText().toString());
                                        S.addIndex("bm" , "b", t2.getText().toString());
                                        S.addIndex("btm" , "bt", "" + System.currentTimeMillis());
                                        bookmark_list.setAdapter(bookmark.getAdapter());
                                        Toast.makeText(Main.this, R.string.lang20, Toast.LENGTH_SHORT).show();
                                    }
                                }).show();
                            break;
                        case 5:
                            //开发者模式
                            web.loadUrl("javascript:(function(){var script=document.createElement('script');script.src='https://cdn.bootcss.com/eruda/1.2.6/eruda.min.js'; document.body.appendChild(script);script.onload=function(){eruda.init()}})()");
                            break;
                        default:
                            Toast.makeText(Main.this, "不存在的操作", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });

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
                                    url = HeyHelper.getSearch(url);
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
        bookmark_list = (ListView)findViewById(R.id.main_manager_bookmark_list);
        history_list = (ListView)findViewById(R.id.main_manager_history_list);
        ripple_version(manager_back);

        back_icon = findViewById(R.id.main_back_icon);
        back_icon.setBackground(new BitmapDrawable(HeyHelper.ColoBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.back), S.getColor(R.color.colorBackground))));
        forward_icon = findViewById(R.id.main_forward_icon);
        forward_icon.setBackground(new BitmapDrawable(HeyHelper.ColoBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.forward), S.getColor(R.color.colorBackground))));
        multi_scroll_box = (HorizontalScrollView)findViewById(R.id.main_multi_scroll_box);
        multi_scroll = (LinearLayout)findViewById(R.id.main_multi_scroll);
        multi_box = (LinearLayout)findViewById(R.id.main_multi_box);
        multi_text = (TextView)findViewById(R.id.main_multi_text);
        aniimage = findViewById(R.id.main_aniimage);
        addMulti();//

        multi_box.setBackgroundColor(S.getColor(R.color.colorPrimary));

        onNewIntent(getIntent());

        onChangeBackground(Color.TRANSPARENT, new ColorDrawable(S.getColor(R.color.colorPrimary)));

        bookmark_list.setAdapter(bookmark.getAdapter());
        bookmark_list.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView view, View v, int index, long l) {
                    text.setText(bookmark.getData().get(index).get("url").toString());
                }
            });
        bookmark_list.setOnItemLongClickListener(new OnItemLongClickListener() {
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
                                S.put("bn" + index, t1.getText().toString());
                                S.put("b" + index, t2.getText().toString());
                                S.put("bt" + index, "" + System.currentTimeMillis());
                                S.ok();
                                bookmark_list.setAdapter(bookmark.getAdapter());
                            }
                        }).setNeutralButton(R.string.lang10, new DialogInterface.OnClickListener() {   
                            public void onClick(DialogInterface dialog, int i) {
                                S.delIndex("bm", "b", index);
                                S.delIndex("bnm", "bn", index);
                                S.delIndex("btm", "bt", index);
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
                    text.setText(history.getData().get(index).get("url").toString());
                }
            });
        history_list.setOnItemLongClickListener(new OnItemLongClickListener() {
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
                                S.put("hn" + index, t1.getText().toString());
                                S.put("h" + index, t2.getText().toString());
                                S.put("ht" + index, "" + System.currentTimeMillis());
                                S.ok();
                                history_list.setAdapter(history.getAdapter());
                            }
                        }).setNeutralButton(R.string.lang10, new DialogInterface.OnClickListener() {   
                            public void onClick(DialogInterface dialog, int i) {
                                S.delIndex("hm", "h", index);
                                S.delIndex("hnm", "hn", index);
                                S.delIndex("htm", "ht", index);
                                history_list.setAdapter(history.getAdapter());
                            }
                        }).show();
                    return true;
                }
            });
    }


    public void onSettingClick(View v) {
        switch (v.getId()) {
            case R.id.setting_1:
                final EditText et = new EditText(this);
                et.setText(S.get("home", HeyHelper.DEFAULT_HOME));
                new AlertDialog.Builder(this).setView(et).setTitle(R.string.lang11)
                    .setNegativeButton(R.string.lang4, null).setPositiveButton(R.string.lang3, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int i) {
                            S.put("home", et.getText().toString());
                            S.ok();
                        }
                    }).setNeutralButton(R.string.lang1, new DialogInterface.OnClickListener() {   
                        public void onClick(DialogInterface dialog, int i) {
                            S.put("home", HeyHelper.DEFAULT_HOME);
                            S.ok();
                        }
                    }).show();
                break;
            case R.id.setting_2:
                S.put("pagecolor", ((HeySetting)v).isChecked());
                S.ok();
                break;
        }
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
        if (intent == null) {
            web = addPage("");
            return;
        }
        String action = intent.getAction();
        if (Intent.ACTION_SEND.equals(action)) {
            web = addPage(HeyHelper.getSearch(intent.getStringExtra(Intent.EXTRA_TEXT)));
        } else if (Intent.ACTION_VIEW.equals(action)) {
            web = addPage(intent.getData().toString());
        } else if (Intent.ACTION_WEB_SEARCH.equals(action)) {
            web = addPage(HeyHelper.getSearch(intent.getStringExtra("query")));
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
        } else if (manager.getVisibility() == View.VISIBLE) {
            onManagerBackClick(null);
        } else {
            if (web.canGoBack()) {
                web.goBack();

                web.loadUrl("javascript:document.title = " + web.getTitle());
            } else {
                if (pages.size() != 1) {
                    if (!isExit) {
                        isExit = true;
                        handler.sendEmptyMessageDelayed(0, 1500);
                        Toast.makeText(this, "再按一次关闭标签页", Toast.LENGTH_SHORT).show();
                    } else {
                        removePage(webindex);
                        freshMulti();
                    }

                } else {
                    if (!isExit) {
                        isExit = true;
                        handler.sendEmptyMessageDelayed(0, 1500);
                        Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
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
        if (web.getProgress() == 100) 
            progressbar.setVisibility(View.GONE);
        else
            progressbar.setProgress(web.getProgress());
        dock.setText(web.getTitle());
        onDockClick(null);
    } public void onDockClick(View v) {
        multi_text.setText("" + pages.size());
        if (multi_scroll_box.getVisibility() == View.GONE || v != null) {

            multiimages.set(webindex, getWebDrawing());
            multiimage.get(webindex).setImageBitmap(HeyHelper.getRoundedCornerBitmap(multiimages.get(webindex), dip2px(this, 2)));
            multiimage.get(webindex).setTag(webindex);
            multitext.get(webindex).setText(web.getTitle());

            if (v != null) return;

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
            multi_box.setVisibility(View.VISIBLE);
            multi_scroll_box.setVisibility(View.VISIBLE);
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
                onChangeBackground(Main.multibottom.get(Main.webindex), Main.multitop.get(Main.webindex));

                mAni1.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator ani) {
                            multi_scroll_box.setVisibility(View.GONE);
                            multi_scroll_box.invalidate();
                            aniimage.setVisibility(View.GONE);
                            web.setVisibility(View.VISIBLE);
                        }
                    });
            } else {
                mAni1 = ValueAnimator.ofInt(k.left, l.left);
                mAni2 = ValueAnimator.ofInt(k.top, l.top);
                mAni3 = ValueAnimator.ofInt(k.width(), l.width());
                mAni4 = ValueAnimator.ofInt(k.height(), l.height());

                AnimationSet aniA = new AnimationSet(true);
                //aniA.addAnimation(new AlphaAnimation(0, 1));
                aniA.addAnimation(new ScaleAnimation(0.9f, 1f, 0.9f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
                aniA.setZAdjustment(AnimationSet.ZORDER_BOTTOM);
                aniA.setDuration(320);
                multi_scroll_box.setAnimation(aniA);

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

            aniimage.setBackground(new BitmapDrawable(multiimages.get(webindex)));
            aniimage.setVisibility(View.VISIBLE);

            mAni1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {  
                    @Override
                    public void onAnimationUpdate(ValueAnimator ani) {  
                        int curValue = ani.getAnimatedValue();
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)aniimage.getLayoutParams();  
                        params.setMargins(curValue, params.topMargin, params.rightMargin, params.bottomMargin);
                        aniimage.setLayoutParams(params);
                    }
                });
            mAni2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {  
                    @Override
                    public void onAnimationUpdate(ValueAnimator ani) {  
                        int curValue = ani.getAnimatedValue();
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)aniimage.getLayoutParams();  
                        params.setMargins(params.leftMargin, curValue, params.rightMargin, params.bottomMargin);
                        aniimage.setLayoutParams(params);
                    }
                });
            mAni3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {  
                    @Override
                    public void onAnimationUpdate(ValueAnimator ani) {  
                        int curValue = ani.getAnimatedValue();
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)aniimage.getLayoutParams();  
                        params.width = curValue;
                        aniimage.setLayoutParams(params);
                    }
                });
            mAni4.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {  
                    @Override
                    public void onAnimationUpdate(ValueAnimator ani) {  
                        int curValue = ani.getAnimatedValue();
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)aniimage.getLayoutParams();  
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
        final View view = getWindow().getDecorView();

        final TranslateAnimation tranA = new TranslateAnimation(0, 0, view.getHeight(), 0);
        tranA.setZAdjustment(AnimationSet.ZORDER_TOP);
        tranA.setDuration(320);

        text.setText(web.getUrl());
        text.selectAll();
        text.requestFocus();
        keyboardState(true);
        history_list.setAdapter(history.getAdapter());
        manager.setAnimation(tranA);
        manager.setVisibility(View.VISIBLE);
    } public void onManagerBackClick(View v) {
        keyboardState(false);
        if (manager.getVisibility() == View.GONE) return;

        final View view = getWindow().getDecorView();

        final TranslateAnimation tranA = new TranslateAnimation(0, 0, 0, view.getHeight());
        tranA.setZAdjustment(AnimationSet.ZORDER_TOP);
        tranA.setDuration(320);

        manager.setAnimation(tranA);

        new Handler(){
            @Override public void handleMessage(Message msg) {
                super.handleMessage(msg);
                manager.setVisibility(View.GONE);
            }
        }.sendEmptyMessageDelayed(0, 320);
    } public void onManagerClick(View v) {
        final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)manager_tab.getLayoutParams();  
        params.setMargins((int)v.getX(), 0, 0, 0);
        manager_tab.setLayoutParams(params);

        final LinearLayout[] page = {
            (LinearLayout)findViewById(R.id.main_manager_p1),
            (LinearLayout)findViewById(R.id.main_manager_p2),
            (LinearLayout)findViewById(R.id.main_manager_p3)
        };

        for (LinearLayout l : page) {
            l.setVisibility(View.GONE);
        }

        switch (v.getId()) {
            case R.id.main_manager_t0:
                page[0].setVisibility(View.VISIBLE);
                break;
            case R.id.main_manager_t1:
                page[1].setVisibility(View.VISIBLE);
                break;
            case R.id.main_manager_t2:
                page[2].setVisibility(View.VISIBLE);
                break;
        }
    } public void onRemoveClick(View v) {
        if (pages.size() == 1) return;
        removePage((int)v.getTag());
        freshMulti();
    } public void onRemoveAllClick(View v) {
        removeAllPage();
        onDockClick(null);
    }

    public HeyWeb addPage(String uri) {
        final String link = uri.equals("") ? S.get("home", "https://www.bing.com") : uri;
        final HeyWeb new_web = new HeyWeb(this);
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

        if (pages.size() > multi.size() * 2) addMulti();
        multi_scroll_box.setVisibility(View.GONE);
        multi_box.setVisibility(View.GONE);

        webindex = pages.size() - 1;

        return new_web;
    }  public HeyWeb addPageB(String uri) {
        String link = uri.equals("") ? S.get("home", "https://www.bing.com") : uri;
        HeyWeb new_web = new HeyWeb(this);
        new_web.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        new_web.loadUrl(link);
        new_web.setVisibility(View.GONE);
        new_web.setWebChromeClient(new HeyWebChrome());
        new_web.setOnTouchListener(HeyWebTouch(new_web));

        desktop.addView(new_web);
        pages.add(new_web);

        multitop.add(new ColorDrawable(S.getColor(R.color.colorPrimary)));
        multibottom.add(0x01000000);
        multiimages.add(getWebDrawingB());

        if (pages.size() > multi.size() * 2) addMulti();
        multi_scroll_box.setVisibility(View.GONE);
        multi_box.setVisibility(View.GONE);

        multiimage.get(pages.size() - 1).setImageBitmap(HeyHelper.getRoundedCornerBitmap(multiimages.get(pages.size() - 1), dip2px(this, 2)));
        multiimage.get(pages.size() - 1).setTag(pages.size() - 1);

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
        multi_scroll.removeAllViews();
        addMulti();
        webindex = - 1;

        onDockClick(dock);
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
            multiimage.get(i).setImageBitmap(HeyHelper.getRoundedCornerBitmap(multiimages.get(i), dip2px(this, 2)));
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
        return Bitmap.createBitmap(bitmap, (int)desktop.getScrollX(), (int)desktop.getY(), view.getWidth() - (int)desktop.getScrollX(), view.getHeight() - dock.getHeight() - (int)desktop.getY());
    } public Bitmap getWebDrawingB() {
        Bitmap bitmap = getWebDrawing();
        return HeyHelper.ColoBitmap(bitmap, Color.WHITE);
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
            if (S.get("pagecolor", true))
                v.loadUrl("javascript:(function(){" +
                          "try{" +
                          "Context.onReceivedThemeColor(document.querySelector(\"meta[name='theme-color']\").getAttribute(\"content\")," + webi + ");" +
                          "}catch(e){" +
                          "Context.onReceivedThemeColor(\"\"," + webi + ");" +
                          "}" +
                          "})()");

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

                if (!(!S.get("pagecolor", true) ||
                    v.getTitle().equals("about:blank") ||
                    Main.manager.getVisibility() == View.VISIBLE ||
                    Main.multi_scroll_box.getVisibility() == View.VISIBLE)) {

                    //v.scrollTo(0, 0);
                    Bitmap b = Main.me.getWebDrawing();
                    FastBlur.doBlur(b, 20, true);
                    Main.multitop.set(Main.webindex, new BitmapDrawable(Bitmap.createBitmap(b , 0, 0, b.getWidth(), 1)));
                    Main.onChangeBackground(Main.multibottom.get(Main.webindex), Main.multitop.get(Main.webindex));
                }
            }
        } else {
            if (v == Main.web) {

                if (View.GONE == Main.progressbar.getVisibility()) {
                    AlphaAnimation alphaA = new AlphaAnimation(0, 1);
                    alphaA.setDuration(320);
                    Main.progressbar.setAnimation(alphaA);

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
