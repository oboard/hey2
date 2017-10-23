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
                return "" + S.getVersionCode();
            case "version":
                return S.getVersionName();
            case "addin":
                return "" + 1000;
            case "code":
                return S.getString(R.string.name);
        }
        return "";
    }

    @JavascriptInterface
    public void set(String name, String value) {
        S.put("h5_" + name, value);
        S.ok();
    }
    
    @JavascriptInterface
    public String get(String name) {
        switch (name) {
            case "app_title":
                return S.getString(R.string.name);

            case "lang23":
                return "设置";
            case "lang24":
                return "关于";
            case "lang25":
                return "扩展插件";
            case "lang27":
                return "天气预报";
            case "lang28":
                return "网址导航";
            case "lang29":
                return "黑色的云";
            case "lang30":
                return "主题设置";
            case "lang40":
                return "Version :";

            case "lang41":
                return "更新日记";
            case "lang42":
                return "使用攻略";
            case "lang43":
                return "历史记录";
            case "lang44":
                return "我的收藏";
            case "lang45":

                return "滤镜设置";
            case "lang46":
                return "搜索引擎设置";
            case "lang47":
                return "UA设置";
            case "lang48":
                return "网站导航设置";

            case "lang49":
                return "广告过滤";
            case "lang50":
                return "云端同步";
            case "lang51":
                return "高级设置";
            case "lang52":
                return "命令脚本";

            case "lang61":
                return "查看User-Agent";
            case "lang62":
                return "查看HTML5兼容测试";
            case "lang63":
                return "查看HTML5性能测试";
            case "lang64":
                return "内核升级";
        }
        return S.get("h5_" + name, name);
    }
    
    @JavascriptInterface
    public String get(char[] name) {
        return get(name.toString());
    }

    @JavascriptInterface
    public String get(String name, String def) {
        switch (name) {
        }
        
        return S.get("h5_" + name, def);
    }

    @JavascriptInterface
    public void cmd(String str) {

    }
}
