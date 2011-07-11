package com.androidquerytest;

import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;

public class ListenerTestActivity extends Activity {

	private AQuery aq;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
				
		setContentView(R.layout.listener_test);
		
		setupTest();
		
	}
	
	private void setupTest(){
		
		AQUtility.setDebug(true);
		
		aq = new AQuery(this);
		
		aq.id(R.id.clicked1).clicked(this, "clicked1");
		
		aq.id(R.id.clicked2).clicked(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TestUtility.showToast(ListenerTestActivity.this, "pass");
			}
		});
		
		
		
		/*
		aq.id(R.id.image1).image("http://www.vikispot.com/z/images/vikispot/android-w.png");
		
		boolean memCache = false;
		boolean fileCache = true;
		aq.id(R.id.image1).image("http://www.vikispot.com/z/images/vikispot/android-w.png", memCache, fileCache);
		*/
		
		
		
		String imageUrl = "http://www.vikispot.com/z/images/vikispot/android-w.png";
		
		final int tint = 0x77AA0000;
		
		aq.id(R.id.image1).image(imageUrl, true, true, new BitmapAjaxCallback(){
		
			@Override
			public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status){
						
				iv.setImageBitmap(bm);
				
				//do something to the bitmap
				iv.setColorFilter(tint, PorterDuff.Mode.SRC_ATOP);
				
			}
			
		});
		
		
		
		aq.id(R.id.image_reload).clicked(this, "reloadImage");
		
		aq.id(R.id.image_clear_mem).clicked(this, "clearMem");
		aq.id(R.id.image_clear_disk).clicked(this, "clearDisk");
		
		aq.id(R.id.async_bytes).clicked(this, "asyncBytes");
		aq.id(R.id.async_json).clicked(this, "asyncJson");
		
		aq.id(R.id.async_bad_json).clicked(this, "asyncBadJson");
		
		aq.id(R.id.async_bm).clicked(this, "asyncBitmap");
		aq.id(R.id.async_html).clicked(this, "asyncHtml");
		
		
	}
	
	public void asyncBytes(){
		
		//fetch a remote resource in raw bytes
		
		String url = "http://www.vikispot.com/z/images/vikispot/android-w.png";
		
		/*
		aq.ajax(url, byte[].class, new AjaxCallback<byte[]>() {

			@Override
			public void callback(String url, byte[] object, AjaxStatus status) {
				Toast.makeText(aq.getContext(), "bytes array:" + object.length, Toast.LENGTH_LONG).show();
			}
		});
		*/
		
		aq.ajax(url, byte[].class, this, "showBytes");
		
	}
	
	public void showBytes(String url, byte[] object, AjaxStatus status){
		Toast.makeText(aq.getContext(), "bytes array:" + object.length, Toast.LENGTH_LONG).show();
	}
	
	public void asyncBitmap(){
		
		//fetch a image over the network
		
		String url = "http://www.vikispot.com/z/images/vikispot/android-w.png";
		
		aq.ajax(url, Bitmap.class, new AjaxCallback<Bitmap>() {

			@Override
			public void callback(String url, Bitmap object, AjaxStatus status) {
				Toast.makeText(aq.getContext(), object.toString(), Toast.LENGTH_LONG).show();
			}
		});
		
	}
	
	public void asyncHtml(){
		
		//fetch Google's homepage in html
		
		String url = "http://www.google.com";
		
		aq.ajax(url, String.class, new AjaxCallback<String>() {

			@Override
			public void callback(String url, String html, AjaxStatus status) {
				
				Toast.makeText(aq.getContext(), html, Toast.LENGTH_LONG).show();
			}
			
		});
		
		
	}
	
	/*
	public void asyncJson(){
		
		//perform a Google search in just a few lines of code
		
		String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
		
		aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {

			@Override
			public void callback(String url, JSONObject json, AjaxStatus status) {
				
				
				if(json != null){
					
					//successful ajax call, show status code and json content
					Toast.makeText(aq.getContext(), status.getCode() + ":" + json.toString(), Toast.LENGTH_LONG).show();
				
				}else{
					
					//ajax error, show error code
					Toast.makeText(aq.getContext(), "Error:" + status.getCode(), Toast.LENGTH_LONG).show();
				}
			}
		});
		
	}
	*/
	
	public void asyncJson(){
		
		//perform a Google search in just a few lines of code
		
		String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";		
		aq.ajax(url, JSONObject.class, this, "jsonCallback");
		
	}
	
	public void jsonCallback(String url, JSONObject json, AjaxStatus status){
		
		if(json != null){
			
			//successful ajax call, show status code and json content
			Toast.makeText(aq.getContext(), status.getCode() + ":" + json.toString(), Toast.LENGTH_LONG).show();
		
		}else{
			
			//ajax error, show error code
			Toast.makeText(aq.getContext(), "Error:" + status.getCode(), Toast.LENGTH_LONG).show();
		}
		
	}
	
	
	public void asyncBadJson(){
		
		AQUtility.debug("bad called");
		
		//perform a Google search in just a few lines of code
		
		String url = "http://www.google.com/xxuds/GnewsSearch?q=Obama&v=1.0";
		
		aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {

			@Override
			public void callback(String url, JSONObject json, AjaxStatus status) {
				
				
				
				if(json != null){
					
					//successful ajax call
					Toast.makeText(aq.getContext(), json.toString(), Toast.LENGTH_LONG).show();
				
				}else{
					
					
					
					//ajax error 
					Toast.makeText(aq.getContext(), "Error:" + status.getCode(), Toast.LENGTH_LONG).show();
				}
			}
		});
		
	}	
	
	public void clicked1(View view){
		
		TestUtility.showToast(this, "pass");
		
		
	}
	
	public void clearDisk(View view){
		AQUtility.cleanCacheAsync(this, 0, 0);
	}
	
	public void clearMem(View view){
		BitmapAjaxCallback.clearCache();
	}
	
	public void reloadImage(View view){
		
		String url = "http://www.vikispot.com/z/images/vikispot/android-w.png"; 
		
		AQUtility.debug("cached image exist", aq.getCachedFile(url).exists());
		
		aq.id(R.id.image1).clear();
		aq.image(url);
		
	}
	
}
