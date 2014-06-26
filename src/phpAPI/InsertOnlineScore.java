package phpAPI;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import util.Score;
import util.game2;






import android.os.AsyncTask;
import android.util.Log;



public class InsertOnlineScore{
public ArrayList<game2>  game;

public String errorMessage;
public JSONArray jarr;
         public InsertOnlineScore() {
			// TODO Auto-generated constructor stub
		Log.i("push online", "po");
	}
	
         class connectionTask extends AsyncTask<JSONObject, Void, String> {
        	 String uid;
     		
     		 String	data;
     		@Override
     		protected String doInBackground(JSONObject... jsonObjects) {
     			// TODO Auto-generated method stub
     			try {
     				uid=jsonObjects[0].getString("user_id");
     			
     				URL url = new URL(
     						"http://10.0.2.2:8081/totalscore_ini.php?uid="+uid+"");
     			URLConnection conn = url.openConnection();
     				Log.i("conn", "pig5");
     				// System.out.println("Establishing connection");
     				conn.setDoOutput(true);
     				// conn.setDoInput(true);
     			HttpURLConnection	http = (HttpURLConnection) conn;
     				http.setRequestMethod("GET");
     				// http.setUseCaches(false);
     				http.setRequestProperty("Content-type", "application/json");
     				
     				// Object httpUrlConnection = setURLConnectionProperties(url);
     				Log.i("conn", "pig6");
     				// writer = new OutputStreamWriter(conn.getOutputStream());
     			OutputStreamWriter	writer = new OutputStreamWriter(http.getOutputStream());
     				Log.i("conn", "pig9");
     				writer.write(jsonObjects[0].toString());
     				// objOutputStrm.writeObject(j.toString());
     				writer.flush();
     				// objOutputStrm.flush();
     				// objOutputStrm.close();
     				Log.i("result", "succeed" + jsonObjects.toString());
     			// accept data

    			BufferedReader	reader = new BufferedReader(new InputStreamReader(
    						http.getInputStream()));
    				Log.i("conn", "read");
    				System.out.println("Retrieving data from response...");
    				data = reader.readLine();
    				System.out.println("received br:" + data);

    				if (data != null && data.contains("\\|")) {
    					data = data.substring(data.lastIndexOf("\\|") + 1,
    							data.length());
    				}

     			} catch (MalformedURLException e) {
     				// TODO Auto-generated catch block
     				e.printStackTrace();
     			} catch (IOException e) {
     				// TODO Auto-generated catch block
     				e.printStackTrace();
     			} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

     			return data;
     		}
		

     	}
	public Score insertServerData(String uid) {
		
		Score u = null ;
		try {
						JSONObject json = new JSONObject();
						json.put("user_id", uid);
					
						 jarr = new JSONArray();
						jarr.put(json);
						
						connectionTask ct = new connectionTask();
						ct.execute(json);
					/* insert datastructure*/
						 u = new Score();
						u.setUserID(uid);
						
					Log.i("login", "1");
			
		} catch(JSONException e) {
			Log.i("QueryErr", e.getMessage());
			errorMessage=e.getMessage();
		}
		return u;
	
	}
}
	
