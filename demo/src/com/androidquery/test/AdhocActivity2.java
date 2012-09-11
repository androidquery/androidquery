package com.androidquery.test;

import android.os.Bundle;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;

public class AdhocActivity2 extends RunSourceActivity{

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		work();
	}
	
	
	private void work(){
		
		//11-16 22:38:26.449: W/AQuery(18289): preset:http://graph.facebook.com/1281625122/picture
		AQUtility.cleanCache(AQUtility.getCacheDir(this, AQuery.CACHE_DEFAULT), 0, 0);
		BitmapAjaxCallback.clearCache();

		String pic = "http://graph.facebook.com/1281625122/picture";
		
		aq.id(R.id.image1).image(pic);
		aq.id(R.id.image2).image(pic);
		

	}
	
	
	protected int getContainer(){
		return R.layout.adhoc_activity2;
	}
	
	@Override
	protected void runSource(){
		
		
		
		
	}
	
	
	
}
