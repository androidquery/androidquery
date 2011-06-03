package com.androidquery.callback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.androidquery.util.Utility;

public abstract class AjaxCallback<T> {
	
	private Class<T> type;
	
	public void setType(Class<T> type){
		this.type = type;
	}
	
	public abstract void callback(String url, T object, int statusCode, String statusMessage);
	
	public T transform(File file){
		try {			
			return transform(Utility.toBytes(new FileInputStream(file)));
		} catch(Exception e) {
			Utility.report(e);
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public T transform(byte[] data){
		
		if(type == null){
			return null;
		}
		
		if(type.equals(Bitmap.class)){			
			return (T) BitmapFactory.decodeByteArray(data, 0, data.length);
		}
		
		
		if(type.equals(JSONObject.class)){
			
			JSONObject result = null;
	    	
	    	try {    		
	    		String str = new String(data);
				result = (JSONObject) new JSONTokener(str).nextValue();
			} catch (Exception e) {	  		
				Utility.report(e);
			}
			return (T) result;
		}
		
		if(type.equals(String.class)){
			String result = null;
	    	
	    	try {    		
	    		result = new String(data, "UTF-8");
			} catch (Exception e) {	  		
				Utility.report(e);
			}
			return (T) result;
		}
		
		if(type.equals(byte[].class)){
			return (T) data;
		}
		
		return null;
	}
	
	public T memGet(String url){
		return null;
	}
	
	public void memPut(String url, T object){
	}
	
	public File fileGet(String url, File cacheDir){
		return Utility.getExistedCacheByUrlSetAccess(cacheDir, url);
	}
	
	public void filePut(String url, File cacheDir, byte[] data){
		Utility.storeAsync(cacheDir, url, data, 1000);
	}
	
}

