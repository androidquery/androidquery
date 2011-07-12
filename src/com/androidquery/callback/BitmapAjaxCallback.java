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

package com.androidquery.callback;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.androidquery.util.Cache;

public class BitmapAjaxCallback extends AjaxCallback<Bitmap>{

	private static int SMALL_MAX = 20;
	private static int BIG_MAX = 20;
	private static int MAX_WIDTH = 400;
	private static int MAX_HEIGHT = 400;
	
	private static Map<String, Bitmap> smallCache;
	private static Map<String, Bitmap> bigCache;
	
	private static HashMap<String, WeakHashMap<ImageView, Void>> queueMap = new HashMap<String, WeakHashMap<ImageView, Void>>();	
	//private WeakHashMap<ImageView, Void> ivs;
	private WeakReference<ImageView> iv;
	private int targetWidth = 0;
	
	public BitmapAjaxCallback(){
		//ivs = new WeakHashMap<ImageView, Void>();			
		
	}
	
	public void setImageView(String url, ImageView view){
		
				
		//ivs.put(view, null);
		presetBitmap(view, url);
		
		iv = new WeakReference<ImageView>(view);
	}
	
	public void setTargetWidth(int targetWidth){
		this.targetWidth = targetWidth;
	}
	
	
	
    private static Bitmap getResizedImage(String path, int targetWidth){
    	
    	BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        
        BitmapFactory.decodeFile(path, options);
    	
        int width = options.outWidth;
        int height = options.outHeight;
        
        AQUtility.debug("width:" + width + " height:" + height);
        
        int ssize = sampleSize(width, targetWidth);
       
        AQUtility.debug("sample:" + ssize + "->" + (width / ssize));
       
        options = new BitmapFactory.Options();
        options.inSampleSize = ssize;
        
        Bitmap bm = BitmapFactory.decodeFile(path, options);
        
        AQUtility.debug("resampled width:" + bm.getWidth());
        
        
        return bm;
    	
    }
    
    private static Bitmap getResizedImage(byte[] data, int targetWidth){
    	
    	BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        
        //BitmapFactory.decodeFile(path, options);
    	BitmapFactory.decodeByteArray(data, 0, data.length, options);
        
        
        int width = options.outWidth;
        int height = options.outHeight;
        
        AQUtility.debug("width:" + width + " height:" + height);
        
        int ssize = sampleSize(width, targetWidth);
       
        AQUtility.debug("sample:" + ssize + "->" + (width / ssize));
       
        options = new BitmapFactory.Options();
        options.inSampleSize = ssize;
        
        //Bitmap bm = BitmapFactory.decodeFile(path, options);
        Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        
        AQUtility.debug("resampled width:" + bm.getWidth());
        
        
        return bm;
    	
    }
    
    private static int sampleSize(int width, int target){
    	
    	int result = 1;
    	
    	for(int i = 0; i < 10; i++){
    		
    		if(width < target * 2){
    			break;
    		}
    		
    		width = width / 2;
    		result = result * 2;
    		
    	}
    	
    	return result;
    }
	
	
	@Override
	public Bitmap fileGet(String url, File file, AjaxStatus status) {
		
		
		Bitmap bm = null;
		
		try{
		
			if(targetWidth > 0){
				bm = getResizedImage(file.getAbsolutePath(), targetWidth);
			}else{
				bm = BitmapFactory.decodeFile(file.getAbsolutePath());
			}
		
		}catch(OutOfMemoryError e){
			AQUtility.report(e);
		}
		
		return bm;
		
	}
	
	@Override
	public Bitmap transform(String url, byte[] data, AjaxStatus status) {
		
		Bitmap bm = null;
		
		try{
		
			if(targetWidth > 0){
				bm = getResizedImage(data, targetWidth);
			}else{
				bm = BitmapFactory.decodeByteArray(data, 0, data.length);
			}
		
		}catch(OutOfMemoryError e){
			AQUtility.report(e);
		}
		
		return bm;
	}
	
	@Override
	public final void callback(String url, Bitmap bm, AjaxStatus status) {
		
		ImageView firstView = iv.get();
		
		checkCb(url, firstView, bm, status);
		
		WeakHashMap<ImageView, Void> ivs = queueMap.remove(url);
		
		if(ivs != null){
		
			Set<ImageView> set = ivs.keySet();
			
			for(ImageView view: set){
				checkCb(url, view, bm, status);
			}
		
		}
		
		AQUtility.debug("concurrent", queueMap.size());
		
	}
	
	private void checkCb(String url, ImageView iv, Bitmap bm, AjaxStatus status){
		
		if(iv == null) return;
		
		if(url.equals(iv.getTag())){
			callback(url, iv, bm, status);
			AQUtility.debug("set img", url + ":" + bm.getWidth());
		}else{
			AQUtility.debug("mismatch", iv.getTag() + ":" + url);
		}
	}
	
	protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status){

		showBitmap(iv, bm);
	}

	public static void setIconCacheLimit(int limit){
		SMALL_MAX = limit;
		clearCache();
	}
	
	public static void setCacheLimit(int limit){
		BIG_MAX = limit;
		clearCache();
	}
	
	public static void clearCache(){
		bigCache = null;
		smallCache = null;
	}
	
	protected static void clearTasks(){
		queueMap.clear();
	}
	
	private static Map<String, Bitmap> getBImgCache(){
		if(bigCache == null){
			bigCache = new Cache<String, Bitmap>(BIG_MAX);
		}
		return bigCache;
	}
	
	
	private static Map<String, Bitmap> getSImgCache(){
		if(smallCache == null){
			smallCache = new Cache<String, Bitmap>(SMALL_MAX);
		}
		return smallCache;
	}
	
	@Override
	public Bitmap memGet(String url){		
		return memGet2(url, targetWidth);
	}
	
	private static Bitmap memGet2(String url, int targetWidth){
		
		url = getKey(url, targetWidth);
		
		Map<String, Bitmap> cache = getBImgCache();
		Bitmap result = cache.get(url);
		
		if(result == null){
			cache = getSImgCache();
			result = cache.get(url);
		}

		return result;
	}
	
	private static String getKey(String url, int targetWidth){
		if(targetWidth <= 0){
			return url;
		}
		return url + "#" + targetWidth;
	}
	
	@Override
	public void memPut(String url, Bitmap bm){
		
		if(bm == null) return;
		
		int width = bm.getWidth();
		int height = bm.getHeight();		
		
		if(width > MAX_WIDTH || height > MAX_HEIGHT){
			AQUtility.debug("rejected memcache");
			return;
		}
		
		Map<String, Bitmap> cache = null;
		
		if(width > 50){
			cache = getBImgCache();
		}else{
			cache = getSImgCache();
		}
		
		cache.put(getKey(url, targetWidth), bm);
		
	}
	
	private static void showBitmap(ImageView iv, Bitmap bm){
		iv.setVisibility(View.VISIBLE);
		iv.setImageBitmap(bm);
	}
	
	private static void setBitmap(ImageView iv, String url, Bitmap bm){
		
		iv.setTag(url);
		
		if(bm != null){			
			showBitmap(iv, bm);
		}else{
			iv.setImageBitmap(null);	
		}
		
	}
	
	private static void presetBitmap(ImageView iw, String url){
		if(!url.equals(iw.getTag())){
			iw.setImageBitmap(null);
			iw.setTag(url);
		}
	}
	
	public static void async(Context context, ImageView iv, String url, boolean memCache, boolean fileCache, int targetWidth){
		
		if(iv == null) return;
		
		//invalid url
		if(url == null || url.length() < 4){
			setBitmap(iv, null, null);
			return;
		}
		
		//presetBitmap(iv, url);
		
		//check memory
		Bitmap bm = memGet2(url, targetWidth);
		if(bm != null){
			showBitmap(iv, bm);
			return;
		}
		
		/*
		WeakHashMap<ImageView, Void> ivs = ivsMap.get(url);
		
		if(ivs == null){		
			BitmapAjaxCallback cb = new BitmapAjaxCallback();
			cb.setImageView(url, iv);
			cb.start(context, url, memCache, fileCache);
		}else{
			ivs.put(iv, null);
		}*/
		
		if(!queueMap.containsKey(url)){
			BitmapAjaxCallback cb = new BitmapAjaxCallback();
			cb.setImageView(url, iv);
			cb.setTargetWidth(targetWidth);
			cb.start(context, url, memCache, fileCache);
		}else{
			presetBitmap(iv, url);			
			addQueue(url, iv);
		}
		
	}
	
	private static void addQueue(String url, ImageView iv){
		
		
		WeakHashMap<ImageView, Void> ivs = queueMap.get(url);
		
		if(ivs == null){
			
			if(queueMap.containsKey(url)){
				//already a image view fetching
				ivs = new WeakHashMap<ImageView, Void>();
				ivs.put(iv, null);
				queueMap.put(url, ivs);
			}else{
				//register a view by putting a url with no value
				queueMap.put(url, null);
			}
			
		}else{
			//add to list of image views
			ivs.put(iv, null);
			
		}
		
	}
	
	
	private void start(Context context, String url, boolean memCache, boolean fileCache){
		
		
		//ivsMap.put(url, ivs);
		addQueue(url, iv.get());
		
		
		super.async(context, url, memCache, fileCache, false);
	}
	
	
}
