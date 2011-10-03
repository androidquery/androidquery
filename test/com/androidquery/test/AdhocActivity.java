package com.androidquery.test;

import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.service.MarketService;
import com.androidquery.test.RunSourceActivity;
import com.androidquery.util.AQUtility;
import com.androidquery.util.Constants;
import com.androidquery.util.RatioDrawable;

public class AdhocActivity extends RunSourceActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		String imageUrl = "http://farm3.static.flickr.com/2199/2218403922_062bc3bcf2.jpg";	
		aq.cache(imageUrl, 0);
		
		try{
			work();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static int AQ_URL = 0x40FFFFFF;
	
	private void work(){
		
		runSource();
		
	}
	
	
	protected int getContainer(){
		return R.layout.adhoc_activity;
	}
	
	private void debug(String name, Bitmap bm){
		if(bm == null){
			AQUtility.debug(name, bm);
			return;
		}
		AQUtility.debug(name, bm.getWidth() + ":" + bm.getHeight());
	}
	
	
	@Override
	protected void runSource(){
		
		aq.id(R.id.image).visible();
		
		String imageUrl = "http://farm3.static.flickr.com/2199/2218403922_062bc3bcf2.jpg";	
		
		Bitmap bm = aq.getCachedImage(R.drawable.image_ph);
		
		Bitmap bm2 = aq.getCachedImage(imageUrl);
		
		ImageView iv = aq.id(R.id.image).width(200).getImageView();
		
		RatioDrawable rd = new RatioDrawable(getResources(), bm, iv, 1.0f);
		
		RatioDrawable rd2 = new RatioDrawable(getResources(), bm2, iv, 1.0f);
		
		//iv.setImageDrawable(rd);
		BitmapDrawable bd = new BitmapDrawable(bm);
		BitmapDrawable bd2 = new BitmapDrawable(aq.getCachedImage(R.drawable.image_missing));
		
		Drawable[] bds = new Drawable[]{rd, rd2};
		
		TransitionDrawable td = new TransitionDrawable(bds);
		td.setCrossFadeEnabled(true);
		iv.setImageDrawable(td);
		td.startTransition(250);
		
		//aq.id(R.id.image).clear().image(imageUrl, false, false, 0, 0, ph, AQuery.FADE_IN, 1.0f);
		
		
		
		/*
		ImageView iv = aq.id(R.id.image).getImageView();
		
		Bitmap bm = aq.getCachedImage(R.drawable.image_ph);
		Bitmap bm2 = aq.getCachedImage(R.drawable.image_missing);
		
		BitmapDrawable bd = new BitmapDrawable(bm);
		BitmapDrawable bd2 = new BitmapDrawable(bm2);
		
		Drawable[] bds = new Drawable[]{bd, bd2};
		
		TransitionDrawable td = new TransitionDrawable(bds);
		td.setCrossFadeEnabled(true);
		
		iv.setImageDrawable(td);
		
		td.startTransition(500);
		*/
	}
	
    private static Matrix makeMatrix(int dwidth, int dheight, int vwidth, int vheight){
    	
    	if(dwidth <= 0 || dheight <= 0 || vwidth <= 0 || vheight <= 0) return null;
    		
        float scale;
        float dx = 0, dy = 0;
        
        Matrix m = new Matrix();
        
        if (dwidth * vheight >= vwidth * dheight) {
        	//if image is super wider
			scale = (float) vheight / (float) dheight;
			dx = (vwidth - dwidth * scale) * 0.5f;
		} else {
			//if image is taller
			scale = (float) vwidth / (float) dwidth;	
			float sy = getYOffset(dwidth, dheight);
			
			dy = (vheight - dheight * scale) * sy;
		}
        
        m.setScale(scale, scale);
        m.postTranslate(dx, dy);
    	
    	return m;
    	
    }
    private static float getYOffset(int vwidth, int vheight){
    	
    	float ratio = (float) vheight / (float) vwidth;
    	
    	ratio = Math.min(1.5f, ratio);
    	ratio = Math.max(1, ratio);
    	
    	return  0.25f + ((1.5f - ratio) / 2.0f);
    	
    }
}
