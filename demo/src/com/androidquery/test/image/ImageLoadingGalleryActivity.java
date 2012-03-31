package com.androidquery.test.image;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
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
import com.androidquery.util.XmlDom;

public class ImageLoadingGalleryActivity extends RunSourceActivity {

	protected AQuery listAq;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		work();	
	}
	
	protected int getContainer(){
		return R.layout.image_gallery_activity;
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
			//tb = tbs.get(tbs.size() - 1).attr("url");
		}
		
		tb = tb.replaceAll("https:", "http:");
		
		Photo photo = new Photo();
		photo.url = url;
		photo.tb = tb;
		photo.title = title;
		photo.author = author;
		
		return photo;
	}
	
	private boolean init = false;
	private int selected = 0;
	
	private boolean shouldDelayG(int position, View convertView, ViewGroup parent, String url){
		
		if(url == null) return false;
		
		boolean hit = BitmapAjaxCallback.getMemoryCached(url, 0) != null;
		if(hit){
			return false;
		}
		
		Gallery gallery = (Gallery) parent;
		
		if(!init){
			
			gallery.setCallbackDuringFling(false);
			
			gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> adapterView, View convertView, int pos, long id) {
					
					AQUtility.debug("on select", pos);
					
					if(selected != pos){
					
						Adapter adapter = adapterView.getAdapter();
						
						selected = pos;
					
						int count = adapterView.getChildCount();
						
						AQUtility.debug("redrawing", count);
						
						int first = adapterView.getFirstVisiblePosition();
						
						for(int i = 0; i < count; i++){
							View v = adapterView.getChildAt(i);
							
							int drawPos = first + i;
							
							Integer lastDrawn = (Integer) v.getTag(AQuery.TAG_LAYOUT);
							
							if(lastDrawn != null && lastDrawn.intValue() == drawPos){
								AQUtility.debug("skip", drawPos);
							}else{						
								AQUtility.debug("redraw", drawPos);
								adapter.getView(drawPos, v, adapterView);
							}
						}
						
						/*
			            int count = view.getChildCount();
			            
			            ListAdapter la = view.getAdapter();
			            
			            for(int i = 0; i < count; i++) {
			                View convertView = (View) view.getChildAt(i);
			                if(convertView.getTag(AQuery.TAG_SCROLL_LISTENER) != null){
			                	la.getView(first + i, convertView, view);	                	
			                	convertView.setTag(AQuery.TAG_SCROLL_LISTENER, null);
			                }
			            }
						*/
						
						
						//adapter.getView(pos, convertView, adapterView);
						
					}
					
					
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
					AQUtility.debug("on nothing");
				}
		
		
			});
			init = true;
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
			
			AQUtility.debug("yes", position + ":" + from + "." + to);
			convertView.setTag(AQuery.TAG_LAYOUT, position);
			
			return false;
		}
		
		AQUtility.debug("no", position + ":" + from + "." + to);
		convertView.setTag(AQuery.TAG_LAYOUT, null);
		return true;
		
	}
	
	
	public void renderPhotos(String url, XmlDom xml, AjaxStatus status) {
		
		if(xml == null) return;
		
		List<Photo> entries = convertAll(xml);
	
		listAq = new AQuery(this);
		
		ArrayAdapter<Photo> aa = new ArrayAdapter<Photo>(this, R.layout.gallery_item, entries){
			
			public View getView(int position, View convertView, ViewGroup parent) {
				
				if(convertView == null){
					convertView = getLayoutInflater().inflate(R.layout.gallery_item, parent, false);
				}
				
				Photo photo = getItem(position);
				
				AQuery aq = listAq.recycle(convertView);
				
				aq.id(R.id.name).text(photo.title);
				
				String tbUrl = photo.tb;
			
				
				
				if(!shouldDelayG(position, convertView, parent, tbUrl)){
					aq.id(R.id.tb).image(tbUrl);
					aq.id(R.id.text).text(photo.title).gone();
				}else{
					aq.id(R.id.tb).clear();
					aq.id(R.id.text).text(photo.title).visible();
				}
				
				
				return convertView;
				
			}
			
			
		};
		
		aq.id(R.id.gallery).adapter(aa);
		
		
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
