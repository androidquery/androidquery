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
		
		//runSource();
		WebView wv = aq.id(R.id.web).getWebView();
		wv.loadUrl("http://m.facebook.com");
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
