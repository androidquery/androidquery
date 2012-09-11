package com.androidquery.test.async;

import android.location.Location;
import android.os.Bundle;

import com.androidquery.R;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.LocationAjaxCallback;
import com.androidquery.test.RunSourceActivity;
import com.androidquery.util.AQUtility;

public class LocationActivity extends RunSourceActivity {

	private String type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		type = getIntent().getStringExtra("type");
			
	}
	
	@Override
	protected void runSource(){
		
		AQUtility.debug("run", type);
		
		AQUtility.invokeHandler(this, type, false, false, null);
	}
	
	private LocationAjaxCallback cb;
	public void location_ajax(){
		
		aq.id(R.id.result).text("");
    	
		LocationAjaxCallback cb = new LocationAjaxCallback();
    	cb.weakHandler(this, "locationCb").timeout(30 * 1000).accuracy(1000).iteration(3);   	
    	cb.async(this);
		
    	this.cb = cb;
	}
	
	public void locationCb(String url, Location loc, AjaxStatus status){
		
		if(loc != null){
			appendResult(loc);
		}
		
	}
	
	@Override
	public void onStop(){
		
		super.onStop();
		
		if(cb != null) cb.stop();
		
	}
	
}
