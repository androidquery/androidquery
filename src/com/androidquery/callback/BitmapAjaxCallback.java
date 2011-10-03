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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.util.AQUtility;
import com.androidquery.util.BitmapCache;
import com.androidquery.util.RatioDrawable;

/**
 * The callback handler for handling Aquery.image() methods.
 */
public class BitmapAjaxCallback extends AbstractAjaxCallback<Bitmap, BitmapAjaxCallback>{

	private static int SMALL_MAX = 20;
	private static int BIG_MAX = 20;
	private static int BIG_PIXELS = 400 * 400;
	private static int BIG_TPIXELS = 1000000;
	
	private static Map<String, Bitmap> smallCache;
	private static Map<String, Bitmap> bigCache;
	
	private static HashMap<String, WeakHashMap<View, BitmapAjaxCallback>> queueMap = new HashMap<String, WeakHashMap<View, BitmapAjaxCallback>>();	
	
	private WeakReference<View> v;
	private int targetWidth;
	private int fallback;
	private File imageFile;
	private Bitmap bm;
	private int animation;
	private Bitmap preset;
	private float ratio;
	
	/**
	 * Instantiates a new bitmap ajax callback.
	 */
	public BitmapAjaxCallback(){
		type(Bitmap.class).memCache(true).fileCache(true).url("");
	}
	
	
	
	/**
	 * Set the target Image view.
	 *
	 * @param view the view
	 * @return self
	 */
	public BitmapAjaxCallback imageView(ImageView view){				
		return view(view);
	}
	
	/**
	 * Set the target view. 
	 *
	 * @param view the view
	 * @return self
	 */
	public BitmapAjaxCallback view(View view){				
		v = new WeakReference<View>(view);		
		return this;
	}
	
	/**
	 * Set the target width for downsampling.
	 *
	 * @param targetWidth the target width
	 * @return self
	 */
	public BitmapAjaxCallback targetWidth(int targetWidth){
		this.targetWidth = targetWidth;
		return this;
	}
	
	/**
	 * Set the image source file.
	 *
	 * @param imageFile the image file
	 * @return self
	 */
	public BitmapAjaxCallback file(File imageFile){
		this.imageFile = imageFile;
		return this;
	}
	
	/**
	 * Set the preset bitmap. This bitmap will be shown immediately until the ajax callback returns the final image from the url.
	 *
	 * @param preset the preset
	 * @return self
	 */
	public BitmapAjaxCallback preset(Bitmap preset){
		
		this.preset = preset;
		return this;
	}
	
	/**
	 * Set the bitmap. This bitmap will be shown immediately with aspect ratio. 
	 *
	 * @param bm 
	 * @return self
	 */
	public BitmapAjaxCallback bitmap(Bitmap bm){
		this.bm = bm;
		return this;
	}
	
	/**
	 * Set the fallback image in resource id.
	 *
	 * @param resId the res id
	 * @return self
	 */
	public BitmapAjaxCallback fallback(int resId){
		this.fallback = resId;
		return this;
	}
	
	/**
	 * Set the animation resource id, or AQuery.FADE_IN.
	 *
	 * @param animation the animation
	 * @return self
	 */
	public BitmapAjaxCallback animation(int animation){
		this.animation = animation;
		return this;
	}
	
	/**
	 * Set the image aspect ratio (height / width).
	 *
	 * @param ratio the ratio
	 * @return self
	 */
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
	
	/**
	 * Utility method for downsampling images.
	 *
	 * @param path the file path
	 * @param data if file path is null, provide the image data directly
	 * @param targetWidth the target width
	 * @return the resized image
	 */
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
		
		View view = v.get();
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
	
	public static Bitmap getMemoryCached(Context context, int resId){
		
		String key = Integer.toString(resId);			
		Bitmap bm = memGet(key, 0);
		
		if(bm == null){
			bm = BitmapFactory.decodeResource(context.getResources(), resId);
			
			if(bm != null){
				memPut(key, 0, bm);
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
		
		View firstView = v.get();
		WeakHashMap<View, BitmapAjaxCallback> ivs = queueMap.remove(url);
		
		//check if view queue already contains first view 
		if(ivs == null || !ivs.containsKey(firstView)){
			checkCb(this, url, firstView, bm, status);
		}
		
		if(ivs != null){
		
			Set<View> set = ivs.keySet();
			
			for(View view: set){
				BitmapAjaxCallback cb = ivs.get(view);
				cb.status = status;				
				checkCb(cb, url, view, bm, status);
			}
		
		}
		
	}
	
	private void checkCb(BitmapAjaxCallback cb, String url, View v, Bitmap bm, AjaxStatus status){
		
		if(v == null || cb == null) return;
		
		if(url.equals(v.getTag(AQuery.TAG_URL))){			
		
			if(v instanceof ImageView){
				cb.callback(url, (ImageView) v, bm, status);
			}else{
				showBitmap(url, v, bm);
			}
			
		}
		
		showProgress(false);
	}
	
	protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status){
		showBitmap(url, iv, bm);
	}
	

	/**
	 * Sets the icon cache size in count. Icons are images less than 50x50 pixels.
	 *
	 * @param limit the new icon cache limit
	 */
	public static void setIconCacheLimit(int limit){
		SMALL_MAX = limit;
		clearCache();
	}
	
	/**
	 * Sets the cache limit in count.
	 *
	 * @param limit the new cache limit
	 */
	public static void setCacheLimit(int limit){
		BIG_MAX = limit;
		clearCache();
	}
	
	/**
	 * Sets the pixel limit per image. Image larger than limit will not be memcached.
	 *
	 * @param pixels the new pixel limit
	 */
	public static void setPixelLimit(int pixels){
		BIG_PIXELS = pixels;
		clearCache();
	}
	
	/**
	 * Sets the max pixel limit for the entire memcache. LRU images will be expunged if max pixels limit is reached.
	 *
	 * @param pixels the new max pixel limit
	 */
	public static void setMaxPixelLimit(int pixels){
		BIG_TPIXELS = pixels;
		clearCache();
	}
	
	/**
	 * Clear the bitmap memcache.
	 */
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
		if(bm != null) return bm;
		if(!memCache) return null;
		return memGet(url, targetWidth);
	}
	
	
	/**
	 * Gets the memory cached bitmap.
	 *
	 * @param url the url
	 * @param targetWidth the target width, 0 for non downsampling
	 * @return the memory cached
	 */
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
	
	private static void memPut(String url, int targetWidth, Bitmap bm){
		
		if(bm == null) return;
		
		int pixels = bm.getWidth() * bm.getHeight();
		
		Map<String, Bitmap> cache = null;
		
		if(pixels <= 2500){
			cache = getSImgCache();
		}else{
			cache = getBImgCache();
		}
		
		cache.put(getKey(url, targetWidth), bm);
		
	}
	
	
	@Override
	protected void memPut(String url, Bitmap bm){
		memPut(url, targetWidth, bm);
	}
	
	private void showBitmap(String url, View iv, Bitmap bm){
			
		
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
		
		
		setBitmap(url, iv, bm, false);
		
	}
	
	private void presetBitmap(String url, View v){
		
		if(!url.equals(v.getTag(AQuery.TAG_URL)) || preset != null){			
			
			v.setTag(AQuery.TAG_URL, url);
			
			if(preset != null && !cacheAvailable(v.getContext())){
				setBitmap(url, v, preset, true);			
			}else{
				setBitmap(url, v, null, true);
			}
		}
		
	}
	
	
	
	
	private void setBitmap(String url, View v, Bitmap bm, boolean isPreset){
		
		if(v instanceof ImageView){
			setBitmap2(url, (ImageView) v, bm, isPreset);
		}else if(v instanceof TextView){
			setBitmap2(url, (TextView) v, bm, isPreset);
		}
		
	}
	
	private void setBitmap2(String url, TextView tv, Bitmap bm, boolean isPreset){
		
		BitmapDrawable bd = null;
		if(bm != null){
			bd = new BitmapDrawable(bm); 
			Context context = tv.getContext();
			bd.setBounds(0, 0, AQUtility.dip2pixel(context, bm.getWidth()), AQUtility.dip2pixel(context, bm.getHeight()));
		}
		
		tv.setCompoundDrawables(bd, null, null, null);
		
	}
	
	private static final int FADE_DUR = 300;
	
	private void setBitmap2(String url, ImageView iv, Bitmap bm, boolean isPreset){
		
		if(bm == null){
			iv.setImageBitmap(null);
			return;
		}
		
		if(isPreset){
			iv.setImageDrawable(makeDrawable(iv, bm));
			return;
		}
		
		animate(iv, bm);
		
	}

	private Drawable makeDrawable(ImageView iv, Bitmap bm){
		
		BitmapDrawable bd = null;
		
		if(ratio > 0){
			bd = new RatioDrawable(iv.getResources(), bm, iv, ratio);
		}else{
			bd = new BitmapDrawable(iv.getResources(), bm);
		}
		
		return bd;
		
	}
	
	private void animate(ImageView iv, Bitmap bm){
		
		if(status == null) return;
		
		Drawable d = makeDrawable(iv, bm);
		Animation anim = null;
		
		if(animation == AQuery.FADE_IN || (animation == AQuery.FADE_IN_NETWORK && status.getSource() == AjaxStatus.NETWORK)){
			
			if(preset == null){
				anim = new AlphaAnimation(0, 1);
				anim.setInterpolator(new DecelerateInterpolator()); 
				anim.setDuration(FADE_DUR);
			}else{
				
				Drawable pd = makeDrawable(iv, preset);
				Drawable[] ds = new Drawable[]{pd, d};
				TransitionDrawable td = new TransitionDrawable(ds);
				td.setCrossFadeEnabled(true);				
				td.startTransition(FADE_DUR);
				d = td;
			}
		}else if(animation > 0){
			anim = AnimationUtils.loadAnimation(iv.getContext(), animation);
			
		}
		
		iv.setImageDrawable(d);
		
		if(anim != null){
			anim.setStartTime(AnimationUtils.currentAnimationTimeMillis());		
			iv.startAnimation(anim);
		}
	}

	@Override
	public void async(Context context){
		
		
		String url = getUrl();		
		
		View v = this.v.get();
		
		if(url == null){
			showProgress(false);
			setBitmap(url, v, null, false);
			return;
		}
		
		Bitmap bm = memGet(url);
		if(bm != null){		
			v.setTag(AQuery.TAG_URL, url);
			status = new AjaxStatus().source(AjaxStatus.MEMORY).done();
			callback(url, bm, status);
			//AQUtility.debug("mem", url);
			return;
		}
		
		presetBitmap(url, v);
		
		if(!queueMap.containsKey(url)){
			addQueue(url, v);	
			super.async(v.getContext());
		}else{	
			showProgress(true);			
			addQueue(url, v);
		}
		
		
	}
	

	
	private void addQueue(String url, View iv){
		
		
		WeakHashMap<View, BitmapAjaxCallback> ivs = queueMap.get(url);
		
		if(ivs == null){
			
			if(queueMap.containsKey(url)){
				//already a image view fetching
				ivs = new WeakHashMap<View, BitmapAjaxCallback>();
				ivs.put(iv, this);
				queueMap.put(url, ivs);
			}else{
				//register a view by putting a url with no value
				queueMap.put(url, null);
			}
			
		}else{
			//add to list of image views
			ivs.put(iv, this);
			
		}
		
	}

}
