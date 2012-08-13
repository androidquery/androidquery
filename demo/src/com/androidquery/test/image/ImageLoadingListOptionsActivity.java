package com.androidquery.test.image;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;
import com.androidquery.util.XmlDom;

public class ImageLoadingListOptionsActivity extends ImageLoadingListActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
	}
	
	protected int getContainer(){
		return R.layout.image_list_options_activity;
	}
	
	public void work(){
	    
		updateOptions();
		
		SlidingDrawer sd = (SlidingDrawer) findViewById(R.id.slidingDrawer);
		sd.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			
			@Override
			public void onDrawerClosed() {
				updateOptions();
			}
		});
		
	}
	
	private void ajax(){
		
		AQUtility.cleanCacheAsync(this, 0, 0);
		BitmapAjaxCallback.clearCache();
		
		aq.id(R.id.list).adapter((Adapter) null);
		
        String url = "https://picasaweb.google.com/data/feed/base/featured?max-results=48";
		aq.progress(R.id.progress).ajax(url, XmlDom.class, this, "renderPhotos");
	     
	}
	
	private Bitmap preset;
	private float ratio;
	private int animation;
	private boolean delay;
	private boolean progress;
	private boolean memcache = true;
	
	private void updateOptions(){
		
		if(aq.id(R.id.preset_cb).isChecked()){
			preset = aq.getCachedImage(R.drawable.image_ph);
		}else{
			preset = null;
		}
		
		if(aq.id(R.id.ratio_cb).isChecked()){
			ratio = 0.75f;
		}else{
			ratio = 0;
		}
		
		if(aq.id(R.id.animation_cb).isChecked()){
			animation = AQuery.FADE_IN_NETWORK;
		}else{
			animation = 0;
		}

		if(aq.id(R.id.delay_cb).isChecked()){
			delay = true;
		}else{
			delay = false;
		}
		
		if(aq.id(R.id.progress_cb).isChecked()){
			progress = true;
		}else{
			progress = false;
		}
		
		if(aq.id(R.id.memcache_cb).isChecked()){
			memcache = true;
		}else{
			memcache = false;
		}
		
		ajax();
			
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
	
	public void scrolledBottom(AbsListView view, int scrollState){
		
		Toast toast = Toast.makeText(this, "ScrolledBottom", Toast.LENGTH_SHORT);
		toast.show();
	}
	
	public void renderPhotos(String url, XmlDom xml, AjaxStatus status) {
	
		if(xml == null) return;
		
		List<Photo> entries = convertAll(xml);
	
		ArrayAdapter<Photo> aa = new ArrayAdapter<Photo>(this, R.layout.photo_item, entries){
			
			public View getView(int position, View convertView, ViewGroup parent) {
				
				if(convertView == null){
					convertView = getLayoutInflater().inflate(R.layout.photo_item, parent, false);
				}
				
				Photo photo = getItem(position);
				
				AQuery aq = new AQuery(convertView);
				
				aq.id(R.id.name).text(photo.title);
				aq.id(R.id.meta).text(photo.author);
				
				String tbUrl = photo.tb;
				
				aq.id(R.id.tb);
				
				if(delay && aq.shouldDelay(convertView, parent, tbUrl, 0)){
					if(preset != null){
						aq.image(preset, ratio);
					}else{
						aq.clear();					
						if(progress){
							aq.invisible();
							aq.id(R.id.pbar).visible();
						}
					}
				}else{
					if(progress){
						aq.progress(R.id.pbar);
					}
					aq.image(tbUrl, memcache, true, 0, R.drawable.image_missing, preset, animation, ratio);
				}
				
				return convertView;
				
			}
			
			
		};
		
		aq.id(R.id.list).adapter(aa);
		
	}
	
		
	class Photo{
		
		String tb;
		String url;
		String title;
		String author;
	}
	
	
}
