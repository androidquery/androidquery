package com.androidquery.callback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.androidquery.util.Utility;

public abstract class AjaxCallback<T> {
	
	public abstract void callback(String url, T object, int statusCode, String statusMessage);
	
	public T transform(File file){
		try {
			return transform(Utility.toBytes(new FileInputStream(file)));
		} catch (FileNotFoundException e) {
			Utility.report(e);
			return null;
		}
	}
	
	public abstract T transform(byte[] data);
	
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

