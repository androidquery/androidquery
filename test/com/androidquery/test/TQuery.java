package com.androidquery.test;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;

import com.androidquery.AQuery;
import com.androidquery.AbstractAQuery;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;
import com.androidquery.util.Common;

public class TQuery extends AbstractAQuery<TQuery>{

	public TQuery(Activity act) {
		super(act);
	}

	public TQuery(View view) {
		super(view);
	}
	
	
	public static boolean shouldLoad(View convertView, ViewGroup parent, String url){
		
		int state = OnScrollListener.SCROLL_STATE_IDLE;
		float vel = 0;
		Common sl = null;
		
		if(BitmapAjaxCallback.getMemoryCached(url, 0) == null && AQUtility.getExistedCacheByUrl(parent.getContext(), url) == null){
			
			if(parent instanceof AbsListView){
				
				sl = (Common) parent.getTag(AQuery.TAG_SCROLL_LISTENER);
				
				if(sl == null){
				
					AbsListView lv = (AbsListView) parent;
					
					if(lv.getAdapter() instanceof BaseAdapter){	
						sl = new Common();
						lv.setOnScrollListener(sl);
						lv.setTag(AQuery.TAG_SCROLL_LISTENER, sl);				
					}
					
				}else{
					state = sl.getScrollState();
					vel = sl.getVelocity();
				}
			}
		}
		
		
		if(state == OnScrollListener.SCROLL_STATE_FLING && vel > 10){			
			if(convertView.getTag(AQuery.TAG_SCROLL_LISTENER) == null && sl != null){
				sl.addSkip();
			}
			convertView.setTag(AQuery.TAG_SCROLL_LISTENER, url);
			return false;
		}else{
			convertView.setTag(AQuery.TAG_SCROLL_LISTENER, null);
			return true;
		}
		
		
	}
	
	
}
