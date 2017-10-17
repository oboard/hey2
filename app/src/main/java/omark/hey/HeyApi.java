package omark.hey;
import android.webkit.JavascriptInterface;
import android.graphics.drawable.ColorDrawable;
import android.widget.Toast;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;

public class HeyApi {

    int webi;

    @JavascriptInterface
    public void setIndex(int index) {
        webi = index;
    }

    @JavascriptInterface
    public void onReceivedThemeColor(String color) {
        if (color.equals("")) {
            Main.multitop.set(webi, new ColorDrawable(0xff707070));
        } else {
            Main.multitop.set(webi, new ColorDrawable(Color.parseColor(color)));
        }
        Main.root.setBackground(Main.multitop.get(webi));
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
