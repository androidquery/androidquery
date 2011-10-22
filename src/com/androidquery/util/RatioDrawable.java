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

package com.androidquery.util;

import java.lang.ref.WeakReference;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.androidquery.AQuery;

/**
 * AQuery internal use only.
 * 
 */

public class RatioDrawable extends BitmapDrawable{

	private float ratio;
	private WeakReference<ImageView> ref;
	private boolean adjusted;
	private Matrix m;
	private int w;
	
	public RatioDrawable(Resources res, Bitmap bm, ImageView iv, float ratio){
		super(res, bm);
		this.ref = new WeakReference<ImageView>(iv);
		this.ratio = ratio;
		iv.setScaleType(ScaleType.MATRIX);
	}
	
	public void draw(Canvas canvas){
		
		ImageView iv = ref.get();
		
		if(ratio == 0 || iv == null){
			super.draw(canvas);
			return;
		}
		
		Bitmap bm = getBitmap();
		int dw = bm.getWidth();
		int dh = bm.getHeight();
		
		int vw = iv.getWidth();
		int vh = iv.getHeight();
		
		int th = targetHeight(dw, dh, vw);
		
		Matrix m = getMatrix(dw, dh, vw, th);	
		
		if(m != null){
			canvas.drawBitmap(getBitmap(), m, getPaint());
		}
		
		if(th != vh && !adjusted){
			
			AQUtility.debug("adj", vh + "->" + th);
			
			adjusted = true;			
			LayoutParams lp = iv.getLayoutParams();
			lp.height = th;
			iv.setLayoutParams(lp);
		}
		
	}
	
	private int targetHeight(int dw, int dh, int vw){
		
		float r = ratio;
		
		if(ratio == AQuery.RATIO_PRESERVE){
			r = (float) dh / (float) dw;
		}
		
		return (int) (vw * r);
	}
	
    private Matrix getMatrix(int dwidth, int dheight, int vwidth, int vheight){
    	
    	if(dwidth <= 0 || dheight <= 0 || vwidth <= 0 || vheight <= 0) return null;
    		
    	if(m == null || dwidth != w){
    	
	        float scale;
	        float dx = 0, dy = 0;
	        
	        m = new Matrix();
	        
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
	        
	        w = dwidth;
    	}
    	
    	return m;
    	
    }
	
    private static float getYOffset(int vwidth, int vheight){
    	
    	float ratio = (float) vheight / (float) vwidth;
    	
    	ratio = Math.min(1.5f, ratio);
    	ratio = Math.max(1, ratio);
    	
    	return  0.25f + ((1.5f - ratio) / 2.0f);
    	
    }
	
	
}
