package com.androidquery.test.image;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;
import com.androidquery.util.XmlDom;

public class ImageLoadingList4Activity extends ImageLoadingListActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		aq.id(R.id.list).scrolled(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				AQUtility.debug("forward", "onScrollStateChanged");
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				AQUtility.debug("forward", "onScroll");
			}
		});
			
	}
	
	
	
	public void work(){
	    
		
		AQUtility.cleanCacheAsync(this, 0, 0);
		BitmapAjaxCallback.clearCache();
		
        String url = "https://picasaweb.google.com/data/feed/base/featured?max-results=48";
		aq.progress(R.id.progress).ajax(url, XmlDom.class, this, "renderPhotos");
	    
		View list = aq.id(R.id.list).getView();
		
		if(list instanceof ListView){
		
			ListView lv = (ListView) list;
			
			for(int i = 0; i < 5; i++){
				TextView tv = new TextView(this);
				tv.setText("Header View " + i);
				lv.addHeaderView(tv);
			}
			
			
			for(int i = 0; i < 5; i++){
				TextView tv = new TextView(this);
				tv.setText("Footer View " + i);
				lv.addFooterView(tv);
			}
			
		}
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
	
	public void scrolledBottom(AbsListView view, int scrollState){
		
		Toast toast = Toast.makeText(this, "ScrolledBottom", Toast.LENGTH_SHORT);
		toast.show();
	}
	
	
	
	public void renderPhotos(String url, XmlDom xml, AjaxStatus status) {
	
		if(xml == null) return;
		
		List<Photo> entries = convertAll(xml);
	
		listAq = new AQuery(this);
		
		ArrayAdapter<Photo> aa = new ArrayAdapter<Photo>(this, R.layout.photo_item, entries){
			
			public View getView(int position, View convertView, ViewGroup parent) {
				
				if(convertView == null){
					convertView = getLayoutInflater().inflate(R.layout.photo_item, parent, false);
				}
				
				Photo photo = getItem(position);
				
				AQuery aq = listAq.recycle(convertView);
				
				aq.id(R.id.name).text(photo.title);
				aq.id(R.id.meta).text(photo.author);
				
				String tbUrl = photo.tb;
				
				Bitmap placeholder = aq.getCachedImage(R.drawable.image_ph);
				
				if(aq.shouldDelay(position, convertView, parent, tbUrl)){
							
					aq.id(R.id.tb).image(placeholder);
				}else{
					
					aq.id(R.id.tb).image(tbUrl, true, true, 0, R.drawable.image_missing, placeholder, AQuery.FADE_IN_NETWORK, 0);
				}
				
				return convertView;
				
			}
			
			
		};
		
		aq.id(R.id.list).adapter(aa);
		
		//aq.scrolledBottom(this, "scrolledBottom");
		
	}
	
		
	class Photo{
		
		String tb;
		String url;
		String title;
		String author;
	}
	
	
}
