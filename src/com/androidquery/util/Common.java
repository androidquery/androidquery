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

import java.io.File;
import java.util.Comparator;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListAdapter;

import com.androidquery.AQuery;
import com.androidquery.callback.BitmapAjaxCallback;

/**
 * AQuery internal use only. A shared listener class to reduce the number of classes.
 * 
 */

public class Common implements Comparator<File>, Runnable, OnClickListener, OnItemClickListener, OnScrollListener, OnItemSelectedListener, TextWatcher{

	private Object handler;
	private String method;
	private Object[] params;
	private boolean fallback;
	private Class<?>[] sig;
	private int methodId;
	
	
	public Common forward(Object handler, String callback, boolean fallback, Class<?>[] sig){
		
		this.handler = handler;
		this.method = callback;
		this.fallback = fallback;
		this.sig = sig;
		
		return this;
	}
	
	
	public Common method(int methodId, Object... params){
		
		this.methodId = methodId;
		this.params = params;
		
		return this;
		
	}
	
	private void invoke(Object... args){
		
		if(method != null){
			
			Object[] input = args;
			if(params != null){
				input = params;
			}
			
			Object cbo = handler;
			if(cbo == null){
				cbo = this;
			}
			
			AQUtility.invokeHandler(cbo, method, fallback, true, sig, input);
			
		}else if(methodId != 0){
			
			switch(methodId){
			
				case CLEAN_CACHE:
					AQUtility.cleanCache((File) params[0], (Long) params[1], (Long) params[2]);
					break;
				case STORE_FILE:
					AQUtility.store((File) params[0], (byte[]) params[1]);
					break;
			
			}
			
		}
		
		
	}
	
	
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

	protected static final int STORE_FILE = 1;
	protected static final int CLEAN_CACHE = 2;
		
	
	@Override
	public void run() {
		invoke();
	}


	@Override
	public void onClick(View v) {
		invoke(v);
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
		invoke(parent, v, pos, id);
	}
	
	
	private int scrollState = OnScrollListener.SCROLL_STATE_IDLE;
	private int scrollSkip;
	private long lastScroll;
	private int lastPosition;
	private float velocity;
	private OnScrollListener osl;

	@Override
	public void onScroll(AbsListView view, int first, int visibleItemCount, int totalItemCount) {
		
		checkScrolledBottom(view, scrollState);
		
		if(scrollState == OnScrollListener.SCROLL_STATE_FLING){
			
			long now = System.currentTimeMillis();	
			int diff = Math.abs(lastPosition - first);
			long dur = now - lastScroll;
			
			if(lastScroll == 0){
				lastScroll = now;
			}else if(diff > 0 && dur > 100){
				
				velocity = ((float) diff / (float) dur) * 1000;
				
				lastPosition = first;
				lastScroll = now;
			}
			
		}else{
			lastScroll = 0;
		}
		
		if(osl != null) osl.onScroll(view, first, visibleItemCount, totalItemCount);
		
	}

	public int getScrollState(){
		return scrollState;
	}
	
	public void addSkip(){
		scrollSkip++;
	}

	public float getVelocity(){
		return velocity;
	}
	
	public void forward(OnScrollListener listener){
		this.osl = listener;
	}
	
	private int lastBottom;
	private void checkScrolledBottom(AbsListView view, int scrollState){
		
		int cc = view.getCount();
		int last = view.getLastVisiblePosition();
		
		if(scrollState == OnScrollListener.SCROLL_STATE_IDLE && cc == last + 1){			
			if(last != lastBottom){
				lastBottom = last;
				invoke(view, scrollState);
			}
			
		}else{
			lastBottom = -1;
		}
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
		checkScrolledBottom(view, scrollState);
		
		int first = view.getFirstVisiblePosition();
		
		this.scrollState = scrollState;
		
		if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
			
			if(scrollSkip > 0){
			
	            int count = view.getChildCount();
	            
	            ListAdapter la = view.getAdapter();
	            
	            for(int i = 0; i < count; i++) {
	                View convertView = (View) view.getChildAt(i);
	                if(convertView.getTag(AQuery.TAG_SCROLL_LISTENER) != null){
	                	la.getView(first + i, convertView, view);	                	
	                	convertView.setTag(AQuery.TAG_SCROLL_LISTENER, null);
	                }
	            }
				
			}
			
			scrollSkip = 0;
		}
		
		if(osl != null) osl.onScrollStateChanged(view, scrollState);
	}


	public static boolean shouldDelay(View convertView, ViewGroup parent, String url, float velocity, boolean fileCheck){
		
		if(url == null) return false;
		
		int state = OnScrollListener.SCROLL_STATE_IDLE;
		float vel = 0;
		Common sl = (Common) parent.getTag(AQuery.TAG_SCROLL_LISTENER);
		if(sl != null){
			state = sl.getScrollState();
			vel = sl.getVelocity();
		}else if(parent instanceof AbsListView){
			AbsListView lv = (AbsListView) parent;
			sl = new Common();
			lv.setOnScrollListener(sl);
			lv.setTag(AQuery.TAG_SCROLL_LISTENER, sl);	
		}
		
		boolean moving = state == OnScrollListener.SCROLL_STATE_FLING && vel >= velocity;
		
		if(!moving){
			convertView.setTag(AQuery.TAG_SCROLL_LISTENER, null);
			return false;
		}
		
		boolean hit = BitmapAjaxCallback.getMemoryCached(url, 0) != null || (fileCheck && AQUtility.getExistedCacheByUrl(parent.getContext(), url) != null);
		
		if(hit){
			return false;
		}
		
		if(sl != null){
			sl.addSkip();
			convertView.setTag(AQuery.TAG_SCROLL_LISTENER, url);
			return true;
		}
		
		return false;
		
		
	}
	
	
	@Override
	public void afterTextChanged(Editable s) {
		
	}


	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		
	}


	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		
		invoke(s, start, before, count);
	}


	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
		invoke(parent, v, pos, id);
	}


	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}
}
