package comet.com;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import phpAPI.InsertGame1Topic;
import phpAPI.InsertOnlineScore;
import phpAPI.getOnlineScore;
import phpAPI.getfriend;
import phpAPI.updateGame1Score;
import phpAPI.updateOfflineScore;
import phpAPI.user2getOnline;

import comet.com.R;

import util.Caching;
import util.DBAdapter;
import util.Onlinefriend;
import util.Score;
import util.Talk;
import util.TalkParser;
import util.game2;
import util.game2receive;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;

public class Game1RecFriend extends Activity implements OnClickListener{
	private ListView game1friendListView;
	private ArrayList<Onlinefriend> game1friend;
	private ListAdapter adapter;
	private ProgressDialog pd;
	private ProgressBar pb;
	private DBAdapter db;
	private String name, uid,id,friendid,friendname;
	private TextView navi, head,message,scoreTextView,totalscore;
	private Score s;
	private ArrayList<game2receive> gamereceive;
	private List<Boolean> checkBoxesStatus1, checkBoxesStatus2;
	private ImageButton letterButton,refresh, help, search;
	 int listsize;
	 private  ViewHolder vh ;
	 private updateOfflineScore uogsOfflineScore;
		private InsertOnlineScore ios;
		private updateGame1Score ups;
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
		
		setContentView(R.layout.guess3); 
		Bundle b = getIntent().getExtras();
		if (b != null) {
			//transfer user2ID
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
	    
		head.setText("Go On Guess Game!");
		navi.setText("Guess who recommend topic to you");
		
		pb = (ProgressBar) this.findViewById(R.id.widget108);
		pb.setVisibility(View.GONE);
		//init DB
		db = new DBAdapter(this);
	game1friendListView=(ListView)findViewById(R.id.game1friendlist);
	game1friendListView.setVisibility(View.VISIBLE);
	if (isSignin()) {
		// showDialog("Loading data, please wait...");
		//new OnlineFriendLoad().execute();
		initViews();
	} else {
		new AlertDialog.Builder(Game1RecFriend.this)
				.setMessage(
						"Guess Game requires login, do you want to sign in first?")
				.setPositiveButton("yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialoginterface, int i) {
								dialoginterface.cancel();
								Game1RecFriend.this.finish();
								Intent in = new Intent(Game1RecFriend.this,
										Signin.class);
								startActivity(in);
							}
						})
				.setNegativeButton("no",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
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
					new AlertDialog.Builder(Game1RecFriend.this)
					.setMessage("Game home function requires login, do you want to sign in first?")
					.setPositiveButton("yes", 
						    		 new DialogInterface.OnClickListener(){ 
									@Override
									public void onClick(DialogInterface dialoginterface, int i){ 
										dialoginterface.cancel();
										Game1RecFriend.this.finish();	
										Intent in = new Intent(Game1RecFriend.this, Signin.class);							
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
}
	public void initViews() {
		game1friend = new ArrayList<Onlinefriend>();
		new OnlineFriendLoad().execute();
		game1friend = getonlineFromDB(uid);

		if (game1friend.size() != 0) {
			
			adapter = new ListAdapter(game1friend);
			this.game1friendListView.setAdapter(adapter);
			this.game1friendListView.setOnItemClickListener(adapter);
			
		}
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
					intent.setClass(Game1RecFriend.this,message.class);
					intent.putExtra("title",jsonObject.toString());
					
				     startActivity(intent);
				}
				
			});
		}
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
	public ArrayList<Onlinefriend> getonlineFromDB(String uid) {
		ArrayList<Onlinefriend> l = new ArrayList<Onlinefriend>();
		db.open();
		l = db.getMyOnlineFriend(uid);
		Log.i("from DB", l.toString());
		db.close();
		return l;
	}
	public ArrayList<Onlinefriend> getonlineFromServer(String uid) {
		ArrayList<Onlinefriend> s = new ArrayList<Onlinefriend>();
		getfriend gf = new getfriend();
		s = gf.getServerData(uid);
		Log.i("tag", "gf");
		// later use server status to show online or offline friend
		// server_return = s.status;
		return s;
	}
	static class ViewHolder {
		TextView username, title, speaker, user0_id, header, col_id, name;
		CheckBox checkbox1, checkbox2;
		CheckedTextView  checkbox;
		// ImageView photo;
	}
	
	private class ListAdapter extends BaseAdapter implements
	OnItemClickListener {
private ArrayList<Onlinefriend> onlinefriends;

public ListAdapter(ArrayList<Onlinefriend> onlinefriends) {
	this.onlinefriends = onlinefriends;
	listsize = onlinefriends.size();
	checkBoxesStatus2 = new ArrayList<Boolean>(listsize);
	for (int i = 0; i < listsize; i++) {

		checkBoxesStatus2.add(false);
	}

}

public List<Boolean> getCheckBoxesStatus() {
	// TODO Auto-generated method stub
	return checkBoxesStatus2;
}

@Override
public int getCount() {
	// TODO Auto-generated method stub
	return onlinefriends.size();
}

@Override
public Object getItem(int position) {
	// TODO Auto-generated method stub
	return onlinefriends.get(position);
}

@Override
public long getItemId(int position) {
	// TODO Auto-generated method stub
	return position;
}

@Override
public View getView(final int position, View convertView, ViewGroup parent) {
	// TODO Auto-generated method stub
	
	Boolean checkBoxStatus = checkBoxesStatus2.get(position);
	if (convertView == null) {
		LayoutInflater li = getLayoutInflater();
		convertView = li.inflate(R.layout.game1friend, null);
		vh = new ViewHolder();

	//	vh.username = (TextView) convertView.findViewById(R.id.name);
		//vh.checkbox2 = (CheckBox) convertView	.findViewById(R.id.checkbox);
		vh.checkbox=(CheckedTextView)convertView.findViewById(R.id.checkText);
		convertView.setTag(vh);
	} else {
		vh = (ViewHolder) convertView.getTag();
	}
	vh.checkbox.setText(onlinefriends.get(position).name);

	vh.checkbox.setId(position);
	vh.checkbox.setChecked(checkBoxStatus);
	
		
	return convertView;

}

public void onCheckedChanged(CompoundButton buttonView,
		boolean isChecked) {
	List<Boolean> checkBoxesStatus = adapter.getCheckBoxesStatus();
	for (int i = 0; i < listsize; i++) {
		checkBoxesStatus.set(i, isChecked);
	}
	adapter.notifyDataSetChanged();
}

public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
		long arg3) {
	// TODO Auto-generated method stub
	// upload to server and db
	InsertGame1Topic igtGame1Topic=new InsertGame1Topic();
	igtGame1Topic.uploadServerData(uid, game1friend.get(pos).userID, id,System.currentTimeMillis(),"0");
	//dialog
	 AlertDialog.Builder dialog = new AlertDialog.Builder(Game1RecFriend.this);
	 dialog.setTitle("Success!");
	 dialog.setMessage("You push one your favarioute topic to: "+game1friend.get(pos).name);
	 dialog.show();
	 //jump to game home
	 Intent intent=new Intent();
	 intent.setClass(Game1RecFriend.this, gameTab.class);
	 startActivity(intent);
	
}

}
	private class TotalScoreLoad extends AsyncTask<Void, Integer, Integer> {

		@Override
		protected Integer doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			if (isConnect(Game1RecFriend.this)) {
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

			if (isConnect(Game1RecFriend.this)) {
				//pb.setVisibility(View.VISIBLE);
				gamereceive =getMessageFromServer(uid);

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
	 public ArrayList<game2receive> getMessageFromServer(String uid) {
			ArrayList<game2receive> s = new ArrayList<game2receive>();
			user2getOnline gf = new user2getOnline();
			s = gf.getServerData(uid);
			Log.i("tag", "gr");
			// later use server status to show online or offline friend
			// server_return = s.status;
			return s;
		}
	private class OnlineFriendLoad extends AsyncTask<Void, Integer, Integer> {

		@Override
		protected Integer doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			if (isConnect(Game1RecFriend.this)) {
				pb.setVisibility(View.VISIBLE);
				game1friend = getonlineFromServer(uid);
Log.i("from server", game1friend.toString());
				try {

					db.open();
					// db.updateToDefaultRecommend();
					db.deleteOnlineFriend1();
					for (int i = 0; i < game1friend.size(); i++) {

						db.insertMyOnlineFriend(game1friend.get(i), uid);
Log.i("insert", uid);
						db.updateonlineFriend(game1friend.get(i), uid);
					}
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
			adapter = new ListAdapter(game1friend);
			Game1RecFriend.this.game1friendListView.setAdapter(adapter);
			Game1RecFriend.this.game1friendListView.setOnItemClickListener(adapter);

		}
	}

	public boolean isSignin() {
		SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
		boolean signin = userinfo.getBoolean("userSignin", false);
		name = userinfo.getString("userName", "The first time");
		uid = userinfo.getString("userID", "0");
		return signin;

	}
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_MENU, 0, "Main Menu").setIcon(R.drawable.menu_s);
		menu.add(0, MENU_HOME, 0, "Browse talk").setIcon(R.drawable.home_s);
		menu.add(0, MENU_SERIES, 0, "Series").setIcon(R.drawable.series_s);
		menu.add(0, MENU_GROUP, 0, "Group").setIcon(R.drawable.groups_s);
		menu.add(0, MENU_SCHEDULE, 0, "Bookmarked").setIcon(
				R.drawable.schedule_s);
		menu.add(0, MENU_LOGIN, 0, "Search").setIcon(R.drawable.search_s);
		menu.add(0, MENU_GAME, 0, "Game").setIcon(R.drawable.game);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Intent itemintent = new Intent();
		switch (item.getItemId()) {
		case MENU_MENU:
			this.finish();
			itemintent.setClass(Game1RecFriend.this, MainInterface.class);
			startActivity(itemintent);
			return true;
		case MENU_HOME:
			this.finish();
			itemintent.setClass(Game1RecFriend.this, Browsetab.class);
			startActivity(itemintent);
			return true;
		case MENU_SERIES:
			this.finish();
			itemintent.setClass(Game1RecFriend.this, series.class);
			startActivity(itemintent);
			return true;
		case MENU_GROUP:
			this.finish();
			itemintent.setClass(Game1RecFriend.this, groups.class);
			startActivity(itemintent);
			return true;
		case MENU_SCHEDULE:
			this.finish();
			itemintent.setClass(Game1RecFriend.this, MyBookmarked.class);
			startActivity(itemintent);
			return true;
		case MENU_LOGIN:
			this.finish();
			itemintent.setClass(Game1RecFriend.this, Search.class);
			startActivity(itemintent);
			return true;
		case MENU_CAL:
			this.finish();
			itemintent.setClass(Game1RecFriend.this, calendar.class);
			startActivity(itemintent);
			return true;
		case MENU_GAME:
			this.finish();
			itemintent.setClass(Game1RecFriend.this, gameTab.class);
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
			Intent in = new Intent(Game1RecFriend.this,Search.class);
			startActivity(in);
			break;
		default:
			break;
		
	}
		
	}
}