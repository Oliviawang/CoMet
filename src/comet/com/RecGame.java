package comet.com;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class RecGame extends Activity implements OnClickListener{

	public void onCreate(Bundle saveInstanceSateBundle){
		
		super.onCreate(saveInstanceSateBundle);
		setContentView(R.layout.recgame);
		Thread timerThread=new Thread(){
			public void run(){
				try{
					sleep(5000);
					
				}
				catch(InterruptedException e) {
					// TODO: handle exception
					e.printStackTrace();
				}finally{
					//Intent intent=new Intent(RecGame.this,);
					//startActivity(intent);
				}
			}
		}
		;
		timerThread.start();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
