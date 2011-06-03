package com.androidquery.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.androidquery.callback.BitmapAjaxCallback;

public class Utility {

	private static boolean debug = false;
	
	public static void setDebug(boolean debug){
		Utility.debug = debug;
	}
	
	public static void debug(Object msg){
		if(debug){
			Log.w("AQuery", msg + "");
		}
	}
	
	public static void debug(Object msg, Object msg2){
		if(debug){
			Log.w("AQuery", msg + ":" + msg2);
		}
	}
	
	public static void report(Throwable e){
		if(debug && e != null){
			String trace = Log.getStackTraceString(e);
			Log.w("AQuery", trace);
		}
	}
	
	public static void invokeHandler(Object handler, String callback, boolean fallback, Class<?>[] cls, Object... params){
    	
		
		try{   
			Method method = findMethod(handler, callback, fallback, cls);
			//invokeMethod(handler, callback, fallback, cls, params);
			method.invoke(handler, params);
		}catch(Exception e){		
			Utility.report(e);
		}
		
		
		
    }
	
	public static Method findMethod(Object handler, String callback, boolean fallback, Class<?>[] cls){
		
		try{   
			Method method = handler.getClass().getMethod(callback, cls);
			//method.invoke(handler, params);
			return method;
		}catch(NoSuchMethodException e){
		}
		
		
		try{
			if(fallback){
				Method method = handler.getClass().getMethod(callback);				
				//method.invoke(handler);
				return method;
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
    	
    	
    	long uiId = Looper.getMainLooper().getThread().getId();
    	long cId = Thread.currentThread().getId();
    	
    	if(uiId != cId){
    		Utility.report(new NullPointerException());
    	}else{
    		Utility.debug("ui ok!");
    	}
    	
    }
	
	
	private static Handler handler;
	public static Handler getHandler(){
		if(handler == null){
			ensureUIThread();
			handler = new Handler();
		}
		return handler;
	}
	
	public static void post(Runnable run){
		handler.post(run);
	}
	
	public static void postDelayed(Runnable run, long delay){
		handler.postDelayed(run, delay);
	}
	
	public static String getMD5Hex(String str){
		byte[] data = getMD5(str.getBytes());
		
		BigInteger bi = new BigInteger(data).abs();
	
		String result = bi.toString(36);
		return result;
	}
	
	
	public static byte[] getMD5(byte[] data){

		MessageDigest digest;
		try {
			digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(data);
		    byte[] hash = digest.digest();
		    return hash;
		} catch (NoSuchAlgorithmException e) {
			Utility.report(e);
		}
	    
		return null;

	}
	
    private static final int IO_BUFFER_SIZE = 4 * 1024;

    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] b = new byte[IO_BUFFER_SIZE];
        int read;
        while ((read = in.read(b)) != -1) {
            out.write(b, 0, read);
        }
    }

    public static byte[] toBytes(InputStream is){
    	
    	byte[] result = null;
    	
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	
    	try {
			copy(is, baos);
			close(is);
			result = baos.toByteArray();
		} catch (IOException e){
			Utility.report(e);
		}
    	
 	
    	return result;
    	
    }

    public static void write(File file, byte[] data){
    	
	    try{
	    	if(!file.exists()){
	    		try{
	    			file.createNewFile();
	    		}catch(Exception e){
	    			Utility.debug("can't make:" + file.getAbsolutePath());
	    			Utility.report(e);
	    		}
	    	}
	    	
	    	FileOutputStream fos = new FileOutputStream(file);
	    	fos.write(data);
	    	fos.close();
    	}catch(Exception e){
    		Utility.debug(file.getAbsolutePath());
    		Utility.report(e);
    	}
    	
    }
    
    public static void close(InputStream is){
    	try{
    		if(is != null){
    			is.close();
    		}
    	}catch(Exception e){   		
    	}
    }
    
	private static boolean checkInProgress(ImageView view, String url){
		
		if(url.equals(view.getTag())){
			return true;
		}else{
			return false;
		}
		
	}
	
	private static void setBitmap(ImageView iw, String url, Bitmap bm){
		
		iw.setTag(url);
		
		if(bm != null){			
			iw.setVisibility(View.VISIBLE);
			iw.setImageBitmap(bm);
		}else{
			iw.setImageBitmap(null);	
		}
		
	}
	
	private static void presetBitmap(ImageView iw, String url){
		iw.setImageBitmap(null);
		iw.setTag(url);
	}
	
	private static void setBitmapIfValid(ImageView iw, String url, Bitmap bm){
		
		if(url.equals(iw.getTag())){
			iw.setVisibility(View.VISIBLE);
			iw.setImageBitmap(bm);
		}else{
			//Utility.debug("url mismatch", url);
			//do nothing, not the right iw anymore
		}
		
		
	}

	
	public static void openAsyncImage(ImageView view, String url, boolean memCache, boolean fileCache){
		
		if(view == null) return;
		
		//invalid url
		if(url == null || url.length() < 4){
			setBitmap(view, null, null);
			return;
		}
		
		final ImageView iw = view;
		BitmapAjaxCallback cb = new BitmapAjaxCallback() {
			
			@Override
			public void callback(String url, Bitmap object, int statusCode, String statusMessage) {
				setBitmapIfValid(iw, url, object);
			}
		};
		
		boolean network = !checkInProgress(view, url);
		
		presetBitmap(view, url);		
		
		AsyncUtility.async(view.getContext(), url, memCache, fileCache, network, cb);
		
		
	}
	
	private static ScheduledExecutorService storeExe;
	private static ScheduledExecutorService getFileStoreExecutor(){
		
		if(storeExe == null){
			storeExe = Executors.newSingleThreadScheduledExecutor();
		}
		
		return storeExe;
	}
	
	private static final Class<?>[] STORE_FILE_SIG = {File.class, byte[].class}; 
	public static void storeAsync(File dir, String url, byte[] data, long delay){
				
		ScheduledExecutorService exe = getFileStoreExecutor();
		
		//Common task = new Common().method("storeFile").put("data", data).put("file", getFile(dir, url));
		Common task = new Common().method("storeFile", STORE_FILE_SIG, getFile(dir, url), data);
		exe.schedule(task, delay, TimeUnit.MILLISECONDS);
	
	}
	
	private static File cacheDir;
	
	public static File getCacheDir(Context context){		
	
		if(cacheDir == null){
			cacheDir = new File(context.getCacheDir(), "aquery");
			makeDir(cacheDir);
		}		
		return cacheDir;
	}
	
	private static void makeDir(File dir){		
		dir.mkdirs();
	}
	
	
	private static File getCacheFile(File dir, String name){
				
		File result = new File(dir, name);		
		return result;
	}
	
	public static String toFileName(String url){
		
		String hash = Utility.getMD5Hex(url);
		return hash;
	}
	
	private static File getFile(File dir, String url){
		String name = toFileName(url);
		File file = getCacheFile(dir, name);
		return file;
	}
	
	public static File getExistedCacheByUrl(File dir, String url){
		
		File file = getFile(dir, url);
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
	
	public static void lastAccess(File file){
		long now = System.currentTimeMillis();		
		file.setLastModified(now);
	}
	
	public static void store(File file, byte[] data){
		
		try{
			
			if(file != null){
			
				Utility.debug("store", file);
				
				Utility.write(file, data);
			}
		}catch(Exception e){
			Utility.report(e);
		}
		
		
	}
	
	private static final Class<?>[] CLEAN_CACHE_SIG = {File.class, long.class, long.class};
	public static void cleanCacheAsync(Context context, long triggerSize, long targetSize){
		
		try{			
			File cacheDir = getCacheDir(context);
			
			Common task = new Common().method("cleanCache", CLEAN_CACHE_SIG, cacheDir, triggerSize, targetSize);
			
			ScheduledExecutorService exe = getFileStoreExecutor();			
			exe.schedule(task, 0, TimeUnit.MILLISECONDS);
			
		}catch(Exception e){
			Utility.report(e);
		}
	}
	
	public static void cleanCache(File cacheDir, long triggerSize, long targetSize){
		
		try{
		
			File[] files = cacheDir.listFiles();
			if(files == null) return;
			
			//Arrays.sort(files, new FileAccessComparator());
			Arrays.sort(files, new Common());
			
			if(testCleanNeeded(files, triggerSize)){
				cleanCache(files, targetSize);
			}else{
				Utility.debug("clean not required");
			}
			
		
		}catch(Exception e){
			Utility.report(e);
		}
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
						
			total += f.length();
			
			if(total < maxSize){
				//ok
			}else{				
				f.delete();
				deletes++;
				//Utility.debug("del:" + f.getAbsolutePath());
			}
			
			
		}
		
		Utility.debug("deleted files" , deletes);
	}
	
	private static int NET_TIMEOUT = 30000;
	
	public static HttpResult openBytes(String urlPath, boolean retry) throws IOException{
				
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
        
        
        
        
        HttpResult result = new HttpResult();
        result.setData(data);
        result.setCode(code);
        result.setMessage(connection.getResponseMessage());
        result.setRedirect(redirect);
        
        return result;
        
	}
	
}
