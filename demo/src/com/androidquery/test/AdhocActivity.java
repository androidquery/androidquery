package com.androidquery.test;

import java.io.File;
import java.io.IOException;

import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;

public class AdhocActivity extends Activity {

	private AQuery aq;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.adhoc_activity);
		
		aq = new AQuery(this);
		
		aq.id(R.id.text).text("point 1");
		
		try{
			work();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	private void work() throws IOException{
		
		String url = "http://farm6.static.flickr.com/5035/5802797131_a729dac808_b.jpg";   
		
		aq.id(new ImageView(this)).image(url, false, true, 200, 0, new BitmapAjaxCallback(){
			
			protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
				AQUtility.debug(bm.getWidth());
			}
			
		});
		
	}
	
	
	
	protected int getContainer(){
		return R.layout.adhoc_activity;
	}
	
	
	

}
