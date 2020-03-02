package omark.hey;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.webkit.JavascriptInterface;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import omark.hey.Main;
import omark.hey.R;

public class HeyMenu {
    // 图片封装为一个数组
    public String [] from, text,com;
    int[] to, icon,textb;
    SimpleAdapter sa;
    GridView gv;
	int oy = 0;
	
	
	FrameLayout mMenu;
    List<Map<String, Object>> data_list;
    ArrayList<String> data_code = new ArrayList<String>();

    public HeyMenu(GridView g) {
        from = new String[] {
            "text",
            "icon",
            "back",
            "stop",
            "stop2"
        };
        to = new int[] {
            R.id.menu_item_text,
            R.id.menu_item_icon,
            R.id.menu_item_icon,
            R.id.menu_item_icon,
            R.id.menu_item_text
        };

        textb = new int[] {
            R.string.lang42,
            R.string.lang18,
            R.string.lang37,
            R.string.lang27,
            R.string.lang17,
            R.string.lang60,
            R.string.lang59,
            R.string.lang62,
            R.string.lang36,
            R.string.lang63,
			R.string.lang71,
            R.string.lang26
        };
		com = new String[] {
			"add",
			"ref",
			"sha",
			"hom",
			"adb",
			"nig",
			"bli",
			"flo",
			"dev",
			"sim",
			"pic",
			"set"
        };

		List<String> ls = new ArrayList<String>();
		for (int i:textb) {
			ls.add(S.getString(i));
		}

		text = new String[ls.size() + 1];
		ls.toArray(text);

        icon = new int[] {
            0xE145,
            0xE5D5,
            0xE80D,
            0xE88A,
            0xE867,
            0xE3A8,
            0xE8F5,
            0xE8AA,
            0xE86F,
            0xE54E,
			0xE3B6,
            0xE8B8
        };

        data_list = getData();
        gv = g;
        //新建适配器
        sa = new SimpleAdapter(Main.me, data_list, R.layout.menu_item, from, to);
        sa.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View v, Object data, String textRepresentation) {
                    if (v.getId() == R.id.menu_item_icon) {
                        if (data instanceof Integer) {
                            HeyHelper.setFont((TextView)v, "m");
                            ((TextView)v).setText(String.valueOf((char)((Integer)data).intValue()));
                        } else if (data instanceof Boolean) {
                            if ((boolean)data)
                                ((TextView)v).setTextColor(0xffffffff);
                            else
                                ((TextView)v).setTextColor(0x55ffffff);
                        } else if (data instanceof String) {
                            if (((String)data).equals(""))
                                ((TextView)v).setTextColor(0x11ffffff);
                        }
                        return true;
                    } else if (v.getId() == R.id.menu_item_text) {
                        if (data instanceof String) {
                            ((TextView)v).setText((String)data);
                        } else if (data instanceof Boolean) {
                            if ((boolean)data)
                                ((TextView)v).setTextColor(0x11ffffff);
                        }
                        return true;
                    }
                    return false;
                    /*
                     if (view instanceof ImageView && data instanceof Bitmap) {
                     ((ImageView)view).setImageBitmap((Bitmap) data);
                     return true;
                     } else {
                     return false;
                     }*/
                }
            });
		
		g.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {

					int y = (int)event.getY();
					
					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							mMenu = Main.menulayout_box;
							oy = y;
							break;
						case MotionEvent.ACTION_MOVE:
							int offsety = mMenu.getScrollY() - y+oy;

							int max = Main.menu_layout.getHeight()/2;
							if (offsety > max) offsety = max;
							mMenu.scrollTo(0, offsety);

							mMenu.invalidate();

							break;
						case MotionEvent.ACTION_UP:
							ValueAnimator ani;
							if (mMenu.getScrollY() > -Main.menu_layout.getHeight() / 2) {
								Main.dock.isUper = true;
							    ani = ValueAnimator.ofInt(mMenu.getScrollY(), 0);

								ani.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
										public void onAnimationUpdate(ValueAnimator animaion) {
											int a = animaion.getAnimatedValue();
											mMenu.setScrollY(a);
										}
									});


								Main.onMenu(true);
							} else {
								Main.dock.isUper = false;

							    ani = ValueAnimator.ofInt(mMenu.getScrollY(), -Main.menu_layout.getHeight());
								ani.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
										public void onAnimationUpdate(ValueAnimator animaion) {
											int a = (int) animaion.getAnimatedValue();
											mMenu.setScrollY(a);
										}
									});

								ScrollText.isMenu = false;
								Main.desktop_float.setVisibility(View.GONE);
							}
							ani.setDuration(125);
							ani.start();
							break;
					}
					return false;
				}
			});
    }

	public void run(int i, Main mm) {
		final Main m = mm;
		switch ((String)getData().get(i).get("com")) {
			case "add":
				m.web = m.addPage("");
				break;
			case "ref":
				if (m.web != null) m.web.reload();
				break;
			case "sha":
				if (m.web != null) {
					try {
						//分享文字
						Intent shareIntent = new Intent();
						shareIntent.setAction(Intent.ACTION_SEND);
						shareIntent.putExtra(Intent.EXTRA_TEXT, m.web.getUrl());
						shareIntent.setType("text/plain");
						//设置分享列表的标题，并且每次都显示分享列表
						m.startActivity(Intent.createChooser(shareIntent, m.getString(R.string.lang37))); 
					} catch (Exception e) {
						Toast.makeText(m, m.getString(R.string.lang21), Toast.LENGTH_SHORT).show();
					}
				}
				break;
			case "hom":
				if (m.web != null) 
					m.web.loadUrl(S.get("home", HeyHelper.DEFAULT_HOME));
				break;
			case "adb":
				LinearLayout dl = (LinearLayout)LayoutInflater.from(m).inflate(R.layout.diglog_bookmark, null);
				final EditText t1 = (EditText)dl.findViewById(R.id.diglog_bookmark_1), t2 = (EditText)dl.findViewById(R.id.diglog_bookmark_2);
				if (m.web != null) {
					t1.setText(m.web.getTitle());
					t2.setText(m.web.getUrl());
				}
				new AlertDialog.Builder(m).setView(dl).setTitle(R.string.lang17)
					.setNegativeButton(R.string.lang4, null).setPositiveButton(R.string.lang3, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int i) {
							S.addIndexX("bm" , new String[] {"b", "bn", "bt"}, new String[] {t2.getText().toString(), t1.getText().toString(), "" + System.currentTimeMillis()});

							m.bookmark_list.setAdapter(m.bookmark.getAdapter());
							Toast.makeText(m, R.string.lang20, Toast.LENGTH_SHORT).show();
						}
					}).show();
				break;
			case "flo":
				//floatview
				if (m.web != null) {
					HeyWindowManager.addWindow(m.getApplicationContext());
					HeyWindowManager.web_x.loadUrl(m.web.getUrl());
				}
				break;
			case "bli":
				//无痕模式
				m.setVmode(!m.vmode);

				break;
			case "nig":
				//夜间模式
				if (!getState(i)) {
					final AlphaAnimation alphaA = new AlphaAnimation(0, 1);
					alphaA.setDuration(225);
					alphaA.setFillAfter(true);
					m.night.startAnimation(alphaA);
					m.night.setTag(true);
					for (int k = 0; k < m.pages.size(); k++) {
						m.menus.get(k).setState(i, true);
					}
				} else {
					final AlphaAnimation alphaA = new AlphaAnimation(1, 0);
					alphaA.setDuration(225);
					alphaA.setFillAfter(true);
					m.night.startAnimation(alphaA);
					m.night.setTag(false);
					for (int k = 0; k < m.pages.size(); k++) {
						m.menus.get(k).setState(i, false);
					}
				}
				m.homemenu.setState(i, !m.homemenu.getState(i));
				break;
			case "dev":
				//开发者模式
				if (m.web != null) {
					if (!m.menus.get(m.webindex).getState(i)) {
						m.web.runJS("var script = document.createElement('script'); script.src='//cdn.jsdelivr.net/npm/eruda'; document.body.appendChild(script); script.onload = function(){eruda.init()}");
						m.menus.get(m.webindex).setState(i, true);
					} else {
						m.web.runJS("eruda.destroy()");
						m.menus.get(m.webindex).setState(i, false);
					}
				}
				break;
			case "sim":
				//仿真
				if (m.web != null) {
					new Handler(Looper.getMainLooper()).post(new Runnable() {
							public void run() {
								m.freshSimulation();

								final AlphaAnimation tranA = new AlphaAnimation(0, 1);
								tranA.setDuration(225);
								m.simulation.startAnimation(tranA);
								m.simulation.setVisibility(View.VISIBLE);

								AnimationSet aniA = new AnimationSet(true);
								aniA.addAnimation(new ScaleAnimation(1, 0.5f, 1f, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.2f));
								aniA.setInterpolator(new DecelerateInterpolator());
								aniA.setFillAfter(true);
								aniA.setDuration(225);
								m.desktop.startAnimation(aniA);

							}
						});
				}
				break;
			case "pic":
				m.web.runJS(
				"var imgs = document.getElementsByTagName(\"img\");" +
				"var urlArray = [];" +
				"for (var i=0;i<imgs.length;i++){" +
				"var src = imgs[i].src;" +
				"urlArray.push(src);" +
				"}" +
				"window.hey.cmd(\"pic\",urlArray.toString());"
				);
				m.web = m.addPage("file:///android_asset/i.html");
				return;
				//break;
			case "set":
				//设置
				m.startActivity(new Intent(m, HeySettingActivity.class));
				break;
			default:
				Toast.makeText(m, "what", Toast.LENGTH_SHORT).show();
				break;
		}

		m.onMenu(false);
	}


    public SimpleAdapter getAdapter() {
        return sa;
    }

    @JavascriptInterface
    public boolean getState(int index) {
        return data_list.get(index).get("back");
    }

    @JavascriptInterface
    public void setState(int index, boolean o) {
        data_list.get(index).put("back", o);

        sa.notifyDataSetChanged();
    }

    public void setStop(int index) {
        data_list.get(index).put("stop", "");
        data_list.get(index).put("stop2", true);

        sa.notifyDataSetChanged();
    }

    @JavascriptInterface
    public void add(int icon, String text, String com, String code) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("text", text);
        map.put("icon", icon);
        map.put("back", false);
        map.put("stop", "false");
        map.put("stop2", false);
		map.put("com", com);
        data_list.add(map);
        data_code.add(code);
        sa.notifyDataSetChanged();
    }

    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> date_list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < icon.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("text", text[i]);
            map.put("icon", icon[i]);
            map.put("back", true);
            map.put("stop", "false");
            map.put("stop2", false);
			map.put("com", com[i]);
            try {
                if (icon[i] == 0xE8F5)
                    map.put("back", Main.vmode);
                else if (icon[i] == 0xE3A8)
                    map.put("back", Main.night.getTag());
            } catch (Exception e) {
            }
            data_code.add("");
			if (i != 7) 
				date_list.add(map);
        }
        return date_list;
    }






}
