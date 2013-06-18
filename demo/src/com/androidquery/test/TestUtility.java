package com.androidquery.test;

import java.util.UUID;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;


public class TestUtility {

	
	public static void showToast(Context context, String message) {
      	
    	Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
    	toast.setGravity(Gravity.CENTER, 0, 0);
    	toast.show();
    	
    }
	
	public static void flurryStart(Context context){
		
	    if(true) return;
	    
		if(!isTestDevice(context)){
			FlurryAgent.onStartSession(context, "D29A1QDKNZEIYFJBKXNR");
		}
	}
	
	public static void flurryEvent(Context context, String name){
		
	    if(true) return;
	    
		if(!isTestDevice(context)){
			try{
				FlurryAgent.onEvent(name, null);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public static void flurryStop(Context context){
	    
	    if(true) return;
	    
		if(!isTestDevice(context)){
			FlurryAgent.onEndSession(context);
		}
	}
	
	public static boolean isEmulator(){
		return "sdk".equals(Build.PRODUCT);
	}
	

	private static String[] deviceIds = {"ffffffff-96ef-7cae-47cc-b16b6d421d90", "ffffffff-b588-0cd1-ffff-ffffb12a7939", "00000000-582e-8c83-ffff-ffffb12a7939", "ffffffff-a7af-71df-0033-c5870033c587", "00000000-2e56-36d7-ffff-ffffb12a7939", "ffffffff-b588-0cd1-ae81-42290033c587"};
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
		    
		    System.err.println(deviceId);
		}
	    return deviceId;
	}
	
}
