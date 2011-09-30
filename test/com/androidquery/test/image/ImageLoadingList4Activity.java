package com.androidquery.test.image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.test.RunSourceActivity;
import com.androidquery.test.TQuery;
import com.androidquery.util.AQUtility;
import com.androidquery.util.XmlDom;

public class ImageLoadingList4Activity extends ImageLoadingListActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		
			
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
				
				Bitmap placeholder = aq.getCachedImage(R.drawable.image_ph);
				
				if(aq.shouldDelay(convertView, parent, tbUrl, 10.0f)){
					aq.id(R.id.tb).image(placeholder, 0.75f);
				}else{
					aq.id(R.id.tb).image(tbUrl, true, true, 0, 0, placeholder, 0, 0.75f);
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
