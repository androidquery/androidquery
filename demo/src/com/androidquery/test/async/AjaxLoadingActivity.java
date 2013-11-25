package com.androidquery.test.async;

import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.Transformer;
import com.androidquery.test.PatternUtility;
import com.androidquery.test.RunSourceActivity;
import com.androidquery.util.AQUtility;
import com.androidquery.util.XmlDom;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AjaxLoadingActivity extends RunSourceActivity {

	private String type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		type = getIntent().getStringExtra("type");
		
		if("async_progress_activity".equals(type)){
			requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 
		}else if("async_progress_activity_bar".equals(type)){
			requestWindowFeature(Window.FEATURE_PROGRESS); 
		}
		
		super.onCreate(savedInstanceState);
			
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
	    
		String url = "http://www.androidquery.com/z/images/vikispot/android-w.png";

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
	
	public void async_file(){
		
		String url = "https://picasaweb.google.com/data/feed/base/featured?max-results=8";		
		
		aq.progress(R.id.progress).ajax(url, File.class, new AjaxCallback<File>(){
			
			public void callback(String url, File file, AjaxStatus status) {
				
				if(file != null){
					showResult("File:" + file.length() + ":" + file, status);
				}else{
					showResult("Failed", status);
				}
			}
			
		});
	        
	}
	
	public void async_file_custom(){
		
		String url = "https://picasaweb.google.com/data/feed/base/featured?max-results=16";		
		
		File ext = Environment.getExternalStorageDirectory();
		File target = new File(ext, "aquery/myfolder2/photos1.xml");		
		
		aq.progress(R.id.progress).download(url, target, new AjaxCallback<File>(){
			
			public void callback(String url, File file, AjaxStatus status) {
				
				if(file != null){
					showResult("File:" + file.length() + ":" + file, status);
				}else{
					showResult("Failed", status);
				}
			}
			
		});
		
	}
	
	public void async_progress_dialogbar(){
		
		ProgressDialog dialog = new ProgressDialog(this);
		
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle("Loading...");
		
		String url = "http://farm6.static.flickr.com/5035/5802797131_a729dac808_b.jpg";		
		
		File ext = Environment.getExternalStorageDirectory();
		File target = new File(ext, "aquery/myfolder2/photo.jpg");		
		
		aq.progress(dialog).download(url, target, new AjaxCallback<File>(){
			
			public void callback(String url, File file, AjaxStatus status) {
				
				if(file != null){
					showResult("File:" + file.length() + ":" + file, status);
				}else{
					showResult("Failed", status);
				}
			}
			
		});
	}
	
	public void async_web(){

		String MOBILE_AGENT = "Mozilla/5.0 (Linux; U; Android 2.2) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533";		

		AjaxCallback.setAgent(MOBILE_AGENT);
		
		aq.id(R.id.result).gone();
		aq.id(R.id.web).visible();
		
		//String url = "http://www.shouda8.com/shouda/tunshixingkong/14/2618.htm";
		//String url = "http://www.engadget.com/2012/05/04/samsung-releases-galaxy-tab-2-7-and-10-source-code";
		String url = "http://mashable.com/2012/05/05/new-york-city-tech-startups/";
		
		//wv.loadUrl("file:///android_asset/html_no_copy/demo_welcome.html");
		long expire = 3600000;
		
		aq.progress(R.id.progress).ajax(url, String.class, expire, new AjaxCallback<String>() {

	        @Override
	        public void callback(String url, String html, AjaxStatus status) {
	             
	        	AQUtility.debug("file length:" + html.length());
	        	
	        	showResult("", status);
	        	
	        	WebView wv = aq.id(R.id.web).getWebView();
	        	WebSettings ws = wv.getSettings();
	        	//ws.setJavaScriptEnabled(true);
	        	
	        	wv.loadDataWithBaseURL(url, html, "text/html", "utf-8", null);
	        	//wv.loadUrl(url);
	        	//showResult(html, status);
	        }
		        
		});
	        
	}
	
	
	public void async_inputstream(){
		
		String url = "https://picasaweb.google.com/data/feed/base/featured?max-results=8";		
		
		aq.progress(R.id.progress).ajax(url, InputStream.class, new AjaxCallback<InputStream>(){
			
			public void callback(String url, InputStream is, AjaxStatus status) {
				
				if(is != null){
					showResult("InputStream:" + is, status);
				}else{
					showResult("Failed", status);
				}
			}
			
		});
		
	}
	
	public void async_xpp(){
		
		String url = "https://picasaweb.google.com/data/feed/base/featured?max-results=8";		
		
		aq.progress(R.id.progress).ajax(url, XmlPullParser.class, new AjaxCallback<XmlPullParser>(){
			
			public void callback(String url, XmlPullParser xpp, AjaxStatus status) {
				
				Map<String, String> images = new LinkedHashMap<String, String>();
				String currentTitle = null;
				
				try{
				
					int eventType = xpp.getEventType();
			        while(eventType != XmlPullParser.END_DOCUMENT) {
			          
			        	if(eventType == XmlPullParser.START_TAG){
			        		
			        		String tag = xpp.getName();
			        		
			        		if("title".equals(tag)){
			        			currentTitle = xpp.nextText();
			        		}else if("content".equals(tag)){
			        			String imageUrl = xpp.getAttributeValue(0);
			        			images.put(currentTitle, imageUrl);
			        		}
			        	}
			        	eventType = xpp.next();
			        }
				
				}catch(Exception e){
					AQUtility.report(e);
				}
				
				showResult(images, status);
				
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
	
	
	private static class Profile{
		public String id;
		public String name;		
	}
	

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
	
	 /*
	public void async_post2(){
		
        String url = "your url";
		
        //get your byte array or file
        byte[] data = new byte[1000];
        
		Map<String, Object> params = new HashMap<String, Object>();
		
		//put your post params
		params.put("paramName", data);
		
		AjaxCallback<byte[]> cb = new AjaxCallback<byte[]>() {

            @Override
            public void callback(String url, byte[] data, AjaxStatus status) {
               
            	System.out.println(data);
            	System.out.println(status.getCode() + ":" + status.getError());
                
            	
            }
        };
        
        cb.url(url).type(byte[].class);
        
        //set Content-Length header
        cb.params(params).header("Content-Length", Integer.toString(data.length));
		cb.async(this);
		
	}
	*/
	
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
	
	public void async_delete() {
		
		String url = "http://www.androidquery.com/p/doNothing";
        
		aq.progress(R.id.progress).delete(url, JSONObject.class, this, "jsonCb");
        
    }
	
	public void async_put() throws JSONException, UnsupportedEncodingException {
		
	    String url = "http://www.androidquery.com/p/doNothing";
        
	    JSONObject input = new JSONObject();
	    input.put("param1", "value1");
	    input.put("param2", "value2");
	    
	    StringEntity entity = new StringEntity(input.toString(), "UTF-8");
	    
	    aq.progress(R.id.progress).put(url, "application/json", entity, JSONObject.class, new AjaxCallback<JSONObject>(){
	        
	        @Override
	        public void callback(String url, JSONObject jo, AjaxStatus status){
	            
	            showResult(jo);
	            
	        }
	        
	    });
	    
        
	    
	}
	
	public void putCb(String url, JSONObject jo, AjaxStatus status){
	    
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
        
        //aq.policy(AQuery.CACHE_PERSISTENT).ajax(url, JSONObject.class, this, "jsonCb");
	}	
	
	
	
	public void async_progress_dialog(){
	    
		ProgressDialog dialog = new ProgressDialog(this);
		
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle("Sending...");
		
        String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";                
        aq.progress(dialog).ajax(url, JSONObject.class, this, "jsonCb");
           
	}	
	
	public void async_progress_activity(){
	    
		//Remember onCreate: requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); 
		
        String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";          
        aq.progress(this).ajax(url, JSONObject.class, this, "jsonCb");  
	}	
	
	public void async_progress_activity_bar(){
	    
		//Remember onCreate: requestWindowFeature(Window.FEATURE_PROGRESS); 
		
        String url = "http://farm6.static.flickr.com/5035/5802797131_a729dac808_b.jpg";   
        
		aq.progress(this).ajax(url, File.class, new AjaxCallback<File>(){
			
			public void callback(String url, File file, AjaxStatus status) {
				
				if(file != null){
					showResult("File:" + file.length() + ":" + file, status);
				}else{
					showResult("Failed", status);
				}
			}
			
		});
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
	
	public void async_cookie(){
	    
		String url = "http://www.androidquery.com/p/doNothing";
        
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();		
		cb.url(url).type(JSONObject.class).weakHandler(this, "cookieCb");
		
		cb.cookie("hello", "world").cookie("foo", "bar");		
        aq.ajax(cb);
        
	        
	}		
	
	public void cookieCb(String url, JSONObject jo, AjaxStatus status) {
		
		JSONObject result = new JSONObject();
		
		try {
			result.putOpt("cookies", jo.optJSONObject("cookies"));
		} catch (JSONException e) {
		}
		
		showResult(result, status);
		
	}
	
	
	public void async_encoding(){
		
		//Using String.class type will attempt to detect the encoding of a page and transform it to utf-8
		
		String url = "http://114.xixik.com/gb2312_big5/";
		aq.progress(R.id.progress).ajax(url, String.class, 0, this, "encodingCb");
		
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
	
	
	public void async_rcookies(){
	    
		String url = "http://www.google.com";
		aq.progress(R.id.progress).ajax(url, String.class, this, "rcookieCb");
        
	        
	}		
	
	public void rcookieCb(String url, String html, AjaxStatus status) {
		
		if(html != null){
			
			showResult(status.getCookies(), status);
			
		}
		
	}
	
	public void async_rheaders(){
	    
		String url = "http://www.google.com";
		aq.progress(R.id.progress).ajax(url, String.class, this, "rheaderCb");
        
	        
	}		
	
	public void rheaderCb(String url, String html, AjaxStatus status) {
		
		if(html != null){
			
			showResult(status.getHeaders(), status);
			
		}
		
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
