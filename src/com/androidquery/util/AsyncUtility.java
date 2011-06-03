package com.androidquery.util;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;

import com.androidquery.callback.AjaxCallback;

public class AsyncUtility {

	private static int NETWORK_POOL = 8;
	
	public static <T> void async(Context context, String url, boolean memCache, boolean fileCache, boolean network, AjaxCallback<T> callback){
		
		Utility.getHandler();
		
		T object = callback.memGet(url);
		
		if(object != null){		
			
			callback.callback(url, object, 200, "OK");
		}else{
		
			ExecutorService exe = getExecutor();
			
			File cacheDir = null;
			if(fileCache) cacheDir = Utility.getCacheDir(context);
			
			FetcherTask<T> ft = new FetcherTask<T>(url, callback, memCache, cacheDir, network);
			
			exe.execute(ft);
		}
	}
	
	private static ExecutorService fetchExe;
	private static ExecutorService getExecutor(){
		
		if(fetchExe == null){
			fetchExe = Executors.newFixedThreadPool(NETWORK_POOL);
		}
		
		return fetchExe;
	}
	
	
	public static void cancel(){
		
		if(fetchExe != null){
			fetchExe.shutdownNow();
			fetchExe = null;
		}
		
	}
	
	private static class FetcherTask<T> implements Runnable {

		private String url;
		private AjaxCallback<T> callback;
		private T result;
		private File cacheDir;
		private int code;
		private String message;
		private boolean memCache;
		private boolean network;
		
		public FetcherTask(String url, AjaxCallback<T> callback, boolean memCache, File cacheDir, boolean network){
			
			this.url = url;
			this.callback = callback;
			this.cacheDir = cacheDir;
			this.memCache = memCache;
			this.network = network;
		}
				
		private void clear(){
			url = null;
			result = null;
			callback = null;
		}
		
		@Override
		public void run() {
			
			try{
			
			
				if(result == null){
					//background thread here
					
					File file = null;
					
					if(cacheDir != null){
						file = callback.fileGet(url, cacheDir);//FileCacheUtility.getExistedCacheByUrlSetAccess(cacheDir, url);
					}
					
					if(file == null){
						Utility.debug("file miss", url);
						
						if(network){
						
							HttpResult hr = Utility.openBytes(url, true);
							byte[] data = hr.getData();
							
							if(data != null){
							
								result = callback.transform(data);
								
								if(cacheDir != null){
									callback.filePut(url, cacheDir, data);
								}
							}
			
							code = hr.getCode();
							message = hr.getMessage();
						
						}
						
					}else{
						
						result = callback.transform(file);
						
						Utility.debug("file hit", url);
					}
					
					if(result != null){
						
						Utility.post(this);
						
					}else{
						clear();
					}
				
				}else{
					//ui thread here
					
					if(memCache){
						callback.memPut(url, result);
					}
					
					//setBitmapIfValid(iw, url, bm);
					callback.callback(url, result, code, message);
					
					
					clear();
				}
				
					
			
			}catch(Exception e){
				Utility.report(e);
			}
			
		}
		
		
		
	}
}
