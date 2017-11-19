package omark.hey;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;  

public class FastBlur {

    public static Bitmap rsBlur(Context context, Bitmap sentBitmap, int radius) {
        Bitmap bitmap = Bitmap.createScaledBitmap(sentBitmap, sentBitmap.getWidth() / 2, sentBitmap.getHeight() / 2, false);
        try {
            final RenderScript rs = RenderScript.create(context);
            final Allocation input = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(radius);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(bitmap);
        } catch (OutOfMemoryError e) {
            
        }
        return bitmap;
    }
}
