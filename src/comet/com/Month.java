package comet.com;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import util.Caching;
import util.DBAdapter;
import util.Talk;
import util.TalkParser;
import comet.com.HOME.ViewHolder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Month extends Activity implements OnClickListener {

	public ListView lv;
	public ListAdapter adapter;
	public ArrayList<Talk> recentTalks;
	public ProgressDialog pd;
	private ProgressBar pb;
	public ImageButton refresh;
	public DBAdapter db;
	private TextView status, navi;
	private Button button;
	private ImageButton back, next;
	private String server_return="";
	private int currentmonth, currentyear;
	
	private final int MENU_MENU = Menu.FIRST;
	private final int MENU_SERIES = Menu.FIRST + 1;
	private final int MENU_GROUP = Menu.FIRST + 2;
	private final int MENU_SCHEDULE = Menu.FIRST + 3;
	private final int MENU_LOGIN = Menu.FIRST+4;
	private final int MENU_RECOMMEND = Menu.FIRST+5;
	private final int MENU_CAL = Menu.FIRST+6;
	private final int MENU_GAME=Menu.FIRST+7;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.browse);
		
		//init "this week" parameter
		Calendar c = Calendar.getInstance();
		currentmonth = c.get(Calendar.MONTH);
		currentyear = c.get(Calendar.YEAR);
		
		//init views
		//time button
		button = (Button) findViewById(R.id.timeheader);
		
		//back button
		back = (ImageButton) findViewById(R.id.move_back);
		back.setOnClickListener(this);
		
		//next button
		next = (ImageButton) findViewById(R.id.move_next);
		next.setOnClickListener(this);
		
		//Refresh button
		/*refresh = (ImageButton) findViewById(R.id.ImageButton03);
		refresh.setVisibility(View.VISIBLE);
		refresh.setOnClickListener(this);*/
		
		pb = (ProgressBar) getParent().findViewById(R.id.widget108);
		
		lv = (ListView) findViewById(R.id.ListView);
	
		//search button
		//help button
		
		//init DB and dataset
		db = new DBAdapter(this);
		
		//Set views
		
		initViews(currentmonth, currentyear);
		 	
	}
    protected void onStart(){
    	super.onStart();          
    }
    protected void onRestart(){
    	super.onRestart();     
    }
    protected void onResume(){
    	super.onResume();     
    }
    protected void onPause(){
    	super.onPause();     
    }
    protected void onStop(){
    	super.onStop();     
    }
    protected void onDestroy(){
    	super.onDestroy();
    }
	
	public void initViews(int m, int y){
		
		//set button text
		SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("MMM, yyyy");
		String str = "0";
		try {
			Date date=new SimpleDateFormat("M, yyyy").parse((m+1)+", "+y);
			str   =   formatter.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		button.setText(str);
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, y);
		c.set(Calendar.MONTH, m);
		c.set(Calendar.DAY_OF_MONTH, 1);
		
		int dayid_start = c.get(Calendar.DAY_OF_YEAR);
		int dayid_end = dayid_start+c.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		db.open();	
		recentTalks = new ArrayList<Talk>();
		recentTalks = db.getThisMonthTalks(dayid_start,dayid_end, y);
		if(recentTalks.size() != 0){
			adapter = new ListAdapter(recentTalks);
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(adapter);
		}
		//else{
		//showDialog("Loading data, please wait...");
			new MonthLoadTask().execute(m+1,y);	
		//}
	}
	public ArrayList<Talk> getMonthTalks(int m, int y){
		TalkParser hpp = new TalkParser();
		
		ArrayList<Talk> t =new ArrayList<Talk>();
		t = hpp.getMonthTalks(m, y);
		server_return = hpp.status;

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
		ImageView photo;
	}
	private class ListAdapter extends BaseAdapter implements OnItemClickListener{

		private ArrayList<Talk> tlist;
		public ListAdapter(ArrayList<Talk> recent){
			this.tlist = recent;
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
			
			//if (convertView == null) {
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
				Intent in = new Intent(Month.this, Talktab.class);
				in.putExtra("id", tlist.get(pos).id);				
				startActivity(in);
		}
		
	}
	private class MonthLoadTask extends AsyncTask<Integer,Integer,Integer>{
		
		@Override
		protected Integer doInBackground(Integer... arg) {
			// TODO Auto-generated method stub
			if(isConnect(Month.this)){
				publishProgress(4);
			recentTalks = getMonthTalks(arg[0],arg[1]);
			
			if(recentTalks.size() == 0 && server_return.compareTo("ok") == 0){
			publishProgress(0);
			}
			else if(recentTalks.size() == 0 && server_return.compareTo("ok") !=0){
			publishProgress(2);
			}
			else{
				Thread t = new Thread(){
					public void run(){
						try{
							db.open();
							for(int i=0; i <recentTalks.size(); i++){
								long error = db.insertTalk(recentTalks.get(i));
								if (error == -1)
								{
									db.updateTalk(recentTalks.get(i));
								}
							}
							db.close();
							}
							catch (Exception e) {
								System.out.print(e.getMessage());
							}
					}
				};
				t.start();
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
				back.setEnabled(true);
				next.setEnabled(true);
				back.setImageDrawable(getResources().getDrawable(R.drawable.calendar_left_arrow_selector));
				next.setImageDrawable(getResources().getDrawable(R.drawable.calendar_right_arrow_selector));
				pb.setVisibility(View.GONE);
				status = (TextView) findViewById(R.id.status);
				status.setVisibility(View.VISIBLE);
				status.setText("There is no talks for this month.");
				status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lightbulb, 0, 0, 0);
				adapter = new ListAdapter(recentTalks);
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(adapter);
				break;
			case 1:
				//dismissDialog();
				back.setEnabled(true);
				next.setEnabled(true);
				back.setImageDrawable(getResources().getDrawable(R.drawable.calendar_left_arrow_selector));
				next.setImageDrawable(getResources().getDrawable(R.drawable.calendar_right_arrow_selector));
				pb.setVisibility(View.GONE);
				status = (TextView) findViewById(R.id.status);
				status.setVisibility(View.GONE);
				adapter = new ListAdapter(recentTalks);
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(adapter);
				break;
			case 2:
				//dismissDialog();
				//status = (TextView) findViewById(R.id.status);
				//status.setVisibility(View.VISIBLE);
				//status.setText("Fail to connect to server, please check your internet connection and try again.");
				//.setCompoundDrawablesWithIntrinsicBounds(R.drawable.server_error, 0, 0, 0);
				back.setEnabled(true);
				next.setEnabled(true);
				back.setImageDrawable(getResources().getDrawable(R.drawable.calendar_left_arrow_selector));
				next.setImageDrawable(getResources().getDrawable(R.drawable.calendar_right_arrow_selector));
				pb.setVisibility(View.GONE);
				Toast.makeText(getApplicationContext(), "Fail to connect to server, please check your internet connection and try again.",
					      Toast.LENGTH_SHORT).show();
				break;
			case 3:
				//dismissDialog();
				back.setEnabled(true);
				next.setEnabled(true);
				back.setImageDrawable(getResources().getDrawable(R.drawable.calendar_left_arrow_selector));
				next.setImageDrawable(getResources().getDrawable(R.drawable.calendar_right_arrow_selector));
				pb.setVisibility(View.GONE);
				Toast.makeText(getApplicationContext(), "Fail to connect to server, please check your internet connection and try again.",
					      Toast.LENGTH_SHORT).show();
				break;
			case 4:
				back.setEnabled(false);
				back.setImageDrawable(getResources().getDrawable(R.drawable.calendar_left_arrow_gray));
				next.setEnabled(false);
				next.setImageDrawable(getResources().getDrawable(R.drawable.calendar_right_arrow_gray));
				
				pb.setVisibility(View.VISIBLE);
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
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.move_back:
			//switch year
			Calendar c1 = Calendar.getInstance();
			c1.set(Calendar.YEAR, currentyear);
			if(currentmonth == c1.getActualMinimum(Calendar.MONTH)){
			currentyear--;
			c1.set(Calendar.YEAR, currentyear);
			currentmonth = c1.getActualMaximum(Calendar.MONTH);
			}
			else
				currentmonth--;
			
			initViews(currentmonth, currentyear);
			break;
		case R.id.move_next:
			Calendar c2 = Calendar.getInstance();
			c2.set(Calendar.YEAR, currentyear);
			if(currentmonth == c2.getActualMaximum(Calendar.MONTH)){
			currentyear++;
			c2.set(Calendar.YEAR, currentyear);
			currentmonth = c2.getActualMinimum(Calendar.MONTH);
			}
			else
				currentmonth++;
			
			initViews(currentmonth, currentyear);
			break;
		case R.id.ImageButton03:
			if(isConnect(Month.this)){
				showDialog("Loading data, please wait...");
				new MonthLoadTask().execute();
			}
			else
				new AlertDialog.Builder(Month.this) 
	          .setMessage("This porcess requires internet connection, please check your internet connection.") 
	          .setPositiveButton("close", 
	                         new DialogInterface.OnClickListener(){ 
	                                 public void onClick(DialogInterface dialoginterface, int i){ 
	                             dialoginterface.cancel();
	                                  } 
	                          }) 
	          .show(); 
			break;
		default:
			break;
		}
	}
}

