package omark.hey;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.webkit.JavascriptInterface;

public class HeyApi {

    @JavascriptInterface
    public void onReceivedThemeColor(final String color, final int webi) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
                public void run() {
                    /*if (S.get("pagecolor", true) && webi == Main.webindex) {
                     Bitmap b = Main.me.getWebDrawing();
                     Main.multitop.set(webi - 1, new BitmapDrawable(Bitmap.createBitmap(b, 0, 0, b.getWidth(), 1)));
                     }*/
                    //if (Main.multibottom.get(webi) != Color.TRANSPARENT) {
                    if (!color.equals("")) {
                        Main.multibottom.set(webi, Color.parseColor(color));
                    } else {
                        Main.multibottom.set(webi, Color.TRANSPARENT);
                    }
                    //}
                    if (webi == Main.webindex) Main.onChangeBackground(Main.multibottom.get(webi), Main.multitop.get(webi));
                }
            });
    }

    @JavascriptInterface
    public void showSource(String html) {

    }

    //H5EXT!----------------
    @JavascriptInterface
    public String app(String name) {
        switch (name) {
            case "builder":
                return "" + Build.VERSION.SDK_INT;
            case "version":
                return S.getVersionName();
            case "addin":
                return "" + 1000;
        }
        return "";
    }

    @JavascriptInterface
    public String get(String name) {
        switch (name) {
            case "lang25":
                return S.getString(R.string.name2);
        }
        return name; 
    }

    @JavascriptInterface
    public String get(String name, String def) {
        switch (name) {
            case "lang40":
                return "Version :";
        }
        return def;
    }

    @JavascriptInterface
    public void cmd(String str) {

    }
}
