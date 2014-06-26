package comet.com;

import java.util.ArrayList;

import util.Authorization;
import util.DBAdapter;
import util.Talk;
import util.TalkParser;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Signin extends Activity implements Runnable {

	private EditText emailText, passwordText;
	private Button loginButton, signupButton;
	private String email, password,
				   activityname,id;
	private boolean loginOK = false;
	private ProgressDialog pd;
	private Authorization au;
	private TextView tw4;
	private DBAdapter db;
	
	private final int MENU_MENU = Menu.FIRST;
	private final int MENU_HOME = Menu.FIRST + 1;
	private final int MENU_SERIES = Menu.FIRST + 2;
	private final int MENU_GROUP = Menu.FIRST + 3;
	private final int MENU_SCHEDULE = Menu.FIRST + 4;
	private final int MENU_RECOMMEND = Menu.FIRST+5;
	private final int MENU_GAME=Menu.FIRST+6;

	private static final int ERRORDIALOG = 1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.login);

		Bundle b = getIntent().getExtras();
		if (b != null) {
			activityname = b.getString("activityname");
			if(activityname.compareTo("TalkDetail")==0)
			id = b.getString("id");
		}
		else
			activityname = "";


		db = new DBAdapter(this);
		
		emailText = (EditText) findViewById(R.id.EmailText);
		passwordText = (EditText) findViewById(R.id.PasswordText);
		loginButton = (Button) findViewById(R.id.LoginButton);
		//signupButton = (Button) findViewById(R.id.SignupButton);
		
		tw4 = (TextView)this.findViewById(R.id.TextView08);
		
		loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				login();
			}
		});

		/*signupButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//toSignUp();
			}
		});*/

	}

	public void run() {
		// add authorization method below
		validate();
		handler.sendEmptyMessage(0);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			showLoginResult();
		}
	};

	private void login() {
		pd = ProgressDialog.show(this, "Logging in", "Please Wait...", true,
				false);
		Thread thread = new Thread(this);
		thread.start();
	}

	private void validate() {
		email = emailText.getText().toString();
		password = passwordText.getText().toString();
		au = new Authorization();
		au.login(email, password);
		loginOK = au.isLoginOK;
	}

	private void syncDB(String uid)
	{
		ArrayList<Talk> b = new ArrayList<Talk>();
		ArrayList<Talk> r = new ArrayList<Talk>();
		ArrayList<Talk> ra = new ArrayList<Talk>();
		
		TalkParser tp = new TalkParser();		
		try{

		db.open();
		db.updateToDefaultBookmark();
		db.updateToDefaultRecommend();
		db.updateToDefaultRating();
		db.daleteMyBookmarked(uid);
		db.daleteMyRecommended(uid);
	

		b = tp.getbookmarkedTalks(uid);
		for (int i = 0; i < b.size(); i++) {
			long error = db.insertBookmarkedTalk(b.get(i));
			db.insertMyBookmarked(b.get(i), uid);
			if(error == -1)
			db.updateBookmarkedTalk(b.get(i));
		}
		
		r = tp.getRecommendTalks(uid);
		for (int i = 0; i < r.size(); i++) {
			long error = db.insertRecommendedTalk(r.get(i));
			db.insertMyRecommended(r.get(i), uid);
			if(error == -1)
			db.updateRecommendTalk(r.get(i));
		}

		ra = db.getRatedTalks(uid);
		for(int i=0; i<ra.size(); i++)
			db.updateTalkByRating(ra.get(i), ra.get(i).rating);
		db.close();
		}
		catch (Exception e){
			System.out.print(e.toString());
		}
	}
	
	/*private void updatePaperStatus(String id)
	{
		UserScheduledToServer us2s = new UserScheduledToServer();
		String paperStatus = us2s.addScheduledPaper2Sever(id);
		if(paperStatus.compareTo("no")==0)
			paperStatus = us2s.addScheduledPaper2Sever(id);
	}*/
	
	private void showLoginResult() {
		if (!loginOK) {
			showDialog(ERRORDIALOG);
		} else {
			/***
			 * after successfully login, a file should be created to store email
			 * and password
			 */
			
			
			SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
			SharedPreferences.Editor editor = userinfo.edit();
			editor.putString("userID", au.userID);
			editor.putString("userName", au.userName);
			editor.putBoolean("userSignin",true );
			editor.commit();
			//tw4.setText("your id is: "+au.userID+"; your name is:"+au.userName);
			
			syncDB(au.userID);

			Intent in;
			if(activityname.compareTo("TalkDetail")==0){
			in = new Intent(Signin.this, Talktab.class);
			in.putExtra("id", id);
			}
			else{
				in = new Intent(Signin.this,MainInterface.class);
			}
			startActivity(in);
			this.finish();
		}
			
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case ERRORDIALOG:
			return errorDialog(Signin.this, au.errorMessage);
		}
		return null;
	}

	protected void onPrepareDialog(int id, Dialog dialog) {

	}

	private Dialog errorDialog(Context context, String error) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.alert_dialog_icon);
		builder.setTitle("Login: Please recheck userid or passwd");
		builder.setMessage(error);
		builder.setPositiveButton("ok", null);
		return builder.create();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_MENU, 0, "Main Menu").setIcon(R.drawable.menu_s);
		menu.add(0, MENU_HOME, 0, "Browse talk").setIcon(R.drawable.home_s);
		menu.add(0, MENU_SERIES, 0, "Series").setIcon(R.drawable.series_s);
		menu.add(0, MENU_GROUP, 0, "Group").setIcon(R.drawable.groups_s);
		menu.add(0, MENU_SCHEDULE, 0, "Bookmarked").setIcon(R.drawable.schedule_s);
		menu.add(0, MENU_RECOMMEND, 0, "Recommendation").setIcon(R.drawable.recommend_s);
		menu.add(0, MENU_GAME, 0, "Game").setIcon(R.drawable.comet_logo);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Intent itemintent = new Intent();
		switch (item.getItemId()) {
		case MENU_MENU:
			this.finish();
			itemintent.setClass(Signin.this, MainInterface.class);
			startActivity(itemintent);
			return true;
		case MENU_HOME:
			this.finish();
			itemintent.setClass(Signin.this, Browsetab.class);
			startActivity(itemintent);
			return true;
		case MENU_SERIES:
			this.finish();
			itemintent.setClass(Signin.this, series.class);
			startActivity(itemintent);
			return true;
		case MENU_GROUP:
			this.finish();
			itemintent.setClass(Signin.this, groups.class);
			startActivity(itemintent);
			return true;
		case MENU_SCHEDULE:
			this.finish();
			itemintent.setClass(Signin.this, MyBookmarked.class);
			startActivity(itemintent);
			return true;
		case MENU_RECOMMEND:
			this.finish();
			itemintent.setClass(Signin.this, MyRecommend.class);
			startActivity(itemintent);
			return true;
		case MENU_GAME:
			this.finish();
			itemintent.setClass(Signin.this, gameTab.class);
			startActivity(itemintent);
			return true;

		}
		return false;
	}
	

}

