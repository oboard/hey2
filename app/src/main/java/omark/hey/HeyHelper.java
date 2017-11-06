package omark.hey;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.widget.TextView;

public class HeyHelper {
    static String DEFAULT_HOME = "https://www.bing.com";
    static String DEFAULT_SEARCH = "https://bing.com/search?q=";
    
    public static String getSearch(String body) {
        return S.get("search" , HeyHelper.DEFAULT_SEARCH) + body;
    }
    
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float round) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        // 得到画布
        Canvas canvas = new Canvas(output);
        // 将画布的四角圆化
        final int color = Color.RED;
        final Paint paint = new Paint();
        // 得到与图像相同大小的区域 由构造的四个值决定区域的位置以及大小
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        // 值越大角度越明显
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // drawRoundRect的第2,3个参数一样则画的是正圆的一角，如果数值不同则是椭圆的一角
        canvas.drawRoundRect(rectF, round * 2, round * 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    } public static Bitmap ColoBitmap(Bitmap bitmap, int color) {
        Bitmap b = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        for (int x = 0; x < b.getWidth(); x++) {
            for (int y = 0; y < b.getHeight(); y++) {
                if (b.getPixel(x, y) != Color.TRANSPARENT) b.setPixel(x, y, color);
            }
        }
        return b;
    } public static boolean isLightColor(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return (darkness < 0.5);
    }
    
    public static void setFont(TextView textview, String ttf) {
        Typeface font = Typeface.createFromAsset(textview.getContext().getResources().getAssets(), ttf + ".ttf"); 
        textview.setTypeface(font);
    }
    
    public static String toWeb(String s) {
        String url = s;
        if (!HeyWeb.isUri(url))  {
            if (url.indexOf(".") != -1) 
                url = "http://" + url;
            else
                url = HeyHelper.getSearch(url);
        }
        return url;
    }
}
