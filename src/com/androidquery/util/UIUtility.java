/*
 * Copyright 2011 - Peter Liu (tinyeeliu@gmail.com)
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

package com.androidquery.util;


import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AlphaAnimation;

public class UIUtility {

	public static void transparent(View view, boolean transparent){
		
		float alpha = 1;
		if(transparent) alpha = 0.5f;
		
		setAlpha(view, alpha);
		
	}
	
	
	private static void setAlpha(View view, float alphaValue){
		
    	if(alphaValue == 1){
    		view.clearAnimation();
    	}else{
    		AlphaAnimation alpha = new AlphaAnimation(alphaValue, alphaValue);
        	alpha.setDuration(0); // Make animation instant
        	alpha.setFillAfter(true); // Tell it to persist after the animation ends    	
        	view.startAnimation(alpha);
    	}
		
	}
	
	public static void ensureUIThread(){
    	
    	
    	long uiId = Looper.getMainLooper().getThread().getId();
    	long cId = Thread.currentThread().getId();
    	
    	if(uiId != cId){
    		Utility.report(new NullPointerException());
    	}else{
    		Utility.debug("ui ok!");
    	}
    	
    }
	
	
	private static Handler handler;
	public static Handler getHandler(){
		if(handler == null){
			UIUtility.ensureUIThread();
			handler = new Handler();
		}
		return handler;
	}
	
	public static void post(Runnable run){
		handler.post(run);
	}
	
	public static void postDelayed(Runnable run, long delay){
		handler.postDelayed(run, delay);
	}
}
