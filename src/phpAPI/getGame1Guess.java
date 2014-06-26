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
import util.game1;
import util.onlinegameTopic;

public class getGame1Guess{

	public  String url = "http://10.0.2.2:8081/game1_get.php?uid=1636"; 

	

        public getGame1Guess() {
		// TODO Auto-generated constructor stub
        	
		Log.i("getgame1Guess", "getrec");
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
	public ArrayList<game1> getServerData(String uid) {
		ArrayList<game1> output = new ArrayList<game1>();
		String JsonString = readContent("http://10.0.2.2:8081/game1_get.php?uid="+uid+"");

		try {
			JSONArray jarr = new JSONArray(JsonString);
		
					for (int j = 0; j < jarr.length(); j++) {
						JSONObject json = (JSONObject) jarr.get(j);
						game1 g1= new game1();
						g1.setID(json.getString("id"));
						g1.setTitle(json.getString("title"));
						g1.setDesc(json.getString("des"));
						g1.setName(json.getString("speaker"));
						g1.setUser1ID(json.getString("user1_id"));
						g1.setFlag(json.getString("rec_flag"));
						//ot.setName(json.getString("name"));
						output.add(g1);
						Log.i("output", g1.toString());
					}
				
		} catch (JSONException e) {
			Log.i("QueryErr", e.getMessage());
		}
		return output;
	}
}

