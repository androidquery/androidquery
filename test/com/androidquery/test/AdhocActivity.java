package com.androidquery.test;

import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.service.MarketService;
import com.androidquery.test.RunSourceActivity;
import com.androidquery.util.AQUtility;
import com.androidquery.util.Constants;

public class AdhocActivity extends RunSourceActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		try{
			work();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static int AQ_URL = 0x40FFFFFF;
	
	private void work(){
		
		AQUtility.debug("adhoc");
		
		String url = "http://farm6.static.flickr.com/5035/5802797131_a729dac808_b.jpg";
		//aq.id(R.id.image).width(200).image(url);
		
		Bitmap bm = aq.getCachedImage(url);
		debug("ori", bm);
		
		ImageView iv = aq.id(R.id.image).width(200).getImageView();
		
		iv.setTag(AQ_URL, "hello");
		
		AQUtility.debug(iv.getTag(AQ_URL));
		
	}
	
	/*
	protected int getContainer(){
		return R.layout.adhoc_activity;
	}
	*/
	private void debug(String name, Bitmap bm){
		if(bm == null){
			AQUtility.debug(name, bm);
			return;
		}
		AQUtility.debug(name, bm.getWidth() + ":" + bm.getHeight());
	}
	
	
	@Override
	protected void runSource(){
		
		
		ImageView iv = aq.id(R.id.image).getImageView();
		
		Bitmap bm = iv.getDrawingCache();
		
		debug("dc2", bm);
		
		
		iv.setImageBitmap(bm);
		
	}
	

	
}
