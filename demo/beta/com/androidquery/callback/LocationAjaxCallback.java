/*
 * Copyright 2011 - AndroidQuery.com (tinyeeliu@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.androidquery.callback;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.androidquery.util.AQUtility;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

/**
 * The callback handler for handling Aquery.location() methods.
 */
public class LocationAjaxCallback extends AbstractAjaxCallback<Location, LocationAjaxCallback> implements LocationListener{

	private LocationManager lm;
	private long timeout = 30000;
	private long interval = 1000;
	private float tolerance = 10;
	private long expire = 0;
	private TimerTask task;
	
	public LocationAjaxCallback(){
		type(Location.class).url("device");
	}
	
	
	@Override
	public void async(Context context){
		lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		//super.async(context);
		
		work();
	}
	
	private void callback(Location loc, boolean init){
	
		if(loc != null){		
			if(!init){
				clear();
			}
			if(isDiff(loc)){
				result = loc;
				status(loc, 200);
				callback();
			}
		}
		
	}
	
	private void status(Location loc, int code){
		
		if(status == null){
			status = new AjaxStatus();
		}
		
		if(loc != null){
			status.time(new Date(loc.getTime()));
		}
		
		status.code(code).done().source(AjaxStatus.DEVICE);
		
	}
	
	private boolean isDiff(Location loc){
		
		if(result == null) return true;
		
		float diff = distFrom(result.getLatitude(), result.getLongitude(), loc.getLatitude(), loc.getLongitude());
		
		AQUtility.debug("diff", diff);
		
		if(diff > tolerance){
			return true;
		}else{
			AQUtility.debug("duplicate location");
			return false;
		}
		
	}
	
	private void failure(){
		result = null;
		status(null, AjaxStatus.TRANSFORM_ERROR);
		callback();
	}
	
	private void clear(){
		
		AQUtility.debug("unreg");
		
		lm.removeUpdates(this);
		if(task != null){
			task.cancel();
			task = null;
		}
	}
	
	private void work(){
		
		Location loc = getBestLocation();		
		lm.requestLocationUpdates(getBestProvider(), interval, 0, this, Looper.getMainLooper());   
		
		callback(loc, true);
		
		task = new TimerTask() {
			
			@Override
			public void run() {
				
				if(result == null){
					failure();
				}
				
				clear();
			}
		};

		Timer timer = new Timer(false);
		timer.schedule(task, timeout);
	}
	
	
	private String getBestProvider(){
		
		Criteria c = makeCriteria();
		return lm.getBestProvider(c, true);
		
	}
	
	private Criteria makeCriteria(){
		
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		
		return criteria;
	}
	
	
	private Location getBestLocation(){
		
		Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(loc == null) loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);		
		return loc;
		
	}
	
	private Location getBestLocation(long expire){
		Location loc = getBestLocation();
		if(withIn(loc, expire)) return loc;
		return null;
	}

	private static boolean withIn(Location loc, long expire){
		
		if(loc == null) return false;
		if(expire == 0) return true;
		
		long now = System.currentTimeMillis();
		long diff = now - loc.getTime();
		return diff < expire;
	}
	

    public void onLocationChanged(Location location) {
        // Called when a new location is found by the network location provider.
        //makeUseOfNewLocation(location);
      
      	AQUtility.debug("changed", location);
      	
      	
      	
      	callback(location, false);
      	
      	
	}
	
	public void onStatusChanged(String provider, int status, Bundle extras) {
	  	AQUtility.debug("onStatusChanged");
	  	//lm.removeUpdates(this);
	  	//callback(getBestLocation(), false);
	}
	
	public void onProviderEnabled(String provider) {
	  	AQUtility.debug("onProviderEnabled");
	  	callback(getBestLocation(), false);
	}
	
	public void onProviderDisabled(String provider) {
	  	AQUtility.debug("onProviderDisabled");
	  	//callback(getBestLocation(), false);
	  	
	}
	
	public static float distFrom(double lat1, double lng1, double lat2, double lng2) {
	    
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2-lat1);
		double dLng = Math.toRadians(lng2-lng1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
           Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
           Math.sin(dLng/2) * Math.sin(dLng/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double dist = earthRadius * c;

		int meterConversion = 1609;
		return new Float(dist * meterConversion);
		 
		 
	}
	
}
