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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.auth.BasicHandle;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.View;
import android.widget.TextView;


public class AQueryAuthTest extends AbstractTest<AQueryTestActivity> {

	
	private String url;
	private Object result;
	private AjaxStatus status;
	
	public AQueryAuthTest() {		
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
	

	/*
	public void testBasicAuth() {
		
		//BasicHandle handle = new BasicHandle("tinyeeliu@gmail.com", "password");
		
		String url = "http://xpenser.com/api/v1.0/reports/";
		
		
		AjaxCallback<JSONArray> cb = new AjaxCallback<JSONArray>();
		
		String cred = "tinyeeliu@gmail.com:password";
		
		String auth = "Basic " + Base64.encodeToString(cred.getBytes(), Base64.DEFAULT);
		
		AQUtility.debug("auth", auth);
		
		cb.header("Authorization", auth);
		cb.header("Host", "xpenser.com");
		//cb.header("User-Agent", MOBILE_AGENT);
		
		cb.url(url).type(JSONArray.class).weakHandler(this, "basicCb").async(getActivity());
		
        waitAsync();
        
        JSONArray jo = (JSONArray) result;
        
        AQUtility.debug(jo);
        
        assertNotNull(jo);       
        
    }
	*/
	public void testBasicAuthHandle() {
		
		BasicHandle handle = new BasicHandle("tinyeeliu@gmail.com", "password");
		
		String url = "http://xpenser.com/api/v1.0/reports/";
		
		aq.auth(handle).ajax(url, JSONArray.class, this, "basicCb");
		
        waitAsync();
        
        JSONArray jo = (JSONArray) result;
        
        AQUtility.debug(jo);
        
        assertNotNull(jo);       
        
    }
	
	public void basicCb(String url, JSONArray ja, AjaxStatus status) {
		
		done(url, ja, status);
		
	}
	

}
