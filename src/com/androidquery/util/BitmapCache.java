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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import android.graphics.Bitmap;

/**
 * AQuery internal use only. 
 * 
 */

public class BitmapCache extends LinkedHashMap<String, Bitmap>{

	private static final long serialVersionUID = 1L;
	
	private int maxCount;
	private int maxPixels;
	private int maxTotalPixels;
	private int pixels;
	
	public BitmapCache(int mc, int mp, int mtp){
		
		super(8, 0.75F, true);
		
		this.maxCount = mc;
		this.maxPixels = mp;
		this.maxTotalPixels = mtp;
		
	}
	
	@Override
	public Bitmap put(String key, Bitmap bm){
		
		Bitmap old = null;
		
		int px = pixels(bm);
		if(px <= maxPixels){
			pixels += px;
			old = super.put(key, bm);
			if(old != null){
				pixels -= pixels(old);
			}			
			
			//AQUtility.debug("put", key);
		}else{
			//AQUtility.debug("reject", px + ":" + bm.getWidth() + ":" + bm.getHeight() + ":" + key);
		}
		
		
		
		return old;
	}
	
	
	@Override
	public Bitmap remove(Object key){
		
		Bitmap old = super.remove(key);
		if(old != null){
			pixels -= pixels(old);
		}
		
		//AQUtility.debug("remove pixels", key + ":" + size() + ":" + pixels);
		
		return old;
	}
	
	@Override
	public void clear(){
		super.clear();
		pixels = 0;
	}
	
	private int pixels(Bitmap bm){
		if(bm == null) return 0;
		return bm.getWidth() * bm.getHeight();
	}
	
	private void shrink(){
		
		if(pixels > maxTotalPixels){
			
			Iterator<String> keys = keySet().iterator();
			
			while(keys.hasNext()){
				
				keys.next();
				keys.remove();
				
				if(pixels <= maxTotalPixels){
					break;
				}
			}
			
			
		}
		
	}
	
	
	@Override
	public boolean removeEldestEntry(Map.Entry<String, Bitmap> eldest) {
        
		if(pixels > maxTotalPixels || size() > maxCount){
		
			/*
			if(pixels > maxTotalPixels){
				AQUtility.debug("evict by max size");
			}else{
				AQUtility.debug("evict by count", maxCount);
			}
			*/
			remove(eldest.getKey());
		
			
		}
		
		shrink();
		
		return false;
    }
	
}
