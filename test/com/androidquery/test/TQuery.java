package com.androidquery.test;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;

import com.androidquery.AbstractAQuery;
import com.androidquery.util.AQUtility;

public class TQuery extends AbstractAQuery<TQuery>{

	public TQuery(Activity act) {
		super(act);
	}

	public TQuery(View view) {
		super(view);
	}
	
	public int getScrollState(ViewGroup view){
		
		Integer result = null;
		
		if(view instanceof AbsListView){
			
			result = (Integer) view.getTag();
			
			if(result == null){
			
				AbsListView lv = (AbsListView) view;
				
				if(lv.getAdapter() instanceof BaseAdapter){
				
					lv.setOnScrollListener(new OnScrollListener() {
						
						@Override
						public void onScrollStateChanged(AbsListView view, int scrollState) {
							
							//AQUtility.debug("set", scrollState);
							
							view.setTag(scrollState);
							
							if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
								ListAdapter la = view.getAdapter();
								if(la instanceof BaseAdapter){
									BaseAdapter ba = (BaseAdapter) la;
									ba.notifyDataSetChanged();
								}
							}
							
						}
						
						@Override
						public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
							// TODO Auto-generated method stub
							
						}
					});
				
				}
			}
		}
		
		if(result == null) return OnScrollListener.SCROLL_STATE_IDLE;
		
		return result; 
	}
	
}
