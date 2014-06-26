package comet.com;


import android.R.integer;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

public class Talktab extends TabActivity implements OnClickListener{

	private String id;
	private ProgressBar pb;
	private TextView navi,message,scoreTextView,totalscore;
	private ImageButton refresh, help, search,letterButton;
	
	private final int MENU_MENU = Menu.FIRST;
	private final int MENU_HOME = Menu.FIRST + 1;
	private final int MENU_SERIES = Menu.FIRST + 2;
	private final int MENU_GROUP = Menu.FIRST + 3;
	private final int MENU_SCHEDULE = Menu.FIRST + 4;
	private final int MENU_RECOMMEND = Menu.FIRST+5;
	private final int MENU_GAME= Menu.FIRST+6;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.talks_tab);
		
		Intent intent;    
		   
		Bundle b = getIntent().getExtras();
		if (b != null) {
			id = b.getString("id");
		}  
		
		pb = (ProgressBar) this.findViewById(R.id.widget108);
		pb.setVisibility(View.GONE);
		
		navi = (TextView) this.findViewById(R.id.navi);
		navi.setVisibility(View.GONE);
		
		refresh = (ImageButton) this.findViewById(R.id.ImageButton03);
		refresh.setVisibility(View.GONE);
		
		search = (ImageButton) this.findViewById(R.id.ImageButton01);
		search.setOnClickListener(this);
		
		
		message=(TextView)this.findViewById(R.id.message);
		message.setVisibility(View.INVISIBLE);
		
		 scoreTextView=(TextView)findViewById(R.id.totalview);
		 scoreTextView.setVisibility(View.INVISIBLE);
		    totalscore=(TextView)findViewById(R.id.totalscore);
		    totalscore.setVisibility(View.INVISIBLE);
		    
		    letterButton=(ImageButton)findViewById(R.id.mail);
		    letterButton.setVisibility(View.INVISIBLE);
		//help = (ImageButton) this.findViewById(R.id.ImageButton02);
		//help.setVisibility(View.GONE);
		
		// Set up the tabs
        TabHost host = getTabHost();//(TabHost) findViewById(R.id.tabhost);
       LinearLayout l = (LinearLayout) host.getChildAt(0);
        TabWidget tw = (TabWidget)l.getChildAt(0);  
        //host.setup();
      
        //Detail tab
        intent = new Intent().setClass(this, TalkDetail.class);
        Bundle b1= new Bundle();
        b1.putString("id", id);
        intent.putExtras(b1);
        
       RelativeLayout tab1 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_style, tw, false);  
        TextView text1 = (TextView) tab1.getChildAt(1);
        text1.setText("Detail"); 
        TabSpec detail = host.newTabSpec("Detail");
        detail.setIndicator(tab1);
        detail.setContent(intent);
        
        host.addTab(detail);
        
     //Attending tab
        intent = new Intent().setClass(this, TalkTags.class); 
        intent.putExtras(b1);
        TabSpec tags = host.newTabSpec("Tags");
        
        RelativeLayout tab2 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_style, tw, false);  
        TextView text2 = (TextView) tab2.getChildAt(1);
        text2.setText("Tags"); 
        
        tags.setIndicator(tab2);
        tags.setContent(intent);
        host.addTab(tags);
        
        //Similar tab
        intent = new Intent().setClass(this, TalkComments.class);
        intent.putExtras(b1);
        TabSpec comments = host.newTabSpec("Comments");
        
        RelativeLayout tab3 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_style, tw, false);  
        TextView text3 = (TextView) tab3.getChildAt(1);
        text3.setText("Comments");
        
        comments.setIndicator(tab3);
        comments.setContent(intent);
        host.addTab(comments); 
	//set up default tab
		host.setCurrentTabByTag("Detail");
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
			itemintent.setClass(Talktab.this, MainInterface.class);
			startActivity(itemintent);
			return true;
		case MENU_HOME:
			this.finish();
			itemintent.setClass(Talktab.this, Browsetab.class);
			startActivity(itemintent);
			return true;
		case MENU_SERIES:
			this.finish();
			itemintent.setClass(Talktab.this, series.class);
			startActivity(itemintent);
			return true;
		case MENU_GROUP:
			this.finish();
			itemintent.setClass(Talktab.this, groups.class);
			startActivity(itemintent);
			return true;
		case MENU_SCHEDULE:
			this.finish();
			itemintent.setClass(Talktab.this, MyBookmarked.class);
			startActivity(itemintent);
			return true;
		case MENU_RECOMMEND:
			this.finish();
			itemintent.setClass(Talktab.this, MyRecommend.class);
			startActivity(itemintent);
			return true;
		case MENU_GAME:
			this.finish();
			itemintent.setClass(Talktab.this, gameTab.class);
			startActivity(itemintent);
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()){
		case R.id.ImageButton01:
			this.finish();
			Intent in = new Intent(Talktab.this,Search.class);
			startActivity(in);
			break;
		default:
			break;
		}
	}
}
