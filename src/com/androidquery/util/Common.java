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

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class Common implements Comparator<File>, Runnable, OnClickListener, OnItemClickListener, OnScrollListener{

	private Object handler = this;
	private String method;
	private Object[] params;
	private boolean fallback;
	private Class<?>[] sig;
	
	public Common forward(Object handler, String callback, boolean fallback, Class<?>[] sig){
		
		this.handler = handler;
		this.method = callback;
		this.fallback = fallback;
		this.sig = sig;
		
		return this;
	}
	
	
	public Common method(String callback, Class<?>[] sig, Object... params){
		
		this.handler = this;
		this.method = callback;
		this.params = params;
		this.sig = sig;
		
		return this;
		
	}
	
	private void invoke(Object... args){
		
		if(method != null){
			
			Object[] input = args;
			if(params != null){
				input = params;
			}
			
			
			AQUtility.invokeHandler(handler, method, fallback, sig, input);
			
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

	public void storeFile(File file, byte[] data){
				
		AQUtility.store(file, data);
	}
	
	public void cleanCache(File dir, long trigger, long target) {			

		AQUtility.cleanCache(dir, trigger, target);
		
	}
	
	
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


	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		//do nothing
	}


	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
		int cc = view.getCount();
		int last = view.getLastVisiblePosition();
		
		if(scrollState == OnScrollListener.SCROLL_STATE_IDLE && cc == last + 1){
			
			//Utility.invokeHandler(o, callback, true, ON_SCROLLED_STATE_SIG, view, scrollState);
			invoke(view, scrollState);
			
		}
	}
	
}
