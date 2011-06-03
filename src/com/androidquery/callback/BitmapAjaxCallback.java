package com.androidquery.callback;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.androidquery.util.Cache;
import com.androidquery.util.Utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public abstract class BitmapAjaxCallback extends AjaxCallback<Bitmap>{

	private static int MAX = 40;
	
	@Override
	public Bitmap transform(File file) {
		return BitmapFactory.decodeFile(file.getAbsolutePath());
	}
	
	@Override
	public Bitmap transform(byte[] data) {
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}

	private static Map<String, Bitmap> cache;
	private static Map<String, Bitmap> getImgCache(){
		
		if(cache == null){
			cache = new Cache<String, Bitmap>(MAX);
		}
		
		return cache;
	}
	
	public static void clearCache(){
		cache = null;
	}
	
	@Override
	public Bitmap memGet(String url){
		
		
		Map<String, Bitmap> cache = getImgCache();		
		return cache.get(url);
	
	}
	
	@Override
	public void memPut(String url, Bitmap object){
	
		Map<String, Bitmap> cache = getImgCache();	
		cache.put(url, object);
		
	}
	
}
