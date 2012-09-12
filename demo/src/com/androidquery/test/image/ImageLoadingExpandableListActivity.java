package com.androidquery.test.image;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Gallery;
import android.widget.ListAdapter;
import android.widget.SlidingDrawer;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.test.RunSourceActivity;
import com.androidquery.test.image.ImageLoadingList4Activity.Photo;
import com.androidquery.util.AQUtility;
import com.androidquery.util.Common;
import com.androidquery.util.XmlDom;

public class ImageLoadingExpandableListActivity extends RunSourceActivity {

	protected AQuery listAq;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		work();	
	}
	
	protected int getContainer(){
		return R.layout.image_elist_activity;
	}
	
	
	public void work(){
	    
		
		AQUtility.cleanCacheAsync(this, 0, 0);
		BitmapAjaxCallback.clearCache();
		
        String url = "https://picasaweb.google.com/data/feed/base/featured?max-results=48";
		aq.progress(R.id.progress).ajax(url, XmlDom.class, this, "renderPhotos");
	     
		
		
		
	}
	
	private List<Photo> convertAll(XmlDom xml){
		
		List<XmlDom> entries = xml.children("entry");
		
		List<Photo> result = new ArrayList<Photo>();
		
		for(XmlDom entry: entries){
			result.add(convert(entry));
		}
		
		return result;
	}
	
	private Photo convert(XmlDom xml){
		
		String url = xml.child("content").attr("src");
		String title = xml.child("title").text();
		String author = xml.child("author").text("name");
		
		String tb = url;
		List<XmlDom> tbs = xml.tags("media:thumbnail");
		
		if(tbs.size() > 0){
			tb = tbs.get(0).attr("url");
		}
		
		tb = tb.replaceAll("https:", "http:");
		
		Photo photo = new Photo();
		photo.url = url;
		photo.tb = tb;
		photo.title = title;
		photo.author = author;
		
		return photo;
	}
/*
	private boolean shouldDelay(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent, String url){
		return shouldDelay(groupPosition, -1, false, convertView, parent, url);
	}
	
	private boolean shouldDelay(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent, String url){
		
		Bitmap bm = BitmapAjaxCallback.getMemoryCached(url, 0);
		if(bm != null) return false;
		
		ExpandableListView elv = (ExpandableListView) parent;
		
		OnScrollListener sl = (OnScrollListener) parent.getTag(AQuery.TAG_SCROLL_LISTENER);
		
		if(sl == null){
		
			sl = new OnScrollListener() {
			
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					
					ExpandableListView elv = (ExpandableListView) view;
					
					view.setTag(AQuery.TAG_LAYOUT, scrollState);
					
					if(scrollState == SCROLL_STATE_IDLE){
						
						AQUtility.debug("idle", view.getChildCount());
						
						int first = elv.getFirstVisiblePosition();
						int last = elv.getLastVisiblePosition();
						
						AQUtility.debug(first, last);
						
						int count = last - first;
						
						ExpandableListAdapter ela = elv.getExpandableListAdapter();
						
						for(int i = 0; i <= count; i++){
						
							long packed = elv.getExpandableListPosition(i + first);
							
							
							
							int group = ExpandableListView.getPackedPositionGroup(packed);
							int child = ExpandableListView.getPackedPositionChild(packed);
							
							AQUtility.debug(group,child);
							
							if(group >= 0){
								
								View convertView = elv.getChildAt(i);
								Long targetPacked = (Long) convertView.getTag(AQuery.TAG_LAYOUT);
								
								if(targetPacked != null && targetPacked.longValue() == packed){
								
									if(child == -1){
									
										ela.getGroupView(group, elv.isGroupExpanded(group), convertView, elv);
										
									}else{
										
										ela.getChildView(group, child, child == ela.getChildrenCount(group) - 1, convertView, elv);
										
									}
									convertView.setTag(AQuery.TAG_LAYOUT, null);
								}else{
									AQUtility.debug("skip!");
								}
								
							}
						
							
						}
						
						
						
					}
					
				}
				
				@Override
				public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
					
				}
			};
			
			
		
			elv.setOnScrollListener(sl);
			parent.setTag(AQuery.TAG_SCROLL_LISTENER, sl);
		}
		
		Integer scrollState = (Integer) elv.getTag(AQuery.TAG_LAYOUT);
		
		if(scrollState == null || scrollState.intValue() == OnScrollListener.SCROLL_STATE_IDLE){
			return false;
		}
		
		long packed = ExpandableListView.getPackedPositionForChild(groupPosition, childPosition);
		convertView.setTag(AQuery.TAG_LAYOUT, packed);
		
		return true;
	}
	*/
	
	private List<Photo> group1;
	private List<Photo> group2;
	
	public void renderPhotos(String url, XmlDom xml, AjaxStatus status) {
		
		if(xml == null) return;
		
		List<Photo> entries = convertAll(xml);
		
		group1 = entries.subList(0, entries.size() / 2);
		group2 = entries.subList(entries.size() / 2, entries.size() - 1);
	
		listAq = new AQuery(this);
		
		BaseExpandableListAdapter aa = new BaseExpandableListAdapter(){
			
			@Override
			public Object getChild(int groupPosition, int childPosition) {
				if(groupPosition == 0){
					return group1.get(childPosition);
				}
				return group2.get(childPosition);
			}

			@Override
			public long getChildId(int groupPosition, int childPosition) {
				
				return childPosition;
			}

			@Override
			public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent){
				
				if(convertView == null){
					convertView = getLayoutInflater().inflate(R.layout.content_item_s, parent, false);
				}
				
				Photo photo = (Photo) getChild(groupPosition, childPosition);
				
				AQuery aq = listAq.recycle(convertView);
				
				aq.id(R.id.name).text(photo.title);
				
				String tbUrl = photo.tb;
			
				if(!aq.shouldDelay(groupPosition, childPosition, isLastChild, convertView, parent, tbUrl)){
					aq.id(R.id.tb).image(tbUrl, true, true, 0, 0, null, 0, 1);
					aq.id(R.id.name).text(photo.title);
				}else{
					aq.id(R.id.tb).clear();
					aq.id(R.id.text).text(photo.title);
				}
				
				
				return convertView;
			}

			@Override
			public int getChildrenCount(int groupPosition) {
				if(groupPosition == 0){
					return group1.size();
				}
				return group2.size();
			}

			@Override
			public Object getGroup(int groupPosition) {
				if(groupPosition == 0){
					return group1;
				}
				return group2;
			}

			@Override
			public int getGroupCount() {
				if(group1 != null){
					return 2;
				}
				return 0;
			}

			@Override
			public long getGroupId(int groupPosition) {
				return groupPosition;
			}

			@Override
			public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
				
				if(convertView == null){
					convertView = getLayoutInflater().inflate(R.layout.content_item_s, parent, false);
				}
				
				Photo photo = (Photo) getChild(groupPosition, 0);
				
				AQuery aq = listAq.recycle(convertView);
				
				aq.id(R.id.name).text(photo.title);
				
				String tbUrl = photo.tb;
			
				if(!aq.shouldDelay(groupPosition, isExpanded, convertView, parent, tbUrl)){
					aq.id(R.id.tb).image(tbUrl, true, true, 0, 0, null, 0, 1);
					
				}else{
					aq.id(R.id.tb).clear();
					
				}
				
				aq.id(R.id.name).text("Group " + groupPosition);
				aq.id(R.id.meta).text("");
				
				return convertView;
			}

			@Override
			public boolean hasStableIds() {
				
				return false;
			}

			@Override
			public boolean isChildSelectable(int arg0, int arg1) {
				
				return false;
			}
			
			
		};
		
		aq.id(R.id.list).adapter(aa);
		
		
	}
	
	@Override
	protected void runSource(){
		
		//AQUtility.invokeHandler(this, type, false, null);
	}
	
	class Photo{
		
		String tb;
		String url;
		String title;
		String author;
	}
	
}
