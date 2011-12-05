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

import com.androidquery.util.AQUtility;

import android.content.Context;
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
	private long delay = 3000;
	
	@Override
	public void async(Context context){
		lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		super.async(context);
	}
	

	protected Location datastoreGet(String url){
		
		
    	Location loc = getBestLocation(lm, delay);
		
    	AQUtility.debug("datastore", loc);
		
    	
    	if(loc == null){
    		
    		lm.requestLocationUpdates(getBestProvider(), 0, 0, this, Looper.getMainLooper());   		
    		delay(delay);
    		
    		loc = getBestLocation(lm, 0);
    		
    		lm.removeUpdates(this);
    		
    	}
		
    	AQUtility.debug("result", loc);
		
		return loc;
		
	}
	
	private void delay(long delay){
		
		synchronized(this){
			try {
				AQUtility.debug("waiting", delay);
				this.wait(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
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
	
	public static Location getLastLocation(Context context){
		
		LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return getBestLocation(lm, 0);
	}
	
	private static Location getBestLocation(LocationManager lm, long delay){
		
		Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(!withIn(loc, delay)) loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if(!withIn(loc, delay)) loc = null;
		return loc;
		
	}

	private static boolean withIn(Location loc, long delay){
		
		AQUtility.debug("within", loc);
		
		if(loc == null) return false;
		if(delay == 0) return true;
		
		long now = System.currentTimeMillis();
		long diff = now - loc.getTime();
		return diff < delay;
	}
	

    public void onLocationChanged(Location location) {
        // Called when a new location is found by the network location provider.
        //makeUseOfNewLocation(location);
      
      	AQUtility.debug("changed", location);
      	
      	lm.removeUpdates(this);
      	
      	this.notifyAll();
	}
	
	public void onStatusChanged(String provider, int status, Bundle extras) {
	  	AQUtility.debug("onStatusChanged");
	  	lm.removeUpdates(this);
	}
	
	public void onProviderEnabled(String provider) {
	  	AQUtility.debug("onProviderEnabled");
	  	lm.removeUpdates(this);
	}
	
	public void onProviderDisabled(String provider) {
	  	AQUtility.debug("onProviderDisabled");
	  	lm.removeUpdates(this);
	  	
	}
	
}
