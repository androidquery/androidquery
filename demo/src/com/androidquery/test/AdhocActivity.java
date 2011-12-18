package com.androidquery.test;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;

public class AdhocActivity extends Activity {

	private AQuery aq;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.adhoc_activity);
		
		aq = new AQuery(this);
		
		debug();
		work();
		
	}
	
	private void debug(){
		/*
		WebView wv = aq.id(R.id.web).getWebView();
		
		wv.setWebChromeClient(new WebChromeClient() {
			  public boolean onConsoleMessage(ConsoleMessage cm) {
			    AQUtility.debug(cm.message() + " -- From line "
			                         + cm.lineNumber() + " of "
			                         + cm.sourceId() );
			    return true;
			  }
			});
		
		wv.setWebViewClient(new WebViewClient(){
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				AQUtility.debug("onPageStarted", url);
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
			
				AQUtility.debug("onReceivedError");
			}
			
			@Override
			public void onScaleChanged(WebView view, float oldScale,
					float newScale) {
				AQUtility.debug("onScaleChanged", oldScale + ":" + newScale);
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				AQUtility.debug("onPageFinished");
			}
			
		});
		*/
	}
	private void work(){
		
    	aq.id(R.id.button).clicked(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String url = "http://farm4.static.flickr.com/3531/3769416703_b76406f9de.jpg";    	    	
				aq.id(R.id.web).clear().progress(R.id.progress).webImage(url);
				//url = "http://192.168.1.222/test/test.htm";
				//aq.id(R.id.web).setLayerType11(AQuery.LAYER_TYPE_SOFTWARE, null).getWebView().loadUrl(url);
			}
		});
    	
    	
	}
	
	
	protected int getContainer(){
		return R.layout.adhoc_activity;
	}
	
	
	

}
