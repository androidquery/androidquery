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
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

/**
 * The callback handler for handling Aquery.location() methods.
 */
public class LocationAjaxCallback extends AbstractAjaxCallback<Location, LocationAjaxCallback>{

	private LocationManager lm;
	private long timeout = 30000;
	private long interval = 1000;
	private float tolerance = 10;
	private long expire = 0;
	private Listener networkListener;
	private Listener gpsListener;
	private long initTime;
	
	public LocationAjaxCallback(){
		type(Location.class).url("device");
	}
	
	
	@Override
	public void async(Context context){
		lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		//super.async(context);
		
		work();
	}
	
	public LocationAjaxCallback timeout(long timeout){
		this.timeout = timeout;
		return this;
	}
	
	private void callback(Location loc, boolean init){
	
		if(loc != null){	
			/*
			if(!init){
				stop();
			}
			*/
			if(isBetter(loc)){
				
				if(isDiff(loc)){
					result = loc;
					status(loc, 200);
					callback();
				}else{
					result = loc;
				}
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
		AQUtility.debug("time", new Date(loc.getTime()));
		
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
		result = null;
		status(null, AjaxStatus.TRANSFORM_ERROR);
		callback();
	}
	
	public void stop(){
		
		AQUtility.debug("stop");
		
		if(gpsListener != null){
			lm.removeUpdates(gpsListener);
			gpsListener.cancel();
		}
		
		if(networkListener != null){
			lm.removeUpdates(networkListener);
			networkListener.cancel();
		}
		
		gpsListener = null;
		networkListener = null;
	}
	
	private void work(){
		
		Location loc = getBestLocation();		
		
		String provider = getBestProvider();
		
		AQUtility.debug("registered", provider);
		
		GpsStatus status = lm.getGpsStatus(null);
		AQUtility.debug("gps", status.getTimeToFirstFix() + ":" + status.getSatellites());
		
		
		//lm.requestLocationUpdates(provider, interval, 0, this, Looper.getMainLooper());   
		
		Timer timer = new Timer(false);
		
		if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			AQUtility.debug("register net");
			networkListener = new Listener();
			lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, interval, 0, networkListener, Looper.getMainLooper()); 
			timer.schedule(networkListener, timeout);
		}
		
		if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			AQUtility.debug("register gps");
			gpsListener = new Listener();
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, interval, 0, gpsListener, Looper.getMainLooper());  
			timer.schedule(gpsListener, timeout);
		}
		
		callback(loc, true);
		
		initTime = System.currentTimeMillis();
		
	}
	
	//12-06 22:39:42.652: W/AQuery(14047): cb:Location[mProvider=network,mTime=1323180586944,mLatitude=22.3804801,mLongitude=114.1766253,mHasAltitude=false,mAltitude=0.0,mHasSpeed=false,mSpeed=0.0,mHasBearing=false,mBearing=0.0,mHasAccuracy=true,mAccuracy=740.0,mExtras=Bundle[mParcelledData.dataSize=148]]

	//12-06 22:39:47.699: W/AQuery(14047): cb:Location[mProvider=network,mTime=1323182387660,mLatitude=22.3823468,mLongitude=114.1783388,mHasAltitude=false,mAltitude=0.0,mHasSpeed=false,mSpeed=0.0,mHasBearing=false,mBearing=0.0,mHasAccuracy=true,mAccuracy=719.0,mExtras=Bundle[mParcelledData.dataSize=148]]

	//12-06 22:47:09.101: W/AQuery(14047): cb:Location[mProvider=gps,mTime=1323176878428,mLatitude=22.381488255763212,mLongitude=114.17933830037093,mHasAltitude=true,mAltitude=22.0,mHasSpeed=false,mSpeed=0.0,mHasBearing=false,mBearing=0.0,mHasAccuracy=true,mAccuracy=200.0,mExtras=Bundle[mParcelledData.dataSize=4]]

	
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
	
	private class Listener extends TimerTask implements LocationListener {
		
	    public void onLocationChanged(Location location) {
	        // Called when a new location is found by the network location provider.
	        //makeUseOfNewLocation(location);
	      
	      	AQUtility.debug("changed", location);
	      	
	      	
	      	
	      	callback(location, false);
	      	
	      	lm.removeUpdates(this);
	      	
		}
		
		public void onStatusChanged(String provider, int status, Bundle extras) {
		  	AQUtility.debug("onStatusChanged");
		  	//lm.removeUpdates(this);
		  	//callback(getBestLocation(), false);
		}
		
		public void onProviderEnabled(String provider) {
		  	AQUtility.debug("onProviderEnabled");
		  	callback(getBestLocation(), false);
		  	lm.removeUpdates(this);
		}
		
		public void onProviderDisabled(String provider) {
		  	AQUtility.debug("onProviderDisabled");
		  	//callback(getBestLocation(), false);
		  	
		}

		@Override
		public void run() {

			/*
			if(result == null){
				failure();
			}
			*/
			AQUtility.debug("unreg");
			lm.removeUpdates(this);
			
		}
		
	}
	
	
}
