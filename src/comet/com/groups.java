package comet.com;
import java.util.ArrayList;
import util.DBAdapter;
import util.Group;
import util.GroupParser;
import util.Series;
import util.SeriesParser;
import util.Talk;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.TabSpec;

public class groups extends Activity implements OnClickListener{
	private DBAdapter db;
	private ArrayList<Group> popgroups, recentgroups;
	private ListView lv1,lv2;
	private ImageButton refresh, help, search;
	private ProgressDialog pd;
	private ProgressBar pb;
	private ListAdapter adapter1,adapter2;
	private TextView head, navi,message,scoreTextView,totalscore;
	private ImageButton  letterButton;
	private String server_return="";
	
	private final int MENU_MENU = Menu.FIRST;
	private final int MENU_HOME = Menu.FIRST + 1;
	private final int MENU_SERIES = Menu.FIRST + 2;
	private final int MENU_SCHEDULE = Menu.FIRST + 3;
	private final int MENU_LOGIN = Menu.FIRST+4;
	private final int MENU_RECOMMEND = Menu.FIRST+5;
	private final int MENU_CAL = Menu.FIRST+6;
	private final int MENU_GAME=Menu.FIRST+7;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.series_group);
		
		//init db
		db = new DBAdapter(this);
		
		//init views
		lv1 = (ListView) this.findViewById(R.id.ListView01);
		lv2 = (ListView) this.findViewById(R.id.ListView02);
	
		head = (TextView) findViewById(R.id.TextView01);
		head.setText("Groups");
		
		navi = (TextView) findViewById(R.id.navi);
		navi.setVisibility(View.VISIBLE);
		navi.setText("Popular groups and recent formed groups");
		
		pb = (ProgressBar) this.findViewById(R.id.widget108);
		pb.setVisibility(View.GONE);
		
		//status = (TextView) findViewById(R.id.status);
		message=(TextView)findViewById(R.id.message);
	    message.setVisibility(View.INVISIBLE);
	   
	    scoreTextView=(TextView)findViewById(R.id.totalview);
	    scoreTextView.setVisibility(View.INVISIBLE);
	    totalscore=(TextView)findViewById(R.id.totalscore);
	    totalscore.setVisibility(View.INVISIBLE);

	    letterButton=(ImageButton)findViewById(R.id.mail);
	    letterButton.setVisibility(View.INVISIBLE);
		// Set up the tabs
        TabHost host = (TabHost) findViewById(R.id.tabdates);
        LinearLayout l = (LinearLayout) host.getChildAt(0);
        TabWidget tw = (TabWidget)l.getChildAt(0); 
        host.setup();
      
      //1st day tab
        RelativeLayout tab1 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_style, tw, false);  
        TextView text1 = (TextView) tab1.getChildAt(1);
        text1.setText("Popular group"); 
        TabSpec day1 = host.newTabSpec("Pop");
        day1.setIndicator(tab1);
        day1.setContent(R.id.ListView01);
        host.addTab(day1);
        
     //2nd day tab
        RelativeLayout tab2 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_style, tw, false);  
        TextView text2 = (TextView) tab2.getChildAt(1);
        text2.setText("Active group"); 
        TabSpec day2 = host.newTabSpec("recent");
        day2.setIndicator(tab2);
        day2.setContent(R.id.ListView02);
        host.addTab(day2);
        
        host.setCurrentTabByTag("popular");

        //set Views
        initViews();
        
		//Refresh button
		refresh = (ImageButton) findViewById(R.id.ImageButton03);
		//refresh.setVisibility(View.GONE);
		refresh.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) {
				if(isConnect(groups.this)){
					//showDialog("Loading data, please wait...");
					new GroupLoadTask().execute();
			}
				else
					new AlertDialog.Builder(groups.this) 
		          .setMessage("This porcess requires internet connection, please check your internet connection.") 
		          .setPositiveButton("close", 
		                         new DialogInterface.OnClickListener(){ 
		                                 public void onClick(DialogInterface dialoginterface, int i){ 
		                             dialoginterface.cancel();
		                                  } 
		                          }) 
		          .show(); 		
			}
			
			});	 
		
		search = (ImageButton) this.findViewById(R.id.ImageButton01);
		search.setOnClickListener(this);
		
		//help = (ImageButton) this.findViewById(R.id.ImageButton02);
		//help.setVisibility(View.INVISIBLE);
		}
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_MENU, 0, "Main Menu").setIcon(R.drawable.menu_s);
		menu.add(0, MENU_HOME, 0, "Browse talk").setIcon(R.drawable.home_s);
		menu.add(0, MENU_SERIES, 0, "Series").setIcon(R.drawable.series_s);
		menu.add(0, MENU_SCHEDULE, 0, "Bookmarked").setIcon(R.drawable.schedule_s);
		menu.add(0, MENU_LOGIN, 0, "Search").setIcon(R.drawable.search_s);
		menu.add(0, MENU_RECOMMEND, 0, "Recommendation").setIcon(R.drawable.recommend_s);
		menu.add(0, MENU_GAME, 0, "Game").setIcon(R.drawable.comet_logo);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Intent itemintent = new Intent();
		switch (item.getItemId()) {
		case MENU_MENU:
			this.finish();
			itemintent.setClass(groups.this, MainInterface.class);
			startActivity(itemintent);
			return true;
		case MENU_HOME:
			this.finish();
			itemintent.setClass(groups.this, Browsetab.class);
			startActivity(itemintent);
			return true;
		case MENU_SERIES:
			this.finish();
			itemintent.setClass(groups.this, series.class);
			startActivity(itemintent);
			return true;
		case MENU_SCHEDULE:
			this.finish();
			itemintent.setClass(groups.this, MyBookmarked.class);
			startActivity(itemintent);
			return true;
		case MENU_LOGIN:
			this.finish();
			itemintent.setClass(groups.this, Search.class);
			startActivity(itemintent);
			return true;
		case MENU_RECOMMEND:
			this.finish();
			itemintent.setClass(groups.this, MyRecommend.class);
			startActivity(itemintent);
			return true;
		case MENU_GAME:
			this.finish();
			itemintent.setClass(groups.this, gameTab.class);
			startActivity(itemintent);
			return true;
		}
		return false;
	}
	public void initViews(){
		popgroups = new ArrayList<Group>();
		popgroups = getGroupFromDB("pop");
		if(popgroups.size() != 0){
		adapter1 = new ListAdapter(popgroups);
		this.lv1.setAdapter(adapter1);
		this.lv1.setOnItemClickListener(adapter1);
		}
		recentgroups = new ArrayList<Group>();
		recentgroups = getGroupFromDB("recent");
		if(recentgroups.size() != 0){
		adapter2 = new ListAdapter(recentgroups);
		this.lv2.setAdapter(adapter2);
		this.lv2.setOnItemClickListener(adapter2);
		}
		new GroupLoadTask().execute();
	}
	public ArrayList<Group> getGroupFromDB(String cate){
		ArrayList<Group> g = new ArrayList<Group>();
		db.open();
		g = db.getGroupByCate(cate);
		db.close();
		return g;
	}
	public ArrayList<Group> getGroupFromServer(String action){
		ArrayList<Group> s = new ArrayList<Group>();
		GroupParser sp = new GroupParser();
		
		if(action.compareTo("pop") == 0){
			s = sp.getPopData();
			server_return = sp.status;
		}
		if(action.compareTo("recent")==0){			
			s = sp.getRecentData();
			server_return = sp.status;
		}
		return s;	
	}
	private class GroupLoadTask extends AsyncTask<Void,Integer,Integer>{
		String server_return_pop="";
		String server_return_recent="";
		@Override
		protected Integer doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			if(isConnect(groups.this)){
				publishProgress(4);
			popgroups = getGroupFromServer("pop");
			server_return_pop=server_return;
			
			recentgroups = getGroupFromServer("recent");
			server_return_recent=server_return;
			
			if((popgroups.size() == 0 && server_return_pop.compareTo("ok") ==0) || (recentgroups.size() == 0 && server_return_recent.compareTo("ok") ==0)){
			publishProgress(0);
			}
			else if ((popgroups.size() == 0 && server_return_pop.compareTo("ok") !=0)||(popgroups.size() == 0 && server_return_pop.compareTo("ok") !=0)){
			publishProgress(2);
			}
			else{
			try{
			db.open();
			for(int i=0; i <popgroups.size(); i++){
				long error = db.insertGroup(popgroups.get(i));
				Log.d("groups", "return value is"+error);
			}
			for(int i=0; i <recentgroups.size(); i++){
				long error = db.insertGroup(recentgroups.get(i));
				Log.d("groups", "return value is"+error);
			}
			db.close();
			}
			catch (Exception e) {
				System.out.print(e.getMessage());
			}
			publishProgress(1);
			}
			}
			else
				publishProgress(3);
			return 1;
			
		}
		protected void onProgressUpdate(Integer... progress) {
			int command = progress[0];
			switch(command){
			case 0:
				//dismissDialog();
			
				//status = (TextView) findViewById(R.id.status);
				//status.setVisibility(View.VISIBLE);
				pb.setVisibility(View.GONE);
				navi.setText("There is no series.");
				navi.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lightbulb, 0, 0, 0);			
				adapter1 = new ListAdapter(popgroups);
				groups.this.lv1.setAdapter(adapter1);
				groups.this.lv1.setOnItemClickListener(adapter1);
				adapter2 = new ListAdapter(recentgroups);
				groups.this.lv2.setAdapter(adapter2);
				groups.this.lv2.setOnItemClickListener(adapter2);
				break;
			case 1:
				//dismissDialog();
				Log.d("groups", "This is UI error");
				//status = (TextView) findViewById(R.id.status);
				//status.setVisibility(View.GONE);
				pb.setVisibility(View.GONE);
				adapter1 = new ListAdapter(popgroups);
				groups.this.lv1.setAdapter(adapter1);
				groups.this.lv1.setOnItemClickListener(adapter1);
				adapter2 = new ListAdapter(recentgroups);
				groups.this.lv2.setAdapter(adapter2);
				groups.this.lv2.setOnItemClickListener(adapter2);
				break;
			case 2:
				//dismissDialog();
				//status = (TextView) findViewById(R.id.status);
				//status.setVisibility(View.VISIBLE);
				pb.setVisibility(View.GONE);
				Toast.makeText(getApplicationContext(), "Fail to connect to server, please check your internet connection and try again.",
					      Toast.LENGTH_SHORT).show();
				break;
			case 3:
				pb.setVisibility(View.GONE);
				Toast.makeText(getApplicationContext(), "Fail to connect to server, please check your internet connection and try again.",
					      Toast.LENGTH_SHORT).show();
				break;
			case 4:
				pb.setVisibility(View.VISIBLE);
				break;
			default:
				break;
		}
	}
	}
	static class ViewHolder {
		TextView title, bookmark;
	}
	private class ListAdapter extends BaseAdapter implements OnItemClickListener{

		private ArrayList<Group> tlist;
		public ListAdapter(ArrayList<Group> s){
			this.tlist = s;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return tlist.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return tlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder vh = null;
			
			/*if (convertView == null) {*/
				LayoutInflater li = getLayoutInflater();
				convertView = li.inflate(R.layout.series_group_list_item, null);
				vh = new ViewHolder();
				
				vh.title = (TextView) convertView.findViewById(R.id.title);				
				vh.bookmark = (TextView) convertView.findViewById(R.id.bookmark);
				
				convertView.setTag(vh);
			/*} else {
				vh = (ViewHolder) convertView.getTag();
			}*/
		
			vh.title.setText(tlist.get(position).name);
			if(tlist.get(position).bookmarkno == "")
				vh.bookmark.setText("0");
			else
			vh.bookmark.setText(tlist.get(position).bookmarkno);
			
			return convertView;
		}
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			// TODO Auto-generated method stub
				Intent in = new Intent(groups.this, GroupDetail.class);
				in.putExtra("id", tlist.get(pos).id);				
				startActivity(in);
		}
		
	}
	public void showDialog(String s){
		 pd = ProgressDialog.show(this, "Synchronization", s,true, false);
	 }
	 public void dismissDialog(){
		 pd.dismiss();
	 }
	 public static boolean isConnect(Context context) {   
         
	        ConnectivityManager connectivity = (ConnectivityManager) context   
	                .getSystemService(Context.CONNECTIVITY_SERVICE);   
	        if (connectivity != null) {   
	          
	             NetworkInfo info = connectivity.getActiveNetworkInfo();   
	            if (info != null) {   
       
	                 if (info.getState() == NetworkInfo.State.CONNECTED) {   
	                     return true;   
	                }   
	            }   
	         }   
	        return false;   
	     }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		case R.id.ImageButton01:
			this.finish();
			Intent in = new Intent(groups.this,Search.class);
			startActivity(in);
			break;
		default:
			break;
		}
	}

}

