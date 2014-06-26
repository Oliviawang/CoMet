package comet.com;

import android.webkit.WebView;
import android.webkit.WebViewClient;

public class OurViewClient extends WebViewClient {

	public boolean UpdatewebUrl(WebView v, String url){
		
		v.loadUrl(url);
		return true;
	}
}
