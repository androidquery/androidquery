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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.androidquery.util.BitmapCache;
import com.androidquery.util.Constants;

public class BitmapAjaxCallback extends AjaxCallback<Bitmap>{

	private static int SMALL_MAX = 20;
	private static int BIG_MAX = 20;
	private static int BIG_PIXELS = 400 * 400;
	private static int BIG_TPIXELS = 1000000;
	
	public static final int FADE_IN = Constants.FADE_IN;
	public static final float RATIO_PRESERVE = Constants.RATIO_PRESERVE;
	
	private static Map<String, Bitmap> smallCache;
	private static Map<String, Bitmap> bigCache;
	
	private static HashMap<String, WeakHashMap<ImageView, Void>> queueMap = new HashMap<String, WeakHashMap<ImageView, Void>>();	
	
	private WeakReference<ImageView> iv;
	private int targetWidth;
	private int fallback;
	private File imageFile;
	private int animation;
	private Bitmap preset;
	private float ratio;
	
	public BitmapAjaxCallback(){
		type(Bitmap.class).memCache(true).fileCache(true);
	}
	
	public BitmapAjaxCallback imageView(ImageView view){
				
		iv = new WeakReference<ImageView>(view);		
		return this;
		
	}
	
	@Override
	public void async(Context context){
		
		String url = getUrl();
		
		if(url == null){
			setBitmap(iv.get(), null, null);
			return;
		}
		
		presetBitmap(iv.get(), url, preset);
		
		super.async(context);
		
	}
	
	public BitmapAjaxCallback targetWidth(int targetWidth){
		this.targetWidth = targetWidth;
		return this;
	}
	
	public BitmapAjaxCallback file(File imageFile){
		this.imageFile = imageFile;
		return this;
	}
	
	public BitmapAjaxCallback preset(Bitmap preset){
		
		this.preset = preset;
		return this;
	}
	
	public BitmapAjaxCallback fallback(int resId){
		this.fallback = resId;
		return this;
	}
	
	public BitmapAjaxCallback animation(int animation){
		this.animation = animation;
		return this;
	}
	
	public BitmapAjaxCallback ratio(float ratio){
		this.ratio = ratio;
		return this;
	}
	
	private static Bitmap decode(String path, byte[] data, BitmapFactory.Options options){
		
		Bitmap result = null;
		
		if(path != null){
			result = BitmapFactory.decodeFile(path, options);
		}else if(data != null){
			result = BitmapFactory.decodeByteArray(data, 0, data.length, options);
		}
		
		return result;
	}
	
	public static Bitmap getResizedImage(String path, byte[] data, int targetWidth){
    	
    	BitmapFactory.Options options = null;
    	
    	if(targetWidth > 0){
	    	
    		options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
	        
	    	decode(path, data, options);
	        
	        int width = options.outWidth;
	        
	        int ssize = sampleSize(width, targetWidth);
	       
	        options = new BitmapFactory.Options();
	        options.inSampleSize = ssize;	        
    	
    	}
        
        Bitmap bm = null;
        try{
        	bm = decode(path, data, options);
		}catch(OutOfMemoryError e){
			AQUtility.report(e);
		}
        
        
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
	
    private Bitmap bmGet(String path, byte[] data){
    	
    	return getResizedImage(path, data, targetWidth);
    }
	
    @Override
    protected File accessFile(File cacheDir, String url){		
    	
    	if(imageFile != null && imageFile.exists()){
    		return imageFile;
    	}
    	
		return super.accessFile(cacheDir, url);
	}
    
    
	@Override
	protected Bitmap fileGet(String url, File file, AjaxStatus status) {		
		return bmGet(file.getAbsolutePath(), null);
	}
	
	@Override
	protected Bitmap transform(String url, byte[] data, AjaxStatus status) {
		
		Bitmap bm = bmGet(null, data);
		
		if(bm == null){
			
			if(fallback > 0){			
				bm = getFallback();			
			}else if(fallback == AQuery.GONE || fallback == AQuery.INVISIBLE){
				bm = empty();
			}
		}
		
		return bm;
	}
	
	private Bitmap getFallback(){
		
		Bitmap bm = null;
		
		ImageView view = iv.get();
		if(view != null){
		
			String key = Integer.toString(fallback);			
			bm = memGet(key);
			
			if(bm == null){
				bm = BitmapFactory.decodeResource(view.getResources(), fallback);
				
				if(bm != null){
					memPut(key, bm);
				}
			}
		}
		
		return bm;
	}
	
	private static Bitmap empty;
	private static Bitmap empty(){
		
		if(empty == null){
			empty = Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8);
		}
		
		return empty;
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
		
	}
	
	private void checkCb(String url, ImageView iv, Bitmap bm, AjaxStatus status){
		
		if(iv == null) return;
		
		if(url.equals(iv.getTag())){
			callback(url, iv, bm, status);
		}
	}
	
	protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status){
		showBitmap(iv, bm, fallback, preset, animation, ratio);
	}

	public static void setIconCacheLimit(int limit){
		SMALL_MAX = limit;
		clearCache();
	}
	
	public static void setCacheLimit(int limit){
		BIG_MAX = limit;
		clearCache();
	}
	
	public static void setPixelLimit(int pixels){
		BIG_PIXELS = pixels;
		clearCache();
	}
	
	public static void setMaxPixelLimit(int pixels){
		BIG_TPIXELS = pixels;
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
			bigCache = Collections.synchronizedMap(new BitmapCache(BIG_MAX, BIG_PIXELS, BIG_TPIXELS));
		}
		return bigCache;
	}
	
	
	private static Map<String, Bitmap> getSImgCache(){
		if(smallCache == null){
			smallCache = Collections.synchronizedMap(new BitmapCache(SMALL_MAX, 50 * 50, 250000));
		}
		return smallCache;
	}
	
	@Override
	protected Bitmap memGet(String url){			
		return memGet(url, targetWidth);
	}
	
	
	public static Bitmap getMemoryCached(String url, int targetWidth){
		return memGet(url, targetWidth);
	}
	
	private static Bitmap memGet(String url, int targetWidth){
		
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
	protected void memPut(String url, Bitmap bm){
		
		if(bm == null) return;
		
		int width = bm.getWidth();
		
		Map<String, Bitmap> cache = null;
		
		if(width > 50){
			cache = getBImgCache();
		}else{
			cache = getSImgCache();
		}
		
		cache.put(getKey(url, targetWidth), bm);
		
	}
	
	private static void showBitmap(ImageView iv, Bitmap bm, int fallback, Bitmap preset, int animation, float ratio){
		
		//ignore 1x1 pixels
		if(bm != null && bm.getWidth() == 1 && bm.getHeight() == 1){        
			bm = null;
		}
		
		if(bm != null){
			iv.setVisibility(View.VISIBLE);
		}else if(fallback == AQuery.GONE){
			iv.setVisibility(View.GONE);
		}else if(fallback == AQuery.INVISIBLE){
			iv.setVisibility(View.INVISIBLE);
		}
		
		//AQUtility.debug("show bitmap", bm + ":" + animation);
		iv.setImageBitmap(bm);
		
		if(ratio > 0){
			ratio(iv, bm, ratio);
		}
		
		if(animation != 0 && preset == null){
			animate(iv, bm, animation);
		}
		
		
	}
	
	private static int getWidth(ImageView iv){
		
		int vw = 0;
		
		if(iv != null){
			vw = iv.getWidth();		
		
			AQUtility.debug("vw", vw + ":" + iv.getLayoutParams().width);
		
			if(vw <= 0) vw = iv.getLayoutParams().width;
		}
		
		return vw;
		
	}
	
	
	private static void ratio(ImageView iv, Bitmap bm, float ratio){
		
		int vw = getWidth(iv);
		
		if(vw <= 0) return;
		
		AQUtility.debug("ratio", ratio);
		
		if(bm != null && ratio == RATIO_PRESERVE){
			ratio = ((float) bm.getHeight()) / ((float) bm.getWidth());
		}
		
		int vh = (int) (vw * ratio);
		
		AQUtility.debug("to", vw + "x" + vh);
		
		LayoutParams lp = iv.getLayoutParams();
		lp.height = vh;
		iv.setLayoutParams(lp);
		
	}
	
	
	private static void animate(ImageView iv, Bitmap bm, int animId){
		
		Animation animation = null;
		
		
		if(animId == FADE_IN){
			animation = new AlphaAnimation(0, 1);
			animation.setInterpolator(new DecelerateInterpolator()); 
			animation.setDuration(500);
		}else{
			animation = AnimationUtils.loadAnimation(iv.getContext(), animId);
		}
		
		animation.setStartTime(AnimationUtils.currentAnimationTimeMillis());		
		iv.startAnimation(animation);
		
	}
	
	protected boolean syncMemGet(){
		return ratio == 0 || getWidth(iv.get()) > 0;
	}
	
	private static void setBitmap(ImageView iv, String url, Bitmap bm){
		
		iv.setTag(url);
		
		if(bm != null){			
			showBitmap(iv, bm, 0, null, 0, -1);
		}else{
			//AQUtility.debug("set bitmap", bm);
			iv.setImageBitmap(null);	
		}
		
	}
	
	private static void presetBitmap(ImageView iw, String url, Bitmap preset){
		
		if(!url.equals(iw.getTag()) || preset != null){
			//AQUtility.debug("preset image", preset);
			iw.setImageBitmap(preset);
			iw.setTag(url);
		}
		
	}
	
	public static void async(Context context, ImageView iv, String url, boolean memCache, boolean fileCache, int targetWidth, int resId, Bitmap preset, int animation){
		
		if(iv == null) return;
		
		//invalid url
		if(url == null){
			setBitmap(iv, null, null);
			return;
		}
		
		presetBitmap(iv, url, preset);
		
		//check memory
		Bitmap bm = memGet(url, targetWidth);
		if(bm != null){
			
			AQUtility.debug("mem hit", getWidth(iv));
			
			showBitmap(iv, bm, resId, preset, animation, -1);
			return;
		}
		
		if(!queueMap.containsKey(url)){
			
			addQueue(url, iv);
			
			BitmapAjaxCallback cb = new BitmapAjaxCallback();
			
			cb.imageView(iv).targetWidth(targetWidth).fallback(resId).preset(preset).animation(animation);
			cb.url(url).memCache(memCache).fileCache(fileCache);
			cb.async(context);
			
		}else{		
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
	
	
	
}
