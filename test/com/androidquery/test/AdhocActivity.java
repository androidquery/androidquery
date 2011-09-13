package com.androidquery.test;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.test.RunSourceActivity;
import com.androidquery.util.AQUtility;

public class AdhocActivity extends RunSourceActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		work();	
	}
	
	private void work(){
		
		AQUtility.debug("adhoc");
		
		String thumbnail = "http://farm6.static.flickr.com/5035/5802797131_a729dac808_s.jpg";	
		Bitmap preset = aq.getCachedImage(thumbnail);
		
		String imageUrl = "http://farm6.static.flickr.com/5035/5802797131_a729dac808_b.jpg";		
		aq.id(R.id.image).image(imageUrl, true, true, 0, 0, preset, AQuery.FADE_IN, AQuery.RATIO_PRESERVE);
		
	}
	
	
	protected int getContainer(){
		return R.layout.adhoc_activity;
	}
	
	@Override
	protected void runSource(){
		
	}
	

	
}
