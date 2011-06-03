package com.androidquery.util;

import android.util.Log;

public class Utility {

	private static boolean debug = false;
	
	public static void setDebug(boolean debug){
		Utility.debug = debug;
	}
	
	public static void debug(Object msg){
		if(debug){
			Log.w("AQuery", msg + "");
		}
	}
	
	public static void debug(Object msg, Object msg2){
		if(debug){
			Log.w("AQuery", msg + ":" + msg2);
		}
	}
	
	public static void report(Throwable e){
		if(debug && e != null){
			String trace = Log.getStackTraceString(e);
			Log.w("AQuery", trace);
		}
	}
	
}
