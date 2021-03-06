package phpAPI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.TreeMap;

import org.apache.http.HttpConnection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import util.Onlinefriend;
import util.game2;
import util.game2receive;
import util.onlinegameTopic;

public class user2getOnline{
        public user2getOnline() {
		// TODO Auto-generated constructor stub
        	
		Log.i("User2getonlinetopic", "gettopics");
	}
	
	public String readContent(String url) {
		try {

			StringBuilder sb = new StringBuilder();
			URL u = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();

			//conn.setConnectTimeout(30000);
			conn.setRequestMethod("GET");

			InputStream is = conn.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			while (true) {

				String line = br.readLine();
				if (line == null) {
					break;
				}
				sb.append(line);
			}
			br.close();
			Log.i("content", sb.toString());
			return sb.toString();
		} catch (IOException e) {
			Log.i("QueryIOErr", e.getMessage());
			return e.getMessage();
		} catch (Exception e) {
			Log.i("QueryErr", e.getMessage());
			return e.getMessage();
		}
	}
	public ArrayList<game2receive> getServerData(String uid) {
		ArrayList<game2receive> output = new ArrayList<game2receive>();
		String JsonString = readContent("http://10.0.2.2:8081/game2_user2get.php?uid="+uid+"");

		try {
			  JSONArray jarr = new JSONArray(JsonString);
		
					for (int j = 0; j < jarr.length(); j++) {
						JSONObject json = (JSONObject) jarr.get(j);
						game2receive ot= new game2receive();
						
						ot.setColID(json.getString("col_id"));
						ot.setTitle(json.getString("title"));
						ot.setDesc(json.getString("detail"));
						ot.setUserID(json.getString("user1_id"));
					    ot.setName(json.getString("name"));
						//ot.setName(json.getString("name"));
						output.add(ot);
						Log.i("output", ot.toString());
					}
				
		} catch (JSONException e) {
			Log.i("QueryErr", e.getMessage());
		}
		return output;
	}
}

