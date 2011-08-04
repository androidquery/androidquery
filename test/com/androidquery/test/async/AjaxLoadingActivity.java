package com.androidquery.test.async;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.os.Bundle;

import com.androidquery.R;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.test.RunSourceActivity;
import com.androidquery.util.AQUtility;

public class AjaxLoadingActivity extends RunSourceActivity {

	private String type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		type = getIntent().getStringExtra("type");
			
	}
	
	@Override
	protected void runSource(){
		
		AQUtility.debug("type", type);
		
		AQUtility.invokeHandler(this, type, false, null);
	}
	
	public void async_json(){
	    
        String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
        
        progress(true);
        
        aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                   
            	progress(false);
            	
                showResult(json);
               
            }
        });
	        
	}	
	
	public void async_html(){
	    
		String url = "http://www.google.com";

		progress(true);
		
		aq.ajax(url, String.class, new AjaxCallback<String>() {

	        @Override
	        public void callback(String url, String html, AjaxStatus status) {
	             
	        	progress(false);
	        	
	        	showResult(html);
	        }
		        
		});
	        
	}
	
	
	public void async_bytes(){
	    
		String url = "http://www.vikispot.com/z/images/vikispot/android-w.png";

		progress(true);
		
		aq.ajax(url, byte[].class, new AjaxCallback<byte[]>() {

	        @Override
	        public void callback(String url, byte[] object, AjaxStatus status) {
	        	
	        	progress(false);
	        	
	        	showResult("bytes array length:" + object.length);
	        }
		});
	        
	}
	
	
	public void async_post(){
		
        String url = "http://search.twitter.com/search.json";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("q", "androidquery");
		
        progress(true);
        
        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                   
            	progress(false);
            	
                showResult(json);
               
            }
        });
		
		
	}
	
}
