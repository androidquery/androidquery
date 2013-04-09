package com.androidquery.test.async;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.auth.BasicHandle;
import com.androidquery.auth.FacebookHandle;
import com.androidquery.auth.GoogleHandle;
import com.androidquery.auth.TwitterHandle;
import com.androidquery.callback.AbstractAjaxCallback;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.test.RunSourceActivity;
import com.androidquery.util.AQUtility;
import com.androidquery.util.XmlDom;

public class AjaxAuthActivity extends RunSourceActivity {

	private String type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		type = getIntent().getStringExtra("type");
	
		if("auth_twitter_upload".equals(type)){
			aq.cache(UPLOAD_IMAGE, 0);
		}
	}
	
	@Override
	protected void runSource(){
		
		AQUtility.debug("run", type);
		
		AQUtility.invokeHandler(this, type, false, false, null);
	}

	
	
	private static String APP_ID = "251003261612555";
	private static String PERMISSIONS = "read_stream,read_friendlists,manage_friendlists,manage_notifications,publish_stream,publish_checkins,offline_access,user_photos,user_likes,user_groups,friends_photos";
	
	public void auth_facebook(){
		
		FacebookHandle handle = new FacebookHandle(this, APP_ID, PERMISSIONS){
			
			@Override
			public boolean expired(AbstractAjaxCallback<?, ?> cb, AjaxStatus status) {
				
				//custom check if re-authentication is required
				if(status.getCode() == 401){
					return true;
				}
				
				return super.expired(cb, status);
			}
			
		};
		
		String url = "https://graph.facebook.com/me/feed";
		aq.auth(handle).progress(R.id.progress).ajax(url, JSONObject.class, this, "facebookCb");
		
	}
	
	private FacebookHandle handle;
	private final int ACTIVITY_SSO = 1002;
	public void auth_facebook_sso(){
		
		handle = new FacebookHandle(this, APP_ID, PERMISSIONS);
		handle.sso(ACTIVITY_SSO);
		
		String url = "https://graph.facebook.com/me/feed";
		aq.auth(handle).progress(R.id.progress).ajax(url, JSONObject.class, this, "facebookCb");
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch(requestCode) {
			
	    	case ACTIVITY_SSO: {
	    		if(handle != null){
	    			handle.onActivityResult(requestCode, resultCode, data);	  
	    		}
	    		break;
	    	}
	    	
		}
	}
	
	
	public void facebookCb(String url, JSONObject jo, AjaxStatus status){
		
		showResult(jo, status);
		
	}
	

	
	
	
	private static String CONSUMER_KEY = "x5l8Ax1RSo8T4GjSMYiG8g";
	private static String CONSUMER_SECRET = "8p46vY3H2sk7hnbTAQF0JqLe8J9xtsssGlxVAWdoySg";
	
	public void auth_twitter(){
		
		TwitterHandle handle = new TwitterHandle(this, CONSUMER_KEY, CONSUMER_SECRET);
		
		String url = "http://twitter.com/statuses/mentions.json";
		aq.auth(handle).progress(R.id.progress).ajax(url, JSONArray.class, this, "twitterCb");
		
		
	}
	
	public void auth_twitter_update(){
		
		TwitterHandle handle = new TwitterHandle(this, CONSUMER_KEY, CONSUMER_SECRET);
		
		//1/statuses/update.format
		//https://upload.twitter.com/1/statuses/update_with_media.format

		String url = "http://twitter.com/statuses/update.json";
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("status", "Testing 123");
		
		aq.auth(handle).progress(R.id.progress).ajax(url, params, JSONObject.class, this, "twitterCb2");
		
	}
	
	private String UPLOAD_IMAGE = "http://www.androidquery.com/z/images/vikispot/android-w.png"; 
	public void auth_twitter_upload(){
		
		File file = aq.getCachedFile(UPLOAD_IMAGE);
		
		if(file == null){
			aq.cache(UPLOAD_IMAGE, 0);
			return;
		}
		
		AQUtility.debug("upload file:" + file.length());
		
		TwitterHandle handle = new TwitterHandle(this, CONSUMER_KEY, CONSUMER_SECRET);
		
		String url = "https://upload.twitter.com/1/statuses/update_with_media.json";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", "Testing Status Update with AndroidQuery");
		params.put("media[]", file);
		
		aq.auth(handle).progress(R.id.progress).ajax(url, params, JSONObject.class, this, "twitterCb2");
		
	}
	
	public void twitterCb2(String url, JSONObject jo, AjaxStatus status){
		
		showResult(jo, status);
		
	}
	
	public void twitterCb(String url, JSONArray ja, AjaxStatus status){
		
		showResult(ja, status);
		
	}
	
	public void auth_twitter_token(){
		
		TwitterHandle handle = new TwitterHandle(this, CONSUMER_KEY, CONSUMER_SECRET){
			
			@Override
			protected void authenticated(String secret, String token) {
				showResult("secret:" + secret + " token:" + token, null);
			}
			
		};
		
		handle.authenticate(false);
		
	}
	
	public void tokenCb(String url, JSONArray ja, AjaxStatus status){
		
		showResult(ja, status);
		
	}
	
	public void auth_pick_account(){
	    
		String url = "https://www.google.com/reader/atom/user/-/state/com.google/reading-list?n=8";
		
		AjaxCallback<XmlDom> cb = new AjaxCallback<XmlDom>();
  
		cb.url(url).type(XmlDom.class).weakHandler(this, "readerCb");  
		cb.auth(this, AQuery.AUTH_READER, null);
  
		aq.progress(R.id.progress).ajax(cb);
	        
	}	
	
	public void auth_last_account(){
		
		
		String url = "https://www.google.com/reader/atom/user/-/state/com.google/reading-list?n=8";
		
		AjaxCallback<XmlDom> cb = new AjaxCallback<XmlDom>();
  
		cb.url(url).type(XmlDom.class).weakHandler(this, "readerCb");  
		cb.auth(this, AQuery.AUTH_READER, AQuery.ACTIVE_ACCOUNT);
  
		aq.progress(R.id.progress).ajax(cb);
	        
	}	
	
	public void auth_specific_account(){
		
		
		String url = "https://www.google.com/reader/atom/user/-/state/com.google/reading-list?n=8";
		
		AjaxCallback<XmlDom> cb = new AjaxCallback<XmlDom>();
  
		cb.url(url).type(XmlDom.class).weakHandler(this, "readerCb");  
		cb.auth(this, AQuery.AUTH_READER, AQuery.ACTIVE_ACCOUNT);
  
		aq.progress(R.id.progress).ajax(cb);
	        
	}	
	
	public void readerCb(String url, XmlDom xml, AjaxStatus status) {
		
		if(xml != null){
		
			List<XmlDom> entries = xml.tags("entry");			
			List<String> titles = new ArrayList<String>();
			
			for(XmlDom entry: entries){
				titles.add(entry.text("title"));
			}
			
			showTextResult(titles);			
		}
		
		AQUtility.debug("status:" + status);
		
		showResult(xml, status);
	}
	
	
	public void auth_picasa(){
		/*
		GoogleHandle handle = new GoogleHandle(this, AQuery.AUTH_PICASA, AQuery.ACTIVE_ACCOUNT);
		
		String url = "https://picasaweb.google.com/data/feed/api/user/default?alt=json";
		aq.auth(handle).ajax(url, JSONObject.class, new AjaxCallback<JSONObject>(){
			@Override
			public void callback(String url, JSONObject object, AjaxStatus status) {
				System.out.println(object);
			}
		});
		*/
		 
		String url = "https://picasaweb.google.com/data/feed/api/user/default?alt=json";
		 
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
  
		cb.url(url).type(JSONObject.class).weakHandler(this, "picasaCb");  
		cb.auth(this, AQuery.AUTH_PICASA, AQuery.ACTIVE_ACCOUNT);
  
		aq.progress(R.id.progress).ajax(cb);
	    
	}	
	
	public void picasaCb(String url, JSONObject jo, AjaxStatus status) {
	
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
			
			
		}else{
			showError(status);
		}
	
	}
	
	public void auth_youtube(){
		
		String url = "https://gdata.youtube.com/feeds/api/users/default/subscriptions?v=2&alt=json";
		
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>(); 
		cb.url(url).type(JSONObject.class).weakHandler(this, "youtubeCb").fileCache(true).expire(15 * 60 * 1000);  
		
		cb.auth(this, AQuery.AUTH_YOUTUBE, AQuery.ACTIVE_ACCOUNT);
  
		aq.progress(R.id.progress).ajax(cb);
	        
	}
	
	public void youtubeCb(String url, JSONObject jo, AjaxStatus status) {
		
		//AQUtility.debug("youtube", jo);
		
		//04-04 22:00:45.103: W/AQuery(12658): youtube:{"encoding":"UTF-8","feed":{"logo":{"$t":"http:\/\/www.youtube.com\/img\/pic_youtubelogo_123x63.gif"},"link":[{"type":"application\/atom+xml","href":"https:\/\/gdata.youtube.com\/feeds\/api\/users\/tinyeeliu?v=2","rel":"related"},{"type":"text\/html","href":"https:\/\/www.youtube.com","rel":"alternate"},{"href":"http:\/\/pubsubhubbub.appspot.com","rel":"hub"},{"type":"application\/atom+xml","href":"https:\/\/gdata.youtube.com\/feeds\/api\/users\/tinyeeliu\/subscriptions?v=2","rel":"http:\/\/schemas.google.com\/g\/2005#feed"},{"type":"application\/atom+xml","href":"https:\/\/gdata.youtube.com\/feeds\/api\/users\/tinyeeliu\/subscriptions\/batch?v=2","rel":"http:\/\/schemas.google.com\/g\/2005#batch"},{"type":"application\/atom+xml","href":"https:\/\/gdata.youtube.com\/feeds\/api\/users\/tinyeeliu\/subscriptions?alt=json&start-index=1&max-results=25&v=2","rel":"self"},{"type":"application\/atomsvc+xml","href":"https:\/\/gdata.youtube.com\/feeds\/api\/users\/tinyeeliu\/subscriptions?alt=atom-service&v=2","rel":"service"},{"type":"application\/atom+xml","href":"https:\/\/gdata.youtube.com\/feeds\/api\/users\/tinyeeliu\/subscriptions?alt=json&start-index=26&max-results=25&v=2","rel":"next"}],"openSearch$totalResults":{"$t":30},"xmlns":"http:\/\/www.w3.org\/2005\/Atom","id":{"$t":"tag:youtube.com,2008:user:tinyeeliu:subscriptions"},"author":[{"yt$userId":{"$t":"YC478Q7C1734eEJoAmHn0w"},"uri":{"$t":"https:\/\/gdata.youtube.com\/feeds\/api\/users\/tinyeeliu"},"name":{"$t":"tinyeeliu"}}],"xmlns$openSearch":"http:\/\/a9.com\/-\/spec\/opensearch\/1.1\/","title":{"$t":"Subscriptions of tinyeeliu"},"category":[{"scheme":"http:\/\/schemas.google.com\/g\/2005#kind","term":"http:\/\/gdata.youtube.com\/schemas\/2007#subscription"}],"xmlns$gd":"http:\/\/schemas.google.com\/g\/2005","openSearch$startIndex":{"$t":1},"updated":{"$t":"2012-04-04T13:57:33.428Z"},"xmlns$yt":"http:\/\/gdata.youtube.com\/schemas\/2007","gd$etag":"W\/\"DU8BQno5cCp7I2A9WhVQFUk.\"","entry":[{"id":{"$t":"tag:youtube.com,2008:user:tinyeeliu:subscription:cWEwiMn4Zpb4fh3az7vay4POvq2mUjTPh8FUFdHn_z8"},"author":[{"yt$userId":{"$t":"YC478Q7C1734eEJoAmHn0w"},"uri":{"$t":"https:\/\/gdata.youtube.com\/feeds\/api\/users\/tinyeeliu"},"name":{"$t":"tinyeeliu"}}],"title":{"$t":"Activity of: PressHeartToContinue"},"category":[{"scheme":"http:\/\/schemas.google.com\/g\/2005#kind","term":"http:\/\/gdata.youtube.com\/schemas\/2007#subscription"},{"scheme":"http:\/\/gdata.youtube.com\/schemas\/2007\/subscriptiontypes.cat","term":"user"}],"yt$username":{"yt$display":"PressHeartToContinue","$t":"presshearttocontinue"},"updated":{"$t":"2011-12-15T09:55:23.000Z"},"gd$etag":"W\/\"CE4GQn47eCp7I2A9WhRQGUk.\"","link":[{"type":"application\/atom+xml","href":"https:\/\/gdata.youtube.com\/feeds\/api\/users\/presshearttocontinue?v=2","rel":"related"},{"type":"text\/html","href":"https:\/\/www.youtube.com\/channel\/UC_ufxdQbKBrrMOiZ4LzrUyA","rel":"alternate"},{"type":"application\/atom+xml","href":"https:\/\/gdata.youtube.com\/feeds\/api\/users\/tinyeeliu\/subscriptions\/cWEwiMn4Zpb4fh3az7vay4POvq2mUjTPh8FUFdHn_z8?v=2","rel":"self"}],"published":{"$t":"2011-12-15T09:55:23.000Z"},"yt$userId":{"$t":"_ufxdQbKBrrMOiZ4LzrUyA"}},{"id":{"$t":"tag:youtube.com,2008:user:tinyeeliu:subscription:cWEwiMn4Zpb4fh3az7vay0m5eNKJCqunM00MBNAqWWA"},"author":[{"yt$userId":{"$t":"YC478Q7C1734eEJoAmHn0w"},"uri":{"$t":"https:\/\/gdata.youtube.com\/feeds\/api\/users\/tinyeeliu"},"name":{"$t":"tinyeeliu"}}],"title":{"$t":"Activity of: RoosterTeeth"},"category":[{"scheme":"http:\/\/schemas.google.com\/g\/2005#kind","term":"http:\/\/gdata.youtube.com\/schemas\/2007#subscription"},{"scheme":"http:\/\/gdata.youtube.com\/schemas\/2007\/subscriptiontypes.cat","term":"user"}],"yt$username":{"yt$display":"RoosterTeeth","$t":"roosterteeth"},"updated":{"$t":"2011-04-13T15:14:17.000Z"},"gd$etag":"W\/\"DUEBRn47eCp7I2A9WhZRF00.\"","link":[{"type":"application\/atom+xml","href":"https:\/\/gdata.youtube.com\/feeds\/api\/users\/roosterteeth?v=2","rel":"related"},{"type":"text\/html","href":"https:\/\/ww

		
		if(jo != null){
			
			JSONArray entries = jo.optJSONObject("feed").optJSONArray("entry");
			
			//if(entries.length() > 0){	
				
				//String src = entries.optJSONObject(0).optJSONObject("content").optString("src");			
				//auth_youtube2(src + "&alt=json");
			//}else{
				showResult(jo);				
			//}
			
			
		}else{
			showError(status);
		}
	
	}
	
	private void auth_youtube2(String src){
		
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>(); 
		cb.url(src).type(JSONObject.class).weakHandler(this, "youtubeCb2");  
		
		cb.auth(this, AQuery.AUTH_YOUTUBE, AQuery.ACTIVE_ACCOUNT);
  
		aq.progress(R.id.progress).ajax(cb);
	        
	}
	
	public void youtubeCb2(String url, JSONObject jo, AjaxStatus status) {
		
		
		if(jo != null){
			
			JSONArray entries = jo.optJSONObject("feed").optJSONArray("entry");
			
			if(entries.length() > 0){	
				showResult(entries, status);
				JSONArray tbs = entries.optJSONObject(0).optJSONObject("media$group").optJSONArray("media$thumbnail");
				
				for(int i = 0; i < tbs.length(); i++){
					JSONObject tbo = tbs.optJSONObject(i);
					if("hqdefault".equals(tbo.optString("yt$name"))){
						String tb = tbo.optString("url");							
						aq.id(R.id.image).image(tb);
						break;
					}
				}
				
				
				
			}else{			
				showResult(jo);				
			}
		}else{
			showError(status);
		}
	
	}
	
	public void auth_contacts(){
		
		
		String url = "https://www.google.com/m8/feeds/contacts/default/full";
		
		AjaxCallback<XmlDom> cb = new AjaxCallback<XmlDom>();  
		cb.url(url).type(XmlDom.class).weakHandler(this, "contactsCb");  
		cb.auth(this, AQuery.AUTH_CONTACTS, AQuery.ACTIVE_ACCOUNT);
		aq.progress(R.id.progress).ajax(cb);
	        
	}
	
	public void auth_parallel(){
		
		String url1 = "https://picasaweb.google.com/data/feed/api/user/default";
		String url2 = "https://picasaweb.google.com/data/feed/api/user/default?alt=json";
		
		GoogleHandle gh = new GoogleHandle(this, AQuery.AUTH_PICASA, null);
		
		AjaxCallback<XmlDom> cb = new AjaxCallback<XmlDom>();
  
		cb.url(url1).type(XmlDom.class).weakHandler(this, "pcb1");  
		cb.auth(gh);
		
		AjaxCallback<JSONObject> cb2 = new AjaxCallback<JSONObject>();
		  
		cb2.url(url2).type(JSONObject.class).weakHandler(this, "pcb2");  
		cb2.auth(gh);
		
		cb.async(this);
		cb2.async(this);
		
		//aq.progress(R.id.progress).ajax(cb);
	        
	}
	
	public void pcb1(String url, XmlDom xml, AjaxStatus status){
		
		String result = "Result 1:\n";
		
		if(xml != null){			
			result += xml.toString();
			result = result.substring(0, Math.min(100, result.length())) + " ... ";
		}
		
		this.showTextResult(result);
		
	}
	
	public void pcb2(String url, JSONObject jo, AjaxStatus status){
		
		this.showResult("Result 2:\n" + jo, null);
		
	}
	
	public void contactsCb(String url, XmlDom xml, AjaxStatus status) {
		
		if(xml != null){
		
			List<XmlDom> entries = xml.tags("entry");
			
			List<String> friends = new ArrayList<String>();
			
			for(XmlDom entry: entries){
				friends.add(entry.text("title"));
			}
			
			showTextResult(friends);
			
		}
		
		showResult(xml, status);
	}
	
	public static final String MOBILE_AGENT = "Mozilla/5.0 (Linux; U; Android 2.2) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533";		
	
	
	public void auth_basic(){
		
		BasicHandle handle = new BasicHandle("tinyeeliu@gmail.com", "password");
		String url = "http://xpenser.com/api/v1.0/reports/";
		aq.auth(handle).progress(R.id.progress).ajax(url, JSONArray.class, this, "basicCb");
		
	}
	
	public void basicCb(String url, JSONArray ja, AjaxStatus status) {
		
		showResult(ja, status);
		
	}
	
	public void auth_unauth(){
		
		FacebookHandle fh = new FacebookHandle(this, APP_ID, "read_stream");
		fh.unauth();
		
		TwitterHandle th = new TwitterHandle(this, CONSUMER_KEY, CONSUMER_SECRET);
		th.unauth();
		
		showResult("Auth data cleared", null);
		
	}
	
	
	
	private void showError(AjaxStatus status){
		showResult(status.getCode(), "This account might not exist for this service.");
	}
	
	public void stringCb(String url, String str, AjaxStatus status) {
		
		if(status.getCode() == 401){
			showResult("Authenticate Error with Http Response 401", status);
		}else{		
			showResult(str, status);
		}
		
	}

	@Override
	public void onDestroy(){
		
		aq.dismiss();
		
		super.onDestroy();
	}
}
