package com.androidquery.test.async;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.os.Bundle;

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
        
        showProgress(true);
        
        AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                   
            	showProgress(false);
            	
                showResult(json);
               
            }
        };
        
        cb.url(url).type(JSONObject.class).fileCache(true);
        
        //aq.ajax(url, JSONObject.class, cb);
        aq.ajax(cb);
        
	        
	}	
	
	public void async_html(){
	    
		String url = "http://www.google.com";

		showProgress(true);
		
		aq.ajax(url, String.class, new AjaxCallback<String>() {

	        @Override
	        public void callback(String url, String html, AjaxStatus status) {
	             
	        	showProgress(false);
	        	
	        	showResult(html);
	        }
		        
		});
	        
	}
	
	
	public void async_bytes(){
	    
		String url = "http://www.vikispot.com/z/images/vikispot/android-w.png";

		showProgress(true);
		
		aq.ajax(url, byte[].class, new AjaxCallback<byte[]>() {

	        @Override
	        public void callback(String url, byte[] object, AjaxStatus status) {
	        	
	        	showProgress(false);
	        	
	        	showResult("bytes array length:" + object.length);
	        }
		});
	        
	}
	
	
	public void async_post(){
		
        String url = "http://search.twitter.com/search.json";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("q", "androidquery");
		
        showProgress(true);
        
        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                   
            	showProgress(false);
            	
                showResult(json);
               
            }
        });
		
		
	}
	
	public void async_method_cb(){
	    
        String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
        
        showProgress(true);        
        aq.ajax(url, JSONObject.class, this, "jsonCb");
           
	}	
	
	public void async_cached(){
	    
		String url = "http://www.google.com";

		showProgress(true);
		
		aq.ajax(url, String.class, 15 * 60 * 1000, new AjaxCallback<String>() {

	        @Override
	        public void callback(String url, String html, AjaxStatus status) {
	             
	        	showProgress(false);
	        	
	        	showResult(html);
	        }
		        
		});
	        
	}
	
	public void async_advance(){
	    
        String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
        
        showProgress(true);
        
        AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();        
        cb.url(url).type(JSONObject.class).weakHandler(this, "jsonCb").fileCache(true).expire(0);
        
        aq.ajax(cb);
	        
	}	
	
	public void jsonCb(String url, JSONObject json, AjaxStatus status) {
		showProgress(false);   	
        showResult(json);
	}
	
}
