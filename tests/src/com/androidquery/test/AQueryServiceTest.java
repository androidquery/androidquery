package com.androidquery.test;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.View;
import android.widget.TextView;


public class AQueryServiceTest extends AbstractTest<AQueryTestActivity> {

	
	private String url;
	private Object result;
	private AjaxStatus status;
	
	public AQueryServiceTest() {		
		super(AQueryTestActivity.class);
    }

	
	private void done(String url, Object result, AjaxStatus status){
		
		this.url = url;
		this.result = result;
		this.status = status;

		log("done", result);
		
		done();
	}
	

	
	public void testMarketSubmit() throws IOException{
	
		
		AQUtility.debug("start");
		
		String appId = "com.androidquery";
		String locale = "en_US";
		
		//String gurl = "http://192.168.1.222/test/test.htm";
		String gurl = "https://market.android.com/details?id=" + appId + "&hl=" + locale;
		
		byte[] data = IOUtility.openBytes(gurl);
		
		String html = new String(data, "UTF-8");
		
		//String url = "http://192.168.1.222/api/market?app=com.androidquery&locale=zh-TW&version=0.13.2&code=16";
		
		String url = "https://androidquery.appspot.com/api/market?app=com.androidquery&locale=en_US&version=0.24.46&code=46&aq=0.23.1&force=true";
		//String url = "http://192.168.1.165/api/market?app=com.androidquery&locale=en_US&version=0.24.46&code=46&aq=0.23.1&force=true";
		
		
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();		
		cb.url(url).type(JSONObject.class).handler(this, "jsonCb");
		
		cb.param("html", html);
		
		aq.ajax(cb);
		
		waitAsync();
        
		JSONObject jo = (JSONObject) result;
		
		String pub = jo.optString("published", null);
		String code = jo.optString("code", null);
		
		AQUtility.debug("jo", jo);
		
		AQUtility.debug("pub", pub);
		AQUtility.debug("code", code);
		
		assertNotNull(pub);
		assertNotNull(code);
		
		
	}
	
	public void jsonCb(String url, JSONObject jo, AjaxStatus status){
		done(url, jo, status);
	}
	
	
	
	
}
