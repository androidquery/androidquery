package com.androidquery.test.async;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.test.RunSourceActivity;
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
		cb.auth(this, AQuery.AUTH_READER, null);
  
		aq.ajax(cb);
	        
	}	
	
	public void auth_last_account(){
		
		progress(true);
		
		String url = "http://www.google.com/reader/atom/user/-/state/com.google/reading-list";
		
		AjaxCallback<String> cb = new AjaxCallback<String>();
  
		cb.url(url).type(String.class).weakHandler(this, "stringCb");  
		cb.auth(this, AQuery.AUTH_READER, AQuery.ACTIVE_ACCOUNT);
  
		aq.ajax(cb);
	        
	}	
	
	public void auth_specific_account(){
		
		progress(true);
		
		String url = "http://www.google.com/reader/atom/user/-/state/com.google/reading-list";
		
		AjaxCallback<String> cb = new AjaxCallback<String>();
  
		cb.url(url).type(String.class).weakHandler(this, "stringCb");  
		cb.auth(this, AQuery.AUTH_READER, AQuery.ACTIVE_ACCOUNT);
  
		aq.ajax(cb);
	        
	}	
	
	public void auth_picasa(){
		
		progress(true);
		
		String url = "http://picasaweb.google.com/data/feed/api/user/default?alt=json";
		
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
  
		cb.url(url).type(JSONObject.class).weakHandler(this, "picasaCb");  
		cb.auth(this, AQuery.AUTH_PICASA, AQuery.ACTIVE_ACCOUNT);
  
		aq.ajax(cb);
	        
	}	
	
	public void picasaCb(String url, JSONObject jo, AjaxStatus status) {
	
		progress(false);
		
		showResult(jo);
		
		if(jo != null){
			
			JSONArray entries = jo.optJSONObject("feed").optJSONArray("entry");
			
			AQUtility.debug(entries.toString());
						
			for(int i = 0; i < entries.length(); i++){
				JSONObject entry = entries.optJSONObject(i);
				JSONObject co = entry.optJSONObject("gphoto$numphotos");
				int count = co.optInt("$t", 0);
				if(count > 0){
					String tb = entry.optJSONObject("media$group").optJSONArray("media$content").optJSONObject(0).optString("url");
					AQUtility.debug("tb", tb);
					
					aq.id(R.id.image).image(tb);
					break;
				}
			}
			
			
		}
	
	}
	
	public void auth_youtube(){
		
		progress(true);
		
		String url = "https://gdata.youtube.com/feeds/api/users/default/subscriptions?v=2&alt=json";
		//String url = "http://gdata.youtube.com/feeds/api/users/default?v=2&alt=json";
		
		//String url = "http://gdata.youtube.com/schemas/2007#user.newsubscriptionvideos";
		
		
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>(); 
		cb.url(url).type(JSONObject.class).weakHandler(this, "youtubeCb");  
		
		
		/*
		AjaxCallback<String> cb = new AjaxCallback<String>(); 
		cb.url(url).type(String.class).weakHandler(this, "stringCb"); 
		*/
		cb.auth(this, AQuery.AUTH_YOUTUBE, AQuery.ACTIVE_ACCOUNT);
  
		aq.ajax(cb);
	        
	}
	
	public void youtubeCb(String url, JSONObject jo, AjaxStatus status) {
		
		progress(false);
		
		if(jo != null){
			AQUtility.debug(jo);
			showResult(jo);
		}
	
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
