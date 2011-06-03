package com.androidquery.callback;

import java.io.File;
import java.util.Map;

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
	public Bitmap transform(File file) {
		return BitmapFactory.decodeFile(file.getAbsolutePath());
	}
	
	@Override
	public Bitmap transform(byte[] data) {
		return BitmapFactory.decodeByteArray(data, 0, data.length);
	}
	
	@Override
	public void callback(String url, Bitmap bm, int statusCode, String statusMessage) {
		//setBitmapIfValid(iv, url, object);
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
