package comet.com;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import phpAPI.getGameTopic;
import phpAPI.getOnlineScore;
import phpAPI.getfriend;
import phpAPI.user2getOnline;

import util.Caching;
import util.DBAdapter;
import util.Onlinefriend;
import util.Score;
import util.Talk;
import util.TalkParser;
import util.game2receive;
import util.onlinegameTopic;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.drm.DrmStore.Playback;
import android.drm.DrmStore.RightsStatus;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;

public class gamehome extends Activity implements OnClickListener{

	private ListView lv,list;
	private ListAdapter adapter;
	private MyAdapter adapter2;
	private ArrayList<Talk> recommendedTalks;
	private String[] title;
	private ArrayList<game2receive> gamereceive;
	private ProgressDialog pd;
	private ProgressBar pb;
	private ImageButton refresh, help, search;
	private DBAdapter db;
	private String name, uid, server_return,user1name;
	private TextView navi, head,message,scoreTextView,totalscore;
	private String id;
	private Intent intent; 
	private Score s;
	private ImageButton  gameImageButton,letterButton;
	  private List<Boolean> checkBoxesStatus;
	     int listsize;
	private final int MENU_MENU = Menu.FIRST;
	private final int MENU_HOME = Menu.FIRST + 1;
	private final int MENU_SERIES = Menu.FIRST + 2;
	private final int MENU_GROUP = Menu.FIRST + 3;
	private final int MENU_SCHEDULE = Menu.FIRST + 4;
	private final int MENU_LOGIN = Menu.FIRST+5;
	private final int MENU_CAL = Menu.FIRST+6;
	private final int MENU_GAME=Menu.FIRST+7;
public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN);
	
	setContentView(R.layout.gamehome);
	Bundle b = getIntent().getExtras();
	if (b != null) {
		id = b.getString("id");
	}  
	
	//init views
	navi = (TextView) findViewById(R.id.navi);
	navi.setVisibility(View.VISIBLE);
	//status = (TextView) findViewById(R.id.status);
	head = (TextView) findViewById(R.id.TextView01);
    message=(TextView)findViewById(R.id.message);
    message.setVisibility(View.INVISIBLE);
   
    scoreTextView=(TextView)findViewById(R.id.totalview);
    totalscore=(TextView)findViewById(R.id.totalscore);
    totalscore.setVisibility(View.VISIBLE);
    
    letterButton=(ImageButton)findViewById(R.id.mail);
    letterButton.setVisibility(View.INVISIBLE);
    
	head.setText("Recomendation Game");
	navi.setText("Top Recomendation");
	
	pb = (ProgressBar) this.findViewById(R.id.widget108);
	pb.setVisibility(View.GONE);
	//init DB
	db = new DBAdapter(this);
	// get online game message
	
	
	lv = (ListView) findViewById(R.id.ListView01);
    list=(ListView)findViewById(R.id.messagelist);
	
	if(isSignin()){
		//showDialog("Loading data, please wait...");
		//new RecommendTalkLoad().execute();	
		initViews();
		}
	else{
		new AlertDialog.Builder(gamehome.this)
		.setMessage("Game function requires login, do you want to sign in first?")
		.setPositiveButton("yes", 
			    		 new DialogInterface.OnClickListener(){ 
						@Override
						public void onClick(DialogInterface dialoginterface, int i){ 
							dialoginterface.cancel();
							gamehome.this.finish();	
							Intent in = new Intent(gamehome.this, Signin.class);							
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
					new RecommendTalkLoad().execute();
					new MessageLoad().execute();
					new TotalScoreLoad().execute();
					}
				else{
					new AlertDialog.Builder(gamehome.this)
					.setMessage("Game home function requires login, do you want to sign in first?")
					.setPositiveButton("yes", 
						    		 new DialogInterface.OnClickListener(){ 
									@Override
									public void onClick(DialogInterface dialoginterface, int i){ 
										dialoginterface.cancel();
										gamehome.this.finish();	
										Intent in = new Intent(gamehome.this, Signin.class);							
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
	
	search = (ImageButton) this.findViewById(R.id.ImageButton01);
	search.setOnClickListener(this);
	
	gameImageButton=(ImageButton)findViewById(R.id.gameimage);
	gameImageButton.setOnClickListener(new OnClickListener() {
	
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			gamehome.this.finish();
			Intent in = new Intent(gamehome.this, mygamerec.class);			
			startActivity(in);
			
		}
	});
	
			
}
public boolean onCreateOptionsMenu(Menu menu) {
	menu.add(0, MENU_MENU, 0, "Main Menu").setIcon(R.drawable.menu_s);
	menu.add(0, MENU_HOME, 0, "Browse talk").setIcon(R.drawable.home_s);
	menu.add(0, MENU_SERIES, 0, "Series").setIcon(R.drawable.series_s);
	menu.add(0, MENU_GROUP, 0, "Group").setIcon(R.drawable.groups_s);
	menu.add(0, MENU_SCHEDULE, 0, "Bookmarked").setIcon(R.drawable.schedule_s);
	menu.add(0, MENU_LOGIN, 0, "Search").setIcon(R.drawable.search_s);
	menu.add(0, MENU_GAME, 0, "Game").setIcon(R.drawable.game);
	return true;
}

public boolean onOptionsItemSelected(MenuItem item) {
	Intent itemintent = new Intent();
	switch (item.getItemId()) {
	case MENU_MENU:
		this.finish();
		itemintent.setClass(gamehome.this, MainInterface.class);
		startActivity(itemintent);
		return true;
	case MENU_HOME:
		this.finish();
		itemintent.setClass(gamehome.this, Browsetab.class);
		startActivity(itemintent);
		return true;
	case MENU_SERIES:
		this.finish();
		itemintent.setClass(gamehome.this, series.class);
		startActivity(itemintent);
		return true;
	case MENU_GROUP:
		this.finish();
		itemintent.setClass(gamehome.this, groups.class);
		startActivity(itemintent);
		return true;
	case MENU_SCHEDULE:
		this.finish();
		itemintent.setClass(gamehome.this, MyBookmarked.class);
		startActivity(itemintent);
		return true;
	case MENU_LOGIN:
		this.finish();
		itemintent.setClass(gamehome.this, Search.class);
		startActivity(itemintent);
		return true;
	case MENU_CAL:
		this.finish();
		itemintent.setClass(gamehome.this, calendar.class);
		startActivity(itemintent);
		return true;
	case MENU_GAME:
		this.finish();
		itemintent.setClass(gamehome.this, gameTab.class);
		startActivity(itemintent);
		return true;
	}
	return false;
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
				intent.setClass(gamehome.this,message.class);
				intent.putExtra("title",jsonObject.toString());
				
			     startActivity(intent);
			}
			
		});
	}
	//
	recommendedTalks = new ArrayList<Talk>();
	
	recommendedTalks = getRecommendedTalksFromDB(uid);
	
	if(recommendedTalks.size() != 0){
		//status = (TextView) findViewById(R.id.status);
		//status.setVisibility(View.GONE);
		adapter = new ListAdapter(recommendedTalks);
		this.lv.setAdapter(adapter);
		this.lv.setOnItemClickListener(adapter);
	}
	new RecommendTalkLoad().execute();
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
public ArrayList<Talk> getRecommendedTalksFromDB(String userid){
	ArrayList<Talk> t = new ArrayList<Talk>();
	db.open();
	t = db.getMyRecommendedTalks(userid);
	db.close();
	return t;
}
public ArrayList<Talk> getRecommendedTalksFromServer(String userid){
	ArrayList<Talk> t =new ArrayList<Talk>();
	TalkParser tp = new TalkParser ();
	t = tp.getRecommendTalks(userid);
	server_return = tp.status;
	
	return t;
}
public String getRecommendedStatus(String id){
	db.open();
	String status = db.getTalkRecommendedStatus(id);
	db.close();
	return status;
}
static class ViewHolder {
	TextView title,time,location,author,header,bookmark,view,email;
	TextView gameTitle;
	ImageView photo;
}
public boolean isSignin(){
	SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
	boolean signin = userinfo.getBoolean("userSignin",false);
	name = userinfo.getString("userName", "The first time");
	uid = userinfo.getString("userID", "0");
	return signin;
	
}

private class ListAdapter extends BaseAdapter implements OnItemClickListener{

	private ArrayList<Talk> tlist;
	public ListAdapter(ArrayList<Talk> recent){
		this.tlist = recent;
	}
	public List<Boolean> getCheckBoxesStatus() {  
        return checkBoxesStatus;  
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
			convertView = li.inflate(R.layout.talks_list_item, null);
			vh = new ViewHolder();
			
			vh.time = (TextView) convertView.findViewById(R.id.time);
			vh.title = (TextView) convertView.findViewById(R.id.title);				
			vh.author = (TextView) convertView.findViewById(R.id.author);
			vh.location = (TextView) convertView.findViewById(R.id.location);
			vh.header = (TextView) convertView.findViewById(R.id.header);
			vh.bookmark = (TextView) convertView.findViewById(R.id.bookmark);
			vh.view = (TextView) convertView.findViewById(R.id.view);
			vh.email = (TextView) convertView.findViewById(R.id.email);
			vh.photo = (ImageView) convertView.findViewById(R.id.photo);
			convertView.setTag(vh);
		/*} else {
			vh = (ViewHolder) convertView.getTag();
		}*/
			vh.time.setText(tlist.get(position).date+" "+tlist.get(position).begintime+"-"+tlist.get(position).endtime);
			vh.author.setText(Html.fromHtml("<font color=\"#808080\">BY: </font>"+tlist.get(position).speaker));
			
			if(getRecommendedStatus(tlist.get(position).id).compareTo("yes") == 0)
				vh.title.setText(Html.fromHtml(tlist.get(position).title+"<font color=\"#ff0000\"> &lt;Recommended&gt; </font>"));
			else
				vh.title.setText(tlist.get(position).title);
			
			vh.location.setText(Html.fromHtml("<font color=\"#808080\">AT: </font>"+tlist.get(position).location));
			
			if(tlist.get(position).bookmarkno.compareTo("0") == 0)
				vh.bookmark.setVisibility(View.GONE);
			else
				vh.bookmark.setText(tlist.get(position).bookmarkno);
			
			if(tlist.get(position).viewno.compareTo("0") == 0)
				vh.view.setVisibility(View.GONE);
			else
				vh.view.setText(tlist.get(position).viewno);
			
			if(tlist.get(position).emailno.compareTo("0") == 0)
				vh.email.setVisibility(View.GONE);
			else
				vh.email.setText(tlist.get(position).emailno);
			
			final ImageView iv = vh.photo;
			final String picURL= tlist.get(position).picurl;
			if(picURL.compareTo("") != 0){
			Bitmap bm = Caching.loadPic(getApplicationContext(), picURL );	//load from local cache
			if(bm != null)					
				iv.setImageBitmap(bm);
			else {
				Thread t = new Thread(){
					public void run(){			
	    				URL url;	// load from network
	    		        try {
	    		            url = new URL(picURL);
	    		            URLConnection conn = url.openConnection();
	    		            conn.connect();
	    		            InputStream is = conn.getInputStream();
	    		            final Bitmap bm2 = BitmapFactory.decodeStream(is);
	    		            iv.post(new Runnable(){public void run(){ iv.setImageBitmap(bm2);}});
	    		            Caching.storePic(getApplicationContext(), picURL, bm2);	// store it
	    		        } catch (Exception e) {
	    		            e.printStackTrace();
	    		        }	
					}
				};
				t.start();
			}
			}
			
		int idx = position - 1;   
		String previewChar;
		if(idx >=0){
		previewChar = tlist.get(idx).date;
		}
		else
		previewChar = " "; 
		
	    String currentChar = tlist.get(position).date; 
	 
	    if (currentChar.compareTo(previewChar) != 0) {   
	        vh.header.setVisibility(View.VISIBLE);   
	        vh.header.setText(currentChar.toString());   
	    } else {   
	       
	        vh.header.setVisibility(View.GONE);   
	    }   
		return convertView;
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
			long arg3) {
		// TODO Auto-generated method stub
			Intent in = new Intent(gamehome.this, Talktab.class);
			in.putExtra("id", tlist.get(pos).id);				
			startActivity(in);
	}
	
}
public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){  
    List<Boolean> checkBoxesStatus = adapter.getCheckBoxesStatus();  
    for(int i = 0;i<listsize;i++){  
            checkBoxesStatus.set(i, isChecked);  
        }  
        adapter.notifyDataSetChanged();  
}  



private class TotalScoreLoad extends AsyncTask<Void, Integer, Integer> {

	@Override
	protected Integer doInBackground(Void... arg0) {
		// TODO Auto-generated method stub

		if (isConnect(gamehome.this)) {
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

private class MessageLoad extends AsyncTask<Void, Integer, Integer> {

	@Override
	protected Integer doInBackground(Void... arg0) {
		// TODO Auto-generated method stub

		if (isConnect(gamehome.this)) {
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
public void showDialog(String s){
	 pd = ProgressDialog.show(this, "Synchronization", s,true, false);
 }
 public void dismissDialog(){
	 pd.dismiss();
 }
 
 
 public class MyAdapter extends BaseAdapter implements OnItemClickListener {
		private ArrayList<game2receive> gameTopics;
		//private List<Boolean> checkBoxesStatus1;

		public MyAdapter(ArrayList<game2receive> onlinegameTopics) {
			this.gameTopics = onlinegameTopics;
			listsize = onlinegameTopics.size();
		/*	checkBoxesStatus1 = new ArrayList<Boolean>(listsize);
			for (int i = 0; i < listsize; i++) {
				checkBoxesStatus1.add(false);
			}
	*/
		}

	/*	public List<Boolean> getCheckBoxesStatus() {
			return checkBoxesStatus1;
		}
	*/
		public int getCount() {

			return gameTopics.size();

		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return gameTopics.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder vh = null;
			//Boolean checkBoxStatus = checkBoxesStatus1.get(position);

			// checkBoxesStatus = new
			// ArrayList<Boolean>(onlinegameTopics.size());

			if (convertView == null) {
				LayoutInflater li = getLayoutInflater();
				convertView = li.inflate(R.layout.online_game_list, null);
				vh = new ViewHolder();

				vh.gameTitle = (TextView) convertView.findViewById(R.id.title);
			//	vh.name = (TextView) convertView.findViewById(R.id.name);
				//vh.checkbox1 = (CheckBox) convertView
						//.findViewById(R.id.checkbox);

				// vh.speaker = (TextView)
				// convertView.findViewById(R.id.speaker);
				// vh.desc = (TextView) convertView.findViewById(R.id.desc);

				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			vh.gameTitle.setText(gameTopics.get(position).title);
			//vh.checkbox1.setId(position);
			//vh.checkbox1.setChecked(checkBoxStatus);
			/*vh.checkbox1
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							checkBoxesStatus1.set(buttonView.getId(), isChecked);
							notifyDataSetChanged();
						}
					});

			// vh.speaker.setText(onlinegameTopics.get(position).speakername);
			// vh.desc.setText(onlinegameTopics.get(position).desc);*/
			return convertView;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			// TODO Auto-generated method stub
			Intent in = new Intent(gamehome.this, MainInterface.class);
			// in.putExtra("title", onlinegameTopics.get(pos).title);
			// in.putExtra("name",onlinegameTopics.get(pos).name);
			//in.putExtra("col_id", onlinegameTopics.get(pos).colID);
			// in.putExtra("speaker", onlinegameTopics.get(pos).speakername);
			// in.putExtra("desc", onlinegameTopics.get(pos).desc);
			startActivity(in);
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
 

	private class RecommendTalkLoad extends AsyncTask<Void,Integer,Integer>{
		
		@Override
		protected Integer doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			if(isConnect(gamehome.this)){
				publishProgress(4);
			recommendedTalks = getRecommendedTalksFromServer(uid);
			
			if(recommendedTalks.size() == 0 && server_return.compareTo("ok") == 0){
			publishProgress(0);
			}
			else if(recommendedTalks.size() == 0 && server_return.compareTo("ok") != 0){
			publishProgress(2);
			}
			else{
			try{
				db.open();
				db.updateToDefaultRecommend();
				db.daleteMyRecommended(uid);
				for(int i =0; i<recommendedTalks.size(); i++){
					long error = db.insertRecommendedTalk(recommendedTalks.get(i));
					db.insertMyRecommended(recommendedTalks.get(i), uid);
					if(error == -1)
						db.updateRecommendTalk(recommendedTalks.get(i));
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
				navi.setText("No recommended talks, please try to schedule more");
				navi.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lightbulb, 0, 0, 0);
				
				break;
			case 1:
				//dismissDialog();
				//status = (TextView) findViewById(R.id.status);
				//status.setVisibility(View.GONE);
				pb.setVisibility(View.GONE);
				adapter = new ListAdapter(recommendedTalks);
				gamehome.this.lv.setAdapter(adapter);
				gamehome.this.lv.setOnItemClickListener(adapter);
				break;
			case 2:
				//dismissDialog();
				//status = (TextView) findViewById(R.id.status);
				//status.setVisibility(View.VISIBLE);
				//navi.setText("Fail to connect to server, please check your internet connection and try again.");
				//navi.setCompoundDrawablesWithIntrinsicBounds(R.drawable.server_error, 0, 0, 0);
				pb.setVisibility(View.GONE);
				Toast.makeText(getApplicationContext(), "Fail to connect to server, please check your internet connection and try again.",
					      Toast.LENGTH_SHORT).show();
				break;
			case 3:
				pb.setVisibility(View.GONE);
				Toast.makeText(getApplicationContext(), "Fail t" +
						"" +
						"o connect to server, please check your internet connection and try again.",
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
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		case R.id.ImageButton01:
			this.finish();
			Intent in = new Intent(gamehome.this,Search.class);
			startActivity(in);
			break;
		default:
			break;
		}
	}

}

