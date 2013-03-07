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

import android.app.Activity;
import android.app.Dialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Gallery;
import android.widget.ListAdapter;
import android.widget.ProgressBar;

import com.androidquery.AQuery;
import com.androidquery.callback.BitmapAjaxCallback;

/**
 * AQuery internal use only. A shared listener class to reduce the number of classes.
 * 
 */

public class Common implements Comparator<File>, Runnable, OnClickListener, OnLongClickListener, OnItemClickListener, OnScrollListener, OnItemSelectedListener, TextWatcher{

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
	
	private Object invoke(Object... args){
		
		if(method != null){
			
			Object[] input = args;
			if(params != null){
				input = params;
			}
			
			Object cbo = handler;
			if(cbo == null){
				cbo = this;
			}
			
			Object result = AQUtility.invokeHandler(cbo, method, fallback, true, sig, input);
			return result;
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
		
		return null;
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
	public boolean onLongClick(View v) {
		Object result = invoke(v);
		if(result instanceof Boolean){
			return (Boolean) result;
		}
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
		invoke(parent, v, pos, id);
	}
	
	
	private int scrollState = OnScrollListener.SCROLL_STATE_IDLE;
	private OnScrollListener osl;

	@Override
	public void onScroll(AbsListView view, int first, int visibleItemCount, int totalItemCount) {
		
		checkScrolledBottom(view, scrollState);
		
		if(osl != null) osl.onScroll(view, first, visibleItemCount, totalItemCount);
		
	}

	public int getScrollState(){
		return scrollState;
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
		
		this.scrollState = scrollState;
		
		checkScrolledBottom(view, scrollState);
		
		if(view instanceof ExpandableListView){
			onScrollStateChanged((ExpandableListView) view, scrollState); 
		}else{
			onScrollStateChanged2(view, scrollState);
		}
		
		if(osl != null) osl.onScrollStateChanged(view, scrollState);
	}
	
	private void onScrollStateChanged(ExpandableListView elv, int scrollState){
		
		elv.setTag(AQuery.TAG_NUM, scrollState);
		
		if(scrollState == SCROLL_STATE_IDLE){
			
			int first = elv.getFirstVisiblePosition();
			int last = elv.getLastVisiblePosition();
			
			int count = last - first;
			
			ExpandableListAdapter ela = elv.getExpandableListAdapter();
			
			for(int i = 0; i <= count; i++){
			
				long packed = elv.getExpandableListPosition(i + first);
				
				int group = ExpandableListView.getPackedPositionGroup(packed);
				int child = ExpandableListView.getPackedPositionChild(packed);
				
				if(group >= 0){
					
					View convertView = elv.getChildAt(i);
					Long targetPacked = (Long) convertView.getTag(AQuery.TAG_NUM);
					
					if(targetPacked != null && targetPacked.longValue() == packed){
					
						if(child == -1){
						
							ela.getGroupView(group, elv.isGroupExpanded(group), convertView, elv);
							
						}else{
							
							ela.getChildView(group, child, child == ela.getChildrenCount(group) - 1, convertView, elv);
							
						}
						convertView.setTag(AQuery.TAG_NUM, null);
					}else{
						//AQUtility.debug("skip!");
					}
					
				}
			
				
			}
			
			
			
		}
	}
	
	
	private void onScrollStateChanged2(AbsListView lv, int scrollState){
		
		lv.setTag(AQuery.TAG_NUM, scrollState);
		
		if(scrollState == SCROLL_STATE_IDLE){
			
			int first = lv.getFirstVisiblePosition();
			int last = lv.getLastVisiblePosition();
			
			int count = last - first;
			
			ListAdapter la = lv.getAdapter();
			
			for(int i = 0; i <= count; i++){
			
				long packed = i + first;
				
				View convertView = lv.getChildAt(i);
				Number targetPacked = (Number) convertView.getTag(AQuery.TAG_NUM);
				
				if(targetPacked != null){
					la.getView((int) packed, convertView, lv);
					convertView.setTag(AQuery.TAG_NUM, null);
				}else{
					//AQUtility.debug("skip!");
				}
					
			}
			
		}
	}
	
	
	public static boolean shouldDelay(int groupPosition, int childPosition, View convertView, ViewGroup parent, String url){
		
		if(url == null || BitmapAjaxCallback.isMemoryCached(url)){
			return false;
		}
		
		AbsListView lv = (AbsListView) parent;
		
		
		OnScrollListener sl = (OnScrollListener) parent.getTag(AQuery.TAG_SCROLL_LISTENER);
		
		if(sl == null){
			sl = new Common();
			lv.setOnScrollListener(sl);
			parent.setTag(AQuery.TAG_SCROLL_LISTENER, sl);
		}
		
		Integer scrollState = (Integer) lv.getTag(AQuery.TAG_NUM);
		
		if(scrollState == null || scrollState == OnScrollListener.SCROLL_STATE_IDLE || scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
			return false;
		}
		
		long packed = childPosition;
		if(parent instanceof ExpandableListView){
			packed = ExpandableListView.getPackedPositionForChild(groupPosition, childPosition);
		}
		convertView.setTag(AQuery.TAG_NUM, packed);
		
		//TODO add draw count and skip drawing list if possible
		
		return true;
	}	

	public static boolean shouldDelay(int position, View convertView, ViewGroup parent, String url){
		
		if(parent instanceof Gallery){
			return shouldDelayGallery(position, convertView, parent, url);
		}else{
			return shouldDelay(-2, position, convertView, parent, url);
		}
		

	}
		
	public static boolean shouldDelay(View convertView, ViewGroup parent, String url, float velocity, boolean fileCheck){
		
		return shouldDelay(-1, convertView, parent, url);
	}
	
	private static boolean shouldDelayGallery(int position, View convertView, ViewGroup parent, String url){
		
	
		if(url == null || BitmapAjaxCallback.isMemoryCached(url)){
			return false;
		}
		
		Gallery gallery = (Gallery) parent;
		
		Integer selected = (Integer) gallery.getTag(AQuery.TAG_NUM);
		
		if(selected == null){
			
			selected = 0;
			gallery.setTag(AQuery.TAG_NUM, 0);
			
			gallery.setCallbackDuringFling(false);
			
			Common common = new Common();
			common.listen(gallery);
			
		}
		
		int first = gallery.getFirstVisiblePosition();
		int last = gallery.getLastVisiblePosition();
		
		int diff = last - first;
		int delta = (diff / 2) + 1;
		
		int from = selected - delta;
		int to = selected + delta;
		
		if(from < 0){
			//shift window back to positive region
			to = to - from;
			from = 0;
		}
		
		if((position >= from && position <= to)){
			
			//AQUtility.debug("yes", position + ":" + from + "." + to);
			convertView.setTag(AQuery.TAG_NUM, position);
			
			return false;
		}
		
		//AQUtility.debug("no", position + ":" + from + "." + to);
		convertView.setTag(AQuery.TAG_NUM, null);
		return true;
		
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

	private OnItemSelectedListener galleryListener;
	private boolean galleryListen = false;
	
	public void listen(Gallery gallery){
		
		galleryListener = gallery.getOnItemSelectedListener();
		galleryListen = true;
		
		gallery.setOnItemSelectedListener(this);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
		
		invoke(parent, v, pos, id);
	
		if(galleryListener != null){
			galleryListener.onItemSelected(parent, v, pos, id);
		}
		
		if(galleryListen){
			
			Integer selected = (Integer) parent.getTag(AQuery.TAG_NUM);
			
			if(selected != pos){
			
				Adapter adapter = parent.getAdapter();
				parent.setTag(AQuery.TAG_NUM, pos);
			
				int count = parent.getChildCount();
				
				//AQUtility.debug("redrawing", count);
				
				int first = parent.getFirstVisiblePosition();
				
				for(int i = 0; i < count; i++){
					View convertView = parent.getChildAt(i);
					
					int drawPos = first + i;
					
					Integer lastDrawn = (Integer) convertView.getTag(AQuery.TAG_NUM);
					
					if(lastDrawn != null && lastDrawn.intValue() == drawPos){
						//AQUtility.debug("skip", drawPos);
					}else{						
						//AQUtility.debug("redraw", drawPos);
						adapter.getView(drawPos, convertView, parent);
					}
				}
				
			}
			
		}
	
	}


	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
		if(galleryListener != null){
			galleryListener.onNothingSelected(arg0);
		}
		
	}
	
	public static void showProgress(Object p, String url, boolean show){
		
		if(p != null){
			
			if(p instanceof View){				

				View pv = (View) p;
				
				ProgressBar pbar = null;
				
				if(p instanceof ProgressBar){
					pbar = (ProgressBar) p;
				}
				
				if(show){
					pv.setTag(AQuery.TAG_URL, url);
					pv.setVisibility(View.VISIBLE);
					if(pbar != null){
						pbar.setProgress(0);	
						pbar.setMax(100);
					}
					
				}else{
					Object tag = pv.getTag(AQuery.TAG_URL);
					if(tag == null || tag.equals(url)){
						pv.setTag(AQuery.TAG_URL, null);	
						
						if(pbar == null || pbar.isIndeterminate()){
							pv.setVisibility(View.GONE);						
						}
					}
				}
			}else if(p instanceof Dialog){
				
				Dialog pd = (Dialog) p;
				
				AQuery aq = new AQuery(pd.getContext());
				
				if(show){
					aq.show(pd);
				}else{
					aq.dismiss(pd);
				}
				
			}else if(p instanceof Activity){
				
				Activity act = (Activity) p;;
				act.setProgressBarIndeterminateVisibility(show);
				act.setProgressBarVisibility(show);
			
				if(show){
					act.setProgress(0);
				}
			}
		}
		
	}



}
