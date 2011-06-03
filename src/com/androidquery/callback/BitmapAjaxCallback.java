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
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import com.androidquery.util.Cache;

public class BitmapAjaxCallback extends AjaxCallback<Bitmap>{

	private static int SMALL_MAX = 20;
	private static int BIG_MAX = 20;
	
	private static Map<String, Bitmap> smallCache;
	private static Map<String, Bitmap> bigCache;
	
	private ImageView iv;
	
	public BitmapAjaxCallback(ImageView iv){
		this.iv = iv;
	}
	
	@Override
	protected Bitmap transform(File file) {
		return BitmapFactory.decodeFile(file.getAbsolutePath());
	}
	
	@Override
	protected Bitmap transform(byte[] data) {
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}
	
	@Override
	protected void callback(String url, Bitmap bm, AjaxStatus status) {
		
		if(url.equals(iv.getTag())){
			iv.setVisibility(View.VISIBLE);
			iv.setImageBitmap(bm);
		}
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
	protected Bitmap memGet(String url){
		
		Map<String, Bitmap> cache = getBImgCache();
		Bitmap result = cache.get(url);
		
		if(result == null){
			cache = getSImgCache();
			result = cache.get(url);
		}

		return result;
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
		
		cache.put(url, bm);
		
	}
	
	private static boolean checkInProgress(ImageView view, String url){
		
		if(url.equals(view.getTag())){
			return true;
		}else{
			return false;
		}
		
	}
	
	private static void setBitmap(ImageView iw, String url, Bitmap bm){
		
		iw.setTag(url);
		
		if(bm != null){			
			iw.setVisibility(View.VISIBLE);
			iw.setImageBitmap(bm);
		}else{
			iw.setImageBitmap(null);	
		}
		
	}
	
	private static void presetBitmap(ImageView iw, String url){
		iw.setImageBitmap(null);
		iw.setTag(url);
	}
	
	@Override
	public void async(Context context, String url, boolean memCache, boolean fileCache, boolean network){
		
		if(iv == null) return;
		
		//invalid url
		if(url == null || url.length() < 4){
			setBitmap(iv, null, null);
			return;
		}
		
		
		network = !checkInProgress(iv, url);
		
		presetBitmap(iv, url);		
		
		super.async(context, url, memCache, fileCache, network);
	}
	
	
}
