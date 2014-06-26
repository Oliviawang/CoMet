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


import util.Score;

public class getOnlineScore{
        public getOnlineScore() {
		// TODO Auto-generated constructor stub
		Log.i("getonlinescore", "get");
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
	public Score getServerData(String uid) throws JSONException {
		Score s=new Score();
		String JsonString = readContent("http://10.0.2.2:8081/getOnlineScore.php?uid="+uid+"");
		Log.i("jsonObject", JsonString);
        JSONArray jarry=new JSONArray(JsonString);
        JSONObject jsonObject=(JSONObject) jarry.get(0);
		s.setUserID(uid);
		s.setofflineScore(Float.parseFloat(jsonObject.getString("offline_score")));
		s.setOnlineScore(Float.parseFloat(jsonObject.getString("online_score")));
		Log.i("user1 getscore", jsonObject.getString("online_score"));
		
		return s;
	
	}
}