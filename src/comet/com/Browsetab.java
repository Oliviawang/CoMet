package comet.com;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import phpAPI.getOnlineScore;
import phpAPI.user2getOnline;

import util.DBAdapter;
import util.Score;
import util.Talk;
import util.game2receive;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;


public class Browsetab extends TabActivity implements OnTabChangeListener, OnClickListener{

	private Intent intent;
	private ImageButton refresh, search, help;
	public ProgressBar pb;
	public TextView navi,message,scoreTextView,totalscore;
	private ImageButton  letterButton;
	private String name,uid;
	private Score s;
	private DBAdapter db;
	private ArrayList<game2receive> gamereceive;
	private final int MENU_MENU = Menu.FIRST;
	private final int MENU_HOME = Menu.FIRST + 1;
	private final int MENU_SERIES = Menu.FIRST + 2;
	private final int MENU_SCHEDULE = Menu.FIRST + 3;
	private final int MENU_LOGIN = Menu.FIRST+4;
	private final int MENU_RECOMMEND = Menu.FIRST+5;
	private final int MENU_CAL = Menu.FIRST+6;
	private final int MENU_GAME=Menu.FIRST+7;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.talks_tab);
		//init DB
		db = new DBAdapter(this);
		//init Views
	
		search = (ImageButton) this.findViewById(R.id.ImageButton01);
		search.setOnClickListener(this);
		
		//help = (ImageButton) this.findViewById(R.id.ImageButton02);
		//help.setVisibility(View.INVISIBLE);
		
		pb = (ProgressBar) this.findViewById(R.id.widget108);
		pb.setVisibility(View.GONE);
		
		navi = (TextView) this.findViewById(R.id.navi);
		navi.setVisibility(View.GONE);
		
		   message=(TextView)findViewById(R.id.message);
		    message.setVisibility(View.INVISIBLE);
		   
		    scoreTextView=(TextView)findViewById(R.id.totalview);
		    totalscore=(TextView)findViewById(R.id.totalscore);
		    totalscore.setVisibility(View.VISIBLE);
		    
		    letterButton=(ImageButton)findViewById(R.id.mail);
		    letterButton.setVisibility(View.INVISIBLE);
		    
		    if(isSignin()){
				//showDialog("Loading data, please wait...");
				//new RecommendTalkLoad().execute();	
				initViews();
				}
			else{
				new AlertDialog.Builder(Browsetab.this)
				.setMessage("Browse function requires login, do you want to sign in first?")
				.setPositiveButton("yes", 
					    		 new DialogInterface.OnClickListener(){ 
								@Override
								public void onClick(DialogInterface dialoginterface, int i){ 
									dialoginterface.cancel();
									Browsetab.this.finish();	
									Intent in = new Intent(Browsetab.this, Signin.class);							
									startActivity(in);
								} 
								})
				.setNegativeButton("no", 
					    		 new DialogInterface.OnClickListener() { 
								public void onClick(DialogInterface dialog, int id) { 
									dialog.cancel(); 	
									} 
									}).show();
			}
			refresh = (ImageButton) findViewById(R.id.ImageButton03);
			refresh.setVisibility(View.VISIBLE);
			refresh.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
						if(isSignin()){
							//showDialog("Loading data, please wait...");
							
							new MessageLoad().execute();
							new TotalScoreLoad().execute();
							}
						else{
							new AlertDialog.Builder(Browsetab.this)
							.setMessage("Game home function requires login, do you want to sign in first?")
							.setPositiveButton("yes", 
								    		 new DialogInterface.OnClickListener(){ 
											@Override
											public void onClick(DialogInterface dialoginterface, int i){ 
												dialoginterface.cancel();
												Browsetab.this.finish();	
												Intent in = new Intent(Browsetab.this, Signin.class);							
												startActivity(in);
											} 
											})
							.setNegativeButton("no", 
								    		 new DialogInterface.OnClickListener() { 
											public void onClick(DialogInterface dialog, int id) { 
												dialog.cancel(); 	
												} 
												}).show();
						}
						
				}
			});
			
		
		// Set up the tabs
        TabHost host = getTabHost();//(TabHost) findViewById(R.id.tabhost);
        host.setup();
        host.setOnTabChangedListener(this);
        LinearLayout l = (LinearLayout) host.getChildAt(0);
        TabWidget tw = (TabWidget)l.getChildAt(0); 
        
      
        //Detail tab
        intent = new Intent().setClass(this, calendar.class);
        RelativeLayout tab1 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_style, tw, false);  
        TextView text1 = (TextView) tab1.getChildAt(1);
        text1.setText("By day"); 
        TabSpec detail = host.newTabSpec("Day");
        detail.setIndicator(tab1);
        detail.setContent(intent);
        
        host.addTab(detail);
        
     //Attending tab
        intent = new Intent().setClass(this, HOME.class);
        TabSpec tags = host.newTabSpec("Week");
        RelativeLayout tab2 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_style, tw, false);  
        TextView text2 = (TextView) tab2.getChildAt(1);
        text2.setText("By week"); 
        
        tags.setIndicator(tab2);
        tags.setContent(intent);
        host.addTab(tags);
        
        //Similar tab
        intent = new Intent().setClass(this, Month.class);
        TabSpec comments = host.newTabSpec("Month");
        RelativeLayout tab3 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_style, tw, false);  
        TextView text3 = (TextView) tab3.getChildAt(1);
        text3.setText("By month");
       
        comments.setIndicator(tab3);
        comments.setContent(intent);
        host.addTab(comments); 
	//set up default tab
		host.setCurrentTabByTag("Day");
	
	}

	@Override
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		if(tabId.compareTo("Day") == 0){
			navi.setVisibility(View.VISIBLE);
			navi.setText("click the date bar or arrows to pick date");
		}
		else if(tabId.compareTo("Week") == 0){
			navi.setVisibility(View.VISIBLE);
			navi.setText("click arrows to move forward or backward");
		}
		else{
			navi.setVisibility(View.VISIBLE);
			navi.setText("click arrows to move forward or backward");
		}
				
	}
	
	public void initViews(){
		//get onlinegame message
		gamereceive=new ArrayList<game2receive>();
		new MessageLoad().execute();
		gamereceive=getonlineMessageFromDB(uid);
		Log.i("work", gamereceive.toString());
		if(gamereceive.size()!=0){
			letterButton.setVisibility(View.VISIBLE);
			message.setVisibility(View.VISIBLE);
			message.setText("online game from "+gamereceive.get(0).name);
			message.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					JSONObject jsonObject=new JSONObject();
					JSONArray jsonArray=new JSONArray();
					JSONArray j=new JSONArray();
					//j.put(gamereceive);
					for(int i=0;i<gamereceive.size();i++)
					{
					
						j.put(gamereceive.get(i).colID);    
					    jsonArray.put(gamereceive.get(i).title);
					      
					}
					try {
						jsonObject.put("object", jsonArray);
						jsonObject.put("object1", j);
						jsonObject.put("user0id", gamereceive.get(0).user1ID);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Intent intent=new Intent();
					intent.setClass(Browsetab.this,message.class);
					intent.putExtra("title",jsonObject.toString());
					
				     startActivity(intent);
				}
				
			});
		}
		//
		
		s = new Score();
		new TotalScoreLoad().execute();
		Log.i("scoe", "s");
		s = getTotalScoreFromDB(uid);
		 Log.i("s", s.toString());

		if (s != null) {
			Float string=s.offlineScore+s.onlineScore;
			Log.i("Totalscore", string.toString());
			totalscore.setText(String.valueOf(string));
		} else {

			totalscore.setText("0.0");
		}
	}
	public Score getTotalScoreFromDB(String uid) {
		Score s = new Score();
		db.open();
		s = db.getTotalScore(uid);
		db.close();
		Log.i("score", "DB");
		return s;
	}
	public Score getTotalScoreFromServer(String uid) {
		Score s = new Score();
		getOnlineScore gf = new getOnlineScore();
		try {
			s = gf.getServerData(uid);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i("tag", "gf");
		// later use server status to show online or offline friend
		// server_return = s.status;
		return s;
	}
	private ArrayList<game2receive> getonlineMessageFromDB(String uid) {
		// TODO Auto-generated method stub
		ArrayList<game2receive> t = new ArrayList<game2receive>();
		db.open();
		t = db.getonlineGamefromUser(uid);
		db.close();
		return t;	
		
	}
	private class TotalScoreLoad extends AsyncTask<Void, Integer, Integer> {

		@Override
		protected Integer doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			if (isConnect(Browsetab.this)) {
			//	pb.setVisibility(View.VISIBLE);
				s = getTotalScoreFromServer(uid);
				
				Log.i("fromserver", String.valueOf(s.offlineScore));
				Log.i("from server2", String.valueOf(s.offlineScore+s.onlineScore));
				try {

					db.open();
					// db.updateToDefaultRecommend();
					
					db.deleteTotalScore();
				   db.insertTotalScore(s,uid);
				   db.updateTotalScore(s, uid);
					Log.i("offline",String.valueOf(s.offlineScore));
					Log.i("insertonline", "insertScore");
	//lack a update 
					db.close();
				} catch (Exception e) {
					System.out.print(e.getMessage());
				}

			}
			return 1;
		}

		protected void onProgressUpdate(Integer... progress) {

			// dismissDialog();
			// status = (TextView) findViewById(R.id.status);
			// status.setVisibility(View.GONE);
			pb.setVisibility(View.GONE);
			Float string=s.offlineScore+s.onlineScore;
			Log.i("Totalscore", string.toString());
			totalscore.setText(String.valueOf(string));

		}
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
 public ArrayList<game2receive> getonlineFromServer(String uid) {
		ArrayList<game2receive> s = new ArrayList<game2receive>();
		user2getOnline gf = new user2getOnline();
		s = gf.getServerData(uid);
		Log.i("tag", "gr");
		// later use server status to show online or offline friend
		// server_return = s.status;
		return s;
	}
 
	private class MessageLoad extends AsyncTask<Void, Integer, Integer> {

		@Override
		protected Integer doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			if (isConnect(Browsetab.this)) {
				//pb.setVisibility(View.VISIBLE);
				gamereceive =getonlineFromServer(uid);

				try {

					db.open();
					// db.updateToDefaultRecommend();
					db.deleteGame2User();
					for (int i = 0; i < gamereceive.size(); i++) {

						db.insertGame2User(gamereceive.get(i));
						Log.i("insert2", "2");
						db.updateGame2User(gamereceive.get(i), uid);
						Log.i("update", "update");
					}
					db.close();
				} catch (Exception e) {
					System.out.print(e.getMessage());
				}

			}
			return 1;
		}

		protected void onProgressUpdate(Integer... progress) {

			if(gamereceive.size()!=0){
				letterButton.setVisibility(View.VISIBLE);
				message.setVisibility(View.VISIBLE);
				message.setText("online game from "+gamereceive.get(0).name);
			}
		}
	}
	public boolean isSignin(){
		SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
		boolean signin = userinfo.getBoolean("userSignin",false);
		name = userinfo.getString("userName", "The first time");
		uid = userinfo.getString("userID", "0");
		return signin;
		
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_MENU, 0, "Main Menu").setIcon(R.drawable.menu_s);
		menu.add(0, MENU_HOME, 0, "Groups").setIcon(R.drawable.groups_s);
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
			itemintent.setClass(Browsetab.this, MainInterface.class);
			startActivity(itemintent);
			return true;
		case MENU_HOME:
			this.finish();
			itemintent.setClass(Browsetab.this, groups.class);
			startActivity(itemintent);
			return true;
		case MENU_SERIES:
			this.finish();
			itemintent.setClass(Browsetab.this, series.class);
			startActivity(itemintent);
			return true;
		case MENU_SCHEDULE:
			this.finish();
			itemintent.setClass(Browsetab.this, MyBookmarked.class);
			startActivity(itemintent);
			return true;
		case MENU_LOGIN:
			this.finish();
			itemintent.setClass(Browsetab.this, Search.class);
			startActivity(itemintent);
			return true;
		case MENU_RECOMMEND:
			this.finish();
			itemintent.setClass(Browsetab.this, MyRecommend.class);
			startActivity(itemintent);
			return true;
		case MENU_GAME:
			this.finish();
			itemintent.setClass(Browsetab.this, gamehome.class);
			startActivity(itemintent);
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		case R.id.ImageButton01:
			this.finish();
			Intent in = new Intent(Browsetab.this,Search.class);
			startActivity(in);
			break;
		default:
			break;
		}
	}

}
