package comet.com;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;





import util.Caching;
import util.DBAdapter;
import util.Talk;
import util.TalkParser;
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
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TabHost.TabSpec;

public class guess extends Activity implements OnClickListener{

	private ListView lv,lv1,friendlist;
	private ListAdapter adapter;
	private ArrayList<Talk> recommendedTalks,bookmarkedTalks;
	private ProgressDialog pd;
	private ProgressBar pb;
	private ImageButton refresh, search;
	private DBAdapter db;
	private String name, uid, server_return;
	private TextView navi, head;
	private String id;
	   private Handler mHandler = new Handler(); 
	   private  SlidingDrawer sd;
	   private ListView friendView;
	private List<String> group;           
   private  List<List<String>> child;      
	private static  String[] recType = { "System Recommend", "Friend Recommend" };
	 static String selectedRecType = "System Recommend";
	// private ContactsInfoAdapter adapter1; 
	private final int MENU_MENU = Menu.FIRST;
	private final int MENU_HOME = Menu.FIRST + 1;
	private final int MENU_SERIES = Menu.FIRST + 2;
	private final int MENU_GROUP = Menu.FIRST + 3;
	private final int MENU_SCHEDULE = Menu.FIRST + 4;
	private final int MENU_LOGIN = Menu.FIRST+5;
	private final int MENU_CAL = Menu.FIRST+6;
	private final int MENU_GAME=Menu.FIRST+7;
public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN);
	
	setContentView(R.layout.guess);
	Bundle b = getIntent().getExtras();
	if (b != null) {
		id = b.getString("id");
	}  
	//init views
	navi = (TextView) findViewById(R.id.navi);
	navi.setVisibility(View.VISIBLE);
	//status = (TextView) findViewById(R.id.status);
	head = (TextView) findViewById(R.id.TextView01);

	head.setText("Guess Game");
	navi.setText("Guess who recommend topis to you");
	
	pb = (ProgressBar) this.findViewById(R.id.widget108);
	pb.setVisibility(View.GONE);
	//init DB
	db = new DBAdapter(this);
	
/*Button nextButton=(Button)findViewById(R.id.next);
nextButton.setOnClickListener(new OnClickListener() {

    @Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
   	
   Intent intent=new Intent(guess.this,guess2.class);
   startActivity(intent);
	}

  });*/
    
	lv = (ListView) findViewById(R.id.guesslist_sys);
	lv1=(ListView)findViewById(R.id.guesslist_fri);
     lv.setVisibility(View.VISIBLE);
     lv1.setVisibility(View.GONE);
   Spinner recSpinner = (Spinner) findViewById(R.id.gameSpinner);
	final ArrayAdapter<String> recAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,recType);
	
	// recSpinner.setPrompt("Source");  
	recAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	recSpinner.setAdapter(recAdapter);
   recSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
	   
	   public void onItemSelected(AdapterView<?> parent, View arg1,
				int arg2, long arg3) {

			
			if(parent.getSelectedItemPosition()==0){
				selectedRecType="System Recommend";
				lv.setVisibility(View.VISIBLE);
				lv1.setVisibility(View.GONE);
			}
			if(parent.getSelectedItemPosition()==1){
				selectedRecType="Friend Recommend";
				lv1.setVisibility(View.VISIBLE);
				lv.setVisibility(View.GONE);
			}
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			selectedRecType = "System Recommend";	
			lv.setVisibility(View.VISIBLE);
			lv1.setVisibility(View.GONE);
		}
   });
  
     
	if(isSignin()){
		//showDialog("Loading data, please wait...");
		//new RecommendTalkLoad().execute();	
		
			initViews();
	

		}
	else{
		new AlertDialog.Builder(guess.this)
		.setMessage("personal game recommend need to login, do you want to sign in first?")
		.setPositiveButton("yes", 
			    		 new DialogInterface.OnClickListener(){ 
						@Override
						public void onClick(DialogInterface dialoginterface, int i){ 
							dialoginterface.cancel();
							guess.this.finish();	
							Intent in = new Intent(guess.this, Signin.class);							
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
		@SuppressWarnings("unchecked")
		public void onClick(View view) {
				if(isSignin()){
					//showDialog("Loading data, please wait...");
					if(selectedRecType.equals("System Recommend"))
					{	new RecommendTalkLoad().execute();
					}else {
						//new BookmarkLoadTask().execute();
					}
					}
				else{
					new AlertDialog.Builder(guess.this)
					.setMessage("personal game recommend need to login, do you want to sign in first?")
					.setPositiveButton("yes", 
						    		 new DialogInterface.OnClickListener(){ 
									@Override
									public void onClick(DialogInterface dialoginterface, int i){ 
										dialoginterface.cancel();
										guess.this.finish();	
										Intent in = new Intent(guess.this, Signin.class);							
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
	
	//help = (ImageButton) this.findViewById(R.id.ImageButton02);
	//help.setVisibility(View.INVISIBLE);
	 friendView=(ListView)findViewById(R.id.FriendList);
     friendView.setVisibility(View.INVISIBLE);
	//SlidingDrawer 
	  sd = (SlidingDrawer)findViewById(R.id.slidingDrawer1);
	  sd.setOnDrawerOpenListener

	         (new SlidingDrawer.OnDrawerOpenListener()
   {

    @Override

	       public void onDrawerOpened()

	            {
   
	               friendView.setVisibility(View.VISIBLE);
	             }

	         });
	        sd.setOnDrawerCloseListener

	         (new SlidingDrawer.OnDrawerCloseListener()

	         {

	           @Override

	         public void onDrawerClosed()

	           {

	           friendView.setVisibility(View.INVISIBLE);

	           }

	    });
	         sd.setOnClickListener(new OnClickListener() {
				
			@Override
			public void onClick(View v) {
					// TODO Auto-generated method stub
					friendView.setVisibility(View.VISIBLE);
					
				}
			});
}


	
	

public boolean onCreateOptionsMenu(Menu menu) {
	menu.add(0, MENU_MENU, 0, "Main Menu").setIcon(R.drawable.menu_s);
	menu.add(0, MENU_HOME, 0, "Browse talk").setIcon(R.drawable.home_s);
	menu.add(0, MENU_SERIES, 0, "Series").setIcon(R.drawable.series_s);
	menu.add(0, MENU_GROUP, 0, "Group").setIcon(R.drawable.groups_s);
	menu.add(0, MENU_SCHEDULE, 0, "Bookmarked").setIcon(R.drawable.schedule_s);
	menu.add(0, MENU_LOGIN, 0, "Search").setIcon(R.drawable.search_s);
	menu.add(0, MENU_GAME, 0, "Game").setIcon(R.drawable.comet_logo);
	return true;
}

public boolean onOptionsItemSelected(MenuItem item) {
	Intent itemintent = new Intent();
	switch (item.getItemId()) {
	case MENU_MENU:
		this.finish();
		itemintent.setClass(guess.this, MainInterface.class);
		startActivity(itemintent);
		return true;
	case MENU_HOME:
		this.finish();
		itemintent.setClass(guess.this, Browsetab.class);
		startActivity(itemintent);
		return true;
	case MENU_SERIES:
		this.finish();
		itemintent.setClass(guess.this, series.class);
		startActivity(itemintent);
		return true;
	case MENU_GROUP:
		this.finish();
		itemintent.setClass(guess.this, groups.class);
		startActivity(itemintent);
		return true;
	case MENU_SCHEDULE:
		this.finish();
		itemintent.setClass(guess.this, MyBookmarked.class);
		startActivity(itemintent);
		return true;
	case MENU_LOGIN:
		this.finish();
		itemintent.setClass(guess.this, Search.class);
		startActivity(itemintent);
		return true;
	case MENU_CAL:
		this.finish();
		itemintent.setClass(guess.this, calendar.class);
		startActivity(itemintent);
		return true;
	case MENU_GAME:
		this.finish();
		itemintent.setClass(guess.this, gameTab.class);
		startActivity(itemintent);
		return true;
	}
	return false;
}
public void initViews(){
	recommendedTalks = new ArrayList<Talk>();
	recommendedTalks = getRecommendedTalksFromDB(uid);
	if(recommendedTalks.size() != 0){
		//status = (TextView) findViewById(R.id.status);
		//status.setVisibility(View.GONE);
		adapter = new ListAdapter(recommendedTalks);
		this.lv.setAdapter(adapter);
		this.lv.setOnItemClickListener(adapter);
	}
	
	//new RecommendTalkLoad().execute();
	
	
	bookmarkedTalks = new ArrayList<Talk>();
	bookmarkedTalks = getBookmarkedTalksFromDB(uid);
	 if(bookmarkedTalks.size() != 0){
			//status = (TextView) findViewById(R.id.status);
			//status.setVisibility(View.GONE);
			adapter = new ListAdapter(bookmarkedTalks);
			this.lv1.setAdapter(adapter);
			
			this.lv1.setOnItemClickListener(adapter);}
	       //new BookmarkLoadTask().execute();
}

     
     public ArrayList<Talk> getBookmarkedTalksFromDB(String userid){
    		ArrayList<Talk> t = new ArrayList<Talk>();
    		db.open();
    		t = db.getMyBookmarkedTalks(userid);
    		db.close();
    		return t;
    	}
    	public ArrayList<Talk> getBookmarkedTalksFromServer(String userid){
    		ArrayList<Talk> t =new ArrayList<Talk>();

    		TalkParser tp = new TalkParser ();
    		t = tp.getbookmarkedTalks(userid);
    		server_return= tp.status;

    		return t;
    	}
public ArrayList<Talk> getRecommendedTalksFromDB(String userid){
	ArrayList<Talk> t = new ArrayList<Talk>();
	db.open();
	t = db.getMyRecommendedTalks(userid);
	db.close();
	return t;
}
public ArrayList<Talk> getRecommendedTalksFromServer(String userid){
	ArrayList<Talk> t =new ArrayList<Talk>();

	TalkParser tp = new TalkParser ();
	t = tp.getRecommendTalks(userid);
	server_return = tp.status;
	
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
public boolean isSignin(){
	SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
	boolean signin = userinfo.getBoolean("userSignin",false);
	name = userinfo.getString("userName", "The first time");
	uid = userinfo.getString("userID", "0");
	return signin;
	
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
			Intent in = new Intent(guess.this, Talktab.class);
			in.putExtra("id", tlist.get(pos).id);				
			startActivity(in);
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

	private class RecommendTalkLoad extends AsyncTask<Void,Integer,Integer>{
		
		@Override
	protected Integer doInBackground(Void... arg0) {
			 //TODO Auto-generated method stub
			if(isConnect(guess.this)){
				publishProgress(4);
			recommendedTalks = getRecommendedTalksFromServer(uid);
			
			if(recommendedTalks.size() == 0 && server_return.compareTo("ok") == 0){
			publishProgress(0);
			}
			else if(recommendedTalks.size() == 0 && server_return.compareTo("ok") != 0){
			publishProgress(2);
			}
			else{
			try{
				db.open();
				db.updateToDefaultRecommend();
				db.daleteMyRecommended(uid);
				for(int i =0; i<recommendedTalks.size(); i++){
					long error = db.insertRecommendedTalk(recommendedTalks.get(i));
					db.insertMyRecommended(recommendedTalks.get(i), uid);
					if(error == -1)
						db.updateRecommendTalk(recommendedTalks.get(i));
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
				dismissDialog();
				//status = (TextView) findViewById(R.id.status);
				//status.setVisibility(View.VISIBLE);
				pb.setVisibility(View.GONE);
				navi.setText("No recommended talks, please try to schedule more");
				navi.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lightbulb, 0, 0, 0);
				
				break;
			case 1:
				dismissDialog();
				//status = (TextView) findViewById(R.id.status);
				//status.setVisibility(View.GONE);
				pb.setVisibility(View.GONE);
				adapter = new ListAdapter(recommendedTalks);
			guess.this.lv.setAdapter(adapter);
		guess.this.lv.setOnItemClickListener(adapter);
			
				break;
		case 2:
				dismissDialog();
				//status = (TextView) findViewById(R.id.status);
				//status.setVisibility(View.VISIBLE);
				navi.setText("Fail to connect to server, please check your internet connection and try again.");
				navi.setCompoundDrawablesWithIntrinsicBounds(R.drawable.server_error, 0, 0, 0);
				pb.setVisibility(View.GONE);
				Toast.makeText(getApplicationContext(), "Fail to connect to server, please check your internet connection and try again.",
					      Toast.LENGTH_SHORT).show();
				break;
			case 3:
			pb.setVisibility(View.GONE);
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
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}





	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		
	}





	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
}
	
	
	
/*private class BookmarkLoadTask<adapter> extends AsyncTask<Void,Integer,Integer>{
		
		@Override
		protected Integer doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			//if(isConnect(mygamerec.this)){
			//	publishProgress(4);
			//bookmarkedTalks = getBookmarkedTalksFromServer(uid);
			
			//if(bookmarkedTalks.size() == 0 && server_return.compareTo("ok") == 0){
			//publishProgress(0);
			//}
			//else if(bookmarkedTalks.size() == 0 && server_return.compareTo("ok") != 0){
			//publishProgress(2);
			//}
			//else{
			//try{
				//db.open();
				//db.updateToDefaultBookmark();
				//db.daleteMyBookmarked(uid);
				//for(int i =0; i<bookmarkedTalks.size(); i++){
					//long error = db.insertBookmarkedTalk(bookmarkedTalks.get(i));
					//db.insertMyBookmarked(bookmarkedTalks.get(i), uid);
					//if(error == -1)
						//db.updateBookmarkedTalk(bookmarkedTalks.get(i));
			//	}
				//db.close();
			//}
			//catch (Exception e) {
			//	System.out.print(e.getMessage());
			//}
			//publishProgress(1);
			//}
			//}
			//else
			//	publishProgress(3);
			//return 1;
			
		//}
		//protected void onProgressUpdate(Integer... progress) {
			//int command = progress[0];
			//case 0:
				//dismissDialog();
				//status = (TextView) findViewById(R.id.status);
				//status.setVisibility(View.VISIBLE);
				//pb.setVisibility(View.GONE);
				//navi.setText("No bookmarked talks this month, please try to schedule more.");
				//navi.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lightbulb, 0, 0, 0);
				
			//	break;
			//case 1:
				//dismissDialog();
				//status = (TextView) findViewById(R.id.status);
				//status.setVisibility(View.GONE);
			//	pb.setVisibility(View.GONE);
				//adapter = new ListAdapter(bookmarkedTalks);
				//mygamerec.this.lv1.setAdapter(adapter);
			//	mygamerec.this.lv1.setOnItemClickListener(adapter);
				//break;
			//case 2:
				//dismissDialog();
				//status = (TextView) findViewById(R.id.status);
				//status.setVisibility(View.VISIBLE);
				//navi.setText("Fail to connect to server, please check your internet connection and try again.");
				//navi.setCompoundDrawablesWithIntrinsicBounds(R.drawable.server_error, 0, 0, 0);
				//pb.setVisibility(View.GONE);
				//Toast.makeText(getApplicationContext(), "Fail to connect to server, please check your internet connection and try again.",
				//	      Toast.LENGTH_SHORT).show();
			//	break;
		//	case 3:
				//pb.setVisibility(View.GONE);
				//Toast.makeText(getApplicationContext(), "Fail to connect to server, please check your internet connection and try again.",
				//	      Toast.LENGTH_SHORT).show();
				//break;
			//case 4:
				//pb.setVisibility(View.VISIBLE);
			//	break;
			//default:
			//	break;
		//}
	//}
	//}
		
//}

*/
	