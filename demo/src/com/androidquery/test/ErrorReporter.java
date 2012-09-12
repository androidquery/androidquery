package com.androidquery.test;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

public class ErrorReporter implements Thread.UncaughtExceptionHandler{

	private static Context context;
	
	public static void installReporter(Context appContext){
		
		try{
			Thread.setDefaultUncaughtExceptionHandler(new ErrorReporter());
			context = appContext;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {

		showToast("Sorry. Something went wrong and it's reported. We will fix it soon!");
        
		ex.printStackTrace();
        
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
 
		
	}
	
	
	private void showToast(final String message){
	
		
		new Thread() {
	
	        @Override
	        public void run() {
	            Looper.prepare();
	            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	            Looper.loop();
	        }
	
	    }.start();
	
	}
	
	
}