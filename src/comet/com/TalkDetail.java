package comet.com;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import util.BookmarkAction;
import util.Caching;
import util.DBAdapter;
import util.EmailAction;
import util.RatingAction;
import util.Talk;
import util.TalkDesParser;
import util.bookmark;
import util.sponsor;
import util.tag;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class TalkDetail extends Activity implements OnClickListener, OnTouchListener{

	private String id, name,uid, bookmark_action_status;
	private TextView title_tv, time_tv, location_tv, speaker_tv, pub_tv;
	private WebView des_wv;
	private ImageButton bookmark,email,share;
	private ImageView photo;
	private DBAdapter db;
	private Talk t;
	private ProgressDialog pd;
	private RatingBar rating;
	private EditText emailtext;
	private BookmarkAction ba;
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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.talks_detail);   
		
		//init 
		db = new DBAdapter(this);
		
		Bundle b = getIntent().getExtras();
		if (b != null) {
			id = b.getString("id");
		}
		t = new Talk();
		t = getTalk(id);
		
		//Init Views
		title_tv = (TextView) findViewById(R.id.title);
		time_tv = (TextView) findViewById(R.id.time);
		location_tv = (TextView) findViewById(R.id.location);
		speaker_tv = (TextView) findViewById(R.id.speaker);
		pub_tv = (TextView) findViewById(R.id.pub);
		des_wv = (WebView) findViewById(R.id.des);
		bookmark = (ImageButton) findViewById(R.id.bookmark);
		email = (ImageButton) findViewById(R.id.email);
		share = (ImageButton) findViewById(R.id.share);
		rating = (RatingBar) findViewById(R.id.ratingbar);
		photo = (ImageView) findViewById(R.id.photo);
		//tag = (ImageButton) findViewById(R.id.ImageButton03);
		
		
		//signin visual and not signin visual;
		if(isSignin()){
		if(t.bookmarked.compareTo("yes")==0)
			bookmark.setImageResource(R.drawable.yes_schedule);
		else
			bookmark.setImageResource(R.drawable.no_schedule);
		if(t.rating.compareTo("no")!= 0)
			rating.setRating(Float.parseFloat(t.rating));
		}
		else{
			bookmark.setImageResource(R.drawable.lock_schedule);
			email.setImageResource(R.drawable.lock_email_to_friend);
			rating.setEnabled(false);
		}
		
		//set views;
		if(t.recommended.compareTo("yes") == 0)
			title_tv.setText(Html.fromHtml(t.title+"<font color=\"#ff0000\"> &lt;Recommended&gt; </font>"));
		else
			title_tv.setText(t.title);
		
		time_tv.setText(t.date+" "+t.begintime+"-"+t.endtime);
		location_tv.setText(Html.fromHtml("<font color=\"#808080\">On:	</font>"+t.location));
		speaker_tv.setText(Html.fromHtml("<font color=\"#808080\">By:	</font>"+t.speaker+"<font color=\"#808080\"> from </font>"+t.affiliation));
		
		if(t.ownername == null)
			pub_tv.setText(Html.fromHtml("<font color=\"#808080\">Posted on:	</font>"+t.pubtime));
		else
			pub_tv.setText(Html.fromHtml("<font color=\"#808080\">Posted by:	</font>"+t.ownername +"<font color=\"#808080\"> on </font>"+t.pubtime));
		
		des_wv.getSettings().setJavaScriptEnabled(true);
		des_wv.setBackgroundColor(0);
		des_wv.loadData(t.description, "text/html", "utf-8");
		
		//get description if in DB, download if not
		if(t.description.compareTo("Loading description...") == 0){
			updateDes();
		}
		if(t.picurl.compareTo("") != 0){
			updatePic(t.picurl);
		}
		// Listener
		bookmark.setOnClickListener(this);
		email.setOnClickListener(this);
		share.setOnClickListener(this);
		rating.setOnTouchListener(this);
		
	}
	public void updateDes(){
		Thread t = new Thread() {    	
			public void run() {
				TalkDesParser tdp = new TalkDesParser();
		        	String content = tdp.getTalkDes(id);
		        	if(content != null){
		        		des_wv.getSettings().setJavaScriptEnabled(true);
		        		des_wv.setBackgroundColor(0);
		        		des_wv.loadData(content, "text/html", "utf-8");
		        		insertTalkDes(content);
		        	}
		        	else
		        		des_wv.post(new Runnable(){public void run(){
		        			Toast.makeText(getApplicationContext(), "Fail to connect to server, please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
		        		}});
		        }
		        //dialog.cancel();
			};
			t.start();
	}
	public void updatePic(final String picURL){
		Bitmap bm = Caching.loadPic(getApplicationContext(), picURL );	//load from local cache
		if(bm != null)					
			photo.setImageBitmap(bm);
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
    		            photo.post(new Runnable(){public void run(){ photo.setImageBitmap(bm2);}});
    		            Caching.storePic(getApplicationContext(), picURL, bm2);	// store it
    		        } catch (Exception e) {
    		            e.printStackTrace();
    		        }	
				}
			};
			t.start();
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
	
	public boolean isSignin(){
		SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
		boolean signin = userinfo.getBoolean("userSignin",false);
		name = userinfo.getString("userName", "The first time");
		uid = userinfo.getString("userID", "0");
		return signin;
		
	}
	public Talk getTalk(String id){
		
		db.open();
		Talk t = db.getTalkByID(id);
		db.close();
		return t;
	}
	public void insertTalkDes(String content){
		db.open();
		db.updateTalkDes(content, id);
		db.close();
	}
	public String getRecommendedStatus(String id){
		db.open();
		String status = db.getTalkRecommendedStatus(id);
		db.close();
		return status;
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
	public void showDialog(String s){
		 pd = ProgressDialog.show(this, "Synchronization", s,true, false);
	 }
	public void dismissDialog(){
		 pd.dismiss();
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
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.bookmark:
			if(isConnect(TalkDetail.this)){
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
					new AlertDialog.Builder(TalkDetail.this)
					.setMessage("Bookmark function requires login, do you want to sign in first?")
					.setPositiveButton("yes", 
						    		 new DialogInterface.OnClickListener(){ 
									@Override
									public void onClick(DialogInterface dialoginterface, int i){ 
										dialoginterface.cancel();
										TalkDetail.this.finish();	
										Intent in = new Intent(TalkDetail.this, Signin.class);
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
						new AlertDialog.Builder(TalkDetail.this) 
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
			if(isConnect(TalkDetail.this)){
				if(isSignin()){
					LayoutInflater inflater = getLayoutInflater();
					View layout = inflater.inflate(R.layout.email_dialog,(ViewGroup) findViewById(R.id.dialog));
					emailtext =(EditText) layout.findViewById(R.id.email);
				new AlertDialog.Builder(TalkDetail.this).setTitle("Enter email").setIcon(
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
					new AlertDialog.Builder(TalkDetail.this)
					.setMessage("System email function requires login, do you want to sign in first?")
					.setPositiveButton("yes", 
						    		 new DialogInterface.OnClickListener(){ 
									@Override
									public void onClick(DialogInterface dialoginterface, int i){ 
										dialoginterface.cancel();
										TalkDetail.this.finish();	
										Intent in = new Intent(TalkDetail.this, Signin.class);
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
					new AlertDialog.Builder(TalkDetail.this) 
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
		default:
			break;
			
		}
	}

	@Override
	 public boolean onTouch(View v, MotionEvent event) {
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
		
		new AlertDialog.Builder(TalkDetail.this)
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
