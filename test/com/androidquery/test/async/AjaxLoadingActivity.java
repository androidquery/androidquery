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
        
        AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                 
                showResult(json);
               
            }
        };
        
        cb.url(url).type(JSONObject.class).fileCache(true);
        aq.progress(R.id.progress).ajax(cb);
        
	        
	}	
	
	public void async_html(){
	    
		String url = "http://www.google.com";

		aq.progress(R.id.progress).ajax(url, String.class, new AjaxCallback<String>() {

	        @Override
	        public void callback(String url, String html, AjaxStatus status) {
	             
	        	showResult(html);
	        }
		        
		});
	        
	}
	
	
	public void async_bytes(){
	    
		String url = "http://www.vikispot.com/z/images/vikispot/android-w.png";

		aq.progress(R.id.progress).ajax(url, byte[].class, new AjaxCallback<byte[]>() {

	        @Override
	        public void callback(String url, byte[] object, AjaxStatus status) {
	        	
	        	showResult("bytes array length:" + object.length);
	        }
		});
	        
	}
	
	
	public void async_post(){
		
        String url = "http://search.twitter.com/search.json";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("q", "androidquery");
		
        aq.progress(R.id.progress).ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                
                showResult(json);
               
            }
        });
		
		
	}
	
	public void async_method_cb(){
	    
        String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
                
        aq.progress(R.id.progress).ajax(url, JSONObject.class, this, "jsonCb");
           
	}	
	
	public void async_cached(){
	    
		String url = "http://www.google.com";

		long expire = 15 * 60 * 1000;
		
		aq.progress(R.id.progress).ajax(url, String.class, expire, new AjaxCallback<String>() {

	        @Override
	        public void callback(String url, String html, AjaxStatus status) {
	             
	        	showProgress(false);
	        	
	        	showResult(html);
	        }
		        
		});
	        
	}
	
	public void async_progress(){
	    
        String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";                
        aq.progress(R.id.progress).ajax(url, JSONObject.class, this, "jsonCb");
           
	}	
	
	public void async_advance(){
	    
        String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
        
        AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();        
        cb.url(url).type(JSONObject.class).weakHandler(this, "jsonCb").fileCache(true).expire(0);
        
        aq.progress(R.id.progress).ajax(cb);
	        
	}	
	
	public void async_header(){
	    
        String url = "http://www.google.com";
        
        
        AjaxCallback<String> cb = new AjaxCallback<String>();        
        cb.url(url).type(String.class).weakHandler(this, "stringCb");
        
        cb.header("Referer", "http://code.google.com/p/android-query/");
        cb.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:6.0.2) Gecko/20100101 Firefox/6.0.2");
        
        aq.progress(R.id.progress).ajax(cb);
	        
	}	
	
	public void jsonCb(String url, JSONObject json, AjaxStatus status) {
		  	
        showResult(json);
	}
	
	public void stringCb(String url, String str, AjaxStatus status) {
		 	
        showResult(str);
	}
	
}
