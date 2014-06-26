package comet.com;

import java.util.ArrayList;

import org.apache.http.client.UserTokenHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import phpAPI.InsertOnlineScore;
import phpAPI.getOnlineScore;
import phpAPI.updateScore;
import phpAPI.uploadOnlineScore;
import phpAPI.user2getOnline;

import util.DBAdapter;
import util.RatingAction;
import util.Score;
import util.game2receive;



import android.R.anim;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RatingBar.OnRatingBarChangeListener;

public class message extends Activity implements OnClickListener {
	private String name,uid,user0id;
	private ArrayList<game2receive> game2receives;
	// private String[] topic,colID;
	// private ArrayList<game2receive> topic;
	private ListView list;
	private MyAdapter adapter;
	private int listsize;
	private ViewHolder vh;
	private DBAdapter db;
	private ProgressBar pb;
	private Score s;
	private ArrayList<game2receive> gamereceive;
	private ProgressDialog pd;
	private TextView navi, head,message,scoreTextView,totalscore;
	private uploadOnlineScore uos;
	private InsertOnlineScore ios;
	private ImageButton  letterButton,search,refresh;
	private final int MENU_MENU = Menu.FIRST;
	private final int MENU_HOME = Menu.FIRST + 1;
	private final int MENU_SERIES = Menu.FIRST + 2;
	private final int MENU_GROUP = Menu.FIRST + 3;
	private final int MENU_LOGIN = Menu.FIRST+4;
	private final int MENU_RECOMMEND = Menu.FIRST+5;
	private final int MENU_CAL = Menu.FIRST+6;
	private final int MENU_GAME=Menu.FIRST+7;
	@SuppressWarnings("unchecked")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.message);
		// init db
		db = new DBAdapter(this);
		// db
		navi = (TextView) findViewById(R.id.navi);
		navi.setVisibility(View.VISIBLE);
		// status = (TextView) findViewById(R.id.status);
		head = (TextView) findViewById(R.id.TextView01);

		 head.setText("Get from online Game");
		 navi.setText("Rate by yourself");
		// pb
		pb = (ProgressBar) this.findViewById(R.id.widget108);
		// pb.setVisibility(View.GONE);
		JSONArray jArray = new JSONArray();
		JSONArray j2 = new JSONArray();
		Bundle b = getIntent().getExtras();
		if (b != null) {
			
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject = new JSONObject(b.getString("title"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				jArray = jsonObject.getJSONArray("object");
				j2 = jsonObject.getJSONArray("object1");
				
				user0id=jsonObject.getString("user0id");
				Log.i("sender", user0id);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			game2receives = new ArrayList<game2receive>();
			try {
				for (int m = 0; m < jArray.length(); m++) {

					game2receive ot = new game2receive();

					ot.setTitle(jArray.get(m).toString());
					Log.i("title", ot.title);
					ot.setColID(j2.get(m).toString());
					Log.i("colid", ot.colID);
					// Log.i("colid",ot.colID);
					game2receives.add(ot);
					// game2receives.get(m).colID=j2.get(m).toString();
					// topic[m]=jArray.get(m).toString();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// json.getString("id");
			// game2receives.add(ot);

			/*
			 * for(int n=0;n<j2.length();n++) { try {
			 * colID[n]=j2.get(n).toString(); } catch (JSONException e) { //
			 * TODO Auto-generated catch block e.printStackTrace(); }
			 */

		}
		  message=(TextView)findViewById(R.id.message);
		    message.setVisibility(View.INVISIBLE);
		   
		    scoreTextView=(TextView)findViewById(R.id.totalview);
		    totalscore=(TextView)findViewById(R.id.totalscore);
		    totalscore.setVisibility(View.VISIBLE);
		    
		    letterButton=(ImageButton)findViewById(R.id.mail);
		    letterButton.setVisibility(View.INVISIBLE);
		    
		// Log.i("string", topic.toString());
		// }
		list = (ListView) findViewById(R.id.messagelist);
		list.setVisibility(View.GONE);
		// String[] topic = { "1", "2", "2" };
		// ArrayAdapter<game2receive> adapter = new
		// ArrayAdapter<game2receive>(this, android.R.layout.simple_list_item_1,
		// top// android.R.layout.simple_list_item_1, topic);
		// adapter.addAll(topic);
		// list.setAdapter(adapter);
		/*
		 * list.setOnItemClickListener(new OnItemClickListener() {
		 * 
		 * @Override public void onItemClick(AdapterView<?> arg0, View arg1, int
		 * arg2, long arg3) { // TODO Auto-generated method stub Intent intent =
		 * new Intent(); intent.setClass(message.this, TalkDetail.class);
		 * intent.putExtra("id", game2receives.get(arg2).colID); //
		 * intent.putExtra("id",); startActivity(intent); }
		 * 
		 * });
		 */

		adapter = new MyAdapter(game2receives);
		list.setAdapter(adapter);
		list.setOnItemClickListener(adapter);
		if(isSignin()){
			//showDialog("Loading data, please wait...");
			//new RecommendTalkLoad().execute();	
			list.setVisibility(View.VISIBLE);
			initViews();
			}
		else{
			new AlertDialog.Builder(message.this)
			.setMessage("Message function requires login, do you want to sign in first?")
			.setPositiveButton("yes", 
				    		 new DialogInterface.OnClickListener(){ 
							@Override
							public void onClick(DialogInterface dialoginterface, int i){ 
								dialoginterface.cancel();
								message.this.finish();	
								Intent in = new Intent(message.this, Signin.class);							
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
					
						
						new TotalScoreLoad().execute();
						}
					else{
						new AlertDialog.Builder(message.this)
						.setMessage("Game home function requires login, do you want to sign in first?")
						.setPositiveButton("yes", 
							    		 new DialogInterface.OnClickListener(){ 
										@Override
										public void onClick(DialogInterface dialoginterface, int i){ 
											dialoginterface.cancel();
											message.this.finish();	
											Intent in = new Intent(message.this, Signin.class);							
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
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_MENU, 0, "Main Menu").setIcon(R.drawable.menu_s);
		menu.add(0, MENU_HOME, 0, "Browse talk").setIcon(R.drawable.home_s);
		menu.add(0, MENU_SERIES, 0, "Series").setIcon(R.drawable.series_s);
		menu.add(0, MENU_GROUP, 0, "Group").setIcon(R.drawable.groups_s);
		menu.add(0, MENU_LOGIN, 0, "Search").setIcon(R.drawable.search_s);
		menu.add(0, MENU_RECOMMEND, 0, "Recommendation").setIcon(R.drawable.recommend_s);
		menu.add(0, MENU_GAME, 0, "Game").setIcon(R.drawable.game);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Intent itemintent = new Intent();
		switch (item.getItemId()) {
		case MENU_MENU:
			this.finish();
			itemintent.setClass(message.this, MainInterface.class);
			startActivity(itemintent);
			return true;
		case MENU_HOME:
			this.finish();
			itemintent.setClass(message.this, Browsetab.class);
			startActivity(itemintent);
			return true;
		case MENU_SERIES:
			this.finish();
			itemintent.setClass(message.this, series.class);
			startActivity(itemintent);
			return true;
		case MENU_GROUP:
			this.finish();
			itemintent.setClass(message.this, groups.class);
			startActivity(itemintent);
			return true;
		case MENU_LOGIN:
			this.finish();
			itemintent.setClass(message.this, Search.class);
			startActivity(itemintent);
			return true;
		case MENU_RECOMMEND:
			this.finish();
			itemintent.setClass(message.this, MyRecommend.class);
			startActivity(itemintent);
			return true;
		case MENU_GAME:
			this.finish();
			itemintent.setClass(message.this, gameTab.class);
			startActivity(itemintent);
			return true;
		}
		return false;
	}
	
	public void initViews(){
		
		
		
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
	
	static class ViewHolder {
		TextView gametitle;
		RatingBar bar;
	}
	public boolean isSignin(){
		SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
		boolean signin = userinfo.getBoolean("userSignin",false);
		name = userinfo.getString("userName", "The first time");
		uid = userinfo.getString("userID", "0");
		return signin;
		
	}
	public class MyAdapter extends BaseAdapter implements OnItemClickListener {
		private ArrayList<game2receive> gameTopics;

		// private List<Boolean> checkBoxesStatus1;

		public MyAdapter(ArrayList<game2receive> onlinegameTopics) {
			this.gameTopics = onlinegameTopics;
			listsize = onlinegameTopics.size();
			/*
			 * checkBoxesStatus1 = new ArrayList<Boolean>(listsize); for (int i
			 * = 0; i < listsize; i++) { checkBoxesStatus1.add(false); }
			 */
		}

		/*
		 * public List<Boolean> getCheckBoxesStatus() { return
		 * checkBoxesStatus1; }
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
		         vh = null;
			// Boolean checkBoxStatus = checkBoxesStatus1.get(position);

			// checkBoxesStatus = new
			// ArrayList<Boolean>(onlinegameTopics.size());

			if (convertView == null) {
				LayoutInflater li = getLayoutInflater();
				convertView = li.inflate(R.layout.messagelist, null);
				vh = new ViewHolder();

				vh.gametitle = (TextView) convertView.findViewById(R.id.name);
				vh.bar = (RatingBar) convertView.findViewById(R.id.ratebar);
				// vh.name = (TextView) convertView.findViewById(R.id.name);
				// vh.checkbox1 = (CheckBox) convertView
				// .findViewById(R.id.checkbox);

				// vh.speaker = (TextView)
				// convertView.findViewById(R.id.speaker);
				// vh.desc = (TextView) convertView.findViewById(R.id.desc);

				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			vh.gametitle.setText(gameTopics.get(position).title);
			vh.bar.setEnabled(true);
			vh.bar.setOnTouchListener(new OnTouchListener() {

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
								 if (vh.bar.getRating() != frate) {
						            vh.bar.setRating(frate);
						         }
							}
						
						});
						
						new AlertDialog.Builder(message.this)
							.setTitle("Rating dialog")
							.setView(layout)
						    .setPositiveButton("rate", new DialogInterface.OnClickListener(){ 
									@Override
									public void onClick(DialogInterface dialoginterface, int i){ 
										dialoginterface.cancel();
									Log.i("rating", "tate");
									//showDialog("adding rating to this talk, please wait ...");
									   new RatingTask().execute(position);
									
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
			// vh.bar.setRating(Float.parseFloat(game2receives.get(arg2)));
			// vh.checkbox1.setId(position);
			// vh.checkbox1.setChecked(checkBoxStatus);
			/*
			 * vh.checkbox1 .setOnCheckedChangeListener(new
			 * OnCheckedChangeListener() {
			 * 
			 * @Override public void onCheckedChanged(CompoundButton buttonView,
			 * boolean isChecked) { // TODO Auto-generated method stub
			 * checkBoxesStatus1.set(buttonView.getId(), isChecked);
			 * notifyDataSetChanged(); } });
			 * 
			 * //
			 * vh.speaker.setText(onlinegameTopics.get(position).speakername);
			 * // vh.desc.setText(onlinegameTopics.get(position).desc);
			 */
			return convertView;
		}

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			// TODO Auto-generated method stub
			Intent in = new Intent(message.this, Talktab.class);
			in.putExtra("id", game2receives.get(pos).colID);
			startActivity(in);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ImageButton01:
			this.finish();
			Intent in = new Intent(message.this, Search.class);
			startActivity(in);
			break;
		default:
			break;
		}
	}
	public void showDialog(String s){
		 pd = ProgressDialog.show(this, "Synchronization", s,true, false);
	 }
	public void dismissDialog(){
		 pd.dismiss();
	 }
	private class TotalScoreLoad extends AsyncTask<Void, Integer, Integer> {

		@Override
		protected Integer doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			if (isConnect(message.this)) {
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
			Log.i("tag", "gf");
			// later use server status to show online or offline friend
			// server_return = s.status;
			return s;
		}
	private class RatingTask extends AsyncTask<Integer,Integer,Integer>{

		// uid;
		String data;
		String colid;
float	score;
String time1;
int position;
	


		//RatingAction ra = new RatingAction();
		@Override
		protected Integer doInBackground(Integer... arg0) {
			// TODO Auto-generated method stub
		     position=arg0[0];
			if (isConnect(message.this)) {
				pb.setVisibility(View.VISIBLE);
				long time=System.currentTimeMillis();
				game2receives =getonlineFromServer(uid);
				Log.i("inconnect", "isconnected");
				try {

					   db.open();
					// db.updateToDefaultRecommend();
					
					
				     	colid=game2receives.get(position).colID;
						  score=vh.bar.getRating();
						  time1=String.valueOf(time);
						Log.i("updatescore", "2");
						//update game2User
						db.updateOnlineScore(uid,user0id,game2receives.get(position).colID, vh.bar.getRating(),time);
						Log.i("rating", String.valueOf(vh.bar.getRating()));
						Log.i("upload", "update");
						//upload to server game2
						updateScore uScore=new updateScore();
						uScore.uploadServerData(uid,user0id,game2receives.get(position).colID, vh.bar.getRating(),time);
						Log.i("uploadGame2", "nll");
						//data=uScore.uploadToServer();
						
						//update TotalScore in db
						//db.insertOnlineScore1(user0id);
						//Log.i("insertOnlineScore1", user0id.toString());
					    
						//db.updateTotalScore(user0id,score,0);
						//Log.i("updateTotal", String.valueOf(score));
						//update to server total_score table
						 uos=new uploadOnlineScore();
						 ios=new InsertOnlineScore();
						ios.insertServerData(user0id);
						Log.i("insertServerScoe",user0id);
						uos.uploadServerData(user0id,score);
						Log.i("UpdateServerScoe",user0id);
						
						
					//}
					db.close();
				} catch (Exception e) {
					System.out.print(e.getMessage());
				}

			}
			
			
			return 1;
		}
	
		
		protected void onProgressUpdate(Integer... progress) {
			pb.setVisibility(View.GONE);
			if(data.compareTo("true")==0){
				dismissDialog();
			}
			if(String.valueOf(score)!=null&&String.valueOf(time1)!=null){
				
				db.deleteScoreRecord(uid,user0id, colid);
			}
			adapter = new MyAdapter(game2receives);
			message.this.list.setAdapter(adapter);
			message.this.list.setOnItemClickListener(adapter);
		 
		}}
		
		}

