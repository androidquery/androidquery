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
import com.androidquery.callback.LocationAjaxCallback;
import com.androidquery.util.AQUtility;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.View;
import android.widget.TextView;


public class AQueryLocationTest extends AbstractTest<AQueryTestActivity> {

	
	private String url;
	private Object result;
	private AjaxStatus status;
	
	public AQueryLocationTest() {		
		super(AQueryTestActivity.class);
    }

	
	public void done(String url, Object result, AjaxStatus status){
		
		this.url = url;
		this.result = result;
		this.status = status;

		log("done", result);
		
		checkStatus(status);
		
		log("ui", AQUtility.isUIThread());
		//assertTrue(AQUtility.isUIThread());
		
		done();
		
	}
	
	private void checkStatus(AjaxStatus status){
		
		AQUtility.debug("redirect", status.getRedirect());
		AQUtility.debug("time", status.getTime());
		AQUtility.debug("response", status.getCode());
		
		assertNotNull(status);
		assertNotNull(status.getTime());
		
		
		
		
	}
	
	public void testLocationIter3Acc10000() {
		
		LocationAjaxCallback cb = new LocationAjaxCallback(){
			
			private int n;
			
			@Override
			public void callback(String url, Location loc, AjaxStatus status) {
				
				n++;
				
				AQUtility.debug(n);				
				AQUtility.debug(loc);
				
				assertNotNull(loc);
				
				if(n == 3){
					assertEquals("gps", loc.getProvider());
				}
				
				
			}
		};
    	cb.timeout(30 * 1000).accuracy(10000).iteration(3).tolerance(-1);   	
    	cb.async(getActivity());
		
        waitAsync(5000);
             
        
    }
	
	public void testLocationIter2Acc10000() {
		
		LocationAjaxCallback cb = new LocationAjaxCallback(){
			
			private int n;
			
			@Override
			public void callback(String url, Location loc, AjaxStatus status) {
				
				n++;
				
				AQUtility.debug(n);				
				AQUtility.debug(loc);
				
				assertNotNull(loc);
				
				if(n == 2){
					assertEquals("gps", loc.getProvider());
				}
				
				
			}
		};
    	cb.timeout(30 * 1000).accuracy(10000).iteration(2).tolerance(-1);   	
    	cb.async(getActivity());
		
        waitAsync(5000);
             
        
    }
	
	public void testLocationIter1Acc10000() {
		
		LocationAjaxCallback cb = new LocationAjaxCallback(){
			
			private int n;
			
			@Override
			public void callback(String url, Location loc, AjaxStatus status) {
				
				n++;
				
				AQUtility.debug(n);				
				AQUtility.debug(loc);
				
				assertNotNull(loc);
				
				if(n == 1){
					assertEquals("gps", loc.getProvider());
				}
				
				
			}
		};
    	cb.timeout(5 * 1000).accuracy(10000).iteration(1).tolerance(-1);   	
    	cb.async(getActivity());
		
        waitAsync(5000);
             
        
    }
	
	public void testLocationIter2AccFail() {
		
		LocationAjaxCallback cb = new LocationAjaxCallback(){
			
			private int n;
			
			
			@Override
			public void callback(String url, Location loc, AjaxStatus status) {
				
				n++;
				
				AQUtility.debug(n);				
				AQUtility.debug(loc);
				
				
				if(n == 2){
					assertNull(loc);
					assertEquals(AjaxStatus.TRANSFORM_ERROR, status.getCode());
				}else if(n < 2){
					assertNotNull(loc);
				}else{
					assertTrue(false);
				}
				
				
			}
		};
		
    	cb.timeout(5 * 1000).accuracy(-1).iteration(2).tolerance(-1);   	
    	cb.async(getActivity());
		
        waitAsync(6000);
             
        
    }
}
