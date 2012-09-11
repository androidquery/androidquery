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
	private float anchor;
	
	public RatioDrawable(Resources res, Bitmap bm, ImageView iv, float ratio, float anchor){
		
		super(res, bm);
		
		this.ref = new WeakReference<ImageView>(iv);
		this.ratio = ratio;
		this.anchor = anchor;
		
		iv.setScaleType(ScaleType.MATRIX);
		
		Matrix m = new Matrix();
		iv.setImageMatrix(m);
		
		adjust(iv, bm, false);
		
	}
	
	private int getWidth(ImageView iv){
		
		int width = 0;
		
		LayoutParams lp = iv.getLayoutParams();
		if(lp != null) width = lp.width;
		
		if(width <= 0){
			width = iv.getWidth();
		}
		
		if(width > 0){
			width = width - iv.getPaddingLeft() - iv.getPaddingRight();
		}
		
		return width;
	}
	
	public void draw(Canvas canvas){
		
		ImageView iv = null;
		
		if(ref != null){
			iv = ref.get();
		}
		
		if(ratio == 0 || iv == null){
			
			super.draw(canvas);
		
		}else{
			
			Bitmap bm = getBitmap();
			draw(canvas, iv, bm);
			
		}
		
		
	}
	
	
	private void draw(Canvas canvas, ImageView iv, Bitmap bm){
		
		
		Matrix m = getMatrix(iv, bm);	
		
		if(m != null){
			int vpad = iv.getPaddingTop() + iv.getPaddingBottom();
			int hpad = iv.getPaddingLeft() + iv.getPaddingRight();
			if(vpad > 0 || hpad > 0){
				canvas.clipRect(0, 0, iv.getWidth() - hpad, iv.getHeight() - vpad);
			}
			canvas.drawBitmap(bm, m, getPaint());
		}
		
		if(!adjusted){
			adjust(iv, bm, true);
		}
		
	}
	
	
	private void adjust(ImageView iv, Bitmap bm, boolean done){
		
		int vw = getWidth(iv);
		if(vw <= 0) return;
		
		int dw = bm.getWidth();
		int dh = bm.getHeight();
		
		
		int th = targetHeight(dw, dh, vw) + iv.getPaddingTop() + iv.getPaddingBottom();
			
		LayoutParams lp = iv.getLayoutParams();
		if(lp == null) return;
		
		int vh = lp.height;
		
		
		if(th != vh){
			
			lp.height = th;
			iv.setLayoutParams(lp);
					
		}
		
		if(done) adjusted = true;	
	}
	
	
	private int targetHeight(int dw, int dh, int vw){
		
		float r = ratio;
		
		if(ratio == AQuery.RATIO_PRESERVE){
			r = (float) dh / (float) dw;
		}
		
		return (int) (vw * r);
	}
	
    private Matrix getMatrix(ImageView iv, Bitmap bm){
    	
    	int dw = bm.getWidth();
    	
    	if(m != null && dw == w){
    		return m;
    	}
    	
    	int dh = bm.getHeight();
    	int vw = getWidth(iv);
    	int vh = targetHeight(dw, dh, vw);
    	
    	if(dw <= 0 || dh <= 0 || vw <= 0 || vh <= 0) return null;
    	
    	if(m == null || dw != w){
    	
	        float scale;
	        float dx = 0, dy = 0;
	        
	        m = new Matrix();
	        
	        if(dw * vh >= vw * dh){
	        	//if image is super wider
				scale = (float) vh / (float) dh;
				dx = (vw - dw * scale) * 0.5f;
			}else{
				//if image is taller
				scale = (float) vw / (float) dw;	
				float sy = getYOffset(dw, dh);
				
				dy = (vh - dh * scale) * sy;
			}
	        
	        m.setScale(scale, scale);
	        m.postTranslate(dx, dy);
	        
	        w = dw;
    	}
    	
    	return m;
    	
    }
	
    private float getYOffset(int vwidth, int vheight){
    	
    	if(anchor != AQuery.ANCHOR_DYNAMIC){
    		return (1 - anchor) / 2;
    	}
    	
    	float ratio = (float) vheight / (float) vwidth;
    	
    	ratio = Math.min(1.5f, ratio);
    	ratio = Math.max(1, ratio);
    	
    	return 0.25f + ((1.5f - ratio) / 2.0f);
    	
    }


	
	
}
