package omark.hey;
import android.webkit.JavascriptInterface;
import android.graphics.drawable.ColorDrawable;
import android.widget.Toast;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.graphics.drawable.ShapeDrawable;

public class HeyApi {

    @JavascriptInterface
    public void onReceivedThemeColor(String color, int webi) {
        /*if (S.get("pagecolor", true) && webi == Main.webindex) {
            Bitmap b = Main.me.getWebDrawing();
            Main.multitop.set(webi - 1, new BitmapDrawable(Bitmap.createBitmap(b, 0, 0, b.getWidth(), 1)));
        }*/
        if (Main.multibottom.get(webi - 1) != Color.TRANSPARENT) {
            if (!color.equals("")) {
                Main.multibottom.set(webi - 1, Color.parseColor(color));
            } else {
                Main.multibottom.set(webi - 1, Color.TRANSPARENT);
            }
        }
        if (webi == Main.webindex) Main.onChangeBackground(Color.parseColor(color), Main.multitop.get(webi - 1));
    }

    @JavascriptInterface
    public void showSource(String html) {

    }

    //H5EXT!----------------
    @JavascriptInterface
    public String app(String name) {
        switch (name) {
            case "builder":
                return "" + S.getVersionCode();
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
