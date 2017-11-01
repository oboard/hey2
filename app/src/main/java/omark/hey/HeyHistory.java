package omark.hey;

import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import android.app.DatePickerDialog;
import java.util.Date;

public class HeyHistory {

    List<Map<String, Object>> data_list = new ArrayList<Map<String, Object>>();

    public SimpleAdapter getAdapter() {
        //新建适配器

        final String[] from = new String[] {
            "title",
            "url",
            "time"
        };
        final int[] to = new int[] {
            R.id.history_item_title,
            R.id.history_item_url,
            R.id.history_item_time
        };

        data_list = getData();

        return new SimpleAdapter(Main.me, data_list, R.layout.history_item, from, to);
    }

    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> date_list = new ArrayList<Map<String, Object>>();
        for (int i = S.get("hm", 0) - 1; i >= 0; i--) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", S.get("hn" + i, ""));
            map.put("url", S.get("h" + i, ""));
            String time = S.get("ht" + i, "0");
            //转换格式
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                Date curDate = new Date(Long.parseLong(time));
                time = formatter.format(curDate);
            } catch(Exception e) {
                time = "";
            }
             
            map.put("time", time);
            date_list.add(map);
        }
        return date_list;
    }

}
