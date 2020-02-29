package omark.hey;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import omark.hey.control.HeyProgress;
import android.view.animation.TranslateAnimation;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;

public class Main extends Activity {
    static Main me;
    static int webindex;
    static boolean vmode = false;
    static View popn, night, desktop_float;
    static HeyClipboard clipboard;
    static ScrollText dock;
    static EditText text, simulation_e;
    static GridView menu;
    static FrameLayout desktop, simulation, multi_box, menu_layout, menulayout_box;
    static RelativeLayout root, ground, manager, manager_ground;
    static HeyProgress progressbar;
    static HeyWeb web;
    static ImageView background,aniimage;
    static Spinner simulation_u, simulation_a;
    static TextView multi_text, back_icon, forward_icon, manager_back, button_left, button_right, button_number, manager_th;
    static TextView simulation_back, simulation_test, nomarkbook, multi_box_add, multi_box_remove;
	//  static TextView[] manager_tab_button = new TextView[2];
    static ListView bookmark_list, history_list;
    static LinearLayout multi_scroll;//, home_root;
    static HorizontalScrollView multi_scroll_box;
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
    static HeyMenu homemenu;

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
		//嘟嘟嘟加载界面!!!

        me = this;
		{
			popn = findViewById(R.id.main_popn);
			night = findViewById(R.id.main_night);
			//blacker = findViewById(R.id.main_blacker);
			menu = (GridView)findViewById(R.id.main_menu);
			text = (EditText)findViewById(R.id.main_text);
			//home_text = (EditText)findViewById(R.id.home_text);
			dock = (ScrollText)findViewById(R.id.main_dock);
			//manager_tab = findViewById(R.id.main_manager_tab);
			root = (RelativeLayout)findViewById(R.id.main_root);
			desktop_float = findViewById(R.id.main_desktop_float);
			//home_root = (LinearLayout)findViewById(R.id.home_root);
			desktop = (FrameLayout)findViewById(R.id.main_desktop);
			ground = (RelativeLayout)findViewById(R.id.main_ground);
			manager = (RelativeLayout)findViewById(R.id.main_manager);
			background = (ImageView)findViewById(R.id.main_background);
			aniimage = (ImageView)findViewById(R.id.aniimage);
			progressbar = (HeyProgress)findViewById(R.id.main_progress);
			manager_ground = (RelativeLayout)findViewById(R.id.main_manager_ground);


			bookmark_list = (ListView)findViewById(R.id.main_manager_bookmark_list);
			history_list = (ListView)findViewById(R.id.main_manager_history_list);

			manager_back = (TextView)findViewById(R.id.main_manager_back);
			back_icon = (TextView)findViewById(R.id.main_back_icon);
			forward_icon = (TextView)findViewById(R.id.main_forward_icon);
			button_left = (TextView)findViewById(R.id.main_button_left);
			button_right = (TextView)findViewById(R.id.main_button_right);
			button_number = (TextView)findViewById(R.id.main_button_number);


			multi_box_add = (TextView)findViewById(R.id.main_multi_box_add);
			multi_box_remove = (TextView)findViewById(R.id.main_multi_box_remove);
			multi_scroll_box = (HorizontalScrollView)findViewById(R.id.main_multi_scroll_box);
			multi_scroll = (LinearLayout)findViewById(R.id.main_multi_scroll);
			multi_box = (FrameLayout)findViewById(R.id.main_multi_box);
			multi_text = (TextView)findViewById(R.id.main_multi_box_text);

			manager_th = (TextView)findViewById(R.id.main_manager_th);
			nomarkbook = (TextView)findViewById(R.id.main_nomarkbook);

			simulation_e = (EditText)findViewById(R.id.simulation_e);
			simulation_test = (TextView)findViewById(R.id.simulation_test);

			menulayout_box = (FrameLayout)findViewById(R.id.menulayout_box);
			menu_layout = (FrameLayout)findViewById(R.id.main_menu_layout);
		}

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
                    //点击菜单后，缩回去～
					onBarClick(button_left);
                    if (((TextView)v.findViewById(R.id.menu_item_icon)).getCurrentTextColor() == 0x22ffffff)
                        return;
                    homemenu.run(i, me);
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
		/*
		 home_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {  
		 public boolean onEditorAction(TextView v, int actionId, KeyEvent event)  {
		 if (actionId == EditorInfo.IME_ACTION_GO || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
		 String url = v.getText().toString();
		 if (!url.equals("")) {
		 url = HeyHelper.toWeb(url);
		 //转到页面
		 web = addPage(url);
		 onManagerBackClick(null);
		 home_text.setText("");
		 return true;
		 }
		 }
		 return false;
		 }    
		 });*/


        manager_back.setTextColor(S.getColor(R.color.colorPrimary));
        back_icon.setTextColor(S.getColor(R.color.colorBackground));
        forward_icon.setTextColor(S.getColor(R.color.colorBackground));
        button_left.setText(String.valueOf((char)((Integer)0xE6DD).intValue()));
        button_right.setText(String.valueOf((char)((Integer)0xE3FA).intValue()));

        HeyHelper.setFont(back_icon, "m");
        HeyHelper.setFont(button_left, "m");
        HeyHelper.setFont(button_right, "m");
        HeyHelper.setFont(forward_icon, "m");
        HeyHelper.setFont(manager_back, "m");
        HeyHelper.setFont(multi_box_add, "m");
        HeyHelper.setFont(multi_box_remove, "m");
        ripple_version(manager_back);



        addMulti();


		/*
		 manager_tab_button[0] = (TextView)findViewById(R.id.main_manager_t0);
		 manager_tab_button[1] = (TextView)findViewById(R.id.main_manager_t1);
		 for (TextView tvv : manager_tab_button) {
		 HeyHelper.setFont(tvv, "m");
		 }*/


        HeyHelper.setFont(manager_th, "m");

        homemenu = new HeyMenu(menu);
		/*        homemenu.setStop(1);
		 homemenu.setStop(2);
		 homemenu.setStop(3);
		 homemenu.setStop(7);
		 homemenu.setStop(8);
		 homemenu.setStop(9);*/
        freshDock();
        //menu.setAdapter(homemenu.getAdapter());

		webindex = -1;
        onIntent(getIntent());
        if (webindex < 0)
            web = addPage("");

		//菜单容器***

        menulayout_box.scrollTo(0, -(int)dip2px(this, 250));

		//色彩!!!
        onChangeBackground(Color.TRANSPARENT);

        bookmark_list.setAdapter(bookmark.getAdapter());
        bookmark_list.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView view, View v, int index, long l) {
                    String url = bookmark.getData().get(index).get("url").toString();
                    //if (web == null) web = addPage("about:blank");
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
                    //if (web == null) web = addPage("about:blank");
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
                    if (pos != 10) {
						simulation_a.setVisibility(View.VISIBLE);
						simulation_e.setVisibility(View.GONE);
						setUA(UserAgent.ssr()[pos][0]);
                    	web.reload();
					} else {
						simulation_a.setVisibility(View.GONE);
						simulation_e.setVisibility(View.VISIBLE);
					}
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
        ripple_version(simulation_test);
        HeyHelper.setFont(simulation_back, "m");




    }
	public static void onSimulationTest(View v)  {
		web.loadUrl("http://sited.noear.org/ua/");
		web.invalidate();
	}

    public static void onMenu(boolean open) {
        ScrollText.isMenu = open;
        if (open) {
			desktop_float.setVisibility(View.VISIBLE);
        } else {
            freshDock();
            desktop_float.setVisibility(View.GONE);
        }
    }
	/*
	 public static void toHome() {
	 home_root.setVisibility(View.VISIBLE);
	 menu.setAdapter(homemenu.getAdapter());
	 hidePage();
	 web = null;
	 }*/

    public void setVmode(boolean b) {
        //关掉缓存和Cookies...
        homemenu.setState(6, b);
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
		setUA(UserAgent.ssr()[(int)simulation_u.getSelectedItemPosition()][simulation_a.getSelectedItemPosition()]);
        AnimationSet aniA = new AnimationSet(true);
        aniA.addAnimation(new ScaleAnimation(0.5f, 1f, 0.5f, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.2f));
        aniA.setInterpolator(new DecelerateInterpolator());
        aniA.setFillAfter(true);
        aniA.setDuration(225);
        desktop.startAnimation(aniA);
    }

    public void onBarClick(View v) {
        switch (v.getId()) {
            case R.id.main_button_left:
                onDockClick(v);
                if (menulayout_box.getScrollY() < -menu_layout.getHeight() / 2) {
					ValueAnimation ani = ValueAnimation.ofInt(menulayout_box.getScrollY(), 0);
				    ani.addUpdateListener(new ValueAnimation.OnAnimatorUpdateListener() {
							public void onAnimationUpdate(ValueAnimation animaion) {
								int a = (int) animaion.getAnimatedValue();
								menulayout_box.setScrollY(a);
							}
						}).setDuration(125);
					menulayout_box.setAnimation(ani);
                    onMenu(true);
                } else {
                    ValueAnimation ani = ValueAnimation.ofInt(menulayout_box.getScrollY(), -menu_layout.getHeight());
				    ani.addUpdateListener(new ValueAnimation.OnAnimatorUpdateListener() {
							public void onAnimationUpdate(ValueAnimation animaion) {
								int a = (int) animaion.getAnimatedValue();
								menulayout_box.setScrollY(a);
							}
						}).setDuration(125);
					menulayout_box.setAnimation(ani);
					onMenu(false);
                }
                dock.invalidate();
                break;
            case R.id.main_button_right:
                onDockClick(null);
                break;
            case R.id.main_desktop_float:

                menu.setVisibility(View.VISIBLE);
                onBarClick(button_left);
                break;
        }
    }



    public static void freshSimulation() {
        simulation.invalidate();
        simulation.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
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

        if (web != null)
            web.setVisibility(View.VISIBLE);
		/*
		 //home_root.setVisibility(View.GONE);
		 } else
		 web = Main.me.addPage("");*/
    }

    public static void setUA(String s) {
        for (Object p : pages.toArray()) {
            HeyWeb page = (HeyWeb)p;
            if (page != null)
                page.getSettings().setUserAgentString(s);
        }
		//给自定义UA文本框赋值
		simulation_e.setText(s);
    }

    public static void onChangeBackground(Integer f) {
		//变色龙在此*******

		//if (web != null && S.get("pagecolor", false)) return;
		//menu_layout.setBackgroundDrawable(Main.background.getDrawable());

		int c=f;

        if (c == Color.TRANSPARENT) {
			c = Color.argb(100, 0, 0, 0);
        } else {
			if (S.get("background", 0) == 1) {
				c = Color.argb(200, Color.red(f), Color.green(f), Color.blue(f));
			}

		}
		Main.root.setBackgroundColor(c);
		Main.dock.setBackground(new ColorDrawable(c));
		if (HeyHelper.isLightColor(c)) {
            //blacker.setVisibility(View.VISIBLE);
			setBarTextColor(Color.BLACK);
			me.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            //blacker.setVisibility(View.GONE);
			setBarTextColor(Color.WHITE);
			me.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
		}
		Drawable b = getHeyBackground();
		Main.background.setBackground(b);
		ground.setBackgroundColor(c);
	}

	public static void setBarTextColor(int color) {
		dock.setTextColor(color);
		button_left.setTextColor(color);
		button_right.setTextColor(color);

		//Toast.makeText(me, String.valueOf(color), Toast.LENGTH_LONG).show();
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
		if (intent.getCategories().contains(Intent.CATEGORY_LAUNCHER)) return;
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
			case "omark.hey.add2":
				setVmode(true);
				break;
			case "omark.hey.add":
				web = addPage("");
				break;
			default:
				web = addPage("");
				break;
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
        if (ScrollText.isMenu) {
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

        if (multi_box.getVisibility() == View.VISIBLE)
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

    public void onBackWeb(View v) {
		//返回页面
        onDockClick(null);
    } public void onMultiClick(View v) {
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

        if (multi_box.getVisibility() == View.GONE || v != null) {

            try {
                Bitmap l = getWebDrawing();
                multiimages.set(webindex, l);
                multiimage.get(webindex).setImageBitmap(HeyHelper.getRCB(l));
                multiimage.get(webindex).setTag(webindex);
                multitext.get(webindex).setText(web.getTitle());
            } catch (Exception e) {
                //手速过快23333
            }
            if (v != null) return;

            hidePage();
            dock.setVisibility(View.GONE);
            //home_root.setVisibility(View.GONE);
            button_left.setVisibility(View.GONE);
            button_right.setVisibility(View.GONE);
            button_number.setVisibility(View.GONE);
            scaleAni(false);
			//onChangeBackground(Color.TRANSPARENT);
			freshMulti();
        } else {
            freshDock();
            scaleAni(true);
			try {
				onChangeBackground(multibottom.get(webindex));
			} catch (Exception e) {

			}
        }

    }
	/*
	 private void scaleAni(boolean open) {
	 if (open) {
	 AnimationSet aniA = new AnimationSet(true);
	 aniA.addAnimation(new AlphaAnimation(0f, 1f));
	 aniA.setInterpolator(new DecelerateInterpolator());
	 aniA.setDuration(225);
	 web.startAnimation(aniA);
	 web.setVisibility(View.VISIBLE);

	 if (web != null) {
	 if (pages.size() > webindex)
	 onChangeBackground(Main.multibottom.get(Main.webindex));
	 }

	 AnimationSet aniB = new AnimationSet(true);
	 aniB.addAnimation(new AlphaAnimation(1f, 0f));
	 aniB.addAnimation(new ScaleAnimation(1f, 0.5f, 1f, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f));
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
	 AnimationSet aniA = new AnimationSet(true);
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

	 AnimationSet aniB = new AnimationSet(true);
	 aniB.addAnimation(new AlphaAnimation(0f, 1f));
	 aniB.addAnimation(new ScaleAnimation(0.5f, 1f, 0.5f, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f));
	 aniB.setInterpolator(new DecelerateInterpolator());
	 aniB.setZAdjustment(AnimationSet.ZORDER_BOTTOM);
	 aniB.setDuration(225);
	 multi_scroll_box.startAnimation(aniB);

	 multi_scroll_box.setVisibility(View.VISIBLE);
	 multi_box.setVisibility(View.VISIBLE);

	 }
	 scaleAni2(open);
	 } 

	 private void scaleAni2(boolean open) {
	 final ImageView your = multiimage.get(webindex);
	 final RelativeLayout yourparent = (RelativeLayout)your.getParent();
	 final RelativeLayout.LayoutParams yourparamas = (RelativeLayout.LayoutParams)your.getLayoutParams();

	 multi_scroll_box.setVisibility(View.VISIBLE);
	 multi_scroll_box.invalidate();
	 multiimage.get(webindex).invalidate();
	 Rect l = new Rect(), k = new Rect();
	 //multi_box.setVisibility(View.VISIBLE);
	 //multi_scroll_box.setVisibility(View.VISIBLE);
	 web.getGlobalVisibleRect(k);
	 multiimage.get(webindex).getGlobalVisibleRect(l);

	 //int[] location = new  int[2];


	 //int[] location = new int[2];  
	 //your.getLocationInWindow(location);  
	 int x=l.left;//获取当前位置的横坐标  
	 int y=l.top;//获取当前位置的纵坐标
	 Toast.makeText(this, "x=" + x + "|y=" + y, Toast.LENGTH_SHORT).show();

	 float yourscale = l.width() / k.width();
	 //your.getLocationOnScreen(location);
	 yourparent.removeView(your);
	 ground.addView(your, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT));

	 if (!open) {
	 AnimationSet aniB = new AnimationSet(true);
	 aniB.addAnimation(new TranslateAnimation(0, x, 0, y));
	 aniB.addAnimation(new ScaleAnimation(1f, yourscale, 1f, yourscale, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f));
	 aniB.setInterpolator(new DecelerateInterpolator());
	 aniB.setZAdjustment(AnimationSet.ZORDER_BOTTOM);
	 aniB.setDuration(225);
	 aniB.setAnimationListener(new Animation.AnimationListener() {
	 public void onAnimationStart(Animation ani) {}
	 public void onAnimationRepeat(Animation ani) {}
	 public void onAnimationEnd(Animation ani) {
	 ground.removeView(your);
	 yourparent.addView(your, yourparamas);
	 }
	 });
	 your.startAnimation(aniB);
	 } else {
	 AnimationSet aniB = new AnimationSet(true);
	 aniB.addAnimation(new TranslateAnimation(x, 0, y, 0));
	 aniB.addAnimation(new ScaleAnimation(yourscale, 1f, yourscale, 1f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f));
	 aniB.setInterpolator(new DecelerateInterpolator());
	 aniB.setZAdjustment(AnimationSet.ZORDER_BOTTOM);
	 aniB.setDuration(225);
	 aniB.setAnimationListener(new Animation.AnimationListener() {
	 public void onAnimationStart(Animation ani) {}
	 public void onAnimationRepeat(Animation ani) {}
	 public void onAnimationEnd(Animation ani) {
	 ground.removeView(your);
	 yourparent.addView(your, yourparamas);
	 }
	 });
	 your.startAnimation(aniB);
	 }
	 } 

	 */

	// @TargetApi(11

	private void scaleAni(boolean open) {
		Rect l = new Rect(), k = new Rect();
		multi_box.setVisibility(View.VISIBLE);
		//multi_scroll_box.setVisibility(View.VISIBLE);
		web.getGlobalVisibleRect(k);
		multiimage.get(webindex).getGlobalVisibleRect(l);

		ValueAnimator mAni1, mAni2, mAni3, mAni4;
		web.setVisibility(View.GONE);
		if (l.left == 0) {//获取不到位置
			l.left = k.width() / 2;
			l.top = k.height() / 2;
			l.right = l.left;
			l.bottom = l.top;
		}
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
			onChangeBackground(Main.multibottom.get(Main.webindex));


			//multiimage.get(webindex).setVisibility(View.GONE);
			mAni1.addListener(new AnimatorListenerAdapter() {
					public void onAnimationEnd(Animator ani) {
						//multi_scroll_box.setVisibility(View.GONE);
						//multi_scroll_box.invalidate();
						aniimage.setVisibility(View.GONE);
						web.setVisibility(View.VISIBLE);

						//multiimage.get(webindex).setVisibility(View.VISIBLE);
					}
				});

			/*
			 mAni1.setDuration(220);//.setInterpolator(new DecelerateInterpolator());
			 mAni2.setDuration(220);//.setInterpolator(new DecelerateInterpolator());
			 mAni3.setDuration(320);//.setInterpolator(new DecelerateInterpolator());
			 mAni4.setDuration(320);//.setInterpolator(new DecelerateInterpolator());
			 */

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

			//root.setBackgroundColor(S.getColor(R.color.colorPrimary));
			mAni1.addListener(new AnimatorListenerAdapter() {
					public void onAnimationEnd(Animator ani) {
						//web.setVisibility(View.VISIBLE);
						aniimage.setVisibility(View.GONE);
					}
				});
			/*
			 mAni1.setDuration(220);//.setInterpolator(new DecelerateInterpolator());
			 mAni2.setDuration(220);//.setInterpolator(new DecelerateInterpolator());
			 mAni3.setDuration(320);//.setInterpolator(new DecelerateInterpolator());
			 mAni4.setDuration(320);//.setInterpolator(new DecelerateInterpolator());
			 */
		}

		mAni1.setDuration(320);//.setInterpolator(new DecelerateInterpolator(0.2f));
		mAni2.setDuration(320);//.setInterpolator(new DecelerateInterpolator(0.2f));
		mAni3.setDuration(320);//.setInterpolator(new DecelerateInterpolator(0.2f));
		mAni4.setDuration(320);//.setInterpolator(new DecelerateInterpolator(0.2f));

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


	public void onDockLongClick(View v) {
        if (web != null) text.setText(web.getUrl());
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
                    Bitmap bitmap = getWebDrawing();
					if (bitmap == null) return;
                    bitmap = FastBlur.rsBlur(Main.this, bitmap, 25);
                    manager.setBackgroundDrawable(new BitmapDrawable(bitmap));

                    final AlphaAnimation tranA = new AlphaAnimation(0, 1);
                    tranA.setDuration(225);
                    manager.startAnimation(tranA);
                    manager.setVisibility(View.VISIBLE);
                }
            });
    } 

	public void onManagerBackClick(View v) {
        keyboardState(false);
        if (manager.getVisibility() == View.GONE) return;

        final AlphaAnimation tranA = new AlphaAnimation(1, 0);
        tranA.setDuration(225);
        tranA.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation ani) {}
                public void onAnimationRepeat(Animation ani) {}
                public void onAnimationEnd(Animation ani) {
                    manager.setVisibility(View.GONE);

					onChangeBackground(Main.multibottom.get(Main.webindex));
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
		/*      final FrameLayout[] page = {
		 (FrameLayout)findViewById(R.id.main_manager_p1),
		 (FrameLayout)findViewById(R.id.main_manager_p2)
		 };*/

        //final RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)manager_tab.getLayoutParams();  
		// params.setMargins((int)v.getX(), 0, 0, 0);
		//   manager_tab.setLayoutParams(params);

		/* for (FrameLayout l : page) {
		 l.setVisibility(View.GONE);
		 }*/

		//  manager_th.setVisibility(View.GONE);
        switch (v.getId()) {
				/*
				 case R.id.main_manager_t0:
				 page[0].setVisibility(View.VISIBLE);
				 break;
				 case R.id.main_manager_t1:
				 page[1].setVisibility(View.VISIBLE);
				 manager_th.setVisibility(View.VISIBLE);
				 break;
				 */
            case R.id.main_manager_th:
                //page[1].setVisibility(View.VISIBLE);
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
        removeAllPage();
		onDockClick(null);
		//onDockClick(null);
    } public void onAddPage(View v) {
        onDockClick(null);
		aniimage.setVisibility(View.GONE);
        web = addPage("");
    }

    public HeyWeb addPage(String uri) {
        final String link = uri.equals("") ? S.get("home", "https://www.bing.com") : uri;
        final HeyWeb new_web = new HeyWeb(getApplicationContext());
        new_web.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));
        new_web.loadUrl(link);
        new_web.setVisibility(View.VISIBLE);
        new_web.setWebChromeClient(new HeyWebChrome());
		//  new_web.setOnTouchListener(HeyWebTouch(new_web));
		new_web.setBackgroundColor(Color.DKGRAY);
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
        //multi_scroll_box.setVisibility(View.GONE);
        multi_box.setVisibility(View.GONE);

        AnimationSet aniA = new AnimationSet(true);
        aniA.addAnimation(new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1f));
        aniA.addAnimation(new AlphaAnimation(0f, 1f));
        aniA.setDuration(325);
        new_web.startAnimation(aniA);

        multi_text.setText("" + pages.size());
        button_number.setText(multi_text.getText());
        setVmode(vmode);
        return new_web;
    }  public HeyWeb addPageB(String uri) {
        String link = uri.equals("") ? S.get("home", "https://www.bing.com") : uri;
        HeyWeb new_web = new HeyWeb(getApplicationContext());
        new_web.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));
        new_web.loadUrl(link);
        new_web.setVisibility(View.GONE);
        new_web.setWebChromeClient(new HeyWebChrome());
		//   new_web.setOnTouchListener(HeyWebTouch(new_web));

        desktop.addView(new_web);
        pages.add(new_web);

        multitop.add(getHeyBackground());
        multibottom.add(Color.TRANSPARENT);
        multiimages.add(getWebDrawingB());
        menus.add(new HeyMenu(menu));

        if (pages.size() > multi.size() * 2) addMulti();
        //multi_scroll_box.setVisibility(View.GONE);
        multi_box.setVisibility(View.GONE);

        multiimage.get(pages.size() - 1).setImageBitmap(HeyHelper.getRCB(multiimages.get(pages.size() - 1)));
        multiimage.get(pages.size() - 1).setTag(pages.size() - 1);

        multi_text.setText("" + pages.size());
        button_number.setText(multi_text.getText());
        setVmode(vmode);
        return new_web;
    } public static void hidePage() {
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
        web = addPage("");
        onDockClick(null);
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
			web = addPage("");
			onDockClick(null);
			onDockClick(null);
        } else if (webindex == index) {
            webindex = index + ((index == 0) ? 0 : -1);
            web = pages.get(webindex);
            if (multi_box.getVisibility() != View.VISIBLE)
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
            multiimage.get(i).setImageBitmap(HeyHelper.getRCB(multiimages.get(i)));
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

    public static Drawable getHeyBackground() {
        Drawable d;
		if (S.get("background", 0) == 1) {
			d = new BitmapDrawable(S.getStorePic("background"));
		} else {
			d = new ColorDrawable(S.getColor(R.color.colorPrimary));
		}
		//Toast.makeText(me,String.valueOf(S.get("background", 0)),Toast.LENGTH_SHORT).show();
        return d;
    }
	/*
	 long yt = 0;
	 float yy = 0;
	 public View.OnTouchListener HeyWebTouch(final HeyWeb w) {
	 final int h1 = (int)dip2px(Main.this, 48), h2 = (int)dip2px(Main.this, 24);
	 return (new View.OnTouchListener() {
	 public boolean onTouch(View v, MotionEvent moe) {
	 if (moe.getAction() != MotionEvent.ACTION_DOWN && !ScrollText.isMenu) {
	 if (Math.abs(yy - moe.getY()) > h2) {
	 if (yt < System.currentTimeMillis()) {
	 ValueAnimator valueA;
	 if (yy > moe.getY()) {
	 valueA = ValueAnimator.ofInt(dock.getLayoutParams().height, h2);

	 if (button_left.getVisibility() == View.VISIBLE && S.get("style", 0) == 0) {
	 button_left.setVisibility(View.GONE);
	 button_right.setVisibility(View.GONE);
	 }
	 } else {
	 valueA = ValueAnimator.ofInt(dock.getLayoutParams().height, h1);

	 if (button_left.getVisibility() == View.GONE && S.get("style", 0) == 0) {
	 final AlphaAnimation aniA = new AlphaAnimation(0f, 1f);
	 button_left.setAnimation(aniA);
	 button_right.setAnimation(aniA);
	 aniA.setDuration(125);
	 aniA.startNow();

	 button_left.setVisibility(View.VISIBLE);
	 button_right.setVisibility(View.VISIBLE);
	 }
	 }
	 dock.clearAnimation();

	 valueA.setDuration(125);
	 valueA.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
	 public void onAnimationUpdate(ValueAnimator animaion) {
	 final RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)desktop.getLayoutParams();
	 final int a = animaion.getAnimatedValue();
	 lp.setMargins(0, 0, 0, h2);
	 desktop.setLayoutParams(lp);
	 dock.getLayoutParams().height = a;
	 }
	 });
	 valueA.start();
	 yt = 125 + System.currentTimeMillis();
	 }
	 yy = moe.getY();
	 }

	 } else {
	 yy = moe.getY();
	 }
	 if (moe.getAction() == MotionEvent.ACTION_DOWN) {
	 popn.setX(w.getX() + moe.getX());
	 popn.setY(w.getY() + moe.getY());
	 }
	 return false;
	 }
	 });
	 }*/

    public Bitmap getWebDrawing() {
		/*
		 try {
		 desktop.setDrawingCacheEnabled(true);
		 Bitmap bitmap = Bitmap.createBitmap(desktop.getDrawingCache());
		 desktop.setDrawingCacheEnabled(false);
		 return bitmap;
		 } catch (Exception e) {
		 */
		View view = web;//getWindow().getDecorView();
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		view.draw(canvas);
		return bitmap;
		//lastimage = Bitmap.createBitmap(bitmap, 0, (int)desktop.getY(), view.getWidth(), view.getHeight() - getNavigationBarHeight(this) - (int)desktop.getY());

		//return Bitmap.createBitmap(bitmap, (int)desktop.getScrollX(), (int)desktop.getY(), view.getWidth() - (int)desktop.getScrollX(), view.getHeight() - dock.getHeight() - (int)desktop.getY() - getNavigationBarHeight(this));
		//}


	} 
	public Bitmap getWebDrawingB() {
        Display view = getWindowManager().getDefaultDisplay();
        ColorDrawable drawable = new ColorDrawable(Color.WHITE);
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap); drawable.draw(canvas); //拿到这个bitmap了
        return bitmap;
    }
	public void ripple_version(View view_children) {
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
            ((HeyWeb)v).runJS("try{" +
							  "window.hey.onReceivedThemeColor(document.querySelector(\"meta[name='theme-color']\").getAttribute(\"content\")," + webi + ");" +
							  "}catch(e){" +
							  "window.hey.onReceivedThemeColor(\"\"," + webi + ");" +
							  "}");
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
            }
        } else {
            if (v == Main.web) {

                if (View.GONE == Main.progressbar.getVisibility()) {
                    AlphaAnimation alphaA = new AlphaAnimation(0, 1);
                    alphaA.setDuration(255);
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
