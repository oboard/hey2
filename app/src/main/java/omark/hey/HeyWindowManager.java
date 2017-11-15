package omark.hey;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import java.util.ArrayList;
import omark.hey.HeyWindow;
import omark.hey.Main;

public class HeyWindowManager {

    public static ArrayList<HeyWindow> windows = new ArrayList<HeyWindow>();
    public static ArrayList<HeyWeb> webs = new ArrayList<HeyWeb>();
    public static HeyWindow window_x;
    public static HeyWeb web_x;

    public static HeyWindowManager me;

    public HeyWindowManager() {
    }

    public static HeyWindowManager init() {
        me = new HeyWindowManager();
        return me;
    }


    public static HeyWindowManager addWindow(Context context) {
        window_x = new HeyWindow(context);
        web_x = new HeyWeb(context);
        final HeyWeb webk = web_x;
        
        webk.setWebChromeClient(new HeyWindowWebChrome(window_x, webk));
        webk.addJavascriptInterface(webk.getWebChromeClient(), "CONTEXT_WINDOW");
        window_x.view.addView(webk);
        windows.add(window_x);

        //返回按钮的事件
        window_x.back.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final HeyWindowWebChrome hWWC = ((HeyWindowWebChrome)webk.getWebChromeClient());
                    if (hWWC.mCustomView != null && hWWC.mCustomView.getVisibility() == View.VISIBLE)
                        hWWC.onHideCustomView();
                    else
                        webk.goBack();
                    if (!webk.canGoBack()) v.setVisibility(View.GONE);
                }
            });

        return me;
    } public static HeyWindowManager showWindow(int index) {
        //隐藏窗口
        windows.get(index).setVisibility(View.VISIBLE);
        return me;
    } public static HeyWindowManager hideWindow(int index) {
        //显示窗口
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

class HeyWindowWebChrome extends WebChromeClient {

    public CustomViewCallback mCustomViewCallback;
    public View mCustomView; //全屏视频
    public HeyWindow mWindow;
    public HeyWeb mWeb;
    public HeyWindowWebChrome(HeyWindow window, HeyWeb web) {
        mWindow = window;
        mWeb = web;
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        super.onShowCustomView(view, callback);

        if (mCustomView != null) {
            callback.onCustomViewHidden();
            return;
        }

        mCustomView = view;
        mCustomViewCallback = callback;

        mWeb.setVisibility(View.GONE);
        mWindow.view.addView(mCustomView);
        mWindow.back.setVisibility(View.VISIBLE);
    }

    @Override
    public void onHideCustomView() {
        if (mCustomView == null) return;

        if (mCustomViewCallback != null) mCustomViewCallback.onCustomViewHidden();
        mCustomView.setVisibility(View.GONE);
        mCustomView = null;

        mWeb.setVisibility(View.VISIBLE);
        mWindow.view.removeView(mCustomView);
        if (!mWeb.canGoBack()) mWindow.back.setVisibility(View.GONE);
        super.onHideCustomView();
    }


    public void onReceivedTitle(WebView v, String title) {
        try {
            if (mWeb.canGoBack()) mWindow.back.setVisibility(View.VISIBLE);
            if (title.equals("about:blank")) return;
            mWindow.title.setText(title);
            if (!S.get("pagecolor", true)) return;
            v.loadUrl("javascript:(function(){" +
                      "try{" +
                      "CONTEXT_WINDOW.onReceivedThemeColor(document.querySelector(\"meta[name='theme-color']\").getAttribute(\"content\")," + HeyWindowManager.windows.indexOf(mWindow) + ");" +
                      "}catch(e){" +
                      "CONTEXT_WINDOW.onReceivedThemeColor(\"\"," + HeyWindowManager.windows.indexOf(mWindow) + ");" +
                      "}" +
                      "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void onReceivedThemeColor(final String color, final int index) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    int c = 0;
                    if (!color.equals(""))
                        c = Color.parseColor(color);
                    else
                        c = S.getColor(R.color.colorAccent);
                        
                    HeyWindowManager.windows.get(index).bar.setBackgroundColor(c);
                    HeyWindowManager.windows.get(index).root.setBackgroundColor(HeyHelper.blendColor(c, Color.BLACK, 0.9f));
                }
            });
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
