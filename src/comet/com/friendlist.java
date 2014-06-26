package comet.com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class friendlist extends ListActivity {

    // private List<String> data = new ArrayList<String>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       ListView lvListView=(ListView)findViewById(R.id.FriendList);
       List<Map<String, Object>> list=getData();	 
        SimpleAdapter adapter = new SimpleAdapter(this,list,R.layout.friendlist,
                new String[]{"Name","Address","status"},
                new int[]{R.id.name,R.id.status});
                lvListView.setAdapter(adapter);
    }
 
    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
 
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("Name", "Pittsburgh public theater");
        map.put("Address", "621 Penn Avenue, Pittsburgh, PA");
      
        list.add(map);
 
        map = new HashMap<String, Object>();
        map.put("Name", "Carneige Mellon Library");
        map.put("Address", "4909 Frew Street, Pittsburgh, PA");
       
        list.add(map);
 
        map = new HashMap<String, Object>();
        map.put("title", "Carneige Mellon Library");
        map.put("info", "4141 Fifth Avenue, Pittsburgh, PA");
      
        list.add(map);
         
        return list;
    }
   
}