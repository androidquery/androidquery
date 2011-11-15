package com.androidquery.test;

import android.os.Bundle;
import android.webkit.WebView;

import com.androidquery.R;

public class AdhocActivity extends RunSourceActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		work();
	}
	
	
	private void work(){
		
		String url = "https://graph.facebook.com/oauth/authorize?type=user_agent&redirect_uri=https://www.facebook.com/connect/login_success.html&scope=read_stream,read_friendlists,manage_friendlists,manage_notifications,publish_stream,publish_checkins,offline_access,user_photos,user_likes,user_groups,friends_photos&client_id=251003261612555";
		
		//runSource();
		WebView wv = aq.id(R.id.web).getWebView();
		wv.loadUrl(url);
	}
	
	
	protected int getContainer(){
		return R.layout.adhoc_activity;
	}
	
	@Override
	protected void runSource(){
		
		try{
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	

}
