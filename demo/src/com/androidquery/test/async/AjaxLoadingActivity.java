package com.androidquery.test.async;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.Transformer;
import com.androidquery.test.RunSourceActivity;
import com.androidquery.util.AQUtility;
import com.androidquery.util.XmlDom;
import com.google.gson.Gson;

public class AjaxLoadingActivity extends RunSourceActivity {

	private String type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		type = getIntent().getStringExtra("type");
			
		if("async_multipart".equals(type)){
			aq.id(R.id.go_run).gone();
			aq.id(R.id.result).gone();
		}
	}
	
	@Override
	protected void runSource(){
		
		AQUtility.debug("type", type);
		
		AQUtility.invokeHandler(this, type, false, false, null);
	}
	
	public void async_json(){
	    
		//AjaxCallback.setSSF(SSLSocketFactory.getSocketFactory());
		
        String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
        
        AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                 
                showResult(json, status);
               
            }
        };
        
        cb.url(url).type(JSONObject.class);
        aq.progress(R.id.progress).ajax(cb);
        
	        
	}	
	
	public void async_html(){
	    
		String url = "http://www.google.com";

		aq.progress(R.id.progress).ajax(url, String.class, new AjaxCallback<String>() {

	        @Override
	        public void callback(String url, String html, AjaxStatus status) {
	             
	        	showResult(html, status);
	        }
		        
		});
	        
	}
	
	
	public void async_bytes(){
	    
		String url = "http://www.vikispot.com/z/images/vikispot/android-w.png";

		aq.progress(R.id.progress).ajax(url, byte[].class, new AjaxCallback<byte[]>() {

	        @Override
	        public void callback(String url, byte[] object, AjaxStatus status) {
	        	
	        	showResult("bytes array length:" + object.length, status);
	        }
		});
	        
	}
	
	public void async_xml(){
		
		String url = "https://picasaweb.google.com/data/feed/base/featured?max-results=8";		
		
		aq.progress(R.id.progress).ajax(url, XmlDom.class, new AjaxCallback<XmlDom>(){
			
			public void callback(String url, XmlDom xml, AjaxStatus status) {
				showResult(xml, status);
			}
			
		});
	        
	}
	
	public void async_json_array(){
		
		String url = "http://androidquery.appspot.com/test/jsonarray.json";		
		
		aq.progress(R.id.progress).ajax(url, JSONArray.class, new AjaxCallback<JSONArray>(){
			
			public void callback(String url, JSONArray ja, AjaxStatus status) {
				showResult(ja, status);
			}
			
		});
	        
	}	
	
	
	private static class Profile{
		public String id;
		public String name;		
	}
	
	private static class GsonTransformer implements Transformer{

		public <T> T transform(String url, Class<T> type, String encoding, byte[] data, AjaxStatus status) {			
			Gson g = new Gson();
			return g.fromJson(new String(data), type);
		}
	}
	
	public void async_transformer(){
		
		String url = "https://graph.facebook.com/205050232863343";		
		GsonTransformer t = new GsonTransformer();
		
        aq.transformer(t).progress(R.id.progress).ajax(url, Profile.class, new AjaxCallback<Profile>(){			
			public void callback(String url, Profile profile, AjaxStatus status) {	
				Gson gson = new Gson();
				showResult("GSON Object:" + gson.toJson(profile), status);		
			}			
		});
        
	}
	
	/*
	public void async_transformer() {
		
		String url = "https://graph.facebook.com/205050232863343";
		
		aq.progress(R.id.progress).ajax(url, Profile.class, new AjaxCallback<Profile>(){
			
			@Override
			public Profile transform(String url, Class<Profile> type, String encoding, byte[] data, AjaxStatus status) {
				
				Profile profile = null;
				
				if(data != null){
					Gson g = new Gson();					
					profile = g.fromJson(new String(data), type);
				}
				
				return profile;
			}
			
			
			@Override
			public void callback(String url, Profile profile, AjaxStatus status) {
				
				showTextResult("id:" + profile.id + " name:" + profile.name);
				
			}
			
		});
		
			
        
		
	}
	*/
	public void async_post(){
		
        String url = "http://search.twitter.com/search.json";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("q", "androidquery");
		
        aq.progress(R.id.progress).ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                
                showResult(json, status);
               
            }
        });
		
		
	}
	
	
	public void async_post_entity() throws UnsupportedEncodingException{
		
	    String url = "http://search.twitter.com/search.json";
		
	    List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("q", "androidquery"));				
		HttpEntity entity = new UrlEncodedFormEntity(pairs, "UTF-8");
	    
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(AQuery.POST_ENTITY, entity);
	    
	    aq.progress(R.id.progress).ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
	
	        @Override
	        public void callback(String url, JSONObject json, AjaxStatus status) {
	            
	            showResult(json, status);
	           
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
	        	
	        	showResult(html, status);
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
	
	public void async_encoding(){
		
		String url = "http://www.kyotojp.com/limousine-big5.html";
		
		AjaxCallback<String> cb = new AjaxCallback<String>();
		cb.url(url).type(String.class).encoding("Big5").weakHandler(this, "encodingCb");
		
		aq.progress(R.id.progress).ajax(cb);
		
	}
	
	public void encodingCb(String url, String html, AjaxStatus status){
		
		showResult(html, status);
		
	}
	
	public void async_status(){
		
		String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
        
        aq.progress(R.id.progress).ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                
            	int source = status.getSource();
            	int responseCode = status.getCode();
            	long duration = status.getDuration();
            	Date fetchedTime = status.getTime();
            	String message = status.getMessage();
            	String redirect = status.getRedirect();
            	DefaultHttpClient client = status.getClient();
            	
            	Map<String, Object> map = new HashMap<String, Object>();
            	map.put("source", source);
            	map.put("response code", responseCode);
            	map.put("duration", duration);
            	map.put("object fetched time", fetchedTime);
            	map.put("message", message);
            	map.put("redirect", redirect);
            	map.put("client", client);
            	
            	showResult(map, status);
            	
            }
        });
	}
	
	public void async_error(){
		
		String url = "http://www.google.com";
        
        aq.progress(R.id.progress).ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                
            	if(json != null) return;
            	
            	switch(status.getCode()){
            	
            		case AjaxStatus.TRANSFORM_ERROR:
            			showResult("unable to transform result to JSONObject", status);
            			break;
            		case AjaxStatus.NETWORK_ERROR:
            			showResult("network error without response from server", status);
            			break;
            		case AjaxStatus.AUTH_ERROR:
            			showResult("authentication error", status);
            			break;
            		default:
            			showResult("other errors", status);
            			break;
            	}
            	
            }
        });
	}
	
	public void async_invalidate(){
		
		String url = "http://androidquery.appspot.com/test/invalid.json";
        
        aq.progress(R.id.progress).ajax(url, JSONObject.class, 15 * 60000, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                
            	if(json != null){
            		if("1".equals(json.optString("status"))){
            			//do something
            		}else{
            			//the request is a failure, don't cache it
            			status.invalidate();
            		}
            		
            	}
            	
            	showResult(json, status);
            }
        });
	}
	
	public void jsonCb(String url, JSONObject json, AjaxStatus status) {
		  	
        showResult(json, status);
	}
	
	public void stringCb(String url, String str, AjaxStatus status) {
		 	
        showResult(str, status);
	}
	
}
