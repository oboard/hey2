package omark.hey;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

public class S {
    static Editor e;
    static SharedPreferences s;

    public static void init(Context context, String name) {
        s = context.getSharedPreferences(name, 0);//Context.MODE_WORLD_READABLE);// + Context.MODE_WORLD_WRITEABLE);
        e = s.edit();
    }

    //Get
    public static String get(String name, String def) {
        return s == null ? def : s.getString(name, def);
    } public static boolean get(String name, boolean def) {
        return s == null ? def : s.getBoolean(name, def);
    } public static float get(String name, float def) {
        return s == null ? def : s.getFloat(name, def);
    } public static long get(String name, long def) {
        return s == null ? def : s.getLong(name, def);
    } public static int get(String name, int def) {
        return s == null ? def : s.getInt(name, def);
    } public static Set<String> get(String name, Set<String> def) {
        return s == null ? def : s.getStringSet(name, def);
    }

    public static Map<String, ?> getAll() {
        if (s == null) return null;
        return s.getAll();
    }

    //Put
    public static void put(String name, String def) {
        if (e != null) e.putString(name, def);
    } public static void put(String name, boolean def) {
        if (e != null) e.putBoolean(name, def);
    } public static void put(String name, float def) {
        if (e != null) e.putFloat(name, def);
    } public static void put(String name, long def) {
        if (e != null) e.putLong(name, def);
    } public static void put(String name, int def) {
        if (e != null) e.putInt(name, def);
    } public static void put(String name, Set<String> def) {
        if (e != null) e.putStringSet(name, def);
    }

    public static boolean contains(String name) {
        if (s == null) return false;
        return s.contains(name);
    }

    public static void del(String name) {
        if (e != null) e.remove(name);
    }

    //此函数效率低，请谨慎使用
    public static void delIndex(String name, String max_name, int index) {
        int n = get(max_name, 0);
        ArrayList<String> a = new ArrayList<String>();
        for (int i = 0; i < n; i++) {
            a.add(get(name + i, ""));
        }
        a.remove(index);

        del(name + n);

        for (int i = 0; i < n - 1; i++) {
            put(name + i, a.get(i));
        }
    }

    public static void ok() {
        if (e != null) e.apply();
    }

    public static boolean OK() {
        if (e != null) return e.commit();
        return false;
    }
}
