package com.androidquery.test;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.View;
import android.widget.TextView;


public class AQueryAsyncTest extends AbstractTest<AQueryTestActivity> {

	
	private String url;
	private Object result;
	private AjaxStatus status;
	
	public AQueryAsyncTest() {		
		super(AQueryTestActivity.class);
    }

	
	public void done(String url, Object result, AjaxStatus status){
		
		this.url = url;
		this.result = result;
		this.status = status;

		log("done", result);
		
		checkStatus(status);
		
		assertTrue(AQUtility.isUIThread());
		
		done();
		
	}
	
	private void checkStatus(AjaxStatus status){
		
		AQUtility.debug("redirect", status.getRedirect());
		AQUtility.debug("time", status.getTime());
		AQUtility.debug("response", status.getCode());
		
		assertNotNull(status);
		
		assertNotNull(status.getRedirect());
		
		if(result != null && status.getSource() == AjaxStatus.NETWORK){
			assertNotNull(status.getClient());
		}
		
		assertNotNull(status.getTime());
		assertNotNull(status.getMessage());
		
		
		
		
	}
	

	
	//Test: public <K> T ajax(AjaxCallback<K> callback)
	public void testAjaxAdvance() {
		
		String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
        
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>(){
			
			@Override
			public void callback(String url, JSONObject jo, AjaxStatus status) {
				
				done(url, jo, status);
				
			}
			
		};
		
		cb.url(url).type(JSONObject.class);		
        aq.ajax(cb);
        
        waitAsync();
        
        JSONObject jo = (JSONObject) result;
        
        assertNotNull(jo);       
        assertNotNull(jo.opt("responseData"));
        
    }
	
	//Test: public <K> T ajax(String url, Class<K> type, AjaxCallback<K> callback)
	public void testAjaxCallback() {
		
		String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
        
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>(){
			
			@Override
			public void callback(String url, JSONObject jo, AjaxStatus status) {
				
				done(url, jo, status);
				
			}
			
		};
		
			
        aq.ajax(url, JSONObject.class, cb);
        
        waitAsync();
        
        JSONObject jo = (JSONObject) result;
        
        assertNotNull(jo);       
        assertNotNull(jo.opt("responseData"));
        
    }
	
	//Test: public <K> T ajax(String url, Class<K> type, Object handler, String callback)
	public void testAjaxHandler() {
		
		String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
        
        aq.ajax(url, JSONObject.class, this, "jsonCb");
        
        waitAsync();
        
        JSONObject jo = (JSONObject) result;
        
        assertNotNull(jo);       
        assertNotNull(jo.opt("responseData"));
        
    }
	
	//Test: <K> T ajax(String url, Map<String, Object> params, Class<K> type, AjaxCallback<K> callback)
	public void testAjaxPost(){
		
        String url = "http://search.twitter.com/search.json";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("q", "androidquery");
		
        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jo, AjaxStatus status) {
                   
            	done(url, jo, status);
               
            }
        });
		
        waitAsync();
		
        JSONObject jo = (JSONObject) result;
        assertNotNull(jo);       
        assertNotNull(jo.opt("results"));
        
	}
	
	public void testAjaxPostRaw() throws UnsupportedEncodingException{
		
        String url = "http://search.twitter.com/search.json";
		
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("q", "androidquery"));				
		HttpEntity entity = new UrlEncodedFormEntity(pairs, "UTF-8");
        
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(AQuery.POST_ENTITY, entity);
		
        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jo, AjaxStatus status) {
                   
            	done(url, jo, status);
               
            }
        });
		
        waitAsync();
		
        JSONObject jo = (JSONObject) result;
        assertNotNull(jo);       
        assertNotNull(jo.opt("results"));
        
	}
	
	//Test: public <K> T ajax(String url, Map<String, Object> params, Class<K> type, Object handler, String callback)
	public void testAjaxPostHandler(){
		
        String url = "http://search.twitter.com/search.json";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("q", "androidquery");
		
        aq.ajax(url, params, JSONObject.class, this, "jsonCb");
		
        waitAsync();
		
        JSONObject jo = (JSONObject) result;
        assertNotNull(jo);       
        assertNotNull(jo.opt("results"));
        
	}
	
	//Test: public <K> T ajax(String url, Class<K> type, long expire, AjaxCallback<K> callback)
	public void testAjaxCache() {
		
		String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
        
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>(){
			
			@Override
			public void callback(String url, JSONObject jo, AjaxStatus status) {
				
				done(url, jo, status);
				
			}
			
		};
		
			
        aq.ajax(url, JSONObject.class, 15 * 60 * 1000, cb);
        
        waitAsync(2000);
        
        JSONObject jo = (JSONObject) result;
        
        assertNotNull(jo);       
        assertNotNull(jo.opt("responseData"));
        
       
		File cached = aq.getCachedFile(url);
		assertTrue(cached.exists());
		assertTrue(cached.length() > 100);
		
    }
	
	//Test: public <K> T ajax(String url, Class<K> type, long expire, Object handler, String callback)
	public void testAjaxCacheHandler() {
		
		String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
        
        aq.ajax(url, JSONObject.class, 15 * 60 * 1000, this, "jsonCb");
        
        waitAsync();
        
        JSONObject jo = (JSONObject) result;
        
        assertNotNull(jo);       
        assertNotNull(jo.opt("responseData"));
        
    }
	
	public void jsonCb(String url, JSONObject jo, AjaxStatus status){
				
		done(url, jo, status);
	}
	
	//Test: public T cache(String url, long expire)
	public void testCache(){
		
		
		AQUtility.cleanCache(AQUtility.getCacheDir(getActivity()), 0, 0);
		
		String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
		
		File file = aq.getCachedFile(url);
		
		assertNull(file);
		
		aq.cache(url, 0);
		
		waitAsync();
		
		AQUtility.debugWait(2000);
		
		file = aq.getCachedFile(url);
		
		assertNotNull(file);
		
		
	}
	
	public void test301(){
		
		String url = "http://jigsaw.w3.org/HTTP/300/301.html";
        
		AjaxCallback<String> cb = new AjaxCallback<String>(){
			
			@Override
			public void callback(String url, String html, AjaxStatus status) {
				
				done(url, html, status);
				
			}
			
		};
		
		cb.url(url).type(String.class);		
        aq.ajax(cb);
        
        waitAsync();
        
        String html = (String) result;
        
        assertNotNull(html);       
        
        assertEquals("http://jigsaw.w3.org/HTTP/300/Overview.html", status.getRedirect());
        
	}
	
	
	public void test307(){
		
		String url = "http://jigsaw.w3.org/HTTP/300/307.html";
        
		AjaxCallback<String> cb = new AjaxCallback<String>(){
			
			@Override
			public void callback(String url, String html, AjaxStatus status) {
				
				done(url, html, status);
				
			}
			
		};
		
		cb.url(url).type(String.class);		
        aq.ajax(cb);
        
        waitAsync();
        
        String html = (String) result;
        
        assertNotNull(html);       
        
        assertEquals("http://jigsaw.w3.org/HTTP/300/Overview.html", status.getRedirect());
        
        
		
	}
	
	public void testTransformError(){
		
		
		String url = "http://www.google.com";
        
        aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                
            	done(url, json, status);
            	
            }
        });
		
        waitAsync();
        
        assertNull(result);
        assertEquals(AjaxStatus.TRANSFORM_ERROR, status.getCode());
		
	}
	
	public void test404Error(){
		
		
		String url = "http://androidquery.appspot.com/test/fake";
        
        aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                
            	done(url, json, status);
            	
            }
        });
		
        waitAsync();
        
        assertNull(result);
        assertEquals(404, status.getCode());
		
	}
	
	
	public void testNetworkError(){
		
		
		String url = "httpd://wrongschema";
        
        aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                
            	done(url, json, status);
            	
            }
        });
		
        waitAsync();
        
        assertNull(result);
        assertEquals(AjaxStatus.NETWORK_ERROR, status.getCode());
		
	}
	
	public void testInvalidate(){
		
		testAjaxCache();
		
		String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
        aq.invalidate(url);
		
        File cached = aq.getCachedFile(url);
        assertNull(cached);
        
		
	}
	
	public void testWaitBlock() {
		
		String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
        
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();		
		cb.url(url).type(JSONObject.class);		
        
		aq.sync(cb);
        
		String u = cb.getUrl();
        JSONObject jo = cb.getResult();
        AjaxStatus status = cb.getStatus();
        
        assertNotNull(jo);       
        assertNotNull(jo.opt("responseData"));
        checkStatus(status);
    }
	
	
	public void testWaitNullUrlCacheBlock() {
		
        String url = null;
		
		AjaxCallback<String> cb = new AjaxCallback<String>();
		cb.url(url).type(String.class).fileCache(true).expire(15 * 60 * 1000);

		aq.sync(cb);

		String res = cb.getResult();
		AjaxStatus status = cb.getStatus();
		
		assertNull(res);
		assertNotNull(status);
		assertEquals(AjaxStatus.NETWORK_ERROR, status.getCode());
		
    }
	
	public void testAjaxInactiveActivity() {
		
		String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
        
		Activity act = getActivity();
		act.finish();
		
		assertTrue(act.isFinishing());
		
		AQuery aq = new AQuery(act);
		
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>(){
			
			@Override
			public void callback(String url, JSONObject jo, AjaxStatus status) {
				
				assertFalse(true);
				
				done(url, jo, status);
				
			}
			
		};
		
			
        aq.ajax(url, JSONObject.class, cb);
        
        waitAsync();
        
        JSONObject jo = (JSONObject) result;
        
        assertNull(jo);       
        
    }
	
	
	public void testAjaxNotUICb() {
		
		String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
        
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>(){
			
			@Override
			public void callback(String url, JSONObject jo, AjaxStatus status) {
				
				assertFalse(AQUtility.isUIThread());
				
			}
			
		};
		
		cb.uiCallback(false);
		
		aq.ajax(url, JSONObject.class, cb);
		
		waitAsync();
    }
	
	public void testAjaxBitmap() {
		
		String url = ICON_URL;
        
		AjaxCallback<Bitmap> cb = new AjaxCallback<Bitmap>(){
			
			@Override
			public void callback(String url, Bitmap bm, AjaxStatus status) {
				
				done(url, bm, status);
				
			}
			
		};
		
			
        aq.ajax(url, Bitmap.class, 15 * 60 * 1000, cb);
        
        waitAsync(2000);
        
        assertNotNull(result);
       
		File cached = aq.getCachedFile(url);
		assertTrue(cached.exists());
		assertTrue(cached.length() > 100);
		
    }
	
	private static class Profile{

		public String id;
		public String name;
		
	}
	public void testExtendTransformer() {
		
		String url = "https://graph.facebook.com/205050232863343";
		
		AjaxCallback<Profile> cb = new AjaxCallback<Profile>(){
			
			@Override
			protected Profile transform(String url, byte[] data, AjaxStatus status) {
				
				Profile profile = null;
				
				if(data != null){
					Gson g = new Gson();
					profile = g.fromJson(new String(data), getType());
				}
				
				return profile;
			}
			
			
			@Override
			public void callback(String url, Profile profile, AjaxStatus status) {
				
				done(url, profile, status);
				
			}
			
		};
		
			
        aq.ajax(url, Profile.class, cb);
        
        waitAsync(2000);
        
        assertNotNull(result);
		
	}
	
	
	public void testSetTransformer() {
		
		String url = "https://graph.facebook.com/205050232863343";
		
		
		AjaxCallback<Profile> cb = new AjaxCallback<Profile>(){
			
			
			@Override
			public void callback(String url, Profile profile, AjaxStatus status) {
				
				done(url, profile, status);
				
			}
			
		};
		
		GsonTransformer t = new GsonTransformer();
		cb.transformer(t);
		
        aq.ajax(url, Profile.class, cb);
        
        waitAsync(2000);
        
        assertNotNull(result);
        
        Profile p  = (Profile) result;
        assertNotNull(p.id);
        assertNotNull(p.name);
		
	}
	
	
	public void testAjaxTransformer() {
		
		String url = "https://graph.facebook.com/205050232863343";
		
		GsonTransformer t = new GsonTransformer();
        aq.transformer(t).ajax(url, Profile.class, this, "done");
        
        waitAsync(2000);
        
        assertNotNull(result);
        
        Profile p  = (Profile) result;
        assertNotNull(p.id);
        assertNotNull(p.name);
		
	}
	
	public void testAjaxStaticTransformer() {
		
		String url = "https://graph.facebook.com/205050232863343";
		
		GsonTransformer t = new GsonTransformer();
        
		AjaxCallback.setTransformer(t);
		
		aq.ajax(url, Profile.class, this, "done");
        
        waitAsync(2000);
        
        assertNotNull(result);
        
        Profile p  = (Profile) result;
        assertNotNull(p.id);
        assertNotNull(p.name);
		
	}
	
}
