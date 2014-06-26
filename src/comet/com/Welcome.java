package comet.com;

import java.util.ArrayList;
import java.util.Calendar;

import util.DBAdapter;
import util.Talk;
import util.TalkParser;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Welcome extends Activity {
	private Handler mHandler = new Handler();
	private TextView status, text;
	private ImageView imageview;
	private RelativeLayout rl;
	private DBAdapter db;

	public int alpha = 255;
	public int b = 0;
	private Handler handler = new Handler();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.welcome);

		imageview = (ImageView) this.findViewById(R.id.Logo);
		imageview.setAlpha(alpha);
		status = (TextView) this.findViewById(R.id.status);
		
		//new AsyncDB().doInBackground();
		//updateApp();
		new Thread(new Runnable() {
			public void run() {
				Looper.prepare();
				initApp(); // Run at first time, load data
				while (b < 2) {
					try {
						if (b == 0) {
							Thread.sleep(100);
							b = 1;
						} else {
							Thread.sleep(10);
						}

						updateApp();

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

			}
		}).start();

		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				imageview.setAlpha(alpha);
				//status.invalidate();
			}
		};

	}

	public void updateApp() {
		alpha -= 5;
		if (alpha <= 0) {
			b = 2;
			finish();
			Intent in = new Intent(this, MainInterface.class);
			startActivity(in);
		}

		mHandler.sendMessage(mHandler.obtainMessage());
	}

	public void initApp() {
		if(isConnect(this)){
			TalkParser hpp = new TalkParser();
			Calendar c = Calendar.getInstance();
			ArrayList<Talk> t =new ArrayList<Talk>();
			db = new DBAdapter(this);
			
			db.open();
			t = db.getTodayTalks(c.get(Calendar.DAY_OF_YEAR), c.get(Calendar.YEAR));
			db.close();
			if(t.size() != 0){
				return;
			}
			else{
			t = hpp.getMonthTalks(c.get(Calendar.MONTH)+1, c.get(Calendar.YEAR));
			
			if(t.size() == 0 && hpp.status.compareTo("ok") == 0){
					return;
				}
				else if(t.size() == 0 && hpp.status.compareTo("ok") !=0){
					showToast("Problem with your internet, please check your connection.");
					return;
				}
				else{
				try{
				db.open();
				for(int i=0; i <t.size(); i++){
					long error = db.insertTalk(t.get(i));
					if (error == -1)
					{
						db.updateTalk(t.get(i));
					}
				}
				db.close();
				}
				catch (Exception e) {
					System.out.print(e.getMessage());
				}
				}
				
		}
		}
		else
			showToast("Problem with your internet, please check your connection.");
		
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
	 public void showToast(final String s) {
		  handler.post(new Runnable() {

		   @Override
		   public void run() {
		    Toast.makeText(getApplicationContext(), s,
		      Toast.LENGTH_SHORT).show();

		   }
		  });
		 }
}
