package com.androidquery.test.async;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.test.RunSourceActivity;
import com.androidquery.test.TestQuery;
import com.androidquery.util.AQUtility;

public class AjaxAuthActivity extends RunSourceActivity {

	private String type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		type = getIntent().getStringExtra("type");
			
	}
	
	@Override
	protected void runSource(){
		
		AQUtility.debug("run", type);
		
		AQUtility.invokeHandler(this, type, false, null);
	}
	
	public void auth_pick_account(){
	    		
		progress(true);
		
		String url = "http://www.google.com/reader/atom/user/-/state/com.google/reading-list";
		
		AjaxCallback<String> cb = new AjaxCallback<String>();
  
		cb.url(url).type(String.class).weakHandler(this, "stringCb");  
		cb.auth(this, "reader", null);
  
		aq.ajax(cb);
	        
	}	
	
	public void auth_last_account(){
		
		progress(true);
		
		String url = "http://www.google.com/reader/atom/user/-/state/com.google/reading-list";
		
		AjaxCallback<String> cb = new AjaxCallback<String>();
  
		cb.url(url).type(String.class).weakHandler(this, "stringCb");  
		cb.auth(this, "reader", AQuery.ACTIVE_ACCOUNT);
  
		aq.ajax(cb);
	        
	}	
	
	public void auth_specific_account(){
		
		progress(true);
		
		String url = "http://www.google.com/reader/atom/user/-/state/com.google/reading-list";
		
		AjaxCallback<String> cb = new AjaxCallback<String>();
  
		cb.url(url).type(String.class).weakHandler(this, "stringCb");  
		cb.auth(this, "reader", AQuery.ACTIVE_ACCOUNT);
  
		aq.ajax(cb);
	        
	}	
	
	public void stringCb(String url, String str, AjaxStatus status) {
		
		progress(false);   
		
		if(status.getCode() == 401){
			showResult("Authenticate Error with Http Response 401");
		}else{		
			showResult(str);
		}
	}

	
}
