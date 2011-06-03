package com.androidquerytest;

import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AsyncUtility;
import com.androidquery.util.Utility;

public class ListenerTestActivity extends Activity {

	private AQuery aq;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
				
		setContentView(R.layout.listener_test);
		
		setupTest();
		
	}
	
	private void setupTest(){
		
		Utility.setDebug(true);
		
		aq = new AQuery(this);
		
		aq.id(R.id.clicked1).clicked(this, "clicked1");
		
		aq.id(R.id.clicked2).clicked(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TestUtility.showToast(ListenerTestActivity.this, "pass");
			}
		});
		
		aq.id(R.id.image1).image("http://www.vikispot.com/z/images/vikispot/android-w.png");
		aq.id(R.id.image_reload).clicked(this, "reloadImage");
		
		aq.id(R.id.image_clear_mem).clicked(this, "clearMem");
		aq.id(R.id.image_clear_disk).clicked(this, "clearDisk");
		
		aq.id(R.id.async_bytes).clicked(this, "asyncBytes");
		aq.id(R.id.async_json).clicked(this, "asyncJson");
		aq.id(R.id.async_bm).clicked(this, "asyncBitmap");
		aq.id(R.id.async_html).clicked(this, "asyncHtml");
		
		
	}
	
	public void asyncBytes(){
		
		String url = "http://www.vikispot.com/z/images/vikispot/android-w.png";
		
		aq.ajax(url, byte[].class, new AjaxCallback<byte[]>() {

			@Override
			public void callback(String url, byte[] object, int statusCode, String statusMessage) {
				TestUtility.showToast(ListenerTestActivity.this, "length:" + object.length);
			}
		});
		/*
		AsyncUtility.async(this, url, false, false, true, new BytesAjaxCallback() {

			@Override
			public void callback(String url, byte[] object, int statusCode, String statusMessage) {
				TestUtility.showToast(ListenerTestActivity.this, "length:" + object.length);
			}
		});
		*/
		
	}
	
	public void asyncBitmap(){
		
		String url = "http://www.vikispot.com/z/images/vikispot/android-w.png";
		
		aq.ajax(url, Bitmap.class, new AjaxCallback<Bitmap>() {

			@Override
			public void callback(String url, Bitmap object, int statusCode, String statusMessage) {
				TestUtility.showToast(ListenerTestActivity.this, "bm:" + object);
			}
		});
		/*
		AsyncUtility.async(this, url, true, true, true, new BitmapAjaxCallback() {

			@Override
			public void callback(String url, Bitmap object, int statusCode, String statusMessage) {
				TestUtility.showToast(ListenerTestActivity.this, "bm:" + object);
			}
		});
		
		*/
	}
	
	public void asyncHtml(){
		
		String url = "http://www.google.com";
		
		aq.ajax(url, String.class, new AjaxCallback<String>() {

			@Override
			public void callback(String url, String object, int statusCode, String statusMessage) {
				TestUtility.showToast(ListenerTestActivity.this, "html:" + object);
			}
			
		});
		/*
		AsyncUtility.async(this, url, false, false, new HTMLAjaxCallback() {

			@Override
			public void callback(String url, String object, int statusCode, String statusMessage) {
				TestUtility.showToast(ListenerTestActivity.this, "html:" + object);
			}
		});
		*/
		
	}
	
	public void asyncJson(){
		
		String url = "http://www.vikispot.com/api/children?spotId=1";
		
		aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {

			@Override
			public void callback(String url, JSONObject object, int statusCode, String statusMessage) {
				TestUtility.showToast(ListenerTestActivity.this, "json:" + object);
			}
		});
		/*
		AsyncUtility.async(this, url, false, false, true, new JSONAjaxCallback() {

			@Override
			public void callback(String url, JSONObject object, int statusCode, String statusMessage) {
				TestUtility.showToast(ListenerTestActivity.this, "json:" + object);
			}
		});
		*/
	}
	
	
	public void clicked1(View view){
		
		TestUtility.showToast(this, "pass");
		
		
	}
	
	public void clearDisk(View view){
		Utility.cleanCacheAsync(this, 0, 0);
	}
	
	public void clearMem(View view){
		BitmapAjaxCallback.clearCache();
	}
	
	public void reloadImage(View view){
		
		aq.id(R.id.image1).clear();
		aq.image("http://www.vikispot.com/z/images/vikispot/android-w.png");
		
	}
	
}
