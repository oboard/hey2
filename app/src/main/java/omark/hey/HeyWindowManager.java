package omark.hey;

import android.content.Context;
import android.view.WindowManager;
import java.util.ArrayList;
import omark.hey.HeyWindow;
import android.view.View;

public class HeyWindowManager {

    public static ArrayList<HeyWindow> windows = new ArrayList<HeyWindow>();
    public static ArrayList<HeyWeb> webs = new ArrayList<HeyWeb>();
    public static HeyWindow window_x;
    public static HeyWeb web_x;

    public static HeyWindowManager me;

    public HeyWindowManager() {
    }

    public static HeyWindowManager init() {
        /*window_n = new HeyWindow(Main.me);
        window_n.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean focus) {
                    if (!focus) {
                        window_n.view.removeAllViews();
                        window_n.setVisibility(View.GONE);
                        window_x.setVisibility(View.VISIBLE);
                        HeyWeb web_n = web_x;
                        window_x.view.addView(web_n);
                    }
                }
            });
        window_n.setVisibility(View.GONE);
        HeyWindow.canTouch = false;*/
        me = new HeyWindowManager();
        return me;
    }


    public static HeyWindowManager addWindow(Context context) {
        window_x = new HeyWindow(context);
        web_x = new HeyWeb(context);
        window_x.view.addView(web_x);
        windows.add(window_x);
        return me;
    } public static HeyWindowManager showWindow(int index) {
        windows.get(index).setVisibility(View.VISIBLE);
        return me;
    } public static HeyWindowManager hideWindow(int index) {
        windows.get(index).setVisibility(View.GONE);
        return me;
    }

    public static HeyWindowManager removeWindow(Context context, int index) {
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.removeView(windows.get(index));
        windows.remove(index);
        return me;
    } public static HeyWindowManager removeWindow(Context context, HeyWindow object) {
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.removeView(object);
        windows.remove(object);
        return me;
    }

}
