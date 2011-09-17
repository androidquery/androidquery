package com.androidquery.test;

import java.util.UUID;

import com.flurry.android.FlurryAgent;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.widget.Toast;


public class TestUtility {

	
	public static void showToast(Context context, String message) {
      	
    	Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
    	toast.setGravity(Gravity.CENTER, 0, 0);
    	toast.show();
    	
    }
	
	public static void flurryStart(Context context){
		
		if(!isTestDevice(context)){
			FlurryAgent.onStartSession(context, "D29A1QDKNZEIYFJBKXNR");
		}
	}
	
	public static void flurryEvent(Context context, String name){
		
		if(!isTestDevice(context)){
			try{
				FlurryAgent.onEvent(name, null);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public static void flurryStop(Context context){
		if(!isTestDevice(context)){
			FlurryAgent.onEndSession(context);
		}
	}
	
	private static String[] deviceIds = {"00000000-6b8b-aae1-0356-4b8d0033c587", "00000000-582e-8c83-ffff-ffffb12a7939", "ffffffff-a7af-71df-0033-c5870033c587"};
	private static Boolean testDevice;
	
	public static boolean isTestDevice(Context context){
		
		if(testDevice == null){
			testDevice = isTestDevice(getDeviceId(context));
		}
		
		return testDevice;
	}
	
	private static boolean isTestDevice(String deviceId){
		
		for(int i = 0; i < deviceIds.length; i++){
			if(deviceIds[i].equals(deviceId)){
				return true;
			}
		}
		return false;
	}
	
	private static String deviceId;
	public static String getDeviceId(Context context){
		
		if(deviceId == null){
		
			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	
		    String tmDevice, tmSerial, tmPhone, androidId;
		    tmDevice = "" + tm.getDeviceId();
		    tmSerial = "" + tm.getSimSerialNumber();
		    androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
	
		    UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
		    deviceId = deviceUuid.toString();
		    
		}
	    return deviceId;
	}
	
}
