package omark.hey;

import android.content.Context;

public class HeyClipboard {

    Object clipboard;

    public HeyClipboard(Context c) {
        if (android.os.Build.VERSION.SDK_INT > 11) {
            clipboard = (android.content.ClipboardManager)c.getSystemService(Context.CLIPBOARD_SERVICE);
        } else {
            clipboard = (android.text.ClipboardManager)c.getSystemService(Context.CLIPBOARD_SERVICE);
        }
    }

    public void set(CharSequence s) {
        if (android.os.Build.VERSION.SDK_INT > 11) {
            ((android.content.ClipboardManager)clipboard).setText(s);
        } else {
            ((android.text.ClipboardManager)clipboard).setText(s);
        }
    }
    public CharSequence get() {
        if (android.os.Build.VERSION.SDK_INT > 11) {
            return ((android.content.ClipboardManager)clipboard).getText();
        } else {
            return ((android.text.ClipboardManager)clipboard).getText();
        }
    }
}
