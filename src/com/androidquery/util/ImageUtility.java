package com.androidquery.util;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.androidquery.callback.BitmapAjaxCallback;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;


public class ImageUtility {


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
		
		
		presetBitmap(view, url);
		
		boolean network = !checkInProgress(view, url);
		
		AsyncUtility.async(view.getContext(), url, memCache, fileCache, network, cb);
		
		
	}

	


}
