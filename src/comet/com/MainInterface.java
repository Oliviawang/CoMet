package comet.com;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import phpAPI.getOnlineScore;
import phpAPI.user2getOnline;
import util.Authorization;
import util.DBAdapter;
import util.Score;
import util.game2receive;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainInterface extends Activity implements OnClickListener {

	private ImageButton refresh, help, search;
	private ProgressBar pb;
	private ProgressDialog pd;
	private TextView username,message,scoreTextView,totalscore;
	private ImageButton  letterButton;
	private String name,uid;
	private DBAdapter db;
	private Score s;
	private ArrayList<game2receive> gamereceive;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.main_interface);
		//init DB
		db = new DBAdapter(this);
		username = (TextView) this.findViewById(R.id.navi);
		username.setVisibility(View.VISIBLE);
		
		message=(TextView)this.findViewById(R.id.message);
		message.setVisibility(View.INVISIBLE);
		
		 scoreTextView=(TextView)findViewById(R.id.totalview);
		    totalscore=(TextView)findViewById(R.id.totalscore);
		    totalscore.setVisibility(View.VISIBLE);
		    
		    letterButton=(ImageButton)findViewById(R.id.mail);
		    letterButton.setVisibility(View.INVISIBLE);
		
		
		search = (ImageButton) this.findViewById(R.id.ImageButton01);
		//search.setVisibility(View.GONE);
		search.setOnClickListener(this);
		
		//help = (ImageButton) this.findViewById(R.id.ImageButton02);
		//help.setVisibility(View.GONE);
		//help.setOnClickListener(this);

		
		pb = (ProgressBar) this.findViewById(R.id.widget108);
		pb.setVisibility(View.GONE);
		
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
						new AlertDialog.Builder(MainInterface.this)
						.setMessage("CoMet function requires login, do you want to sign in first?")
						.setPositiveButton("yes", 
							    		 new DialogInterface.OnClickListener(){ 
										@Override
										public void onClick(DialogInterface dialoginterface, int i){ 
											dialoginterface.cancel();
											MainInterface.this.finish();	
											Intent in = new Intent(MainInterface.this, Signin.class);							
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
		
		
		//judge if have game message
	//	if(isGameMessage()){
		//	message.setText(Html.fromHtml("you have a invititation from"+User_recv_id+" "));
		//message.setTag("yes");
		//}else
	//	{
			//message.setTag("no");
		//}
		if(isSignin()){
			username.setText(Html.fromHtml("Welcome back! "+name+", <font color=\"#ffffff\"><u><b>Logout</b></u></font>?"));
			username.setTag("yes");
			initViews();
		}
		else{
			username.setText(Html.fromHtml("Hello! You haven't <font color=\"#ffffff\"><u><b>Login</b></u></font> yet!"));
			username.setTag("not");
		}
		username.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if(username.getTag().toString().compareTo("yes") == 0){
				showDialog("Logging out, please wait...");
				new LogoutTask().execute();
				
				}
				else{
					Intent in = new Intent(MainInterface.this, Signin.class);
					startActivity(in);
					//MainInterface.this.finish();
				}
			}
		});
		
		//Row 1
		GridView gv1 = (GridView) findViewById(R.id.GridView01);
		Integer[] i1={ R.drawable.home,R.drawable.schedule,R.drawable.groups,R.drawable.series,R.drawable.search_menu,R.drawable.recommend,R.drawable.game};
		String[] t1={ "Browse Talks","Bookmarked","Groups","Series","Search","Recommended","Game"};
		gv1.setAdapter(new ImageViewAdapter(this,i1,t1));
		
		

		gv1.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView av, View v, int index, long arg) {
				Intent in;
				switch (index) {
				// HOME
				case 0:
					in = new Intent(MainInterface.this, Browsetab.class);
					startActivity(in);
					break;
				// CALENDAR
				case 1:
						in = new Intent(MainInterface.this,MyBookmarked.class);
						startActivity(in);
						break;
				// groups
				case 2:
						in = new Intent(MainInterface.this,groups.class);
						startActivity(in);
						break;
				// series
				case 3:
						in = new Intent(MainInterface.this,series.class);
						startActivity(in);
						break;
				// login
				case 4:
						in = new Intent(MainInterface.this,Search.class);
						startActivity(in);
						break;
						// login
				case 5:
						in = new Intent(MainInterface.this,MyRecommend.class);
						startActivity(in);
						break;
						//recommend game
				case 6: in=new Intent(MainInterface.this, gameTab.class);
				       startActivity(in);
				default:
					break;

				}
			}
		});

	}
	public boolean isSignin(){
		SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
		boolean signin = userinfo.getBoolean("userSignin",false);
		name = userinfo.getString("userName", "The first time");
		uid = userinfo.getString("userID", "0");
		return signin;
		
	}
	public void showDialog(String s){
		 pd = ProgressDialog.show(this, "Synchronization", s,true, false);
	 }
	 public void dismissDialog(){
		 pd.dismiss();
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
						intent.setClass(MainInterface.this,message.class);
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

				if (isConnect(MainInterface.this)) {
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

				if (isConnect(MainInterface.this)) {
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
	private class ImageViewAdapter extends BaseAdapter {
		private Context mContext;
		private Integer[] mThumbIds;
		private String[] mText;

		public ImageViewAdapter(Context c, Integer[] i, String[] t) {
			mContext = c;
			mThumbIds = i;
			mText = t;
		}

		public int getCount() {
			return mThumbIds.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View v;
			//if (convertView == null) {
				LayoutInflater li = getLayoutInflater();
				v = li.inflate(R.layout.imagetext, null);
				TextView tv = (TextView) v.findViewById(R.id.TextView01);
				tv.setText(mText[position]);
				ImageView iv = (ImageView) v.findViewById(R.id.ImageView01);
				iv.setImageResource(mThumbIds[position]);
			/*} else {
				v = convertView;
			}*/

			return v;
		}
	}
	private class LogoutTask extends AsyncTask<Void,Integer,Integer>{

		Authorization a = new Authorization();
		@Override
		protected Integer doInBackground(Void... params) {
			// TODO Auto-generated method stub
			SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
			SharedPreferences.Editor editor = userinfo.edit();
			editor.putString("userID", "");
			editor.putString("userName", "");
			editor.putBoolean("userSignin",false);
			editor.commit();
			
			if(a.logout())
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
				Toast.makeText(getApplicationContext(),a.errorMessage,
					      Toast.LENGTH_SHORT).show();
				username.setText(Html.fromHtml("Hello! You haven't <font color=\"#ffffff\"><u><b>Login</b></u></font> yet!"));
				username.setTag("not");
				break;
			case 1:
				dismissDialog();
				Toast.makeText(getApplicationContext(),a.errorMessage,
					      Toast.LENGTH_SHORT).show();
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
			Intent in = new Intent(MainInterface.this,Search.class);
			startActivity(in);
			break;
		default:
			break;
		}
	}
}
