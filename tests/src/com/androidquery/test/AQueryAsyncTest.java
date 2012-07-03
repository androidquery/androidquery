package com.androidquery.test;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
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
	
	public void testAjaxPostMultiFile() throws IOException{
		
        String url = "http://www.androidquery.com/p/multipart";
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		File tempFile1 = File.createTempFile("pre1", "bin");
		File tempFile2 = File.createTempFile("pre2", "bin");
		
		byte[] data1 = new byte[1234];
		byte[] data2 = new byte[2345];
		
		AQUtility.write(tempFile1, data1);
		AQUtility.write(tempFile2, data2);
		
		
		//byte[] data2 = new byte[2345];
		
		//params.put("data", data);
		//params.put("data2", data2);
		
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
	
	public void testAjaxProxy() throws ClientProtocolException, IOException{
		
		String url = "http://www.google.com";
        
        aq.ajax(url, String.class, new AjaxCallback<String>() {

            @Override
            public void callback(String url, String json, AjaxStatus status) {
                
            	done(url, json, status);
            	
            }
        }.proxy("62.0.192.219", 80));
		
        waitAsync();
        
        assertNotNull(result);
		
        List<Header> headers = status.getHeaders();
        assertTrue(headers.size() > 0);
        
        Header c = headers.get(0);
        AQUtility.debug(c.getName(), c.getValue());
        
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
	
}
