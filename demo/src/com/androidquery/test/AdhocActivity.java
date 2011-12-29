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
		
		work();
		
	}
	
	private void work(){
		
		String url = "http://www.vikispot.com/z/images/vikispot/android-w.png";
		aq.id(R.id.image2).image(url, true, true);
    	
    	aq.id(R.id.image3).image(url, true, true, 0, 0, null, AQuery.FADE_IN_NETWORK, 1.0f);
    	
    	
	}
	
	
	protected int getContainer(){
		return R.layout.adhoc_activity;
	}
	
	
	

}
