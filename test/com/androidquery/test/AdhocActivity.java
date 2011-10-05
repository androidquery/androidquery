package com.androidquery.test;

import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.service.MarketService;
import com.androidquery.test.RunSourceActivity;
import com.androidquery.util.AQUtility;
import com.androidquery.util.Constants;
import com.androidquery.util.RatioDrawable;

public class AdhocActivity extends RunSourceActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		String imageUrl = "http://farm3.static.flickr.com/2199/2218403922_062bc3bcf2.jpg";	
		aq.cache(imageUrl, 0);
		
		try{
			work();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static int AQ_URL = 0x40FFFFFF;
	
	private void work(){
		
		runSource();
		
	}
	
	
	protected int getContainer(){
		return R.layout.adhoc_activity;
	}
	
	private void debug(String name, Bitmap bm){
		if(bm == null){
			AQUtility.debug(name, bm);
			return;
		}
		AQUtility.debug(name, bm.getWidth() + ":" + bm.getHeight());
	}
	
	
	@Override
	protected void runSource(){
		
		aq.id(R.id.image).visible();
		
		String imageUrl = "http://farm3.static.flickr.com/2199/2218403922_062bc3bcf2.jpg";	
		
		Bitmap bm = aq.getCachedImage(R.drawable.image_ph);
		
		BitmapDrawable bd = new BitmapDrawable(bm);
	
		aq.getImageView().setImageDrawable(bd);
	}

}
