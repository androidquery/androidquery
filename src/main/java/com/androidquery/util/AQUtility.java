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

package com.androidquery.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;

import com.androidquery.AQuery;

/**
 * Utility methods. Warning: Methods might changed in future versions.
 *
 */

public class AQUtility {

	private static boolean debug = false;
	private static Object wait;
	
	public static void setDebug(boolean debug){
		AQUtility.debug = debug;
	}
	
	public static void debugWait(long time){
		
		if(!debug) return;
		
		if(wait == null) wait = new Object();
		
		synchronized(wait) {
			
			try {
				wait.wait(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	
	public static void debugNotify(){
		
		if(!debug || wait == null) return;
		
		synchronized(wait) {
			wait.notifyAll();			
		}
		
	}
	
	
	public static void debug(Object msg){
		if(debug){
			Log.w("AQuery", msg + "");
		}
	}
	
	public static void warn(Object msg, Object msg2){
		Log.w("AQuery", msg + ":" + msg2);
	}
	
	public static void debug(Object msg, Object msg2){
		if(debug){
			Log.w("AQuery", msg + ":" + msg2);
		}
	}
	
	public static void debug(Throwable e){
		if(debug){
			String trace = Log.getStackTraceString(e);
			Log.w("AQuery", trace);
		}
	}
	
	public static void report(Throwable e){
		
		if(e == null) return;

		try{

			//debug(e);
			warn("reporting", Log.getStackTraceString(e));
			
			if(eh != null){
				eh.uncaughtException(Thread.currentThread(), e);
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	private static UncaughtExceptionHandler eh;
	public static void setExceptionHandler(UncaughtExceptionHandler handler){
		eh = handler;
	}
	
	private static Map<String, Long> times = new HashMap<String, Long>();
	public static void time(String tag){
		
		times.put(tag, System.currentTimeMillis());
		
	}
	
	public static long timeEnd(String tag, long threshold){
		
		
		Long old = times.get(tag);
		if(old == null) return 0;
		
		long now = System.currentTimeMillis();
		
		long diff = now - old;
		
		if(threshold == 0 || diff > threshold){
			debug(tag, diff);
		}
		
		return diff;
		
		
	}
	
	public static Object invokeHandler(Object handler, String callback, boolean fallback, boolean report, Class<?>[] cls, Object... params){
    	
		return invokeHandler(handler, callback, fallback, report, cls, null, params);
		
    }

	public static Object invokeHandler(Object handler, String callback, boolean fallback, boolean report, Class<?>[] cls, Class<?>[] cls2, Object... params){
		try {
			return invokeMethod(handler, callback, fallback, cls, cls2, params);
		} catch (Exception e) {
			if(report){
				AQUtility.report(e);
			}else{
				AQUtility.debug(e);
			}
			return null;
		}
	}
	
	
	
	
	private static Object invokeMethod(Object handler, String callback, boolean fallback, Class<?>[] cls, Class<?>[] cls2, Object... params) throws Exception{
		
		if(handler == null || callback == null) return null;
		
		Method method = null;
		
		try{   
			if(cls == null) cls = new Class[0];
			method = handler.getClass().getMethod(callback, cls);
			return method.invoke(handler, params);			
		}catch(NoSuchMethodException e){
			//AQUtility.debug(e.getMessage());
		}
		
		
		try{
			if(fallback){
			
				if(cls2 == null){
					method = handler.getClass().getMethod(callback);	
					return method.invoke(handler);
				}else{
					method = handler.getClass().getMethod(callback, cls2);
					return method.invoke(handler, params);	
				}
				
			}
		}catch(NoSuchMethodException e){
		}
		
		return null;
		
	}
	
	public static void transparent(View view, boolean transparent){
		
		float alpha = 1;
		if(transparent) alpha = 0.5f;
		
		setAlpha(view, alpha);
		
	}
	
	
	private static void setAlpha(View view, float alphaValue){
		
    	if(alphaValue == 1){
    		view.clearAnimation();
    	}else{
    		AlphaAnimation alpha = new AlphaAnimation(alphaValue, alphaValue);
        	alpha.setDuration(0); // Make animation instant
        	alpha.setFillAfter(true); // Tell it to persist after the animation ends    	
        	view.startAnimation(alpha);
    	}
		
	}
	
	public static void ensureUIThread(){
    	
    	if(!isUIThread()){
    		AQUtility.report(new IllegalStateException("Not UI Thread"));
    	}
    	
    }
	
	public static boolean isUIThread(){
    	
    	long uiId = Looper.getMainLooper().getThread().getId();
    	long cId = Thread.currentThread().getId();
    	
    	return uiId == cId;
    	
    	
    }
	
	
	private static Handler handler;
	public static Handler getHandler(){
		if(handler == null){			
			handler = new Handler(Looper.getMainLooper());			
		}
		return handler;
	}
	
	public static void post(Runnable run){
		getHandler().post(run);
	}
	
	public static void post(Object handler, String method){
		post(handler, method, new Class[0]);
	}
	
	
	public static void post(final Object handler, final String method, final Class<?>[] sig, final Object... params){
		post(new Runnable() {
			
			@Override
			public void run() {
				
				AQUtility.invokeHandler(handler, method, false, true, sig, params);
				
			}
		});
	}
	
	public static void postAsync(Object handler, String method){
		postAsync(handler, method, new Class[0]);
	}
	
	public static void postAsync(final Object handler, final String method, final Class<?>[] sig, final Object... params){
		
		ExecutorService exe = getFileStoreExecutor();
			
		exe.execute(new Runnable() {
			
			@Override
			public void run() {
				
				AQUtility.invokeHandler(handler, method, false, true, sig, params);
				
			}
		});
	}
	
	public static void removePost(Runnable run){
		getHandler().removeCallbacks(run);
	}
	
	public static void postDelayed(Runnable run, long delay){
		getHandler().postDelayed(run, delay);
	}
	
	public static void apply(Editor editor){
		
		if(AQuery.SDK_INT >= 9){
			AQUtility.invokeHandler(editor, "apply", false, true, null, (Object[]) null);
		}else{
			editor.commit();
		}
		
	}
	
	private static String getMD5Hex(String str){
		byte[] data = getMD5(str.getBytes());
		
		BigInteger bi = new BigInteger(data).abs();
	
		String result = bi.toString(36);
		return result;
	}
	
	
	private static byte[] getMD5(byte[] data){

		MessageDigest digest;
		try {
			digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(data);
		    byte[] hash = digest.digest();
		    return hash;
		} catch (NoSuchAlgorithmException e) {
			AQUtility.report(e);
		}
	    
		return null;

	}
	
    private static final int IO_BUFFER_SIZE = 1024 * 4;
    public static void copy(InputStream in, OutputStream out) throws IOException {
    	//copy(in, out, 0, null, null);
    	copy(in, out, 0, null);
    }
    
    public static void copy(InputStream in, OutputStream out, int max, Progress progress) throws IOException {
    	
    	AQUtility.debug("content header", max);
    	
    	if(progress != null){
    		progress.reset();
    		progress.setBytes(max);
    	}
    	
    	byte[] b = new byte[IO_BUFFER_SIZE];
        int read;
        while((read = in.read(b)) != -1){
            out.write(b, 0, read);
            if(progress != null){
            	progress.increment(read);
            }
        }
        
        if(progress != null){
        	progress.done();
        }
    	
    }
    
    public static byte[] toBytes(InputStream is){
    	
    	byte[] result = null;
    	
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	
    	try {
			copy(is, baos);			
			result = baos.toByteArray();
		} catch (IOException e){
			AQUtility.report(e);
		}
		
		close(is);
    	
 	
    	return result;
    	
    }

    public static void write(File file, byte[] data){
    	
	    try{
	    	if(!file.exists()){
	    		try{
	    			file.createNewFile();
	    		}catch(Exception e){
	    			AQUtility.debug("file create fail", file);
	    			AQUtility.report(e);
	    		}
	    	}
	    	
	    	FileOutputStream fos = new FileOutputStream(file);
	    	fos.write(data);
	    	fos.close();
    	}catch(Exception e){
    		AQUtility.report(e);
    	}
    	
    }
    
    public static void close(Closeable c){
    	try{
    		if(c != null){
    			c.close();
    		}
    	}catch(Exception e){   		
    	}
    }
   
	
	private static ScheduledExecutorService storeExe;
	private static ScheduledExecutorService getFileStoreExecutor(){
		
		if(storeExe == null){
			storeExe = Executors.newSingleThreadScheduledExecutor();
		}
		
		return storeExe;
	}
	
	
	public static void storeAsync(File file, byte[] data, long delay){
				
		ScheduledExecutorService exe = getFileStoreExecutor();
		
		Common task = new Common().method(Common.STORE_FILE, file, data);
		exe.schedule(task, delay, TimeUnit.MILLISECONDS);
	
	}
	
	private static File cacheDir;
	private static File pcacheDir;
	
	public static File getCacheDir(Context context, int policy){
		
		if(policy == AQuery.CACHE_PERSISTENT){
			
			if(pcacheDir != null) return pcacheDir;
			
			File cd = getCacheDir(context);
			pcacheDir = new File(cd, "persistent");
			pcacheDir.mkdirs();
			
			return pcacheDir;
		}else{
			return getCacheDir(context);
		}
		
	}
	
	public static File getCacheDir(Context context){			
		
		if(cacheDir == null){
			cacheDir = new File(context.getCacheDir(), "aquery");
			cacheDir.mkdirs();
		}	
		
		return cacheDir;
		
	}
	
	public static void setCacheDir(File dir){
		cacheDir = dir;
		if(cacheDir != null){
			cacheDir.mkdirs();
		}
	}
	
	
	private static File makeCacheFile(File dir, String name){
				
		File result = new File(dir, name);		
		return result;
	}
	
	private static String getCacheFileName(String url){
		
		String hash = getMD5Hex(url);
		return hash;
	}
	/*
	public static File getExistedCacheByUrl(Context context, String url){
		return getExistedCacheByUrl(getCacheDir(context), url);
	}
	*/
	public static File getCacheFile(File dir, String url){
		if(url == null) return null;
		if(url.startsWith(File.separator)){
			return new File(url);
		}
		
		String name = getCacheFileName(url);
		File file = makeCacheFile(dir, name);
		return file;
	}
	
	public static File getExistedCacheByUrl(File dir, String url){
		
		File file = getCacheFile(dir, url);
		if(file == null || !file.exists()){
			return null;
		}
		return file;
	}
	
	public static File getExistedCacheByUrlSetAccess(File dir, String url){
		File file = getExistedCacheByUrl(dir, url);
		if(file != null){
			lastAccess(file);
		}
		return file;
	}
	
	private static void lastAccess(File file){
		long now = System.currentTimeMillis();		
		file.setLastModified(now);
	}
	
	public static void store(File file, byte[] data){
		
		try{
			
			if(file != null){			
				AQUtility.write(file, data);
			}
		}catch(Exception e){
			AQUtility.report(e);
		}
		
		
	}
	
	public static void cleanCacheAsync(Context context){
		
		long triggerSize = 3000000;
		long targetSize = 2000000;	
		cleanCacheAsync(context, triggerSize, targetSize);
	}
	
	public static void cleanCacheAsync(Context context, long triggerSize, long targetSize){
		
		try{			
			File cacheDir = getCacheDir(context);
			
			Common task = new Common().method(Common.CLEAN_CACHE, cacheDir, triggerSize, targetSize);
			
			ScheduledExecutorService exe = getFileStoreExecutor();	
			
			exe.schedule(task, 0, TimeUnit.MILLISECONDS);
			
		}catch(Exception e){
			AQUtility.report(e);
		}
	}
	
	public static void cleanCache(File cacheDir, long triggerSize, long targetSize){
		
		try{
		
			File[] files = cacheDir.listFiles();
			
			if(files == null) return;
			
			Arrays.sort(files, new Common());
			
			if(testCleanNeeded(files, triggerSize)){
				cleanCache(files, targetSize);
			}
			
			File temp = getTempDir();
			if(temp != null && temp.exists()){
				cleanCache(temp.listFiles(), 0);
			}
		}catch(Exception e){
			AQUtility.report(e);
		}
	}
	
	public static File getTempDir(){
		File ext = Environment.getExternalStorageDirectory();
		File tempDir = new File(ext, "aquery/temp");		
		tempDir.mkdirs();
		if(!tempDir.exists()){
		    return null;
		}
		return tempDir;
	}
	
	private static boolean testCleanNeeded(File[] files, long triggerSize){
		
		long total = 0;
		
		for(File f: files){
			total += f.length();
			if(total > triggerSize){
				return true;
			}
		}
		
		return false;
	}
	
	private static void cleanCache(File[] files, long maxSize){
		
		long total = 0;
		int deletes = 0;
		
		for(int i = 0; i < files.length; i++){
			
			File f = files[i];
			
			if(f.isFile()){
			
				total += f.length();
				
				if(total < maxSize){
					//ok
				}else{				
					f.delete();
					deletes++;
					//AQUtility.debug("del", f.getAbsolutePath());
				}
				
			}
		}
		
		AQUtility.debug("deleted" , deletes);
	}
	
	public static int dip2pixel(Context context, float n){
		int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, n, context.getResources().getDisplayMetrics());
		return value;
	}
	
	private static Context context;
	public static void setContext(Application app){
		context = app.getApplicationContext();
	}
	
	public static Context getContext(){
		if(context == null){
			AQUtility.warn("warn", "getContext with null");
			AQUtility.debug(new IllegalStateException());
		}
		return context;
	}
}
