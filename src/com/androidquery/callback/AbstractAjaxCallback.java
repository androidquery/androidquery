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
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ProgressBar;

import com.androidquery.util.AQUtility;
import com.androidquery.util.AccountHandle;
import com.androidquery.util.XmlDom;

public abstract class AbstractAjaxCallback<T, K> implements Runnable{
	
	private static int NET_TIMEOUT = 30000;
	private static String AGENT = null;
	private static int NETWORK_POOL = 4;
	
	private Class<T> type;
	private Reference<Object> whandler;
	private Object handler;
	private String callback;
	private WeakReference<ProgressBar> pbar;
	
	private String url;
	private Map<String, Object> params;
	private Map<String, String> headers;
	
	private T result;
	
	private File cacheDir;
	private AccountHandle ah;
	
	private AjaxStatus status;
	
	private boolean fileCache;
	private boolean memCache;
	private boolean refresh;
	
	private long expire;
	
	

	
	@SuppressWarnings("unchecked")
	private K self(){
		return (K) this;
	}
	
	
	
	private void clear(){		
		whandler = null;
		result = null;
		status = null;
		handler = null;
	}
	
	public static void setTimeout(int timeout){
		NET_TIMEOUT = timeout;
	}
	
	public static void setAgent(String agent){
		AGENT = agent;
	}
	
	public Class<T> getType() {
		return type;
	}

	public K weakHandler(Object handler, String callback){
		this.whandler = new WeakReference<Object>(handler);
		this.callback = callback;
		this.handler = null;
		return self();
	}
	
	public K handler(Object handler, String callback){
		this.handler = handler;
		this.callback = callback;
		this.whandler = null;
		return self();
	}
	
	public K url(String url){
		this.url = url;
		return self();
	}
	
	public K type(Class<T> type){
		this.type = type;
		return self();
	}
	
	public K fileCache(boolean cache){
		this.fileCache = cache;
		return self();
	}
	
	public K memCache(boolean cache){
		this.memCache = cache;
		return self();
	}
	
	public K refresh(boolean refresh){
		this.refresh = refresh;
		return self();
	}
	
	public K expire(long expire){
		this.expire = expire;
		return self();
	}
	
	public K header(String name, String value){
		if(headers == null){
			headers = new HashMap<String, String>();
		}
		headers.put(name, value);
		return self();
	}
	
	public K param(String name, Object value){
		if(params == null){
			params = new HashMap<String, Object>();
		}
		params.put(name, value);
		return self();
	}
	
	public K params(Map<String, Object> params){
		this.params = params;
		return self();
	}
	
	public K progress(ProgressBar pbar){
		if(pbar != null){
			this.pbar = new WeakReference<ProgressBar>(pbar);
		}
		return self();
	}
	
	private static final Class<?>[] DEFAULT_SIG = {String.class, Object.class, AjaxStatus.class};	
	
	private void callback(){
		
		showProgress(false);
		
		if(callback != null){
			Class<?>[] AJAX_SIG = {String.class, type, AjaxStatus.class};				
			AQUtility.invokeHandler(getHandler(), callback, true, AJAX_SIG, DEFAULT_SIG, url, result, status);			
		}else{		
			callback(url, result, status);
		}
		
		AQUtility.debugNotify();
	}
	
	
	public void callback(String url, T object, AjaxStatus status){
		
	}
	
	protected T fileGet(String url, File file, AjaxStatus status){
		try {			
			byte[] data = AQUtility.toBytes(new FileInputStream(file));			
			return transform(url, data, status);
		} catch(Exception e) {
			AQUtility.report(e);
			return null;
		}
	}
	
	protected T datastoreGet(String url){
		
		return null;
		
	}
	
	private void showProgress(boolean show){
		
		if(pbar != null){
			ProgressBar pb = pbar.get();
			if(pb != null){
				if(show){
					pb.setVisibility(View.VISIBLE);
				}else{
					pb.setVisibility(View.GONE);
				}
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	protected T transform(String url, byte[] data, AjaxStatus status){
				
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
		
		if(type.equals(XmlDom.class)){
			
			XmlDom result = null;
			
			try {    
				result = new XmlDom(data);
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
	
	protected T memGet(String url){
		return null;
	}
	
	
	protected void memPut(String url, T object){
	}
	
	protected String getRefreshUrl(String url){
		return url;
	}
	
	protected void filePut(String url, T object, File file, byte[] data){
		
		if(file == null || data == null) return;
		
		AQUtility.storeAsync(file, data, 1000);
		
	}
	
	protected File accessFile(File cacheDir, String url){		
		File file = AQUtility.getExistedCacheByUrl(cacheDir, url);
		
		if(file != null && expire != 0){
			long diff = System.currentTimeMillis() - file.lastModified();
			if(diff > expire){
				return null;
			}
			
		}
		
		return file;
	}
	
	private static AjaxStatus makeStatus(String url, Date time, boolean refresh){
		return new AjaxStatus(200, "OK", url, null, time, refresh);
	}
	
	public void async(Context context){
		
		if(ah != null){
			
			if(ah.needToken()){
				ah.async(this);
				return;
			}
			
			if(ah.getToken() == null){
				status = new AjaxStatus(401, "Auth failed.", url, null, new Date(), true);
				callback();
				return;
			}
		}
		
		work(context, true);
		
	
	}
	
	
	protected void execute(){
		AQUtility.getHandler();				
		ExecutorService exe = getExecutor();
		exe.execute(this);
	}
	
	private void work(Context context, boolean async){
		
		T object = memGet(url);
			
		if(object != null){		
			result = object;
			status = makeStatus(url, null, refresh);
			callback();
		}else{
		
			if(fileCache) cacheDir = AQUtility.getCacheDir(context);
			
			if(async){			
				execute();			
			}else{
				backgroundWork();
				afterWork();
			}
		}
	}
	
	public void sync(Context context){
		
		work(context, false);
		
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
	
	protected void background(){
		
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
			
			status = network();
			
			if(ah != null && (status.getCode() == 401 || status.getCode() == 403)){
				AQUtility.debug("reauth needed!");				
				authToken(ah.getType(), ah.reauth());
				status = network();
			}
			
			
			status.setRefresh(refresh);						
			
			data = status.getData();
		}catch(Exception e){
			AQUtility.report(e);
		}
		
		
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
		
		
		
	}
	
	private AjaxStatus network() throws IOException{
		
		String networkUrl = url;
		if(refresh) networkUrl = getRefreshUrl(url);
		
		AjaxStatus status;
		if(params == null){
			status = httpGet(networkUrl, headers, true);						
		}else{
			status = httpPost(networkUrl, headers, params);
		}
		
		return status;
	}
	
	
	private void afterWork(){
		
		if(url != null && memCache){
			memPut(url, result);
		}
		
		callback();
		
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
	
	
	private static AjaxStatus httpGet(String urlPath, Map<String, String> headers, boolean retry) throws IOException{
				
		AQUtility.debug("net", urlPath);
		
		URL url = new URL(patchUrl(urlPath));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
           
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);     
        connection.setConnectTimeout(NET_TIMEOUT);
        
        if(AGENT != null){
        	connection.addRequestProperty("User-Agent", AGENT);
        }
        
        if(headers != null){
        	for(String name: headers.keySet()){
        		connection.addRequestProperty(name, headers.get(name));
        	}
        }
        
        int code = connection.getResponseCode();
       
        if(code == -1 && retry){
        	AQUtility.debug("code -1", urlPath);
        	return httpGet(urlPath, headers, false);
        }
        
        if(code == 307 && retry){
        	String redirect = connection.getHeaderField("Location");
        	AQUtility.debug("redirect", redirect);
        	return httpGet(redirect, headers, false);
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
	
	private static AjaxStatus httpPost(String url, Map<String, String> headers, Map<String, Object> params) throws ClientProtocolException, IOException{
		
		AQUtility.debug("post", url);
		
		
		HttpPost post = new HttpPost(url);
		
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		
		for(Map.Entry<String, Object> e: params.entrySet()){
			Object value = e.getValue();
			if(value != null){
				pairs.add(new BasicNameValuePair(e.getKey(), value.toString()));
			}
		}
		
		if(headers != null){
        	for(String name: headers.keySet()){
        		//connection.addRequestProperty(name, headers.get(name));
        		post.addHeader(name, headers.get(name));
        	}
        }
		
		post.setEntity(new UrlEncodedFormEntity(pairs));
		return httpDo(post, url);
		
		
	}
	
	private static AjaxStatus httpDo(HttpUriRequest hr, String url) throws ClientProtocolException, IOException{
		
		
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, NET_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, NET_TIMEOUT);
		
		DefaultHttpClient client = new DefaultHttpClient(httpParams);
		
		HttpResponse response = client.execute(hr);
		
		
        byte[] data = null;
        
        String redirect = url;
        
        int code = response.getStatusLine().getStatusCode();
        String message = response.getStatusLine().getReasonPhrase();
        
        if(code == -1 || code < 200 || code >= 300){        	
        	//throw new IOException();
        }else{
        	
        	HttpEntity entity = response.getEntity();				
			InputStream is = entity.getContent();
			
			data = AQUtility.toBytes(is);
        }
        
        AQUtility.debug("response", code);
        
        AjaxStatus result = new AjaxStatus(code, message, redirect, data, new Date(), false);
		result.setClient(client);
		return result;
			
		
		
		
	}
	
	public K auth(Activity act, String type, String account){
		
		if(android.os.Build.VERSION.SDK_INT >= 5){		
			ah = new AccountHandle(act, type, account);
		}
		return self();
		
	}
	

	public K authToken(String type, String token){
		if(token != null){
			header("Authorization", "GoogleLogin auth=" + token);
		}
		return self();
	}
	
	
	public String getUrl(){
		return url;
	}
	
	public Object getHandler() {
		if(handler != null) return handler;
		if(whandler == null) return null;
		return whandler.get();
	}

	public String getCallback() {
		return callback;
	}

	
	
}



