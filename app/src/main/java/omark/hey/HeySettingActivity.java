package omark.hey;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.EditText;
import java.io.FileNotFoundException;
import omark.hey.HeyHelper;

public class HeySettingActivity extends PreferenceActivity implements  Preference.OnPreferenceClickListener{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        this.addPreferencesFromResource(R.layout.settings);
		
		findPreference("h").setOnPreferenceClickListener(this);
		findPreference("s").setOnPreferenceClickListener(this);
		findPreference("b").setOnPreferenceClickListener(this);
		findPreference("st").setOnPreferenceClickListener(this);
		findPreference("a").setOnPreferenceClickListener(this);
		
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
            getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
    public boolean onPreferenceClick(Preference preference) {
       
        switch (preference.getOrder()) {
            case 0:
                final EditText et = new EditText(this);
                et.setText(S.get("home", HeyHelper.DEFAULT_HOME));
                new AlertDialog.Builder(this).setView(et).setTitle(preference.getTitle())
                    .setNegativeButton(R.string.lang4, null).setPositiveButton(R.string.lang3, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int i) {
                            S.put("home", et.getText().toString())
                                .ok();
                        }
                    }).show();
                break;
            case 1:
                final String[] se = {
                    "Bing",
                    "Google",
                    "Baidu",
                    "Sogou",
                    "Yandex",
                    "Yahoo",
                    "360"
                };
                final String[] su = {
                    "https://bing.com/search?q=",
                    "https://google.com.hk/search?q=",
                    "https://baidu.com/s?word=",
                    "https://sogo.com/web?query=",
                    "https://yandex.com/search/?text=",
                    "https://search.yahoo.com/search?p=",
                    "https://so.com/s?q="
                };
                new AlertDialog.Builder(this).setTitle(preference.getTitle()).setSingleChoiceItems(se, S.get("searchindex", 0), new DialogInterface.OnClickListener() { 
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            S.put("search", su[which])
                                .put("searchindex", which)
                                .ok();

                            dialog.dismiss();
                        } 
                    }).show();
                break;
            case 2:
                new AlertDialog.Builder(this).setTitle(preference.getTitle())
                    .setItems(new String[] {
                        S.getString(R.string.lang1),
                        S.getString(R.string.lang13)
                    }, new DialogInterface.OnClickListener() { 
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            S.put("background", which).ok();
                            if (which == 0) return;
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent, 1);
                        }
                    }).show();
                break;
            case 3:
                new AlertDialog.Builder(this).setTitle(preference.getTitle())
                    .setSingleChoiceItems(new String[] {
                        S.getString(R.string.lang14),
                        S.getString(R.string.lang15)
                    }, S.get("style", 0), new DialogInterface.OnClickListener() { 
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            S.put("style", which).ok();
                            Main.freshDock();

                            dialog.dismiss();
                        }
                    }).show();
                break;
            case 4:
                startActivity(new Intent(this, About.class));
                break;
        }
    return true;
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)  {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				Uri uri = intent.getData();
				ContentResolver cr = this.getContentResolver();
				try {
					S.storePic("background", BitmapFactory.decodeStream(cr.openInputStream(uri)));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
    }

}
