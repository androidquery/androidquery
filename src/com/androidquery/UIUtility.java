package com.androidquery;

import android.view.View;
import android.view.animation.AlphaAnimation;

public class UIUtility {

	protected static void transparent(View view, boolean transparent){
		
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
	
}
