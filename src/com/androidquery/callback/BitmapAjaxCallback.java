package com.androidquery.callback;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.androidquery.util.Cache;
import com.androidquery.util.Utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public abstract class BitmapAjaxCallback extends AjaxCallback<Bitmap>{

	private static int SMALL_MAX = 40;
	private static int BIG_MAX = 40;
	
	private static Map<String, Bitmap> smallCache;
	private static Map<String, Bitmap> bigCache;
	
	@Override
	public Bitmap transform(File file) {
		return BitmapFactory.decodeFile(file.getAbsolutePath());
	}
	
	@Override
	public Bitmap transform(byte[] data) {
		return BitmapFactory.decodeByteArray(data, 0, data.length);
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
	public Bitmap memGet(String url){
		
		Map<String, Bitmap> cache = getBImgCache();
		Bitmap result = cache.get(url);
		
		if(result == null){
			cache = getSImgCache();
			result = cache.get(url);
		}

		return result;
	}
	
	@Override
	public void memPut(String url, Bitmap bm){
		
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
	
}
