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

import util.Onlinefriend;
import util.game1;
import util.game2;
import util.onlinegameTopic;





import android.os.AsyncTask;
import android.util.Log;



public class InsertGame1Topic{
public ArrayList<game2>  game;

public String errorMessage;
public JSONArray jarr;
         public InsertGame1Topic() {
			// TODO Auto-generated constructor stub
		Log.i("insert game1", "game1");
	}
	
         class connectionTask extends AsyncTask<JSONObject, Void, String> {
        	
        	 String uid1;
     		String uid2;
     		String col;
     		String flag;
     		long time;
     		 String	data;
     		@Override
     		protected String doInBackground(JSONObject... jsonObjects) {
     			// TODO Auto-generated method stub
     			try {
     				uid1=jsonObjects[0].getString("user1_id");
     				uid2=jsonObjects[0].getString("user2_id");
     				col=jsonObjects[0].getString("col_id");
     				flag=jsonObjects[0].getString("flag");
     				time=jsonObjects[0].getLong("launch_time");
     				URL url = new URL(
     						"http://10.0.2.2:8081/game1_insert.php?user1_id="+uid1+"&user2_id="+uid2+"&col_id="+col+"&time="+time+"&flag="+flag+"");
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
	public ArrayList<game1> uploadServerData(String uid1,String uid2,String colid,Long time, String flag) {
		ArrayList<game1> output = new ArrayList<game1>();
	   

		try {
						JSONObject json = new JSONObject();
						json.put("user1_id", uid1);
						json.put("user2_id", uid2);
						json.put("col_id", colid);
						json.put("flag",flag);
						json.put("launch_time", time);
					
						 jarr = new JSONArray();
						jarr.put(json);
						
						connectionTask ct = new connectionTask();
						ct.execute(json);
					/* insert datastructure*/
						game1 u = new game1();
						u.setUser1ID(uid1);
					//	u.setUser2ID(uid2);
						u.setID(colid);
						u.setFlag(flag);
						output.add(u);
						
					Log.i("login", "1");
			
		} catch(JSONException e) {
			Log.i("QueryErr", e.getMessage());
			errorMessage=e.getMessage();
		}
		return output;
	
	}
	
}