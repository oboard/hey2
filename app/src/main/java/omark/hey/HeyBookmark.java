package omark.hey;

import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HeyBookmark {
    
    List<Map<String, Object>> data_list = new ArrayList<Map<String, Object>>();
    
    public SimpleAdapter getAdapter() {
        //新建适配器
        
        String[] from = new String[] {
            "title",
            "url"
        };
        int[] to = new int[] {
            R.id.bookmark_item_title,
            R.id.bookmark_item_url
        };
        
        data_list = getData();

        return new SimpleAdapter(Main.me, data_list, R.layout.bookmark_item, from, to);
    }
    
    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> date_list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < S.get("bm", 0); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", S.get("bn" + i, ""));
            map.put("url", S.get("b" + i, ""));
            date_list.add(map);
        }
        return date_list;
    }
    
}
