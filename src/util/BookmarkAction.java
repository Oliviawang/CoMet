package util;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import android.content.SharedPreferences;

public class BookmarkAction {

	public String addBookmark(String id, String userid) {

		String status = "";
		String url = "http://halley.exp.sis.pitt.edu/comet/utils/addBookmarkXML.jsp?";
		HttpPost httpRequest = new HttpPost(url);
		
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user_id", userid));
		params.add(new BasicNameValuePair("col_id", id));
		try {

			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);

			if (httpResponse.getStatusLine().getStatusCode() == 200) {

				String result = EntityUtils.toString(httpResponse.getEntity());
				int start = result.indexOf("<status>");
				int end = result.indexOf("</status>");
				result = result.substring(start+8, end);
				if (result
						.compareTo("OK") == 0)
					status = "yes";
				else
					status = result;

			} else {
				// System.out.print("error: status code not 200");
			}
		} catch (Exception e) {
			System.out.print("exception" + e);
		}

		return status;

	}
	public String removeBookmark(String id, String userid) {
		String status = "";
		String url = "http://halley.exp.sis.pitt.edu/comet/utils/removeBookmarkXML.jsp?";
		HttpPost httpRequest = new HttpPost(url);
			
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user_id", userid));
		params.add(new BasicNameValuePair("col_id", id));
		try {

			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);

			if (httpResponse.getStatusLine().getStatusCode() == 200) {

				String result = EntityUtils.toString(httpResponse.getEntity());
				int start = result.indexOf("<status>");
				int end = result.indexOf("</status>");
				result = result.substring(start, end);
				if (result
						.compareTo("<status>OK") == 0)
					status = "no";
				else
					status = result;

			} else {
				// System.out.print("error: status code not 200");
			}
		} catch (Exception e) {
			System.out.print("exception" + e);
		}

		return status;

	}
}
