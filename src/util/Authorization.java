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

import android.R.integer;

public class Authorization {
	public boolean isLoginOK = false;
	public boolean isLogoutOK = false;
	public boolean isSignUp = false;
	public String userID=null;
	public String userName=null;
	public String errorMessage = null;
	public int[] onlineID=null;

	public void login(String email, String password) {
		
		String url = "http://halley.exp.sis.pitt.edu/comet/authentication/loginXML.jsp?";
		HttpPost httpRequest = new HttpPost(url);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userEmail", email));
		params.add(new BasicNameValuePair("password", password));
		try {

			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);

			//System.out.print(httpResponse.getStatusLine().getStatusCode());
			if (httpResponse.getStatusLine().getStatusCode() == 200) {

				String result = EntityUtils.toString(httpResponse.getEntity());
				System.out.println(result);
				int start = result.indexOf("<authentication>");
				int end = result.indexOf("</authentication>");
				String status;
				//end = result.indexOf("\r\n");
				status = result.substring(start+16, start+18);
				if(status.compareTo("OK")==0)
				{
					isLoginOK = true;
					String s[] = result.substring(start+18,end).split("\n");
					userID = s[1];
					userName = s[2];
				}
				else
				{
					isLoginOK = false;
					errorMessage = result.substring(start+16, end);
				}
				
			} else {
				isLoginOK = false;
				errorMessage="error: status code not 200";
			}
		} catch (Exception e) {
			isLoginOK = false;
			errorMessage= e.toString();
		}

	}
	
	public boolean logout() {
		String url = "http://halley.exp.sis.pitt.edu/comet/authentication/logoutXML.jsp?";
		HttpPost httpRequest = new HttpPost(url);

		try {

			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);

			//System.out.print(httpResponse.getStatusLine().getStatusCode());
			if (httpResponse.getStatusLine().getStatusCode() == 200) {

				String result = EntityUtils.toString(httpResponse.getEntity());
				System.out.println(result);
				int start = result.indexOf("<status>");
				int end = result.indexOf("</status>");
				String status;
				//end = result.indexOf("\r\n");
				status = result.substring(start+8, end);
				if(status.compareTo("OK")==0)
				{
					isLogoutOK = true;
					int ms = result.indexOf("<message>");
					int me = result.indexOf("</message>");
					errorMessage = result.substring(ms+9,me);
				}
				else
				{
					isLoginOK = false;
					
					int ms = result.indexOf("<message>");
					int me = result.indexOf("</message>");
					errorMessage = result.substring(ms+9,me);
				}
				
			} else {
				isLoginOK = false;
				errorMessage ="error: status code not 200";
			}
		} catch (Exception e) {
			isLoginOK = false;
			errorMessage = e.toString();
		}
		return isLogoutOK;
	}
	public int[] online() {
		String url = "http://halley.exp.sis.pitt.edu/comet/authentication/onlineXML.jsp?";
		HttpPost httpRequest = new HttpPost(url);

		try {

			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);

			//System.out.print(httpResponse.getStatusLine().getStatusCode());
			if (httpResponse.getStatusLine().getStatusCode() == 200) {

				String result = EntityUtils.toString(httpResponse.getEntity());
				System.out.println(result);
				int start = result.indexOf("<authentication>");
				int end = result.indexOf("</authentication>");
				String status;
				//end = result.indexOf("\r\n");
				
				
				
					String s[] = result.substring(start+16,end).split("\n");
					for(int i=0;i<s.length;i++)
					{
					onlineID[i]=Integer.parseInt(s[i]);;
					
					}
				
		} }catch (Exception e) {
			isLoginOK = false;
			errorMessage= e.toString();
		}

		return onlineID;
	}
}

