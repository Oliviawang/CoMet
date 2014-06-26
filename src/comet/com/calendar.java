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
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class calendar extends Activity implements OnClickListener{

	private ListView lv;
	private ListAdapter adapter;
	private ArrayList<Talk> calendarTalks;
	private ProgressDialog pd;
	private ImageButton back, next, refresh;
	private ProgressBar pb;
	private Button button;
	private TextView navi, status, head;
	private DBAdapter db;
	private static String TAG="calendar";
	private String server_return="";
	private int current_dayid, current_year;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.browse);
		
		//init date
		Calendar c = Calendar.getInstance();
		current_dayid = c.get(Calendar.DAY_OF_YEAR);
		current_year = c.get(Calendar.YEAR);
		
		//init views
		
		button = (Button) findViewById(R.id.timeheader);
		button.setOnClickListener(this);		
		
		back = (ImageButton) findViewById(R.id.move_back);
		back.setOnClickListener(this);
		
		next = (ImageButton) findViewById(R.id.move_next);
		next.setOnClickListener(this);
		
		status = (TextView) findViewById(R.id.status);
		
		lv = (ListView) findViewById(R.id.ListView);

		pb = (ProgressBar) getParent().findViewById(R.id.widget108);
		
		//search = (ImageButton) findViewById(R.id.ImageButton01);
		//help = (ImageButton) findViewById(R.id.ImageButton02);
		
		//init DB and dataset
		db = new DBAdapter(this);
		
		//Set views
		initViews(current_dayid, current_year);
		
	}	

	public void initViews(int dayid, int y){
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, y);
		c.set(Calendar.DAY_OF_YEAR,dayid);
		
		int d = c.get(Calendar.DAY_OF_MONTH);
		int m = c.get(Calendar.MONTH);
		int year = c.get(Calendar.YEAR);
		
		//set button text
		SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("EEEE, MMM dd, yyyy");
		String str = "0";
		try {
			Date date=new SimpleDateFormat("d, M, yyyy").parse(d+", "+(m+1)+", "+year);
			str   =   formatter.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		button.setText(str);
		
		db.open();	
		calendarTalks = new ArrayList<Talk>();
		calendarTalks = db.getTodayTalks(dayid, y);
		db.close();
		if(calendarTalks.size() != 0){
			adapter = new ListAdapter(calendarTalks);
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(adapter);
		}
		
		Log.d("calendar","not found "+dayid);

		new CalendarLoadTask().execute(d, m+1, year);	
		//}
	}	
	public ArrayList<Talk> getCalendarTalks(int day, int month, int year){
		TalkParser hpp = new TalkParser();
		
		ArrayList<Talk> t =new ArrayList<Talk>();
		t = hpp.getDayTalks(day, month, year);

		server_return = hpp.status;
		
		if(t.size() == 0){
			Log.d(calendar.TAG, "Loaded data size is"+t.size());		
		}
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
				Intent in = new Intent(calendar.this, Talktab.class);
				in.putExtra("id", tlist.get(pos).id);				
				startActivity(in);
		}
		
	}
	private class CalendarLoadTask extends AsyncTask<Integer,Integer,Integer>{
		
		@Override
		protected Integer doInBackground(Integer... nos) {
			// TODO Auto-generated method stub
			if(isConnect(calendar.this)){
			publishProgress(4);
			calendarTalks = getCalendarTalks(nos[0],nos[1],nos[2]);
			
			if(calendarTalks.size() == 0 && server_return.compareTo("ok") == 0){
			publishProgress(0);
			}
			else if(calendarTalks.size() == 0 && server_return.compareTo("ok") != 0){
			publishProgress(2);
			}
			else {
				Thread t = new Thread(){
					public void run(){
						try{
							db.open();
							for(int i=0; i <calendarTalks.size(); i++){
								long error = db.insertTalk(calendarTalks.get(i));
								if (error == -1)
								{
									db.updateTalk(calendarTalks.get(i));
								};
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
				//status = (TextView) findViewById(R.id.status);			
				back.setEnabled(true);
				next.setEnabled(true);
				back.setImageDrawable(getResources().getDrawable(R.drawable.calendar_left_arrow_selector));
				next.setImageDrawable(getResources().getDrawable(R.drawable.calendar_right_arrow_selector));
				pb.setVisibility(View.GONE);
				status.setVisibility(View.VISIBLE);
				status.setText("There is no talks for this date");
				status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lightbulb, 0, 0, 0);
				adapter = new ListAdapter(calendarTalks);
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(adapter);
				break;
			case 1:
				//dismissDialog();
				//status = (TextView) findViewById(R.id.status);
				back.setEnabled(true);
				next.setEnabled(true);
				back.setImageDrawable(getResources().getDrawable(R.drawable.calendar_left_arrow_selector));
				next.setImageDrawable(getResources().getDrawable(R.drawable.calendar_right_arrow_selector));
				pb.setVisibility(View.GONE);
				status.setVisibility(View.GONE);
				adapter = new ListAdapter(calendarTalks);
				lv.setAdapter(adapter);
				lv.setOnItemClickListener(adapter);
				break;
			case 2:
				//dismissDialog();
				//status = (TextView) findViewById(R.id.status);
				//status.setVisibility(View.VISIBLE);
				//status.setText("Fail to connect to server, please check your internet connection and try again.");
				//status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.server_error, 0, 0, 0);
				//adapter = new ListAdapter(calendarTalks);
				//lv.setAdapter(adapter);
				//lv.setOnItemClickListener(adapter);
				back.setEnabled(true);
				next.setEnabled(true);
				back.setImageDrawable(getResources().getDrawable(R.drawable.calendar_left_arrow_selector));
				next.setImageDrawable(getResources().getDrawable(R.drawable.calendar_right_arrow_selector));
				pb.setVisibility(View.GONE);
				Toast.makeText(getApplicationContext(), "Fail to connect to server, please check your internet connection and try again.",
					      Toast.LENGTH_SHORT).show();
				break;
			case 3:
				back.setEnabled(true);
				next.setEnabled(true);
				back.setImageDrawable(getResources().getDrawable(R.drawable.calendar_left_arrow_selector));
				next.setImageDrawable(getResources().getDrawable(R.drawable.calendar_right_arrow_selector));
				pb.setVisibility(View.GONE);
				Toast.makeText(getApplicationContext(), "Fail to connect to server, please check your internet connection and try again.",
					      Toast.LENGTH_SHORT).show();
				break;
			case 4:
				pb.setVisibility(View.VISIBLE);				
				back.setEnabled(false);
				back.setImageDrawable(getResources().getDrawable(R.drawable.calendar_left_arrow_gray));
				next.setEnabled(false);
				next.setImageDrawable(getResources().getDrawable(R.drawable.calendar_right_arrow_gray));
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
		case R.id.timeheader:
			showDialog(DATE_DIALOG_ID);	
			break;
		case R.id.move_back:
			Calendar c_1 = Calendar.getInstance();
			c_1.set(Calendar.YEAR, current_year);
			
			if(current_dayid == c_1.getActualMinimum(Calendar.DAY_OF_YEAR)){
				current_year --;
				c_1.set(Calendar.YEAR, current_year);
				current_dayid = c_1.getActualMaximum(Calendar.DAY_OF_YEAR);
			}
			else
				current_dayid--;
			
			initViews(current_dayid, current_year);
			break;
		case R.id.move_next:
			Calendar c_2 = Calendar.getInstance();
			c_2.set(Calendar.YEAR, current_year);
			
			if(current_dayid == c_2.getActualMaximum(Calendar.DAY_OF_YEAR)){
				current_year ++;
				current_dayid = 1;
			}
			else
				current_dayid++;
			
			initViews(current_dayid, current_year);
			break;
		case R.id.ImageButton03:
			Calendar c_3 = Calendar.getInstance();
			c_3.set(Calendar.DAY_OF_YEAR, current_dayid);
			c_3.set(Calendar.YEAR, current_year);
			
			int d = c_3.get(Calendar.DAY_OF_MONTH);
			int m = c_3.get(Calendar.MONTH);
			int y = c_3.get(Calendar.YEAR);
			showDialog("loading...");
			new CalendarLoadTask().execute(d, m+1, y);
			break;
		default:
			break;
		}
		
	}

	static final int DATE_DIALOG_ID = 0;
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
		// get the current date
		Calendar c = Calendar.getInstance();
		int mYear = c.get(Calendar.YEAR);
		int mMonth = c.get(Calendar.MONTH);
		int mDay = c.get(Calendar.DAY_OF_MONTH);
		return new DatePickerDialog(this,
		    mDateSetListener,
		    mYear, mMonth, mDay);
		}
		return null;
	}
	
	
	private DatePickerDialog.OnDateSetListener mDateSetListener =
	new DatePickerDialog.OnDateSetListener() {
	
		public void onDateSet(DatePicker view, int year, 
		              int monthOfYear, int dayOfMonth) 
		{       
			Calendar c = Calendar.getInstance();
			c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			c.set(Calendar.MONTH, monthOfYear);
			c.set(Calendar.YEAR, year);
			
			current_dayid = c.get(Calendar.DAY_OF_YEAR);
			current_year = c.get(Calendar.YEAR);
			//lv = (ListView) findViewById(R.id.ListView01);
			initViews(current_dayid, current_year);
		}
	};
}

