package com.androidquery.test.image;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView.ScaleType;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.test.RunSourceActivity;
import com.androidquery.test.TQuery;
import com.androidquery.util.AQUtility;
import com.androidquery.util.XmlDom;

public class ImageLoadingList4Activity extends RunSourceActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		aq.id(R.id.code_area).gone();
		
		AQUtility.cleanCacheAsync(this, 0, 0);
		//AQUtility.cleanCache(AQUtility.getCacheDir(this), 0, 0);
		BitmapAjaxCallback.clearCache();
		
		async_json();	
	}
	
	protected int getContainer(){
		return R.layout.image_list_activity;
	}
	
	public void async_json(){
	    
        //String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0&rsz=8";        
        String url = "https://picasaweb.google.com/data/feed/base/featured?max-results=24";
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
		
		
		Photo photo = new Photo();
		photo.url = url;
		photo.tb = tb;
		photo.title = title;
		photo.author = author;
		
		AQUtility.debug("url", url);
		AQUtility.debug("tb", tb);
		AQUtility.debug("title", title);
		AQUtility.debug("author", author);
		
		return photo;
	}
	
	
	public void renderPhotos(String url, XmlDom xml, AjaxStatus status) {
	
		if(xml == null) return;
		
		List<Photo> entries = convertAll(xml);
		
		
		ArrayAdapter<Photo> aa = new ArrayAdapter<Photo>(this, R.layout.content_item_s, entries){
			
			@Override
			public View getView(int position, View view, ViewGroup parent) {
				
				AQUtility.time("gv");
				
				if(view == null){
					view = getLayoutInflater().inflate(R.layout.content_item_s, null);
				}
				
				Photo photo = getItem(position);
				
				//AQuery aq = new AQuery(view);
				TQuery aq = new TQuery(view);
				
				
				
				aq.id(R.id.name).text(photo.title);
				aq.id(R.id.meta).text(photo.author);
				
				String tb = photo.tb;
				
				int state = aq.getScrollState(parent);
				//AQUtility.debug("state", state);
				
				aq.id(R.id.tb);
				
				
				
				Bitmap ph = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.image_ph);
				
				
				/*
				Bitmap ph = null;
				
				if(state != OnScrollListener.SCROLL_STATE_FLING){
					aq.image(tb, true, true, 0, 0, ph, 0, 1.0f);
				}else{
					
					
					Bitmap bm = BitmapAjaxCallback.getMemoryCached(tb, 0);
					if(bm != null){
						aq.image(tb, true, true, 0, 0, ph, 0, 1.0f);						
					}else{											
						aq.clear().image(ph).getImageView().setScaleType(ScaleType.FIT_XY);
					}
				}*/
				
				aq.image(R.drawable.image_ph);
				
				AQUtility.timeEnd("gv", 0);
				
				return view;
				
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
