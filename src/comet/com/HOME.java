package comet.com;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import util.DBAdapter;
import util.Talk;
import util.TalkParser;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class HOME extends Activity implements OnClickListener {

	public ListView lv;
	public ListAdapter adapter;
	public ArrayList<Talk> recentTalks;
	public ProgressDialog pd;
	public ProgressBar pb;
	public ImageButton refresh;
	public DBAdapter db;
	private TextView status, navi;
	private Button button;
	private ImageButton back, next;
	private String server_return="";
	private int currentyear, current_weekyear;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.browse);
		
		//init "this week" parameter
		Calendar c = Calendar.getInstance();
		currentyear = c.get(Calendar.YEAR);
		current_weekyear = c.get(Calendar.WEEK_OF_YEAR);
		
		//init views
		//time button
		button = (Button) this.findViewById(R.id.timeheader);
		
		//back button
		back = (ImageButton) this.findViewById(R.id.move_back);
		back.setOnClickListener(this);
		
		//next button
		next = (ImageButton) this.findViewById(R.id.move_next);
		next.setOnClickListener(this);
		
		pb = (ProgressBar) getParent().findViewById(R.id.widget108);
		
		status = (TextView) this.findViewById(R.id.status);
		
		lv = (ListView) this.findViewById(R.id.ListView);
		
		//search button
		//help button
		
		//init DB and dataset
		db = new DBAdapter(this);
		
		//Set views
		
		initViews(current_weekyear, currentyear);
	
	}
	
	public void initViews(int w, int y){
		int m, w_m, dayid_1, day_1, m_1, y_1, dayid_2, day_2, m_2, y_2;
		
		//set current week and year
		Calendar c1 = Calendar.getInstance();
		c1.set(Calendar.YEAR, y);
		c1.set(Calendar.WEEK_OF_YEAR, w);
		
		//get week of month, month, year
		m = c1.get(Calendar.MONTH);
		w_m = c1.get(Calendar.WEEK_OF_MONTH);		
		//Log.d("initViews", "month "+m+" week of month "+w_m);
		
		
		//start date
		c1.set(Calendar.DAY_OF_WEEK, 2);
		dayid_1 = c1.get(Calendar.DAY_OF_YEAR);
		day_1 = c1.get(Calendar.DAY_OF_MONTH);
		m_1 = c1.get(Calendar.MONTH);
		y_1 = c1.get(Calendar.YEAR);
		
		//set current week and year, dayid_end
		if((dayid_1+6)>c1.getActualMaximum(Calendar.YEAR)){
				dayid_2 = dayid_1+6-c1.getActualMaximum(Calendar.YEAR);
				y_2 = y_1+1;
		}
		else{
				dayid_2 = dayid_1+6;
				y_2 = y_1;
		}
		Calendar c2 = Calendar.getInstance();
		c2.set(Calendar.DAY_OF_YEAR, dayid_2);
		c2.set(Calendar.YEAR, y_2);
		day_2 = c2.get(Calendar.DAY_OF_MONTH);
		m_2 = c2.get(Calendar.MONTH);
		y_2 = c2.get(Calendar.YEAR);
		
		//set button text
		SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("EEEE, MMM dd, yyyy");
		String str1 = "0";
		String str2 ="0";
		try {
			Date date1=new SimpleDateFormat("d, M, yyyy").parse(day_1+", "+(m_1+1)+", "+y_1);
			str1   =   formatter.format(date1);
			Date date2=new SimpleDateFormat("d, M, yyyy").parse(day_2+", "+(m_2+1)+", "+y_2);
			str2   =   formatter.format(date2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		button.setText(str1+"--"+str2);
		
		db.open();	
		recentTalks = new ArrayList<Talk>();
		recentTalks = db.getThisWeekTalks(dayid_1,dayid_2, y);
		db.close();
		if(recentTalks.size() != 0){
			adapter = new ListAdapter(recentTalks);
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(adapter);
		}
		//else{
		//showDialog("Loading data, please wait...");

			new RecentLoadTask().execute(w_m, m+1, y);	
		//}
	}
	public ArrayList<Talk> getRecentTalks(int week, int month, int year){
		TalkParser hpp = new TalkParser();
		
		ArrayList<Talk> t =new ArrayList<Talk>();
		t = hpp.getTalks(week, month, year);
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
			
			final String picURL = tlist.get(position).picurl;
			if( picURL.compareTo("") != 0){
				final ImageView iv = vh.photo;
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
	    		            //DAO.storePic(getApplicationContext(), picId, bm2);	// store it
	    		        } catch (Exception e) {
	    		            e.printStackTrace();
	    		        }	
					}
				};
				t.start();
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
				Intent in = new Intent(HOME.this, Talktab.class);
				in.putExtra("id", tlist.get(pos).id);				
				startActivity(in);
		}
		
	}
	private class RecentLoadTask extends AsyncTask<Integer,Integer,Integer>{
		
		@Override
		protected Integer doInBackground(Integer... arg) {
			// TODO Auto-generated method stub
			if(isConnect(HOME.this)){
				publishProgress(4);
			recentTalks = getRecentTalks(arg[0], arg[1],arg[2]);
			
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
				status.setText("There is no talks for this week");
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
				//status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.server_error, 0, 0, 0);
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
			Calendar c_1 = Calendar.getInstance();
			c_1.set(Calendar.YEAR, currentyear);
			
			if(current_weekyear == c_1.getActualMinimum(Calendar.WEEK_OF_YEAR)) {
			currentyear--;
			c_1.set(Calendar.YEAR, currentyear);
			current_weekyear = c_1.getActualMaximum(Calendar.WEEK_OF_YEAR);
			}
			else
				current_weekyear--;
			Log.d("move_back", "current_week "+ current_weekyear+" currentyear "+currentyear);
			initViews(current_weekyear, currentyear);
			break;
		case R.id.move_next:
			//switch year
			Calendar c_2 = Calendar.getInstance();
			c_2.set(Calendar.YEAR, currentyear);
			if(current_weekyear == c_2.getActualMaximum(Calendar.WEEK_OF_YEAR)) {
			currentyear++;
			current_weekyear = 1;
			}
			else
				current_weekyear++;
			Log.d("move_back", "current_week "+ current_weekyear+" currentyear "+currentyear);
			initViews(current_weekyear, currentyear);
			break;
		case R.id.ImageButton03:
			if(isConnect(HOME.this)){
				showDialog("Loading data, please wait...");
				Calendar c_3 = Calendar.getInstance();
				c_3.set(Calendar.YEAR, currentyear);
				c_3.set(Calendar.WEEK_OF_YEAR, current_weekyear);
				new RecentLoadTask().execute(c_3.get(Calendar.WEEK_OF_MONTH), c_3.get(Calendar.MONTH)+1, c_3.get(Calendar.YEAR));
			}
			else
				new AlertDialog.Builder(HOME.this) 
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