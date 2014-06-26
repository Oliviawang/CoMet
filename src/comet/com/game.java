package comet.com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import phpAPI.getGameTopic;
import phpAPI.getOnlineScore;
import phpAPI.getfriend;
import phpAPI.user2getOnline;
import util.DBAdapter;
import util.Onlinefriend;
import util.Score;
import util.game2;
import util.game2receive;
import util.onlinegameTopic;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.SlidingDrawer;
import android.widget.TextView;

public class game extends Activity implements OnClickListener {

	private Handler mHandler = new Handler();
	private SlidingDrawer sd;
	private ListView friendView, listView;
	private ImageButton bookmark,friend,letterButton,refresh, help, search;
	// private ImageButton email;
	// private ImageButton share;
	// private RatingBar rating;
	private ArrayList<game2receive> gamereceive;
	private TextView scoreTextView, score,message,scoreTextView1,totalscore;
	private TextView comment;
	private EditText commenTextView;
	private Button pushButton;
	private ListAdapter adapter;
	private MyAdapter adapter2;
	private DBAdapter db;
	private ProgressBar pb;

	private TextView navi, head;
	private List<Boolean> checkBoxesStatus1, checkBoxesStatus2;
	int listsize, friendsize;
	// arraylist friend
	private ArrayList<Onlinefriend> onlinelist;
	private ArrayList<onlinegameTopic> onlinegameTopics;
	private Onlinefriend onlinefriends;
	private Score s;
private  Dialog viewDialog;
	HashMap<Integer, Boolean> isSelected;
	private ViewHolder vh1;
	boolean result;
	// private getfriend friendlistGetfriend;
	// String
	private String name, uid, server_return;

	// MENU
	private final int MENU_MENU = Menu.FIRST;
	private final int MENU_HOME = Menu.FIRST + 1;
	private final int MENU_SERIES = Menu.FIRST + 2;
	private final int MENU_GROUP = Menu.FIRST + 3;
	private final int MENU_SCHEDULE = Menu.FIRST + 4;
	private final int MENU_LOGIN = Menu.FIRST + 5;
	private final int MENU_CAL = Menu.FIRST + 6;
	private final int MENU_GAME = Menu.FIRST + 7;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.game);
		// textview
		scoreTextView = (TextView) findViewById(R.id.ScoreText);
		score = (TextView) findViewById(R.id.score);

		// rating = (RatingBar) findViewById(R.id.ratingbar1);
		comment = (TextView) findViewById(R.id.comment);
		pushButton = (Button) findViewById(R.id.Push);
		listView = (ListView) findViewById(R.id.listView1);
		listView.setVisibility(View.VISIBLE);
		// FriendList
		
		// init db
		db = new DBAdapter(this);
		// db
		navi = (TextView) findViewById(R.id.navi);
		navi.setVisibility(View.VISIBLE);
		// status = (TextView) findViewById(R.id.status);
		head = (TextView) findViewById(R.id.TextView01);

		head.setText("OnlineGame");
		 navi.setText("Play topic with online friend");
		// pb
		pb = (ProgressBar) this.findViewById(R.id.widget108);
		// pb.setVisibility(View.GONE);
		  message=(TextView)findViewById(R.id.message);
		    message.setVisibility(View.INVISIBLE);
		   
		    scoreTextView1=(TextView)findViewById(R.id.totalview);
		    totalscore=(TextView)findViewById(R.id.totalscore);
		    totalscore.setVisibility(View.VISIBLE);
		    
		    letterButton=(ImageButton)findViewById(R.id.mail);
		    letterButton.setVisibility(View.INVISIBLE);
		    
		// SlidingDrawer
		
	friend=(ImageButton)findViewById(R.id.friend);
	friend.setVisibility(View.VISIBLE);
	friend.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
 viewDialog = new Dialog(game.this);
			viewDialog.getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
					WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
			viewDialog.setTitle("Choose friend:");
			LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View dialogView = li.inflate(R.layout.list, null);
			viewDialog.setContentView(dialogView);
			viewDialog.show();
			friendView=(ListView)dialogView.findViewById(R.id.FriendList);
			initViews1();
			
		}
	});
	
		
		pushButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				phpAPI.pushOnlineGame pGame = new phpAPI.pushOnlineGame();
				List<Boolean> checkBoxesStatus2 = adapter.getCheckBoxesStatus();
				List<Boolean> checkBoxesStatus1 = adapter2
						.getCheckBoxesStatus();
				db.open();
				for (int j = 0; j < friendsize; j++) {
					for (int i = 0; i < listsize; i++) {
						if (checkBoxesStatus1.get(i).booleanValue() == true
								&& checkBoxesStatus2.get(j).booleanValue() == true) {
							// insert local database
							db.insertGame2(uid, onlinelist.get(j).userID,
									onlinegameTopics.get(i).id,
									System.currentTimeMillis());
							// upload to server
							pGame.uploadServerData(uid,
									onlinelist.get(j).userID,
									onlinegameTopics.get(i).id,
									System.currentTimeMillis());
						}
					}
				}
				db.close();
				// insert success message
				AlertDialog.Builder dialog = new AlertDialog.Builder(game.this);
				dialog.setTitle("Push sucessfully!");
				dialog.setMessage("Wait for get points from your friend!");
				dialog.show();

			}
		});
		if (isSignin()) {
			// showDialog("Loading data, please wait...");
			new OnlineFriendLoad().execute();
			new OnlineGameLoad().execute();
			initViews();
		} else {
			new AlertDialog.Builder(game.this)
					.setMessage(
							"Recommendation onlinegame requires login, do you want to sign in first?")
					.setPositiveButton("yes",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialoginterface, int i) {
									dialoginterface.cancel();
									game.this.finish();
									Intent in = new Intent(game.this,
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
						new OnlineGameLoad().execute();
						new MessageLoad().execute();
						new OnlineScoreLoad().execute();
						}
					else{
						new AlertDialog.Builder(game.this)
						.setMessage("Online Game function requires login, do you want to sign in first?")
						.setPositiveButton("yes", 
							    		 new DialogInterface.OnClickListener(){ 
										@Override
										public void onClick(DialogInterface dialoginterface, int i){ 
											dialoginterface.cancel();
											game.this.finish();	
											Intent in = new Intent(game.this, Signin.class);							
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

	protected boolean pushOnlineGame(String id, String title, String speaker) {
		// TODO Auto-generated method stub

		return true;
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
			itemintent.setClass(game.this, MainInterface.class);
			startActivity(itemintent);
			return true;
		case MENU_HOME:
			this.finish();
			itemintent.setClass(game.this, Browsetab.class);
			startActivity(itemintent);
			return true;
		case MENU_SERIES:
			this.finish();
			itemintent.setClass(game.this, series.class);
			startActivity(itemintent);
			return true;
		case MENU_GROUP:
			this.finish();
			itemintent.setClass(game.this, groups.class);
			startActivity(itemintent);
			return true;
		case MENU_SCHEDULE:
			this.finish();
			itemintent.setClass(game.this, MyBookmarked.class);
			startActivity(itemintent);
			return true;
		case MENU_LOGIN:
			this.finish();
			itemintent.setClass(game.this, Search.class);
			startActivity(itemintent);
			return true;
		case MENU_CAL:
			this.finish();
			itemintent.setClass(game.this, calendar.class);
			startActivity(itemintent);
			return true;
		case MENU_GAME:
			this.finish();
			itemintent.setClass(game.this, gameTab.class);
			startActivity(itemintent);
			return true;
		}
		return false;

	}
public void initViews1(){
	
	onlinelist = new ArrayList<Onlinefriend>();
	new OnlineFriendLoad().execute();
	onlinelist = getonlineFromDB(uid);

	if (onlinelist.size() != 0) {
		// status = (TextView) findViewById(R.id.status);
		// status.setVisibility(View.GONE);
		friendView.setVisibility(View.VISIBLE);
		adapter = new ListAdapter(onlinelist);
		friendView.setAdapter(adapter);
		friendView.setOnItemClickListener(adapter);
		// this.friendView.setAdapter(adapter);
		// this.friendView.setOnItemClickListener(adapter);
	}
	
}
	public void initViews() {
		// online friends
		// online game topics
		onlinegameTopics = new ArrayList<onlinegameTopic>();
		new OnlineGameLoad().execute();
		onlinegameTopics = getonlineTopicFromDB(uid);
Log.i("onlinegames", onlinegameTopics.toString());
		if (onlinegameTopics.size() != 0) {
			// status = (TextView) findViewById(R.id.status);
			// status.setVisibility(View.GONE);
			adapter2 = new MyAdapter(onlinegameTopics);
			this.listView.setAdapter(adapter2);
			this.listView.setOnItemClickListener(adapter2);
			// this.friendView.setAdapter(adapter);
			// this.friendView.setOnItemClickListener(adapter);
			// online score

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
					intent.setClass(game.this,message.class);
					intent.putExtra("title",jsonObject.toString());
					
				     startActivity(intent);
				}
				
			});
		}
		s = new Score();
		new OnlineScoreLoad().execute();
		Log.i("scoe", "s");
		s = getonlineScoreFromDB(uid);
		// Log.i("s", s.toString());

		if (s != null) {
			Float string=s.offlineScore+s.onlineScore;
			Log.i("Totalscore", string.toString());
			totalscore.setText(String.valueOf(string));
			score.setText(String.valueOf(s.onlineScore));
		} else {
			totalscore.setText("0.0");
			score.setText("No Score");
		}
		
	}
	private ArrayList<game2receive> getonlineMessageFromDB(String uid) {
		// TODO Auto-generated method stub
		ArrayList<game2receive> t = new ArrayList<game2receive>();
		db.open();
		t = db.getonlineGamefromUser(uid);
		db.close();
		return t;	
		
	}
	public Score getonlineScoreFromDB(String uid) {
		Score s = new Score();
		db.open();
		s = db.getTotalScore(uid);
		db.close();
		Log.i("score", "DB");
		return s;
	}

	public ArrayList<onlinegameTopic> getonlineTopicFromDB(String uid) {
		ArrayList<onlinegameTopic> l = new ArrayList<onlinegameTopic>();
		db.open();
		l = db.getonlineTopics(uid);
		db.close();
		Log.i("topic", "DB");
		return l;
	}

	public ArrayList<Onlinefriend> getonlineFromDB(String uid) {
		ArrayList<Onlinefriend> l = new ArrayList<Onlinefriend>();
		db.open();
		l = db.getMyOnlineFriend(uid);
		db.close();
		return l;
	}

	public ArrayList<onlinegameTopic> getonlineTopicFromServer(String uid) {
		ArrayList<onlinegameTopic> s = new ArrayList<onlinegameTopic>();
		getGameTopic ggt = new getGameTopic();
		s = ggt.getServerData();
		Log.i("tag", "ggt");
		// later use server status to show online or offline friend
		// server_return = s.status;
		Log.i("server", s.toString());
		return s;

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

	public Score getonlineScoreFromServer(String uid) {
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

	static class ViewHolder {
		TextView username, title, speaker, user0_id, header, col_id, name;
		CheckBox checkbox1, checkbox2;
		// ImageView photo;
	}

	public boolean isSignin() {
		SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
		boolean signin = userinfo.getBoolean("userSignin", false);
		name = userinfo.getString("userName", "The first time");
		uid = userinfo.getString("userID", "0");
		return signin;

	}

	private class MyAdapter extends BaseAdapter implements OnItemClickListener {
		private ArrayList<onlinegameTopic> onlinegameTopics;
		private List<Boolean> checkBoxesStatus1;

		public MyAdapter(ArrayList<onlinegameTopic> onlinegameTopics) {
			this.onlinegameTopics = onlinegameTopics;
			listsize = onlinegameTopics.size();
			checkBoxesStatus1 = new ArrayList<Boolean>(listsize);
			for (int i = 0; i < listsize; i++) {
				checkBoxesStatus1.add(false);
			}

		}

		public List<Boolean> getCheckBoxesStatus() {
			return checkBoxesStatus1;
		}

		public int getCount() {

			return onlinegameTopics.size();

		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return onlinegameTopics.get(position);
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
			Boolean checkBoxStatus = checkBoxesStatus1.get(position);

			// checkBoxesStatus = new
			// ArrayList<Boolean>(onlinegameTopics.size());

			if (convertView == null) {
				LayoutInflater li = getLayoutInflater();
				convertView = li.inflate(R.layout.online_game_list, null);
				vh = new ViewHolder();

				vh.title = (TextView) convertView.findViewById(R.id.title);
				vh.name = (TextView) convertView.findViewById(R.id.name);
				vh.checkbox1 = (CheckBox) convertView
						.findViewById(R.id.checkbox);

				// vh.speaker = (TextView)
				// convertView.findViewById(R.id.speaker);
				// vh.desc = (TextView) convertView.findViewById(R.id.desc);

				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			vh.title.setText(onlinegameTopics.get(position).title);
			vh.name.setText(onlinegameTopics.get(position).name);
			vh.checkbox1.setId(position);
			vh.checkbox1.setChecked(checkBoxStatus);
			vh.checkbox1
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
			// vh.desc.setText(onlinegameTopics.get(position).desc);
			return convertView;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			// TODO Auto-generated method stub
			Intent in = new Intent(game.this, Talktab.class);
			// in.putExtra("title", onlinegameTopics.get(pos).title);
			// in.putExtra("name",onlinegameTopics.get(pos).name);
			in.putExtra("col_id", onlinegameTopics.get(pos).id);
			// in.putExtra("speaker", onlinegameTopics.get(pos).speakername);
			// in.putExtra("desc", onlinegameTopics.get(pos).desc);
			startActivity(in);
		}

	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		List<Boolean> checkBoxesStatus1 = adapter2.getCheckBoxesStatus();
		for (int i = 0; i < listsize; i++) {
			checkBoxesStatus1.set(i, isChecked);
		}
		adapter2.notifyDataSetChanged();
	}

	private class ListAdapter extends BaseAdapter implements
			OnItemClickListener {
		private ArrayList<Onlinefriend> onlinefriends;

		public ListAdapter(ArrayList<Onlinefriend> onlinefriends) {
			this.onlinefriends = onlinefriends;
			friendsize = onlinefriends.size();
			checkBoxesStatus2 = new ArrayList<Boolean>(friendsize);
			for (int i = 0; i < friendsize; i++) {

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
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder vh = null;
			Boolean checkBoxStatus = checkBoxesStatus2.get(position);
			if (convertView == null) {
				LayoutInflater li = getLayoutInflater();
				convertView = li.inflate(R.layout.friendlist, null);
				vh = new ViewHolder();

				vh.username = (TextView) convertView.findViewById(R.id.name);
				vh.checkbox2 = (CheckBox) convertView
						.findViewById(R.id.checkbox);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			vh.username.setText(onlinefriends.get(position).name);

			vh.checkbox2.setId(position);
			vh.checkbox2.setChecked(checkBoxStatus);
			vh.checkbox2
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							checkBoxesStatus2.set(buttonView.getId(), isChecked);
							notifyDataSetChanged();

						}
					});
			return convertView;

		}

		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			List<Boolean> checkBoxesStatus = adapter.getCheckBoxesStatus();
			for (int i = 0; i < friendsize; i++) {
				checkBoxesStatus.set(i, isChecked);
			}
			adapter.notifyDataSetChanged();
		}

		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			// TODO Auto-generated method stub
			/*Intent in = new Intent(game.this, gamehome.class);
			in.putExtra("id", onlinelist.get(pos).userID);
			in.putExtra("name", onlinefriends.get(pos).name);
			startActivity(in);*/
			viewDialog.cancel();
		}

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
	private class MessageLoad extends AsyncTask<Void, Integer, Integer> {

		@Override
		protected Integer doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			if (isConnect(game.this)) {
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

	private class OnlineFriendLoad extends AsyncTask<Void, Integer, Integer> {

		@Override
		protected Integer doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			if (isConnect(game.this)) {
				pb.setVisibility(View.VISIBLE);
				onlinelist = getonlineFromServer(uid);

				try {

					db.open();
					// db.updateToDefaultRecommend();
					db.deleteOnlineFriend1();
					for (int i = 0; i < onlinelist.size(); i++) {

						db.insertMyOnlineFriend(onlinelist.get(i), uid);

						db.updateonlineFriend(onlinelist.get(i), uid);
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
			adapter = new ListAdapter(onlinelist);
			game.this.friendView.setAdapter(adapter);
			game.this.friendView.setOnItemClickListener(adapter);

		}
	}

	private class OnlineGameLoad extends AsyncTask<Void, Integer, Integer> {

		@Override
		protected Integer doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			if (isConnect(game.this)) {
				pb.setVisibility(View.VISIBLE);
				onlinegameTopics = getonlineTopicFromServer(uid);

				try {

					db.open();
					// db.updateToDefaultRecommend();
					db.deleteOnlineGame();
					for (int i = 0; i < onlinegameTopics.size(); i++) {
						db.insertOnlineGame(onlinegameTopics.get(i), uid);
						Log.i("insert1", "1");
						db.updateOnlineGame(onlinegameTopics.get(i), uid);
						Log.i("update1", "update1");
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
			adapter2 = new MyAdapter(onlinegameTopics);
			game.this.listView.setAdapter(adapter2);
			game.this.listView.setOnItemClickListener(adapter2);

		}
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ImageButton01:
			this.finish();
			Intent in = new Intent(game.this, Search.class);
			startActivity(in);
			break;
		default:
			break;
		}
	}

	private class OnlineScoreLoad extends AsyncTask<Void, Integer, Integer> {

		@Override
		protected Integer doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			if (isConnect(game.this)) {
				pb.setVisibility(View.VISIBLE);
				s = getonlineScoreFromServer(uid);
				Log.i("fromserver", String.valueOf(s.onlineScore));
				try {

					db.open();
					// db.updateToDefaultRecommend();
					// db.deleteOnlineFriend1();
					Score score = new Score();
					score = getonlineScoreFromDB(uid);
					if (score == null) {
						db.insertOnlineScore1(uid);
						db.updateTotalScore(s, uid);
					} else {
						db.updateOnlineScore(s, uid);
					}

					Log.i("insertonline", "insertScore");
					// lack a update
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
			score.setText(String.valueOf(s.onlineScore));

		}
	}

}
