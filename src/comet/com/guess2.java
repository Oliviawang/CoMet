package comet.com;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import phpAPI.InsertOnlineScore;
import phpAPI.getGame1Guess;
import phpAPI.getOnlineScore;
import phpAPI.updateGame1Score;
import phpAPI.updateOfflineScore;
import phpAPI.user2getOnline;

import comet.com.R;




import util.BookmarkAction;
import util.Caching;
import util.DBAdapter;
import util.EmailAction;
import util.Onlinefriend;
import util.RatingAction;
import util.Score;
import util.Talk;
import util.TalkParser;
import util.game1;
import util.game2receive;
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
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TabHost.TabSpec;

public class guess2 extends Activity implements OnClickListener, OnTouchListener{
	private TextView title,speaker,detail;
	private DBAdapter db;
	private ProgressBar pb;
	private TextView navi, head,message,scoreTextView1,totalscore;
	private String name,uid,id,bookmark_action_status,flag,user1ID;
	private ArrayList<game1> game1list;
	private ImageButton bookmark,email,share,search,refresh,rec,letterButton;
	private RatingBar rating;
	private Talk t;
	private BookmarkAction ba;
	private EditText emailtext;
	private ProgressDialog pd;
	private RadioGroup raGroup1;
	private String checkString;
	private RadioButton radio1, radio0;
	private updateGame1Score ups;
	private updateOfflineScore uogsOfflineScore;
	private InsertOnlineScore ios;
	 private Score s;
		private ArrayList<game2receive> gamereceive;
	
	// MENU
	private final int MENU_MENU = Menu.FIRST;
	private final int MENU_HOME = Menu.FIRST + 1;
	private final int MENU_SERIES = Menu.FIRST + 2;
	private final int MENU_GROUP = Menu.FIRST + 3;
	private final int MENU_SCHEDULE = Menu.FIRST + 4;
	private final int MENU_LOGIN = Menu.FIRST + 5;
	private final int MENU_CAL = Menu.FIRST + 6;
	private final int MENU_GAME = Menu.FIRST + 7;
	private Handler mhandler = new Handler(){
		@Override
        public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
				if(bookmark_action_status.compareTo("yes") == 0){
                	updateBookedTalk(t,"yes");
                	updateMyBookmarkedTalks(uid, t, "insert");
				}
				else if(bookmark_action_status.compareTo("no") == 0){
	            	updateBookedTalk(t,"no");
	            	updateMyBookmarkedTalks(uid, t, "delete");
				}
				else{
                	Toast.makeText(getApplicationContext(),  
    		                bookmark_action_status,  
    		                Toast.LENGTH_SHORT)  
    		             .show();
                		//reverse the update action, since fail to update
                		if(getBookmarkedStatus(t.id).compareTo("yes") == 0)
                			updateBookedTalk(t,"no");             
	            		else
	            			updateBookedTalk(t,"yes");	              
				}
        }

	};
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.guess2); 
		title=(TextView)findViewById(R.id.guesstitle);
		speaker=(TextView)findViewById(R.id.guessspeaker);
		detail=(TextView)findViewById(R.id.guessdetail);
		
		  message=(TextView)findViewById(R.id.message);
		    message.setVisibility(View.INVISIBLE);
		   
		    scoreTextView1=(TextView)findViewById(R.id.totalview);
		    totalscore=(TextView)findViewById(R.id.totalscore);
		    totalscore.setVisibility(View.VISIBLE);
		    
		    letterButton=(ImageButton)findViewById(R.id.mail);
		    letterButton.setVisibility(View.INVISIBLE);
		    
		 raGroup1=(RadioGroup)findViewById(R.id.radioGroup1);
		 radio1=(RadioButton)findViewById(R.id.radio1);
		 radio0=(RadioButton)findViewById(R.id.radio0);
		 checkString="friend recommend";
	    raGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			
			 if(checkedId==radio1.getId()){ 
				 
				 checkString="system recommend";
				 Log.i("checkstring", "system");
			 }else if(checkedId==radio0.getId()){
				 
				 checkString="friend recommend";
				 Log.i("checkstring", "friend");
			 }
		}
		
		
		
	});
	    rec=(ImageButton)findViewById(R.id.rec);
	    rec.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(guess2.this, guess4.class);
				startActivity(intent);
			}
		});
		// init db
		db = new DBAdapter(this);
		// db
		navi = (TextView) findViewById(R.id.navi);
		navi.setVisibility(View.VISIBLE);
		// status = (TextView) findViewById(R.id.status);
		head = (TextView) findViewById(R.id.TextView01);

		// head.setText("Recommended");
		// navi.setText("Recommended talks for next week");
		// pb
		pb = (ProgressBar) this.findViewById(R.id.widget108);
		bookmark = (ImageButton) findViewById(R.id.bookmark);
		email = (ImageButton) findViewById(R.id.email);
		share = (ImageButton) findViewById(R.id.share);
		rating = (RatingBar) findViewById(R.id.rbar);
		rating.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				 if (event.getAction() == MotionEvent.ACTION_UP) {
			          // TODO perform your action here		
					LayoutInflater inflater = getLayoutInflater();
					View layout = inflater.inflate(R.layout.rating_dialog,(ViewGroup) findViewById(R.id.dialog));
					RatingBar settingrate =(RatingBar) layout.findViewById(R.id.ratingbar);
					settingrate.setOnRatingBarChangeListener(new OnRatingBarChangeListener(){

						@Override
						public void onRatingChanged(RatingBar ratingBar,
								float frate, boolean fromUser) {
							// TODO Auto-generated method stub
							 if (rating.getRating() != frate) {
					             rating.setRating(frate);
					         }
						}
					
					});
					
					new AlertDialog.Builder(guess2.this)
						.setTitle("Rating dialog")
						.setView(layout)
					    .setPositiveButton("rate", new DialogInterface.OnClickListener(){ 
								@Override
								public void onClick(DialogInterface dialoginterface, int i){ 
									dialoginterface.cancel();
									showDialog("adding rating to this talk, please wait ...");
									new RatingTask().execute(uid, id, rating.getRating()+"");
								
								} 
								})
					     .setNegativeButton("cancel",  new DialogInterface.OnClickListener() { 
								public void onClick(DialogInterface dialog, int id) { 
									dialog.cancel(); 	
									} 
									}).show();
				}
			      return true;
			}
		});
		
		refresh = (ImageButton) findViewById(R.id.ImageButton03);
		refresh.setVisibility(View.VISIBLE);
		refresh.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
					if(isSignin()){
						//showDialog("Loading data, please wait...");
						new Game1TopicLoad().execute();	
						new TotalScoreLoad().execute();
						new MessageLoad().execute();
					}
					else{
						new AlertDialog.Builder(guess2.this)
						.setMessage("Guess Game requires login, do you want to sign in first?")
						.setPositiveButton("yes", 
							    		 new DialogInterface.OnClickListener(){ 
										@Override
										public void onClick(DialogInterface dialoginterface, int i){ 
											dialoginterface.cancel();
											guess2.this.finish();	
											Intent in = new Intent(guess2.this, Signin.class);							
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
		//0 stands for friend recommend, 1 stands for system recommend
	ImageButton Button=(ImageButton)findViewById(R.id.next);
	Button.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(flag.equals("0")&&checkString.equals("system recommend")){
				//guess wrong
				 AlertDialog.Builder dialog = new AlertDialog.Builder(guess2.this);
				 dialog.setTitle("Result");
				 dialog.setMessage("Sorry, you guess wrong!");
				 dialog.show();
				//execute asyntask delete guess topic
				 new guessLoad().execute(1) ;
				 Intent intent=new Intent();
				 intent.setClass(guess2.this, gameTab.class);
				 startActivity(intent);
			}else if(flag.equals("1")&&checkString.equals("friend recommend")){
				//guess wrong
				 AlertDialog.Builder dialog = new AlertDialog.Builder(guess2.this);
				 dialog.setTitle("Result");
				 dialog.setMessage("Sorry, you guess wrong!");
				 dialog.show();
					//execute asyntask delete guess topic
				 new guessLoad().execute(2) ;
				 Intent intent=new Intent();
				 intent.setClass(guess2.this, gameTab.class);
				 startActivity(intent);
			}else if(flag.equals("0")&&checkString.equals("friend recommend")){
				//guess correct
				AlertDialog.Builder dialog = new AlertDialog.Builder(guess2.this);
				 dialog.setTitle("Congrualation!");
				 dialog.setMessage("Please guess who recommend to you:");
				 dialog.show();
				 
				 Intent intent=new Intent();
				 intent.setClass(guess2.this, guess3.class);
				 intent.putExtra("uid", user1ID);
				intent.putExtra("id", id );
				 startActivity(intent);
				 //jump to another page
				new guessLoad().execute(3);
			}else if (flag.equals("1")&&checkString.equals("system recommend")) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(guess2.this);
				 dialog.setTitle("Congrualation!");
				 dialog.setMessage("You guess right!");
				 dialog.show();
				 //upload score in game1 and total score
				 new guessLoad().execute(4);
				 new guessLoad().execute(2) ;
				 Intent intent=new Intent();
				 intent.setClass(guess2.this, gameTab.class);
				 startActivity(intent);
			}else if(flag.equals(null)){
				
				Toast.makeText(getApplicationContext(), "there is no topic recommended from your friends", 5000);
			}
			
		}
	});

	
	

	if (isSignin()) {
		
		// showDialog("Loading data, please wait...");
		new Game1TopicLoad().execute();
		initViews();
		
		
	} else {
		new AlertDialog.Builder(guess2.this)
				.setMessage(
						"Guess Recommendation requires login, do you want to sign in first?")
				.setPositiveButton("yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialoginterface, int i) {
								dialoginterface.cancel();
								guess2.this.finish();
								Intent in = new Intent(guess2.this,
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
}

	private class guessLoad extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected Integer doInBackground(Integer... arg0) {
			// TODO Auto-generated method stub

			if (isConnect(guess2.this)) {
				pb.setVisibility(View.VISIBLE);
		//		onlinelist = getonlineFromServer(uid);
				 ups=new updateGame1Score();
				  uogsOfflineScore=new updateOfflineScore();
				  ios=new InsertOnlineScore();
				try {

					db.open();
					switch(arg0[0]){
					case 1:
						
						ups.uploadServerData(uid, id, "-1",System.currentTimeMillis(), flag);
						db.updateGame1Score(uid, user1ID, id, "-1",System.currentTimeMillis(), flag);
						//upload total score
						ios.insertServerData(uid);
						uogsOfflineScore.uploadServerData(uid, -1);
					//upload to db
						db.insertOfflineScore(uid);
						db.updateTotalScore(uid, 0, -1);
						
						break;
					case 2:
						ups.uploadServerData(uid, id, "-1",System.currentTimeMillis(), flag);
						db.updateGame1Score(uid, user1ID, id, "-1",System.currentTimeMillis(), flag);
						//upload total score
						ios.insertServerData(uid);
						uogsOfflineScore.uploadServerData(uid, -1);
					//upload to db
						db.insertOfflineScore(uid);
						db.updateTotalScore(uid, 0, -1);
						break;
					case 3:
						
						
						break;
						
					case 4:
					
						ups.uploadServerData(uid, id, "10",System.currentTimeMillis(), flag);
						db.updateGame1Score(uid, null, id, "10",System.currentTimeMillis(), flag);
						//upload total score
						ios.insertServerData(uid);
						uogsOfflineScore.uploadServerData(uid, 10);
					//upload to db
						db.insertOfflineScore(uid);
						db.updateTotalScore(uid, 0, 10);
						break;
						
						
					default:
						break;
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
			//adapter = new ListAdapter(onlinelist);
			//game.this.friendView.setAdapter(adapter);
			//game.this.friendView.setOnItemClickListener(adapter);

		}
	}
public void initViews() {
	//message load
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
				intent.setClass(guess2.this,message.class);
				intent.putExtra("title",jsonObject.toString());
				
			     startActivity(intent);
			}
			
		});
	}
	game1list=new ArrayList<game1>();
	new Game1TopicLoad().execute();
	game1list = getonlineFromDB(uid);
	//Log.i("game1DB", game1list.get(0).title);

	if (game1list.size() != 0) {
		id=game1list.get(0).colID;
		Log.i("id", id);
		flag=game1list.get(0).flag;
		Log.i("flag", flag);
		Log.i("check", checkString);
		if(flag.equals("0")){
			
			user1ID=game1list.get(0).user1ID;
			Log.i("uid1", user1ID);
		}
		if(game1list.get(0).title!=null)
		{title.setText(game1list.get(0).title);
		title.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent intent=new Intent();
				intent.setClass(guess2.this, TalkDetail.class);
				intent.putExtra("id", id);
				startActivity(intent);
			}
		});
		
		}
		if(game1list.get(0).speaker!=null){
		speaker.setText(game1list.get(0).speaker);
		speaker.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(guess2.this, TalkDetail.class);
				intent.putExtra("id", id);
				startActivity(intent);
				
			}
		});
		}
		Log.i("speaker", game1list.get(0).speaker);
		if(game1list.get(0).detail!=null)
		{detail.setText(game1list.get(0).detail);
		
		detail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(guess2.this, TalkDetail.class);
				intent.putExtra("id", id);
				startActivity(intent);
			}
		});
		}
		
	}
	t = new Talk();
	if(id!=null)
	{t = getTalk(id);
	if(t.bookmarked.compareTo("yes")==0)
		bookmark.setImageResource(R.drawable.yes_schedule);
	else
		bookmark.setImageResource(R.drawable.no_schedule);
	if(t.rating.compareTo("no")!= 0){
		rating.setRating(Float.parseFloat(t.rating));
	}
	else{
		bookmark.setImageResource(R.drawable.lock_schedule);
		email.setImageResource(R.drawable.lock_email_to_friend);
		rating.setEnabled(false);
	}
	bookmark.setOnClickListener(this);
	email.setOnClickListener(this);
	share.setOnClickListener(this);
	rating.setOnTouchListener(this);
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
public ArrayList<game1> getonlineFromDB(String uid) {
	ArrayList<game1> l = new ArrayList<game1>();
	db.open();
	l = db.getGame1Guess(uid);
	Log.i("getDB", l.toString());
	db.close();
	return l;
}
public boolean isSignin() {
	SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
	boolean signin = userinfo.getBoolean("userSignin", false);
	name = userinfo.getString("userName", "The first time");
	uid = userinfo.getString("userID", "0");
	return signin;

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
public ArrayList<game1> getonlineFromServer(String uid) {
	ArrayList<game1> s = new ArrayList<game1>();
	getGame1Guess gf = new getGame1Guess();
	s = gf.getServerData(uid);
	Log.i("tag", "gf");
	// later use server status to show online or offline friend
	// server_return = s.status;
	return s;
}
public ArrayList<game2receive> getonlineFromServer1(String uid) {
	ArrayList<game2receive> s = new ArrayList<game2receive>();
	user2getOnline gf = new user2getOnline();
	s = gf.getServerData(uid);
	Log.i("tag", "gf");
	// later use server status to show online or offline friend
	// server_return = s.status;
	return s;
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
private class TotalScoreLoad extends AsyncTask<Void, Integer, Integer> {

	@Override
	protected Integer doInBackground(Void... arg0) {
		// TODO Auto-generated method stub

		if (isConnect(guess2.this)) {
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

		if (isConnect(guess2.this)) {
			pb.setVisibility(View.VISIBLE);
			gamereceive =getonlineFromServer1(uid);

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
private class Game1TopicLoad extends AsyncTask<Void, Integer, Integer> {

	@Override
	protected Integer doInBackground(Void... arg0) {
		// TODO Auto-generated method stub

		if (isConnect(guess2.this)) {
			pb.setVisibility(View.VISIBLE);
			game1list = getonlineFromServer(uid);
Log.i("get from server game1", game1list.get(0).speaker);
			try {

				db.open();
				// db.updateToDefaultGame1Guess();
				db.deleteGame1Guess(uid);
				for (int i = 0; i < game1list.size(); i++) {
Log.i("size",String.valueOf(game1list.size()));
					db.insertGame1Guess(game1list.get(i), uid);
                    Log.i("insertGame1",game1list.get(0).toString() );
					db.updateGame1Guess(game1list.get(i), uid);
					Log.i("updategame1", game1list.get(0).toString());
				}
			      db.close();	
			}catch (Exception e) {
				// TODO: handle exception
			
                    e.getMessage();
			
				}
			} 
			

		
		return 1;
	}

	protected void onProgressUpdate(Integer... progress) {

		// dismissDialog();
		// status = (TextView) findViewById(R.id.status);
		// status.setVisibility(View.GONE);
		pb.setVisibility(View.GONE);
		title.setText(game1list.get(0).title);
		speaker.setText(game1list.get(0).speaker);
		detail.setText(game1list.get(0).detail);

	}
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
		itemintent.setClass(guess2.this, MainInterface.class);
		startActivity(itemintent);
		return true;
	case MENU_HOME:
		this.finish();
		itemintent.setClass(guess2.this, Browsetab.class);
		startActivity(itemintent);
		return true;
	case MENU_SERIES:
		this.finish();
		itemintent.setClass(guess2.this, series.class);
		startActivity(itemintent);
		return true;
	case MENU_GROUP:
		this.finish();
		itemintent.setClass(guess2.this, groups.class);
		startActivity(itemintent);
		return true;
	case MENU_SCHEDULE:
		this.finish();
		itemintent.setClass(guess2.this, MyBookmarked.class);
		startActivity(itemintent);
		return true;
	case MENU_LOGIN:
		this.finish();
		itemintent.setClass(guess2.this, Search.class);
		startActivity(itemintent);
		return true;
	case MENU_CAL:
		this.finish();
		itemintent.setClass(guess2.this, calendar.class);
		startActivity(itemintent);
		return true;
	case MENU_GAME:
		this.finish();
		itemintent.setClass(guess2.this, gameTab.class);
		startActivity(itemintent);
		return true;
	}
	return false;

}
private class RatingTask extends AsyncTask<String,Integer,Integer>{

	String action;
	String id;
	String userid;
	String rate;
	String status;
	
	RatingAction ra = new RatingAction();
	@Override
	protected Integer doInBackground(String... args) {
		// TODO Auto-generated method stub
		userid = args[0];
		id = args[1];
		action = args[2];
		
		if(action.compareTo("get") == 0){
			rate = ra.getRating(id, userid);
			publishProgress(0);
		}
		else{
			status = ra.addRating(id, userid, action);
			if(status.compareToIgnoreCase("ok") == 0){
				insertRatedTalk(t, userid, action);
				publishProgress(1);
			}
			else
				publishProgress(2);
		}
		
		
		return 0;
	}
	protected void onProgressUpdate(Integer... progress) {
		int command = progress[0];
		switch(command){
		case 0:
			if(rate.compareToIgnoreCase("null") != 0)
			rating.setRating(Float.parseFloat(rate));
			break;
		case 1:
			dismissDialog();
			break;
		default:
			dismissDialog();
			Toast.makeText(getApplicationContext(), "Fail to connect to server, please check your internet connection and try again.",
				      Toast.LENGTH_SHORT).show();
			break;
	}
	
}
}
private class EmailTask extends AsyncTask<String,Integer,Integer>{

	String email;
	String id;
	String userid;
	EmailAction ea = new EmailAction();
	@Override
	protected Integer doInBackground(String... args) {
		// TODO Auto-generated method stub
		
		email = args[0];
		id = args[1];
		userid = args[2];
		
		if(ea.sendEmail(id, email, userid).compareToIgnoreCase("yes") == 0)
			publishProgress(0);
		else
			publishProgress(1);
		
		return 0;
	}
	protected void onProgressUpdate(Integer... progress) {
		int command = progress[0];
		switch(command){
		case 0:
			dismissDialog();
			Toast.makeText(getApplicationContext(),  
	                "Emails are sent",  
	                Toast.LENGTH_SHORT)  
	             .show();
			break;
		case 1:
			dismissDialog();
			Toast.makeText(getApplicationContext(), "Fail to connect to server, please check your internet connection and try again.",
				      Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
	}
	
}
}
public void showDialog(String s){
	 pd = ProgressDialog.show(this, "Synchronization", s,true, false);
}
public void dismissDialog(){
	 pd.dismiss();
}
public Talk getTalk(String id){
	
	db.open();
	Talk t = db.getTalkByID(id);
	db.close();
	return t;
}
public String getBookmarkedStatus(String id){
	db.open();
	String status = db.getTalkBookmarkedStatus(id);
	db.close();
	
	return status;
}
public void updateBookedTalk(Talk t, String bookmark){
	db.open();
	db.updateTalkByBookmark(t,bookmark);
	db.close();
}
public void updateMyBookmarkedTalks(String userid, Talk t, String action){
	db.open();
	if(action.compareTo("insert") == 0)
		db.insertMyBookmarked(t, userid);
	else
		db.deleteMyBookmarked(t, userid);
	db.close();
}
public void insertRatedTalk(Talk t, String userid, String rating){
	db.open();
	if (db.insertRatedTalk(t, userid, rating) == 0)
		db.updateRatedTalk(t, userid, rating);
	Log.d("rating","rating is"+rating);
	db.updateTalkByRating(t, rating);
	db.close();
}
@Override
public void onClick(View v) {
	// TODO Auto-generated method stub
	switch(v.getId()){
	case R.id.bookmark:
		if(isConnect(guess2.this)){
			if(isSignin()){
				if (getBookmarkedStatus(id).compareTo("no") == 0) 
					bookmark.setImageResource(R.drawable.yes_schedule);
				else
					bookmark.setImageResource(R.drawable.no_schedule);
			Thread bookthread = new Thread() {    	
				public void run() {
					if (getBookmarkedStatus(id).compareTo("no") == 0) {
						updateBookedTalk(t,"yes");
						ba = new BookmarkAction();
						bookmark_action_status = ba.addBookmark(id,uid);							
					} else {
						updateBookedTalk(t,"no");
						ba = new BookmarkAction();
						bookmark_action_status = ba.removeBookmark(id, uid);
					}
					mhandler.sendEmptyMessage(0);
				}
			        //dialog.cancel();
				};
			bookthread.start();
		}
			else{
				new AlertDialog.Builder(guess2.this)
				.setMessage("Bookmark function requires login, do you want to sign in first?")
				.setPositiveButton("yes", 
					    		 new DialogInterface.OnClickListener(){ 
								@Override
								public void onClick(DialogInterface dialoginterface, int i){ 
									dialoginterface.cancel();
									guess2.this.finish();	
									Intent in = new Intent(guess2.this, Signin.class);
									in.putExtra("activityname", "TalkDetail");
									in.putExtra("id", id);
									
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
				else
					new AlertDialog.Builder(guess2.this) 
		          .setMessage("This porcess requires internet connection, please check your internet connection.") 
		          .setPositiveButton("close", 
		                         new DialogInterface.OnClickListener(){ 
		                                 public void onClick(DialogInterface dialoginterface, int i){ 
		                             dialoginterface.cancel();
		                                  } 
		                          }) 
		          .show(); 
		break;
	case R.id.email:
		if(isConnect(guess2.this)){
			if(isSignin()){
				LayoutInflater inflater = getLayoutInflater();
				View layout = inflater.inflate(R.layout.email_dialog,(ViewGroup) findViewById(R.id.dialog));
				 emailtext = (EditText) layout.findViewById(R.id.email);
			new AlertDialog.Builder(guess2.this).setTitle("Enter email").setIcon(
				     android.R.drawable.ic_dialog_info)
				     .setView(layout)
				     .setPositiveButton("send", 
				    		 new DialogInterface.OnClickListener(){ 
							@Override
							public void onClick(DialogInterface dialoginterface, int i){
								String emails = emailtext.getText().toString();
								dialoginterface.cancel();
								showDialog("sending email from server...");
								new EmailTask().execute(emails, id, uid);
								//update();
							} 
							})
				     .setNegativeButton("cancel", 
				    		 new DialogInterface.OnClickListener() { 
							public void onClick(DialogInterface dialog, int id) { 
								dialog.cancel(); 	
								} 
								}).show();

			}
							else{
				new AlertDialog.Builder(guess2.this)
				.setMessage("System email function requires login, do you want to sign in first?")
				.setPositiveButton("yes", 
					    		 new DialogInterface.OnClickListener(){ 
								@Override
								public void onClick(DialogInterface dialoginterface, int i){ 
									dialoginterface.cancel();
									guess2.this.finish();	
									Intent in = new Intent(guess2.this, Signin.class);
									in.putExtra("activityname", "TalkDetail");
									in.putExtra("id", id);
									
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
			else
				new AlertDialog.Builder(guess2.this) 
	          .setMessage("This porcess requires internet connection, please check your internet connection.") 
	          .setPositiveButton("close", 
	                         new DialogInterface.OnClickListener(){ 
	                                 public void onClick(DialogInterface dialoginterface, int i){ 
	                             dialoginterface.cancel();
	                                  } 
	                          }) 
	          .show(); 
		break;
	case R.id.share:
		Intent connectSocN = new Intent(Intent.ACTION_SEND);     
		connectSocN.setType("text/plain");    
		connectSocN.putExtra(android.content.Intent.EXTRA_SUBJECT,"CoMeT: sugguested colloquium to you");     
		connectSocN.putExtra(Intent.EXTRA_TEXT,
				"Watch \""+t.title+"\""+
				" on "+t.date+" "+t.begintime+" - "+t.endtime+"\n"+
				"More detail please visit http://pittcomet.info/presentColloquium.do?"+id);     
		startActivity(Intent.createChooser(connectSocN, "Share"));
		break;
	case R.id.ImageButton01:
		this.finish();
		Intent in = new Intent(guess2.this,Search.class);
		startActivity(in);
		break;
	default:
		break;
		
	}
}


@Override
public boolean onTouch(View v, MotionEvent event) {
	// TODO Auto-generated method stub
	  if (event.getAction() == MotionEvent.ACTION_UP) {
          // TODO perform your action here		
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.rating_dialog,(ViewGroup) findViewById(R.id.dialog));
		RatingBar settingrate =(RatingBar) layout.findViewById(R.id.ratingbar);
		settingrate.setOnRatingBarChangeListener(new OnRatingBarChangeListener(){

			@Override
			public void onRatingChanged(RatingBar ratingBar,
					float frate, boolean fromUser) {
				// TODO Auto-generated method stub
				 if (rating.getRating() != frate) {
		             rating.setRating(frate);
		         }
			}
		
		});
		
		new AlertDialog.Builder(guess2.this)
			.setTitle("Rating dialog")
			.setView(layout)
		    .setPositiveButton("rate", new DialogInterface.OnClickListener(){ 
					@Override
					public void onClick(DialogInterface dialoginterface, int i){ 
						dialoginterface.cancel();
						showDialog("adding rating to this talk, please wait ...");
						new RatingTask().execute(uid, id, rating.getRating()+"");
					
					} 
					})
		     .setNegativeButton("cancel",  new DialogInterface.OnClickListener() { 
					public void onClick(DialogInterface dialog, int id) { 
						dialog.cancel(); 	
						} 
						}).show();
	}
      return true;
  
	

}
	
}
