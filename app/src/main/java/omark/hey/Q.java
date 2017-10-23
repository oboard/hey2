package omark.hey;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class Q {
    
    static Context mContext;
    static SQLiteDatabase mSQLite;
    
    public static void init(Context context, String name) {
        if (context == null) return;
        mContext = context;
        mSQLite = SQLiteDatabase.openOrCreateDatabase("/data/data/" + context.getPackageName() + "/databases/" + name + ".db", null);
    }
    
    public static String get(String key, String def) {
        
        return def;
    }
    
}
