package com.androidquery.test.async;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xml.sax.SAXException;

import android.location.Location;
import android.os.Bundle;

import com.androidquery.R;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.LocationAjaxCallback;
import com.androidquery.test.RunSourceActivity;
import com.androidquery.util.AQUtility;
import com.androidquery.util.XmlDom;

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
	
	public void location_ajax(){
		
		aq.id(R.id.result).text("");
    	
		LocationAjaxCallback cb = new LocationAjaxCallback();
    	cb.weakHandler(this, "locationCb").timeout(40 * 1000);   	
    	cb.async(this);
		
	}
	
	public void locationCb(String url, Location loc, AjaxStatus status){
		
		if(loc != null){
			appendResult(loc);
		}
		
	}
	
}
