package comet.com;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import util.Caching;
import util.DBAdapter;
import util.SearchParser;
import util.Talk;
import comet.com.MyBookmarked.ViewHolder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.View.OnKeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Search extends Activity implements OnClickListener, OnKeyListener{

	private RelativeLayout search;
	private TextView navi, status,message,scoreTextView,totalscore;
	private EditText searchbar;
	private Button searchbutton;
	private ImageButton refresh, search_h,letterButton;
	private ProgressBar pb;
	private ListView lv;
	private ArrayList<Talk> SearchTalks;
	private String server_return="";
	private ListAdapter adapter;
	private DBAdapter db;
	
	private final int MENU_MENU = Menu.FIRST;
	private final int MENU_HOME = Menu.FIRST + 1;
	private final int MENU_SERIES = Menu.FIRST + 2;
	private final int MENU_GROUP = Menu.FIRST + 3;
	private final int MENU_LOGIN = Menu.FIRST+4;
	private final int MENU_RECOMMEND = Menu.FIRST+5;
	private final int MENU_GAME=Menu.FIRST+6;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.main);
		
		
		//views
		refresh = (ImageButton) this.findViewById(R.id.ImageButton03);
		refresh.setVisibility(View.INVISIBLE);
		
		search_h = (ImageButton) this.findViewById(R.id.ImageButton01);
		search_h.setVisibility(View.INVISIBLE);
		
		
		search = (RelativeLayout) this.findViewById(R.id.search);
		search.setVisibility(View.VISIBLE);
		
		navi = (TextView) this.findViewById(R.id.navi);
		navi.setVisibility(View.GONE);
		
		searchbar = (EditText) this.findViewById(R.id.searchbar);
		searchbar.setOnKeyListener(this);
		
		searchbutton = (Button) this.findViewById(R.id.searchbutton);
		searchbutton.setOnClickListener(this);
		
		message=(TextView)findViewById(R.id.message);
	    message.setVisibility(View.INVISIBLE);
	   
	    scoreTextView=(TextView)findViewById(R.id.totalview);
	    scoreTextView.setVisibility(View.INVISIBLE);
	    totalscore=(TextView)findViewById(R.id.totalscore);
	    totalscore.setVisibility(View.INVISIBLE);
	    
	    letterButton=(ImageButton)findViewById(R.id.mail);
	    letterButton.setVisibility(View.INVISIBLE);
		
		pb = (ProgressBar) this.findViewById(R.id.widget108);
		pb.setVisibility(View.GONE);
		
		lv = (ListView) this.findViewById(R.id.ListView01);
		
		//data
		db = new DBAdapter(this);
		SearchTalks = new ArrayList<Talk>();
	}
	public ArrayList<Talk> getSearchResult(String query){
		SearchParser sp = new SearchParser();
		ArrayList<Talk> t = new ArrayList<Talk>();
		t = sp.getSearchResult(query);
		server_return = sp.status;
		return t;
	}
	public String getRecommendedStatus(String id){
		db.open();
		String status = db.getTalkRecommendedStatus(id);
		db.close();
		return status;
	}
	private class SearchLoadTask extends AsyncTask<String,Integer,Integer>{
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		protected Integer doInBackground(String... arg) {
			// TODO Auto-generated method stub
			if(isConnect(Search.this)){
				publishProgress(4);
			SearchTalks = getSearchResult(arg[0]);
			
			if(SearchTalks.size() == 0 && server_return.compareTo("ok") == 0){
			publishProgress(0);
			}
			else if(SearchTalks.size() == 0 && server_return.compareTo("ok") != 0){
			publishProgress(2);
			}
			else{
				Collections.sort(SearchTalks,new   Comparator   ()   { 
					public   int   compare(Object   o1,Object   o2)   { 
						Talk   p1=(Talk)o1; 
						Talk   p2=(Talk)o2; 
						return   (Integer.parseInt(p1.dateid)+Integer.parseInt(p1.year)*365) - (Integer.parseInt(p2.dateid)+Integer.parseInt(p2.year)*365); 
						} 
						});
				try{
					db.open();
					for(int i=0; i <SearchTalks.size(); i++){
						long error = db.insertTalk(SearchTalks.get(i));
						if (error == -1)
						{
							db.updateTalk(SearchTalks.get(i));
						};
					}
					db.close();
					}
					catch (Exception e) {
						System.out.print(e.getMessage());
					}
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
				//status.setVisibility(View.VISIBLE);
				searchbar.setEnabled(true);
				pb.setVisibility(View.GONE);
				adapter = new ListAdapter(SearchTalks);
				Search.this.lv.setAdapter(adapter);
				Search.this.lv.setOnItemClickListener(adapter);
				status = (TextView) findViewById(R.id.status_search);
				status.setVisibility(View.VISIBLE);
				status.setText("No search result for your query.");
				status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lightbulb, 0, 0, 0);
				
				break;
			case 1:
				//dismissDialog();
				//status = (TextView) findViewById(R.id.status);
				//status.setVisibility(View.GONE);
				searchbar.setEnabled(true);
				pb.setVisibility(View.GONE);
				adapter = new ListAdapter(SearchTalks);
				Search.this.lv.setAdapter(adapter);
				Search.this.lv.setOnItemClickListener(adapter);
				break;
			case 2:
				//dismissDialog();
				//status = (TextView) findViewById(R.id.status);
				//status.setVisibility(View.VISIBLE);
				//navi.setText("Fail to connect to server, please check your internet connection and try again.");
				//navi.setCompoundDrawablesWithIntrinsicBounds(R.drawable.server_error, 0, 0, 0);
				
				searchbar.setEnabled(true);
				adapter = new ListAdapter(SearchTalks);
				Search.this.lv.setAdapter(adapter);
				Search.this.lv.setOnItemClickListener(adapter);
				pb.setVisibility(View.GONE);
				Toast.makeText(getApplicationContext(), "Fail to connect to server, please check your internet connection and try again.",
					      Toast.LENGTH_SHORT).show();
				break;
			case 3:
				searchbar.setEnabled(true);
				pb.setVisibility(View.GONE);
				adapter = new ListAdapter(SearchTalks);
				Search.this.lv.setAdapter(adapter);
				Search.this.lv.setOnItemClickListener(adapter);
				Toast.makeText(getApplicationContext(), "Fail to connect to server, please check your internet connection and try again.",
					      Toast.LENGTH_SHORT).show();
				break;
			case 4:
				pb.setVisibility(View.VISIBLE);
				break;
			default:
				break;
		}
	}
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
				vh.photo = (ImageView)convertView.findViewById(R.id.photo);
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
				Intent in = new Intent(Search.this, Talktab.class);
				in.putExtra("id", tlist.get(pos).id);				
				startActivity(in);
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
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String query = searchbar.getText().toString();
		if (query == null || query.equals("")) {   
            Toast.makeText(getApplicationContext(),   
    "Search key word should not be blank", Toast.LENGTH_SHORT).show();   
      }
		else{
		searchbar.setEnabled(false);
		new SearchLoadTask().execute(query);
		}
	}
	@Override
	public boolean onKey(View arg0, int keycode, KeyEvent arg2) {
		// TODO Auto-generated method stub
		if(keycode == 66){
			String query = searchbar.getText().toString();
			if (query == null || query.equals("")) {   
	            Toast.makeText(getApplicationContext(),   
	    "Search key word should not be blank", Toast.LENGTH_SHORT).show();   
	      }
			else{
			searchbar.setEnabled(false);
			new SearchLoadTask().execute(query);
			}
		}
		return false;
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_MENU, 0, "Main Menu").setIcon(R.drawable.menu_s);
		menu.add(0, MENU_HOME, 0, "Browse talk").setIcon(R.drawable.home_s);
		menu.add(0, MENU_SERIES, 0, "Series").setIcon(R.drawable.series_s);
		menu.add(0, MENU_GROUP, 0, "Group").setIcon(R.drawable.groups_s);
		menu.add(0, MENU_LOGIN, 0, "Bookmarked").setIcon(R.drawable.schedule_s);
		menu.add(0, MENU_RECOMMEND, 0, "Recommendation").setIcon(R.drawable.recommend_s);
		menu.add(0, MENU_GAME, 0, "Game").setIcon(R.drawable.comet_logo);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Intent itemintent = new Intent();
		switch (item.getItemId()) {
		case MENU_MENU:
			this.finish();
			itemintent.setClass(Search.this, MainInterface.class);
			startActivity(itemintent);
			return true;
		case MENU_HOME:
			this.finish();
			itemintent.setClass(Search.this, Browsetab.class);
			startActivity(itemintent);
			return true;
		case MENU_SERIES:
			this.finish();
			itemintent.setClass(Search.this, series.class);
			startActivity(itemintent);
			return true;
		case MENU_GROUP:
			this.finish();
			itemintent.setClass(Search.this, groups.class);
			startActivity(itemintent);
			return true;
		case MENU_LOGIN:
			this.finish();
			itemintent.setClass(Search.this, MyBookmarked.class);
			startActivity(itemintent);
			return true;
		case MENU_RECOMMEND:
			this.finish();
			itemintent.setClass(Search.this, MyRecommend.class);
			startActivity(itemintent);
			return true;
		case MENU_GAME:
			this.finish();
			itemintent.setClass(Search.this, gameTab.class);
			startActivity(itemintent);
			return true;
		}
		return false;
	}
}
