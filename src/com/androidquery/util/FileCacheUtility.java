package com.androidquery.util;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import android.content.Context;
import android.os.AsyncTask;

public class FileCacheUtility {

	private static ScheduledExecutorService storeExe;
	private static ScheduledExecutorService getFileStoreExecutor(){
		
		if(storeExe == null){
			storeExe = Executors.newSingleThreadScheduledExecutor();
		}
		
		return storeExe;
	}
	
	public static void storeAsync(File dir, String url, byte[] data, long delay){
				
		ScheduledExecutorService exe = getFileStoreExecutor();
		FileStoreTask task = new FileStoreTask(getFile(dir, url), data);
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
		
		String hash = DataUtility.getMD5Hex(url);
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
	
	private static void store(File file, byte[] data){
		
		try{
			
			if(file != null){
			
				Utility.debug("store", file);
				
				DataUtility.write(file, data);
			}
		}catch(Exception e){
			Utility.report(e);
		}
		
		
	}
	
	public static void cleanCacheAsync(Context context, long triggerSize, long targetSize){
		
		try{			
			File cacheDir = getCacheDir(context);
			CleanCacheTask task = new CleanCacheTask(cacheDir);
			task.execute(triggerSize, targetSize);
		
		}catch(Exception e){
			Utility.report(e);
		}
	}
	
	private static void cleanCache(File cacheDir, long triggerSize, long targetSize){
		
		try{
		
			File[] files = cacheDir.listFiles();
			if(files == null) return;
			
			Arrays.sort(files, new FileAccessComparator());
			
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
	
	
	private static class FileAccessComparator implements Comparator<File>{

		@Override
		public int compare(File f1, File f2) {
			
			long m1 = f1.lastModified();
			long m2 = f2.lastModified();
			
			if(m2 > m1){
				return 1;
			}else if(m2 == m1){
				return 0;
			}else{
				return -1;
			}
			
			
		}
		
	}
	
    private static class CleanCacheTask extends AsyncTask<Long, Void, String>{

    	private File dir;
    	
    	public CleanCacheTask(File dir){
    		this.dir = dir;
    	}
    	
		@Override
		protected String doInBackground(Long... maxSizes) {			
			
			try{
				cleanCache(dir, maxSizes[0], maxSizes[1]);
			}catch(Exception e){
				Utility.report(e);
			}
			return null;
		}
				
    	
    }
	
	private static class FileStoreTask implements Runnable{
	    		
		private byte[] data;
		private File file;
		
		public FileStoreTask(File file, byte[] data){				
			this.data = data;
			this.file = file;
		}
		
		
		@Override
		public void run() {
			store(file, data);
		    data = null;
		}
	    
	   
	}
}
