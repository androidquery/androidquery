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

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

import com.androidquery.util.AQUtility;

/**
 * The callback handler for handling Aquery.location() methods.
 */
public class LocationAjaxCallback extends AbstractAjaxCallback<Location, LocationAjaxCallback>{

	private LocationManager lm;
	private long timeout = 30000;
	private long interval = 1000;
	private float tolerance = 10;
	private float accuracy = 1000;
	private int iteration = 3;
	private int n = 0;
	private boolean networkEnabled = false;
	private boolean gpsEnabled = false;
	
	//private long expire = 0;
	private Listener networkListener;
	private Listener gpsListener;
	private long initTime;
	
	public LocationAjaxCallback(){
		type(Location.class).url("device");
	}
	
	
	@Override
	public void async(Context context){
		lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);		
		gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		work();
	}
	
	public LocationAjaxCallback timeout(long timeout){
		this.timeout = timeout;
		return this;
	}
	
	public LocationAjaxCallback accuracy(float accuracy){
		this.accuracy = accuracy;
		return this;
	}
	
	public LocationAjaxCallback tolerance(float tolerance){
		this.tolerance = tolerance;
		return this;
	}
	
	public LocationAjaxCallback iteration(int iteration){
		this.iteration = iteration;
		return this;
	}
	
	private void check(Location loc){
	
		if(loc != null){	
			
			if(isBetter(loc)){

				n++;				
				boolean last = n >= iteration;
				
				boolean accurate = isAccurate(loc);
				boolean diff = isDiff(loc);
				
				boolean best = !gpsEnabled || LocationManager.GPS_PROVIDER.equals(loc.getProvider());
				
				AQUtility.debug(n, iteration);
				AQUtility.debug("acc", accurate);
				AQUtility.debug("best", best);
				
				
				if(diff){
					if(last){
						if(accurate && best){
							stop();
							callback(loc);
						}
					}else{
						if(accurate && best){
							stop();
						}
						callback(loc);
					}
					
				}
				
			}
		}
		
	}
	
	private void callback(Location loc){
		result = loc;					
		status(loc, 200);
		callback();
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
	
	private boolean isAccurate(Location loc){
		
		return loc.getAccuracy() < accuracy;
		
	}
	
	
	private boolean isDiff(Location loc){

		if(result == null) return true;
		
		float diff = distFrom(result.getLatitude(), result.getLongitude(), loc.getLatitude(), loc.getLongitude());
		
		if(diff < tolerance){
			AQUtility.debug("duplicate location");
			return false;
		}else{
			return true;
		}
	}
	
	
	private boolean isBetter(Location loc){
		
		if(result == null) return true;
		
		// if this loc is network and there's already an recent async gps update
		if(result.getTime() > initTime && result.getProvider().equals(LocationManager.GPS_PROVIDER) && loc.getProvider().equals(LocationManager.NETWORK_PROVIDER)){
			AQUtility.debug("inferior location");
			return false;
		}
		
		return true;
		
		
	}
	
	private void failure(){
		
		if(gpsListener == null && networkListener == null) return;
		
		AQUtility.debug("fail");
		
		result = null;
		status(null, AjaxStatus.TRANSFORM_ERROR);
		stop();
		callback();
	}
	
	public void stop(){
		
		AQUtility.debug("stop");
		
		Listener gListener = gpsListener;
		
		if(gListener != null){
			lm.removeUpdates(gListener);
			gListener.cancel();
		}
		
		Listener nListener = networkListener;
		
		if(nListener != null){
			lm.removeUpdates(nListener);
			nListener.cancel();
		}
		
		gpsListener = null;
		networkListener = null;
	}
	
	private void work(){
		
		Location loc = getBestLocation();		
		
		Timer timer = new Timer(false);
		
		if(networkEnabled){
			AQUtility.debug("register net");
			networkListener = new Listener();
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, interval, 0, networkListener, Looper.getMainLooper()); 
			timer.schedule(networkListener, timeout);
		}
		
		
		if(gpsEnabled){
			AQUtility.debug("register gps");
			gpsListener = new Listener();
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, interval, 0, gpsListener, Looper.getMainLooper());  
			timer.schedule(gpsListener, timeout);
		}
		
		if(iteration > 1 && loc != null){
			n++;
			callback(loc);
		}
		
		initTime = System.currentTimeMillis();
		
	}
	
	
	private Location getBestLocation(){
		
		Location loc1 = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		Location loc2 = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);	
		
		if(loc2 == null) return loc1;
		if(loc1 == null) return loc2;
		
		if(loc1.getTime() > loc2.getTime()){
			return loc1;
		}else{
			return loc2;
		}
		
		
	}
	
	private static float distFrom(double lat1, double lng1, double lat2, double lng2) {
	    
		double earthRadius = 3958.75;
		double dLat = Math.toRadians(lat2-lat1);
		double dLng = Math.toRadians(lng2-lng1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
           Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
           Math.sin(dLng/2) * Math.sin(dLng/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double dist = earthRadius * c;

		int meterConversion = 1609;
		return (float) dist * meterConversion;
		 
		 
	}
	
	private class Listener extends TimerTask implements LocationListener {
		
	    public void onLocationChanged(Location location) {
	        
	      	AQUtility.debug("changed", location);
	      	check(location);
	      	
	      	
		}
		
		public void onStatusChanged(String provider, int status, Bundle extras) {
		  	AQUtility.debug("onStatusChanged");
		}
		
		public void onProviderEnabled(String provider) {
		  	AQUtility.debug("onProviderEnabled");
		  	check(getBestLocation());
		  	lm.removeUpdates(this);
		}
		
		public void onProviderDisabled(String provider) {
		  	AQUtility.debug("onProviderDisabled");
		}

		@Override
		public void run() {			
			failure();
		}
		
	}
	
	
}
