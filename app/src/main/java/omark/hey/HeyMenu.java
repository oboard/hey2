package omark.hey;

import android.animation.ValueAnimator;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeyMenu {
    // 图片封装为一个数组
    String [] from, text;
    int[] to, icon;
    SimpleAdapter sa;
    GridView gv;

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

        text = new String[] {
            S.getString(R.string.lang42),
            S.getString(R.string.lang18),
            S.getString(R.string.lang37),
            S.getString(R.string.lang27),
            S.getString(R.string.lang17),
            S.getString(R.string.lang60),
            S.getString(R.string.lang59),
            S.getString(R.string.lang62),
            S.getString(R.string.lang36),
            S.getString(R.string.lang63),
            S.getString(R.string.lang26)
        };

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

							break;
						case MotionEvent.ACTION_MOVE:
							int offsety = mMenu.getScrollY() - y;

							int max = Main.menu_layout.getHeight();
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
    public void add(int icon, String text, String code) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("text", text);
        map.put("icon", icon);
        map.put("back", false);
        map.put("stop", "false");
        map.put("stop2", false);
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
            try {
                if (icon[i] == 0xE8F5)
                    map.put("back", Main.vmode);
                else if (icon[i] == 0xE3A8)
                    map.put("back", Main.night.getTag());
            } catch (Exception e) {
            }
            data_code.add("");
            date_list.add(map);
        }
        return date_list;
    }
}
