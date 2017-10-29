package omark.hey;

import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeyMenu {
    // 图片封装为一个数组
    String [] from, text;
    int[] to, icon;
    
    private List<Map<String, Object>> data_list = new ArrayList<Map<String, Object>>();

    public HeyMenu() {
        from = new String[] {
            "text",
            "icon"
            };
        to = new int[] {
            R.id.menu_item_text,
            R.id.menu_item_icon
        };
        
        text = new String[] {
            S.getString(R.string.lang42),
            S.getString(R.string.lang18),
            S.getString(R.string.lang37),
            S.getString(R.string.lang27),
            S.getString(R.string.lang17),
            S.getString(R.string.lang36)
        };
        
        icon = new int[] {
            0xE145,
            0xE5D5,
            0xE80D,
            0xE88A,
            0xE867,
            0xE86F
        };
    }
    
    public SimpleAdapter getAdapter() {
        getData();
        //新建适配器
        
        SimpleAdapter sa = new SimpleAdapter(Main.me, data_list, R.layout.menu_item, from, to);
        sa.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data, String textRepresentation) {
                    if (view.getId() == R.id.menu_item_icon) {
                        HeyHelper.setFont((TextView)view, "m");
                        ((TextView)view).setText(String.valueOf((char)((Integer)data).intValue()));
                        return true;
                    }
                    return false;
                    /*
                     if (view instanceof ImageView && data instanceof Bitmap) {
                     ((ImageView)view).setImageBitmap((Bitmap) data);
                     return true;
                     } else {
                     return false;
                     }*/
                }
            });
        return sa;
    }

    public List<Map<String, Object>> getData() {
        //cion和iconName的长度是相同的，这里任选其一都可以
        for (int i = 0; i < icon.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("text", text[i]);
            map.put("icon", icon[i]);
            data_list.add(map);
        }
        return data_list;
    }
}
