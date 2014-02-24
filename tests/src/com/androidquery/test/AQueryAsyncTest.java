package com.androidquery.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xmlpull.v1.XmlPullParser;

import com.androidquery.AQuery;
import com.androidquery.auth.BasicHandle;
import com.androidquery.callback.AbstractAjaxCallback;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.callback.ProxyHandle;
import com.androidquery.util.AQUtility;
import com.androidquery.util.XmlDom;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
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
		
        String url = "http://www.androidquery.com/p/doNothing";
		
        Map<String, String> params = new HashMap<String, String>();
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
        assertNotNull(jo.opt("params"));
        
        assertEquals("POST", jo.optString("method"));
        
        
	}

	public void testAjaxPostRaw() throws UnsupportedEncodingException{
		
        String url = "http://www.androidquery.com/p/doNothing";
		
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
        assertNotNull(jo.opt("params"));
        
        assertEquals("POST", jo.optString("method"));
        
	}
	
	//Test: public <K> T ajax(String url, Map<String, Object> params, Class<K> type, Object handler, String callback)
	public void testAjaxPostHandler(){
		
        String url = "http://www.androidquery.com/p/doNothing";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("q", "androidquery");
		
        aq.ajax(url, params, JSONObject.class, this, "jsonCb");
		
        waitAsync();
		
        JSONObject jo = (JSONObject) result;
        assertNotNull(jo);       
        assertNotNull(jo.opt("params"));
        
        assertEquals("POST", jo.optString("method"));
        
	}
	
	//Test: public <K> T ajax(String url, Class<K> type, long expire, AjaxCallback<K> callback)
	public void testAjaxCache() {
		
	    AQUtility.cleanCache(AQUtility.getCacheDir(getActivity()), 0, 0);
        
	    String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
	    
        File file = aq.getCachedFile(url);
        
        assertNull(file);
	    
	    
		
        
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
	//mouVUQ
	
	
	public void test304(){
		
		String url = "http://www.androidquery.com/p/doNothing?response=304";
		
        aq.ajax(url, File.class, new AjaxCallback<File>() {

            @Override
            public void callback(String url, File file, AjaxStatus status) {
                
            	done(url, file, status);
            	
            }
        }.header("If-Modified-Since", "Sat, 15 May 2010 12:06:39 GMT"));
		
        //If-Modified-Since: Sat, 15 May 2010 12:06:39 GMT
        
        waitAsync();
        
        assertNull(result);
        assertEquals(304, status.getCode());
		
        
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
	
	//http://beauharnois_bupa.tripod.com/chutessaint-louis-small.jpg
	public void testUnderscoreDomain(){
		
		String url = "http://beauharnois_bupa.tripod.com/chutessaint-louis-small.jpg";
        
        aq.ajax(url, byte[].class, new AjaxCallback<byte[]>() {

            @Override
            public void callback(String url, byte[] json, AjaxStatus status) {
                
            	done(url, json, status);
            	
            }
        });
		
        waitAsync();
        
        assertNotNull(result);
        //assertEquals(404, status.getCode());
		
        
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
	
	public void testWaitBlockInputStream() {
		
		String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
        
		AjaxCallback<InputStream> cb = new AjaxCallback<InputStream>();		
		cb.url(url).type(InputStream.class);		
        
		aq.sync(cb);
        
		String u = cb.getUrl();
		InputStream is = cb.getResult();
        AjaxStatus status = cb.getStatus();
        
        byte[] data = AQUtility.toBytes(is);
        
        JSONObject jo = null;
		String str = null;
    	try {    		
    		str = new String(data, "UTF-8");
			jo = (JSONObject) new JSONTokener(str).nextValue();
		} catch (Exception e) {	  		
			AQUtility.debug(e);
			AQUtility.debug(str);
		}
        
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
	
	public void testAjaxLongBitmapURL() {
		
		String dummy = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
		
		String title = "Very long title " + dummy + dummy + dummy + dummy + dummy;
		
		AQUtility.debug("title len", title.length());
		
		String url = "https://chart.googleapis.com/chart?chid=1234&cht=lc&chtt=" + title + "&chs=300x200&chxt=x&chd=t:40,20,50,20,100";
		
        
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
        
        Profile p = (Profile) result;
        assertNotNull(p.id);
        assertNotNull(p.name);
		
	}
	
	public void testAjaxCookie() {
		
		String url = "http://www.androidquery.com/p/doNothing";
        
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>(){
			
			@Override
			public void callback(String url, JSONObject jo, AjaxStatus status) {
				
				done(url, jo, status);
				
			}
			
		};
		
		cb.url(url).type(JSONObject.class).cookie("hello", "world").cookie("foo", "bar");		
        aq.ajax(cb);
        
        waitAsync();
        
        JSONObject jo = (JSONObject) result;
        
        AQUtility.debug(jo);
        
        assertNotNull(jo);       
        
        JSONObject cookies = (JSONObject) jo.optJSONObject("cookies");
        assertNotNull(cookies); 
        
        assertEquals("world", cookies.optString("hello"));
        assertEquals("bar", cookies.optString("foo"));
        
        
    }
	
	public void testAjaxPostMulti(){
		
        String url = "http://www.androidquery.com/p/multipart";
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		byte[] data = new byte[1234];
		byte[] data2 = new byte[2345];
		
		params.put("data", data);
		params.put("data2", data2);
		
        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jo, AjaxStatus status) {
                   
            	AQUtility.debug(status.getCode(), status.getError());
            	
        		AQueryAsyncTest.this.result = jo;
            }
        });
		
        waitAsync();
		
        JSONObject jo = (JSONObject) result;
        
        AQUtility.debug(jo);
        
        assertNotNull(jo);       
        
        assertEquals(1234, jo.optInt("data"));
        assertEquals(2345, jo.optInt("data2"));
	}
	
	public void testAjaxPostMultiAuth(){
		
        String url = "http://www.androidquery.com/p/multipart";
		
        BasicHandle handle = new BasicHandle("username", "1234");
        
		Map<String, Object> params = new HashMap<String, Object>();
		
		byte[] data = new byte[1234];
		byte[] data2 = new byte[2345];
		
		params.put("data", data);
		params.put("data2", data2);
		
        aq.auth(handle).ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jo, AjaxStatus status) {
                   
            	AQUtility.debug(status.getCode(), status.getError());
            	
        		AQueryAsyncTest.this.result = jo;
            }
        });
		
        waitAsync();
		
        JSONObject jo = (JSONObject) result;
        
        AQUtility.debug(jo);
        
        assertNotNull(jo);       
        
        assertEquals(1234, jo.optInt("data"));
        assertEquals(2345, jo.optInt("data2"));
        
        JSONObject headers = jo.optJSONObject("headers");
        
        assertNotNull(headers.optString("Authorization"));
	}
	
	
	public void testAjaxPostMultiFile() throws IOException{
		
        String url = "http://www.androidquery.com/p/multipart";
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		File tempFile1 = AQUtility.getCacheFile(AQUtility.getCacheDir(getActivity()), "pre1");
		File tempFile2 = AQUtility.getCacheFile(AQUtility.getCacheDir(getActivity()), "pre2");
		
		byte[] data1 = new byte[1234];
		byte[] data2 = new byte[2345];
		
		AQUtility.write(tempFile1, data1);
		AQUtility.write(tempFile2, data2);
		
		params.put("data", tempFile1);
		params.put("data2", tempFile2);
		
        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jo, AjaxStatus status) {
                   
            	AQUtility.debug(status.getCode(), status.getError());
            	
        		AQueryAsyncTest.this.result = jo;
            }
        });
		
        waitAsync();
		
        JSONObject jo = (JSONObject) result;
        
        AQUtility.debug(jo);
        
        assertNotNull(jo);       
        
        assertEquals(1234, jo.optInt("data"));
        assertEquals(2345, jo.optInt("data2"));
	}
	
	public void testAjaxPostMultiInputStream(){
		
        String url = "http://www.androidquery.com/p/multipart";
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		byte[] data = new byte[1234];
		byte[] data2 = new byte[2345];
		
		params.put("data", new ByteArrayInputStream(data));
		params.put("data2", new ByteArrayInputStream(data2));
		
        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jo, AjaxStatus status) {
                   
            	AQUtility.debug(status.getCode(), status.getError());
            	
        		AQueryAsyncTest.this.result = jo;
            }
        });
		
        waitAsync();
		
        JSONObject jo = (JSONObject) result;
        
        AQUtility.debug(jo);
        
        assertNotNull(jo);       
        
        assertEquals(1234, jo.optInt("data"));
        assertEquals(2345, jo.optInt("data2"));
	}
	
	public void testAjaxPostMultiError(){
		
        String url = "http://www.androidquery.com/p/multipart2";
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		byte[] data = new byte[1234];
		byte[] data2 = new byte[2345];
		
		params.put("data", data);
		params.put("data2", data2);
		
        aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jo, AjaxStatus status) {
                   
            	AQUtility.debug(status.getCode(), status.getError());
            	
        		AQueryAsyncTest.this.result = jo;
        		AQueryAsyncTest.this.status = status;
            }
        });
		
        waitAsync();
		
        JSONObject jo = (JSONObject) result;
        
        AQUtility.debug("error code", status.getCode());
        
        assertNull(jo);       
        assertEquals(404, status.getCode());
        
        
        String error = status.getError();
        assertNotNull(error);
        
	}
	
	
	public void testAjaxCookieGet(){
		
		
		String url = "http://www.google.com";
        
        aq.ajax(url, String.class, new AjaxCallback<String>() {

            @Override
            public void callback(String url, String json, AjaxStatus status) {
                
            	done(url, json, status);
            	
            }
        });
		
        waitAsync();
        
        assertNotNull(result);
		
        List<Cookie> cookies = status.getCookies();
        assertTrue(cookies.size() > 0);
        
        Cookie c = cookies.get(0);
        AQUtility.debug(c.getName(), c.getValue());
        
	}
	
	
	public void testAjaxHeadersGet(){
		
		
		String url = "http://www.google.com";
        
        aq.ajax(url, String.class, new AjaxCallback<String>() {

            @Override
            public void callback(String url, String json, AjaxStatus status) {
                
            	done(url, json, status);
            	
            }
        });
		
        waitAsync();
        
        assertNotNull(result);
		
        List<Header> headers = status.getHeaders();
        assertTrue(headers.size() > 0);
        
        Header c = headers.get(0);
        AQUtility.debug(c.getName(), c.getValue());
        
	}
	
	//http://www.proxynova.com/proxy-server-list/
	public void testAjaxProxy() throws ClientProtocolException, IOException{
		
		String url = "http://www.google.com";
		
        aq.ajax(url, String.class, new AjaxCallback<String>() {

            @Override
            public void callback(String url, String json, AjaxStatus status) {
                
            	done(url, json, status);
            	
            }
        }.proxy("192.168.0.105", 3128));
		
        waitAsync();
        
        assertNotNull(result);
		
        List<Header> headers = status.getHeaders();
        assertTrue(headers.size() > 0);
        
        Header c = headers.get(0);
        AQUtility.debug(c.getName(), c.getValue());
        
	}
	
	public void testAjaxProxyGzip() throws ClientProtocolException, IOException{
        
	    //BasicProxyHandle handle = new BasicProxyHandle("192.168.0.105", 3128, null, null);
	    
	    //AjaxCallback.setProxyHandle(handle);
	    
        String url = "http://androidquery.appspot.com/p/doNothing";
        
        AjaxCallback<byte[]> cb = new AjaxCallback<byte[]>() {

            @Override
            public void callback(String url, byte[] data, AjaxStatus status) {
                
                AQUtility.debug("callback size", data.length);
                
                done(url, data, status);
                
            }
        };
        
        cb.proxy("192.168.0.105", 3128);
        
        //cb.header("User-Agent", "AppleDailyiPAD/1.0 CFNetwork/672.0.2 Darwin/14.0.0");
        
        aq.ajax(url, byte[].class, cb);
        
        waitAsync();
        
        assertNotNull(result);
        
        assertEquals("gzip", status.getHeader("Content-Encoding"));
        
        String str = new String((byte[]) result);
        AQUtility.debug(str);
    }
	
    public void testAjaxProxyBasicCredential() throws ClientProtocolException, IOException{
        
        String url = "http://www.google.com";
        String host = "192.168.111.56";
        int port = 8081;
        String user = "Peter";
        String password = "orange99";
        
        //host = "192.168.111.20";
        //user = "user1";
        //password = "Orange99";
        
        aq.ajax(url, String.class, new AjaxCallback<String>() {

            @Override
            public void callback(String url, String json, AjaxStatus status) {
                
                done(url, json, status);
                
            }
        }.proxy(host, port, user, password));
        
        waitAsync();
        
        assertNotNull(result);
        
        
    }	
    
    public void testAjaxProxyStaticBasicCredential() throws ClientProtocolException, IOException{
        
        String url = "http://www.google.com";
        String host = "192.168.1.6";
        int port = 8081;
        String user = "Peter";
        String password = "orange99";
        
        //AjaxCallback.setProxy(host, port, user, password);
        
        
        
        ProxyHandle handle = new BasicProxyHandle(host, port, user, password);
        
        AjaxCallback.setProxyHandle(handle);
        
        
        aq.ajax(url, String.class, new AjaxCallback<String>() {

            @Override
            public void callback(String url, String json, AjaxStatus status) {
                
                done(url, json, status);
                
            }
        });
        
        waitAsync();
        
        assertNotNull(result);
        
        
    }   
    
    public void testAjaxProxyStaticNTLMCredential() throws ClientProtocolException, IOException{
        
        String url = "http://www.google.com";
        
        String proxyHost = "192.168.111.20";
        String domain = "AIGENSTEST.com";
        String user = "user3";
        String password = "Orange99";
        int port = 8081;
        
        //ProxyHandle handle = new BasicProxyHandle(host, port, user, password);
        
        ProxyHandle handle = new NTLMProxyHandle(proxyHost, port, domain, user, password);
     
        AjaxCallback.setProxyHandle(handle);
        
        
        aq.ajax(url, String.class, new AjaxCallback<String>() {

            @Override
            public void callback(String url, String json, AjaxStatus status) {
                
                done(url, json, status);
                
            }
        });
        
        waitAsync();
        
        assertNotNull(result);
        
        
    }   
	
	public void testAjaxXmlPullParser(){
		
		String url = "https://picasaweb.google.com/data/feed/base/featured?max-results=8";		
		
		aq.ajax(url, XmlPullParser.class, new AjaxCallback<XmlPullParser>(){
			
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
				
				AQUtility.debug(images);
			}
			
		});
	        
		waitAsync();
	}
	
	
	public void testAjaxParseEncoding(){
				
		//String url = "http://www.kyotojp.com/limousine-big5.html";
		String url = "http://big5.china.com.cn/";
		//String url = "http://www.shouda8.com/shouda/tunshixingkong/14/2618.htm";
		
		aq.ajax(url, String.class, -1, new AjaxCallback<String>(){
			
			public void callback(String url, String html, AjaxStatus status) {
				
				AQUtility.debug("charset", html);
				
			}
			
		});
	        
		waitAsync();
	}
	
	private String getCharset(String html){
		
		String pattern = "<(META|meta) [^>]*http-equiv[^>]*\"Content-Type\"[^>]*>";
		
		Pattern p = Pattern.compile(pattern);		
		Matcher m = p.matcher(html);
		
		if(!m.find()) return null;
		
		String tag = m.group();
		
		if(tag == null) return null;
		int i = tag.indexOf("charset");
		if(i == -1) return null;
		
		String charset = tag.substring(i + 7).replaceAll("[^\\w-]", "");
		
		return charset;
	}
	
	private String correctEncoding(byte[] data){
		
		String result = null;
		
		try{
			result = new String(data, "utf-8");
			
			String charset = getCharset(result);
			if(charset != null || !"utf-8".equalsIgnoreCase(charset)){		
				result = new String(data, charset);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
		
	}
	
	public void testAjaxGzip() {
		
		String url = "http://www.yahoo.com";
        
		AjaxCallback<String> cb = new AjaxCallback<String>(){
			
			@Override
			public void callback(String url, String jo, AjaxStatus status) {
				
				done(url, jo, status);
				
			}
			
		};
		
		
		cb.url(url).type(String.class);		
        aq.ajax(cb);
        
        waitAsync();
            
        String html = (String) result;
        
        assertNotNull(result);
        assertTrue(html.contains("<html"));
        
        assertEquals("gzip", status.getHeader("Content-Encoding"));
    }
	
	
	public void testAjaxGzipError() {
		
		String url = "http://www.thenorthface.com/invalid";
        
		AjaxCallback<String> cb = new AjaxCallback<String>(){
			
			@Override
			public void callback(String url, String jo, AjaxStatus status) {
				
				done(url, jo, status);
				
			}
			
		};
		
		cb.url(url).type(String.class);		
        aq.ajax(cb);
        
        waitAsync();
            
        assertNull(result);
        assertTrue(status.getCode() == 404);
        
        assertTrue(status.getError().contains("<html"));
    }
	
	public void testAjaxFileUrl() {
		
		String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
        
		AjaxCallback<File> cb = new AjaxCallback<File>();
		cb.url(url).type(File.class);		
		
        //aq.ajax(url, JSONObject.class, cb);
        aq.sync(cb);
		
        File file = cb.getResult();
        
        assertNotNull(file);       
        
        AQUtility.debug(file.getAbsolutePath());
        
        String path = file.getAbsolutePath();
        
        AjaxCallback<String> cb2 = new AjaxCallback<String>();
		cb2.url(path).fileCache(true).type(String.class);
        
		aq.sync(cb2);
		
		String html = cb2.getResult();
		
		AQUtility.debug(html);
		
		assertNotNull(html);
		
    }
	
	public void testFile404() {
		
		String url = "http://androidquery.appspot.com/test/fake";
		
		File old = aq.getCachedFile(url);
		if(old != null){
			old.delete();
		}
		
		old = aq.getCachedFile(url);
		assertNull(old);
        
		AjaxCallback<File> cb = new AjaxCallback<File>();
		cb.url(url).type(File.class);		
		
        //aq.ajax(url, JSONObject.class, cb);
        aq.sync(cb);
		
        File file = cb.getResult();
        AjaxStatus status = cb.getStatus();
        
        assertNull(file);       
        assertEquals(404, status.getCode());
        
		old = aq.getCachedFile(url);
		assertNull(old);
		
    }
	
	
	public void testFile404NotOverwritenOldFile() throws IOException {
		
		String url = "http://androidquery.appspot.com/test/fake";
		
		File old = AQUtility.getCacheFile(AQUtility.getCacheDir(getActivity()), url);
		if(old != null){
			old.createNewFile();
			AQUtility.write(old, new byte[1234]);
		}
		
		old = aq.getCachedFile(url);
		assertNotNull(old);
        assertEquals(1234, old.length());
		
		AjaxCallback<File> cb = new AjaxCallback<File>();
		cb.url(url).type(File.class);		
		
        aq.sync(cb);
		
        File file = cb.getResult();
        AjaxStatus status = cb.getStatus();
        
        assertNull(file);       
        assertEquals(404, status.getCode());
        
		old = aq.getCachedFile(url);
		assertNotNull(old);
        assertEquals(1234, old.length());
		
    }
	
	public void testFileBatchDownloads(){
		
		String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
		
		List<String> urls = new ArrayList<String>();
		
		for(int i = 0; i < 10; i++){
			urls.add(url + "&test=" + i);
		}
		
		for(String u: urls){
			
			AjaxCallback<File> cb = new AjaxCallback<File>();
			cb.type(File.class).fileCache(true).url(u);
			
			aq.sync(cb);
			
			File result = cb.getResult();
			
			AQUtility.debug("cached", result.getAbsoluteFile());
			
		}
		
		
	}
	
	public void testAjaxDelete() {
		
		String url = "http://www.androidquery.com/p/doNothing";
        
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>(){
			
			@Override
			public void callback(String url, JSONObject jo, AjaxStatus status) {
				
				done(url, jo, status);
				
			}
			
		};
		
				
        //aq.ajax(cb);
        aq.delete(url, JSONObject.class, cb);
		
        waitAsync();
        
        JSONObject jo = (JSONObject) result;
        
        AQUtility.debug(jo);
        
        assertNotNull(jo);       
        
        assertEquals("DELETE", jo.optString("method"));
        
        
    }
	
	   public void testAjaxPostJson() throws UnsupportedEncodingException, JSONException{
	        
	        String url = "http://www.androidquery.com/p/doNothing";
	        
	        AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>(){
	            
	            @Override
	            public void callback(String url, JSONObject jo, AjaxStatus status) {
	                
	                done(url, jo, status);
	                
	            }
	            
	        };
	        
	        JSONObject input = new JSONObject();
	        input.putOpt("hello", "world");
	        
	        aq.post(url, input, JSONObject.class, cb);
	        
	        
	        waitAsync();
	        
	        JSONObject jo = (JSONObject) result;
	        
	        AQUtility.debug(jo);
	        
	        assertNotNull(jo);       
	        
	        assertEquals("POST", jo.optString("method"));
	        
	    }
	
	
	public void testAjaxPut() throws UnsupportedEncodingException{
		
		String url = "http://www.androidquery.com/p/doNothing";
		
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>(){
			
			@Override
			public void callback(String url, JSONObject jo, AjaxStatus status) {
				
				done(url, jo, status);
				
			}
			
		};
		
		StringEntity entity = new StringEntity(new JSONObject().toString());
	
        aq.put(url, "application/json", entity, JSONObject.class, cb);
		
        waitAsync();
        
        JSONObject jo = (JSONObject) result;
        
        AQUtility.debug(jo);
        
        assertNotNull(jo);       
        
        assertEquals("PUT", jo.optString("method"));
        
	}
	
	
	public void testAjaxPutNamedValues() throws UnsupportedEncodingException{
		
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("count", "5"));

		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, "UTF-8");
		entity.setContentType("application/x-www-form-urlencoded;charset=UTF-8");
		
		String url = "http://www.androidquery.com/p/doNothing";
		
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>(){
			
			@Override
			public void callback(String url, JSONObject jo, AjaxStatus status) {
				
				done(url, jo, status);
				
			}
			
		};
		
		
        aq.put(url, "application/x-www-form-urlencoded;charset=UTF-8", entity, JSONObject.class, cb);
		
        waitAsync();
        
        JSONObject jo = (JSONObject) result;
        
        AQUtility.debug(jo);
        
        assertNotNull(jo);       
        
        JSONObject params = jo.optJSONObject("params");
        assertEquals("5", params.optString("count"));
        
        //assertEquals("PUT", jo.optString("method"));
        
	}
	
	public void testAjaxPostWithEmptyParams() {
		
		String url = "http://www.androidquery.com/p/doNothing";
        
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>(){
			
			@Override
			public void callback(String url, JSONObject jo, AjaxStatus status) {
				
				done(url, jo, status);
				
			}
			
		};
		
		cb.url(url).type(JSONObject.class).method(AQuery.METHOD_POST);
		
		aq.ajax(cb);		
        
        waitAsync();
        
        JSONObject jo = (JSONObject) result;
        
        AQUtility.debug(jo);
        
        assertNotNull(jo);       
        
        assertEquals("POST", jo.optString("method"));
        
        
    }
	
	
	public void testAjaxTimeout() {
		
		String url = "http://farm6.static.flickr.com/5035/5802797131_a729dac808_b.jpg";
        
		AjaxCallback<File> cb = new AjaxCallback<File>();
		cb.url(url).type(File.class).timeout(1);		
		
        aq.sync(cb);
		
        File file = cb.getResult();
        AjaxStatus status = cb.getStatus();
        
        assertNull(file);       
        assertTrue(status.getCode() == AjaxStatus.NETWORK_ERROR);
    }
	
	public void testAjaxTimeoutFiveSeconds() {
		
		
		String url = "http://farm6.static.flickr.com/5035/5802797131_a729dac808_b.jpg";
        
		url = "http://deelay.me/10000/" + url;
		
		AjaxCallback.setTimeout(5000);
		
		AjaxCallback<File> cb = new AjaxCallback<File>();
		cb.url(url).type(File.class);		
		
		long start = System.currentTimeMillis();
		
        aq.sync(cb);
		
        File file = cb.getResult();
        AjaxStatus status = cb.getStatus();
        
        long end = System.currentTimeMillis();
        
        long diff = end - start;
        
        AQUtility.debug("timeout", diff);
        
        assertTrue(diff < 10000);
        
        assertNull(file);       
        assertTrue(status.getCode() == AjaxStatus.NETWORK_ERROR);
    }
	
	public void testAjaxAbortAfterNetwork() {
		
		String url = "http://shopsixapp.appspot.com/z/music/01.mp3";
        
		final AjaxCallback<File> cb = new AjaxCallback<File>(){
			
			@Override
			public void callback(String url, File object, AjaxStatus status) {
				
				done(url, object, status);
			}
			
		};
		cb.url(url).type(File.class);		
		
		aq.ajax(cb);
		
		AQUtility.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				cb.abort();
			}
		}, 1000);
		
		waitAsync();
        
        assertNull(result);      
        assertTrue(status.getCode() == AjaxStatus.NETWORK_ERROR);
    }
	
	public void testAjaxAbortBeforeNetwork() {
		
		String url = "http://shopsixapp.appspot.com/z/music/01.mp3";
        
		final AjaxCallback<File> cb = new AjaxCallback<File>(){
			
			@Override
			public void callback(String url, File object, AjaxStatus status) {
				
				done(url, object, status);
			}
			
		};
		cb.url(url).type(File.class);		
		
		aq.ajax(cb);
		cb.abort();
		
		waitAsync();
        
        assertNull(result);      
        assertTrue(status.getCode() == AjaxStatus.NETWORK_ERROR);
    }
	
	
	public void testAjaxEmptyString() {
		
		String url = "http://www.androidquery.com/p/doNothing?response=200";
        
		AjaxCallback<String> cb = new AjaxCallback<String>(){
			
			@Override
			public void callback(String url, String str, AjaxStatus status) {
				
				done(url, str, status);
				
			}
			
		};
		
			
        aq.ajax(url, String.class, cb);
        
        waitAsync();
        
        assertNotNull(result);       
        assertTrue("".equals(result));
        
    }
	
	
	public void testAjaxNetworkUrlCallback() {
		
		String url = "http://dummy.com/1234";
		String networkUrl = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
        
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>(){
			
			@Override
			public void callback(String url, JSONObject jo, AjaxStatus status) {
				
				done(url, jo, status);
				
			}
			
		}.networkUrl(networkUrl);
		
			
        aq.ajax(url, JSONObject.class, 1000, cb);
        
        waitAsync();
        
        JSONObject jo = (JSONObject) result;
        
        assertNotNull(jo);       
        assertNotNull(jo.opt("responseData"));
        
        waitSec(2000);
        
        File file = aq.getCachedFile(url);
        assertNotNull(file);
        
        
    }
	
	
	public void testAjaxActiveCount() {
		
		assertEquals(0, AjaxCallback.getActiveCount());
        
		
		String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
        
        aq.ajax(url, JSONObject.class, this, "jsonCb");
        
        
        int count = AjaxCallback.getActiveCount();
        AQUtility.debug("active count", count);
        
        assertEquals(1, AjaxCallback.getActiveCount());
        
        waitAsync();
        
        assertEquals(0, AjaxCallback.getActiveCount());
        
        JSONObject jo = (JSONObject) result;
        
        assertNotNull(jo);       
        assertNotNull(jo.opt("responseData"));
        
    }
	
	public void testRetryFailed() {
		
		String url = "http://www.androidquery.com/p/retry?wait=5000";
	
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.weakHandler(this, "jsonCb").timeout(1000);
		
		aq.ajax(url, JSONObject.class, cb);
        
        waitAsync();
       
        JSONObject jo = (JSONObject) result;
        
        assertNull(jo);
        assertEquals(-101, status.getCode());
        
        
	}
	
	public void testRetryOk() {
		
		String url = "http://www.androidquery.com/p/retry?wait=3000";
	
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.retry(1).weakHandler(this, "jsonCb").timeout(1000);
		
		aq.ajax(url, JSONObject.class, cb);
        
        waitAsync();
       
        JSONObject jo = (JSONObject) result;
        
        assertNotNull(jo);
        assertEquals(200, status.getCode());
        
        
	}
	
	public void testAjaxSimulateError() {
        
	    AjaxCallback.setSimulateError(true);
	    
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
        
        assertNull(jo);       
        assertNotNull(status);
        assertEquals(AjaxStatus.NETWORK_ERROR, status.getCode());
        
    }
	
	
	public void testAjaxTransformErrorWontCache() {
        
        String url = "http://www.google.com";
        
        /*
        AjaxCallback<String> cb = new AjaxCallback<String>(){
            
            @Override
            public void callback(String url, String str, AjaxStatus status) {
                
               
           
            }
            
        };
        
        aq.ajax(url, String.class, 0, cb);
        
        waitAsync(2000);
        
        assertNotNull(aq.getCachedFile(url));
        */
        
        AjaxCallback<JSONObject> cb2 = new AjaxCallback<JSONObject>(){
            
            @Override
            public void callback(String url, JSONObject jo, AjaxStatus status) {
                
                done(url, jo, status);
                
            }
            
        };
        
            
        aq.ajax(url, JSONObject.class, 0, cb2);
        
        waitAsync(2000);
        
        assertEquals(AjaxStatus.TRANSFORM_ERROR, status.getCode());
        
        JSONObject jo = (JSONObject) result;
        
        assertNull(jo);       
        
        File file = aq.getCachedFile(url);
        
        AQUtility.debug("check file", file);
        
        assertNull(file);
        
        
    }
	
	public void testAjaxPostMultiWithProxy(){
        
        String url = "http://www.androidquery.com/p/multipart";
        
        Map<String, Object> params = new HashMap<String, Object>();
        
        byte[] data = new byte[1234];
        byte[] data2 = new byte[2345];
        
        params.put("data", data);
        params.put("data2", data2);
        
        AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jo, AjaxStatus status) {
                   
                AQUtility.debug(status.getCode(), status.getError());
                
                AQueryAsyncTest.this.result = jo;
            }
        };
        
        cb.proxy("192.168.0.102", 3128);
        
        aq.ajax(url, params, JSONObject.class, cb);
        
        waitAsync();
        
        JSONObject jo = (JSONObject) result;
        
        AQUtility.debug(jo);
        
        assertNotNull(jo);       
        
        assertEquals(1234, jo.optInt("data"));
        assertEquals(2345, jo.optInt("data2"));
    }

	
	public void testAjaxPostMultiWithProxyHandle(){
        
	    BasicProxyHandle handle = new BasicProxyHandle("192.168.0.102", 3128, null, null);
        AjaxCallback.setProxyHandle(handle);
	    
	    
        String url = "http://www.androidquery.com/p/multipart";
        
        Map<String, Object> params = new HashMap<String, Object>();
        
        byte[] data = new byte[1234];
        byte[] data2 = new byte[2345];
        
        params.put("data", data);
        params.put("data2", data2);
        
        AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject jo, AjaxStatus status) {
                   
                AQUtility.debug(status.getCode(), status.getError());
                
                AQueryAsyncTest.this.result = jo;
            }
        };
        
        
        aq.ajax(url, params, JSONObject.class, cb);
        
        waitAsync();
        
        JSONObject jo = (JSONObject) result;
        
        AQUtility.debug(jo);
        
        assertNotNull(jo);       
        
        assertEquals(1234, jo.optInt("data"));
        assertEquals(2345, jo.optInt("data2"));
    }

	   
    public void testIOError(){
        
        AQUtility.cleanCache(AQUtility.getCacheDir(getActivity()), 0, 0);
        
        String LAND_URL = "http://farm6.static.flickr.com/5035/5802797131_a729dac808_b.jpg";
        File file = aq.getCachedFile(LAND_URL);
        
        assertNull(file);
        
        AQUtility.TEST_IO_EXCEPTION = true;
        
        AjaxCallback<File> cb = new AjaxCallback<File>(){
            
            @Override
            public void callback(String url, File file, AjaxStatus status) {
                
                done(url, file, status);
                
            }
            
        };
        
        cb.fileCache(true);
        
        aq.ajax(LAND_URL, File.class, 0, cb);
        
        waitAsync();
        
        file = (File) result;
        
        
        assertNull(result);
        
        File file2 = aq.getCachedFile(LAND_URL);
        
        if(file2 != null){
            AQUtility.debug("file length", file2.length());
        }
        
        assertNull(file2);
        
        
    }
    

	
}
