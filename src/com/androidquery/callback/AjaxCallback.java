package com.androidquery.callback;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.androidquery.util.Utility;

public abstract class AjaxCallback<T> {
	
	private Class<T> type;
	
	public void setType(Class<T> type){
		this.type = type;
	}
	
	protected abstract void callback(String url, T object, int statusCode, String statusMessage);
	
	protected T transform(File file){
		try {			
			return transform(Utility.toBytes(new FileInputStream(file)));
		} catch(Exception e) {
			Utility.report(e);
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected T transform(byte[] data){
		
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
	
	protected T memGet(String url){
		return null;
	}
	
	protected void memPut(String url, T object){
	}
	
	protected File fileGet(String url, File cacheDir){
		return Utility.getExistedCacheByUrlSetAccess(cacheDir, url);
	}
	
	protected void filePut(String url, File cacheDir, byte[] data){
		Utility.storeAsync(cacheDir, url, data, 1000);
	}
	
	
	private static int NETWORK_POOL = 4;
	
	public void async(Context context, String url, boolean memCache, boolean fileCache, boolean network){
		
		Utility.getHandler();
		
		T object = memGet(url);
		
		if(object != null){					
			callback(url, object, 200, "OK");
		}else{
		
			ExecutorService exe = getExecutor();
			
			File cacheDir = null;
			if(fileCache) cacheDir = Utility.getCacheDir(context);
			
			FetcherTask<T> ft = new FetcherTask<T>(url, this, memCache, cacheDir, network);
			
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
	
	public static void setNetworkLimit(int limit){
		
		NETWORK_POOL = Math.max(1, Math.min(8, limit));
		fetchExe = null;
	}
	
	public static void cancel(){
		
		if(fetchExe != null){
			fetchExe.shutdownNow();
			fetchExe = null;
		}
		
	}
	
	private static int NET_TIMEOUT = 30000;
	
	private static Map<String, Object> openBytes(String urlPath, boolean retry) throws IOException{
				
		URL url = new URL(urlPath);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
           
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);     
        connection.setConnectTimeout(NET_TIMEOUT);
        
        int code = connection.getResponseCode();
       
        if(code == -1 && retry){
        	Utility.debug("code -1", urlPath);
        	return openBytes(urlPath, false);
        }
        
        byte[] data = null;
        String redirect = urlPath;
        if(code == -1 || code < 200 || code >= 300){        	
        	//throw new IOException();
        }else{
        	data = Utility.toBytes(connection.getInputStream());
        	redirect = connection.getURL().toExternalForm();
        }
        
        Map<String, Object> result = new HashMap<String, Object>();
        
        result.put("data", data);
        result.put("code", code);
        result.put("message", connection.getResponseMessage());
        result.put("redirect", redirect);
        
        return result;
        
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
						file = callback.fileGet(url, cacheDir);
					}
					
					if(file == null){
						
						if(network){
						
							byte[] data = null;
							
							try{
								Map<String, Object> net = openBytes(url, true);
								data = (byte[]) net.get("data");
								code = (Integer) net.get("code");
								message = (String) net.get("message");
							}catch(Exception e){
								Utility.report(e);
							}
							
							if(data != null){
							
								result = callback.transform(data);
								
								if(cacheDir != null){
									callback.filePut(url, cacheDir, data);
								}
							}
			
						
						}
						
					}else{
						
						result = callback.transform(file);
						
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
					
					callback.callback(url, result, code, message);
					clear();
				}
				
					
			
			}catch(Exception e){
				Utility.report(e);
			}
			
		}
		
		
		
	}
	
}

