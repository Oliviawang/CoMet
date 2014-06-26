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

public class EmailAction {

	public String sendEmail(String id, String email, String userid) {
		String status = "";
		String url = "http://halley.exp.sis.pitt.edu/comet/utils/sendEmailsXML.jsp?";
		HttpPost httpRequest = new HttpPost(url);
			
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("user_id", userid));
		params.add(new BasicNameValuePair("col_id", id));
		params.add(new BasicNameValuePair("emails", email));
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
					status = "yes";
				if (result
						.compareTo("<status>ERROR") == 0)
					status = "no";

			} else {
				// System.out.print("error: status code not 200");
			}
		} catch (Exception e) {
			System.out.print("exception" + e);
		}

		return status;

	}
}
