package com.androidquery.test;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
		
		aq.id(R.id.image1).text("Hihi");
		
		
		aq.id(R.id.clicked1).clicked(this, "clicked1");
		
		aq.id(R.id.clicked2).clicked(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TestUtility.showToast(ListenerTestActivity.this, "pass");
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
		
		aq.id(R.id.async_post).clicked(this, "asyncPost");
		
		loadImage();
	}
	
	public void asyncPost(){
		
		//String url = "http://www.google.com/uds/GnewsSearch";
		String url = "http://www.androidquery.com/api/likes";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("spotId", "1677246");
		

		aq.ajax(url, params, JSONObject.class, this, "jsonCallback");
		
	}
	
	
	public void asyncBytes(){
		
		//fetch a remote resource in raw bytes
		
		String url = "http://www.androidquery.com/z/images/vikispot/android-w.png";
		
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
		
		String url = "http://www.androidquery.com/z/images/vikispot/android-w.png";
		
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
		
		//String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";		
		String url = "http://androidquery.appspot.com/api/memorize";
		aq.ajax(url, JSONObject.class, this, "jsonCallback");
		
		
		
	}
	
	//07-28 03:22:16.410: WARN/AQuery(931): {"req":"GET http:\/\/androidquery.appspot.com\/api\/memorize HTTP\/1.1\nHost: androidquery.appspot.com\r\nCookie: \r\nVia: 1.1 202.140.101.82:3128 (squid\/2.7.STABLE6)\r\nCache-Control: max-age=259200\r\nUser-Agent: Dalvik\/1.4.0 (Linux; U; Android 2.3.3; GT-I9000 Build\/GINGERBREAD)\r\nX-AppEngine-Country: HK\r\n\r\n","radd":"202.140.101.82","status":"1","rhost":"202.140.101.82"}

	public void jsonCallback(String url, JSONObject json, AjaxStatus status){
		
		if(json != null){
			
			//successful ajax call, show status code and json content
			Toast.makeText(aq.getContext(), status.getCode() + ":" + json.toString(), Toast.LENGTH_LONG).show();
		
			//AQUtility.debug(json.);
			
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
	
	public void loadImage(){
		String imageUrl = "http://farm6.static.flickr.com/5035/5802797131_a729dac808_b.jpg";			
		aq.id(R.id.image1).image(imageUrl, true, true, 200, AQuery.INVISIBLE);
	}
	
	public void reloadImage(View view){
		

		/*
		aq.id(R.id.image1).image("http://www.androidquery.com/z/images/vikispot/android-w.png");
		
		boolean memCache = false;
		boolean fileCache = true;
		aq.id(R.id.image1).image("http://www.androidquery.com/z/images/vikispot/android-w.png", memCache, fileCache);
		*/
		
		/*
		BitmapAjaxCallback cb = new BitmapAjaxCallback(){
			
			@Override
			public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status){
						
				iv.setImageBitmap(bm);
				
				iv.setDrawingCacheEnabled(true);
				Bitmap dc = iv.getDrawingCache();
				
				AQUtility.debug("dc", dc.getWidth() + "x" + dc.getHeight());
				
				//do something to the bitmap
				//iv.setColorFilter(tint, PorterDuff.Mode.SRC_ATOP);
				
			}
			
		};
		*/
		
		//String imageUrl = "http://www.androidquery.com/z/images/vikispot/android-w.png";
		//String imageUrl = "http://lh6.ggpht.com/hgQVg7upCNxcSqJ9T2XabDm9d6IsRjI2lXDKJ03vHSlg5nXDV-2Actla3H8kCVCKdAu5-8-xDAXpxl_9";
		//String imageUrl = "http://farm6.static.flickr.com/5035/5802797131_a729dac808_b.jpg";		
		String imageUrl = "http://a.b.com/invalid.jpg";		
		aq.id(R.id.image1).image(imageUrl, true, true, 200, AQuery.INVISIBLE);
		
	}
	
}
