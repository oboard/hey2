package omark.hey;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import omark.hey.HeyHelper;
import omark.hey.R;

public class HeySetting extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		

		((HeySetting)findViewById(R.id.setting_2)).setChecked(S.get("pagecolor", true));
	}

	public void onSettingClick(View v) {
        switch (v.getId()) {
            case R.id.setting_1:
                final EditText et = new EditText(this);
                et.setText(S.get("home", HeyHelper.DEFAULT_HOME));
                new AlertDialog.Builder(this).setView(et).setTitle(v.getTag().toString())
                    .setNegativeButton(R.string.lang4, null).setPositiveButton(R.string.lang3, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int i) {
                            S.put("home", et.getText().toString())
                                .ok();
                        }
                    }).show();
                break;
            case R.id.setting_2:
                S.put("pagecolor", ((HeySetting)v).isChecked())
                    .ok();
                break;
            case R.id.setting_3:
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
                new AlertDialog.Builder(this).setTitle(v.getTag().toString()).setSingleChoiceItems(se, S.get("searchindex", 0), new DialogInterface.OnClickListener() { 
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            S.put("search", su[which])
                                .put("searchindex", which)
                                .ok();

                            dialog.dismiss();
                        } 
                    }).show();
                break;
            case R.id.setting_4:
                new AlertDialog.Builder(this).setTitle(v.getTag().toString())
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
            case R.id.setting_5:
                new AlertDialog.Builder(this).setTitle(v.getTag().toString())
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
            case R.id.setting_6:
                startActivity(new Intent(this, About.class));
                break;
        }
    }
	
}
