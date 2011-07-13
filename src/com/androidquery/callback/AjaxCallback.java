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
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.androidquery.util.AQUtility;

public class AjaxCallback<T> implements Runnable{
	
	private Class<T> type;
	private WeakReference<Object> handler;
	private String callback;
	
	private String url;
	private T result;
	private File cacheDir;
	private AjaxStatus status;
	
	private boolean fileCache;
	private boolean memCache;
	private boolean refresh;
	
	private void clear(){		
		handler = null;
		result = null;
		status = null;
	}
	
	
	public Class<T> getType() {
		return type;
	}

	public void setCallback(Object handler, String callback){
		this.handler = new WeakReference<Object>(handler);
		this.callback = callback;
	}
	
	public void setType(Class<T> type){
		this.type = type;
	}
	
	public void callback(String url, T object, AjaxStatus status){
		
		Class<?>[] AJAX_SIG = {String.class, type, AjaxStatus.class};
		
		AQUtility.invokeHandler(getHandler(), callback, false, AJAX_SIG, url, object, status);
		
		
	}
	
	public T fileGet(String url, File file, AjaxStatus status){
		try {			
			byte[] data = AQUtility.toBytes(new FileInputStream(file));			
			return transform(url, data, status);
		} catch(Exception e) {
			AQUtility.report(e);
			return null;
		}
	}
	
	public T datastoreGet(String url){
		
		return null;
		
	}
	
	@SuppressWarnings("unchecked")
	public T transform(String url, byte[] data, AjaxStatus status){
				
		if(data == null || type == null){
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
				AQUtility.report(e);
			}
			return (T) result;
		}
		
		if(type.equals(String.class)){
			String result = null;
	    	
	    	try {    		
	    		result = new String(data, "UTF-8");
			} catch (Exception e) {	  		
				AQUtility.report(e);
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
	
	public String getRefreshUrl(String url){
		return url;
	}
	
	public void filePut(String url, T object, File file, byte[] data){
		
		if(file == null || data == null) return;
		
		AQUtility.storeAsync(file, data, 1000);
		
	}
	
	public File accessFile(File cacheDir, String url){		
		return AQUtility.getExistedCacheByUrl(cacheDir, url);
	}
	
	private static AjaxStatus makeStatus(String url, Date time, boolean refresh){
		return new AjaxStatus(200, "OK", url, null, time, refresh);
	}
	
	private static int NETWORK_POOL = 4;
	
	public void async(Context context, String url){
		work(true, context, url, false, false, false);
	}
	
	public void async(Context context, String url, boolean memCache, boolean fileCache, boolean refresh){
		work(true, context, url, memCache, fileCache, refresh);
	}
	
	private void work(boolean async, Context context, String url, boolean memCache, boolean fileCache, boolean refresh){
		
		
		T object = memGet(url);
		
		if(object != null){		
			
			callback(url, object, makeStatus(url, null, refresh));
		
		}else{
		
			if(fileCache) cacheDir = AQUtility.getCacheDir(context);
			
			this.url = url;
			this.memCache = memCache;
			this.fileCache = fileCache;
			this.refresh = refresh;
			
			if(async){
			
				AQUtility.getHandler();				
				ExecutorService exe = getExecutor();
				exe.execute(this);
			
			}else{
				
				backgroundWork();
				afterWork();
			}
		}
	}
	
	public void sync(Context context, String url, boolean memCache, boolean fileCache, boolean refresh){
		
		work(false, context, url, memCache, fileCache, refresh);
		
	}
	
	@Override
	public void run() {
		
		try{
			
			if(status == null){
				
				backgroundWork();
			
				if(status == null){
					status = new AjaxStatus(-1, "OK", url, null, null, refresh);
				}
					
				AQUtility.post(this);
				
			}else{
				
				afterWork();
				clear();
			}
			
				
		
		}catch(Exception e){
			AQUtility.report(e);
		}
		
		
	}
	
	public void background(){
		
	}
	
	
	private void backgroundWork(){
	
		background();
		
		
		if(!refresh){
			
			if(fileCache){				
				fileWork();			
			}
		
		}
		
		
		if(result == null){
			datastoreWork();			
		}
		
		if(result == null){
			networkWork();
		}
		
		
	}
	
	private void fileWork(){
		
		File file = accessFile(cacheDir, url);
				
		//if file exist
		if(file != null){
			//convert
			result = fileGet(url, file, status);
			//if result is ok
			if(result != null){				
				status = makeStatus(url, new Date(file.lastModified()), refresh);
			}
		}
	}
	
	private void datastoreWork(){
		
		result = datastoreGet(url);
		
		if(result != null){		
			status = makeStatus(url, null, refresh);
		}
	}
	
	private void networkWork(){
		
		if(url == null) return;
		
		byte[] data = null;
		
		try{
			
			String networkUrl = url;
			if(refresh) networkUrl = getRefreshUrl(url);
			
			status = openBytes(networkUrl, true);						
			status.setRefresh(refresh);						
			
			data = status.getData();
		}catch(Exception e){
			AQUtility.report(e);
		}
		
		//if(data != null){
		
			try{
				result = transform(url, data, status);
			}catch(Exception e){
				AQUtility.report(e);
			}
			
			if(result != null && fileCache){
				try{
					filePut(url, result, AQUtility.getCacheFile(cacheDir, url), data);
				}catch(Exception e){
					AQUtility.report(e);
				}
			}
		//}
		
		
	}
	
	
	private void afterWork(){
		
		if(url != null && memCache){
			memPut(url, result);
		}
		
		callback(url, result, status);
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
		
		BitmapAjaxCallback.clearTasks();
	}
	
	private static String patchUrl(String url){
		
		url = url.replaceAll(" ", "%20");
		return url;
	}
	
	private static int NET_TIMEOUT = 30000;
	private static String MOBILE_AGENT = "Mozilla/5.0 (Linux; U; Android 2.2) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533";	
	
	
	private static AjaxStatus openBytes(String urlPath, boolean retry) throws IOException{
				
		AQUtility.debug("net", urlPath);
		
		URL url = new URL(patchUrl(urlPath));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
           
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);     
        connection.setConnectTimeout(NET_TIMEOUT);
        connection.addRequestProperty("User-Agent", MOBILE_AGENT);
        
        int code = connection.getResponseCode();
       
        if(code == -1 && retry){
        	AQUtility.debug("code -1", urlPath);
        	return openBytes(urlPath, false);
        }
        
        if(code == 307 && retry){
        	String redirect = connection.getHeaderField("Location");
        	AQUtility.debug("redirect", redirect);
        	return openBytes(redirect, false);
        }
        
        byte[] data = null;
        String redirect = urlPath;
        if(code == -1 || code < 200 || code >= 300){        	
        	//throw new IOException();
        }else{
        	data = AQUtility.toBytes(connection.getInputStream());
        	
        	//AQUtility.debug("length", data.length);
        	redirect = connection.getURL().toExternalForm();
        }
        
        AQUtility.debug("response", code);
        
        return new AjaxStatus(code, connection.getResponseMessage(), redirect, data, new Date(), false);
	}
	

	protected Object getHandler() {
		if(handler == null) return null;
		return handler.get();
	}

	protected String getCallback() {
		return callback;
	}

	
	
}



