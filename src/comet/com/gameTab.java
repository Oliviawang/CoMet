package comet.com;


import android.R.integer;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

public class gameTab extends TabActivity implements OnClickListener, OnTabChangeListener{

	private String id;
	private ProgressBar pb;
	private TextView navi, head;
	private ImageButton refresh, help, search;
	private String name,uid;
	private boolean signin;
	private final int MENU_MENU = Menu.FIRST;
	private final int MENU_HOME = Menu.FIRST + 1;
	private final int MENU_SERIES = Menu.FIRST + 2;
	private final int MENU_GROUP = Menu.FIRST + 3;
	private final int MENU_SCHEDULE = Menu.FIRST + 4;
	private final int MENU_RECOMMEND = Menu.FIRST+5;
	private final int MENU_GAME= Menu.FIRST+6;
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.game_tab);
		Intent intent;    
		   
		Bundle b = getIntent().getExtras();
		if (b != null) {
			id = b.getString("id");
		}  
	/*	//signin
		signin=false;
		pb = (ProgressBar) this.findViewById(R.id.widget108);
		pb.setVisibility(View.GONE);
		
		navi = (TextView) this.findViewById(R.id.navi);
		navi.setVisibility(View.VISIBLE);
		head = (TextView) findViewById(R.id.TextView01);
		
		refresh = (ImageButton) this.findViewById(R.id.ImageButton03);
		refresh.setVisibility(View.VISIBLE);

		
		
		
		search = (ImageButton) this.findViewById(R.id.ImageButton01);
		search.setOnClickListener(this);*/
		
		//help = (ImageButton) this.findViewById(R.id.ImageButton02);
		//help.setVisibility(View.GONE);
	
		// Set up the tabs
        
		
		TabHost host = getTabHost();//(TabHost) findViewById(R.id.tabhost);
        host.setup();
        host.setOnTabChangedListener(this);
        LinearLayout l = (LinearLayout) host.getChildAt(0);
        TabWidget tw = (TabWidget)l.getChildAt(0); 
        
      
        //Detail tab
        intent = new Intent().setClass(this, gamehome.class);
        RelativeLayout tab1 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_style, tw, false);  
        TextView text1 = (TextView) tab1.getChildAt(1);
        text1.setText("Home"); 
        TabSpec detail = host.newTabSpec("Home");
        detail.setIndicator(tab1);
        detail.setContent(intent);
        
        host.addTab(detail);
        
     //Attending tab
        intent = new Intent().setClass(this, guess2.class);
        TabSpec tags = host.newTabSpec("Guess");
        RelativeLayout tab2 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_style, tw, false);  
        TextView text2 = (TextView) tab2.getChildAt(1);
        text2.setText("Guess"); 
        
        tags.setIndicator(tab2);
        tags.setContent(intent);
        host.addTab(tags);
        
        //Similar tab
        intent = new Intent().setClass(this,game.class);
        TabSpec comments = host.newTabSpec("Game");
        RelativeLayout tab3 = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_style, tw, false);  
        TextView text3 = (TextView) tab3.getChildAt(1);
        text3.setText("Game");
       
        comments.setIndicator(tab3);
        comments.setContent(intent);
        host.addTab(comments); 
	//set up default tab
		host.setCurrentTabByTag("Home");  }

		public void onTabChanged(String tabId) {
			// TODO Auto-generated method stub
			if(tabId.compareTo("Home") == 0){
				
			
				//head.setText("Recomendation Game");
				//navi.setText("Top Recomendation");
			}
			else if(tabId.compareTo("Guess") == 0){
				
				
				//head.setText("Guess Game");
				//navi.setText("Guess who recommend topis to you");
			}
			else{
				//head.setText("Recomendation Game");
				//navi.setText("online game");
			}
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
			itemintent.setClass(gameTab.this, MainInterface.class);
			startActivity(itemintent);
			return true;
		case MENU_HOME:
			this.finish();
			itemintent.setClass(gameTab.this, Browsetab.class);
			startActivity(itemintent);
			return true;
		case MENU_SERIES:
			this.finish();
			itemintent.setClass(gameTab.this, series.class);
			startActivity(itemintent);
			return true;
		case MENU_GROUP:
			this.finish();
			itemintent.setClass(gameTab.this, groups.class);
			startActivity(itemintent);
			return true;
		case MENU_SCHEDULE:
			this.finish();
			itemintent.setClass(gameTab.this, MyBookmarked.class);
			startActivity(itemintent);
			return true;
		case MENU_RECOMMEND:
			this.finish();
			itemintent.setClass(gameTab.this, MyRecommend.class);
			startActivity(itemintent);
			return true;
		case MENU_GAME:
			this.finish();
			itemintent.setClass(gameTab.this, gameTab.class);
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
			Intent in = new Intent(gameTab.this,Search.class);
			startActivity(in);
			break;
		default:
			break;
		}
	}
}
