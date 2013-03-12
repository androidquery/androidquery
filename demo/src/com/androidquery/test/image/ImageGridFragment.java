package com.androidquery.test.image;


import java.util.ArrayList;
import java.util.List;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.test.image.ImageLoadingList4Activity.Photo;
import com.androidquery.util.AQUtility;
import com.androidquery.util.XmlDom;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class ImageGridFragment extends Fragment{

	private AQuery aq;
	private AQuery aq2;
	
	private String topic;
	private List<Photo> photos;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
    	View view = inflater.inflate(R.layout.image_grid_fragment, container, false);        	
		
    	aq = new AQuery(getActivity(), view);
    	aq2 = new AQuery(getActivity());
    	
		return view;
		
    }
    
    @Override
	public void onActivityCreated(Bundle savedInstanceState){
    	
    	
    	super.onActivityCreated(savedInstanceState);
    	
    	topic = getArguments().getString("topic");
    	
    }
    
    private boolean inited;
    public void init(){
    	
    	if(aq == null || inited) return;
    	
    	inited = true;
    	
    	refresh();
    }
    
    public void refresh(){
  
    	ajaxPhotos();
    	
    }
    
    private void ajaxPhotos(){
    	
		String url = "https://picasaweb.google.com/data/feed/api/all?q=" + topic +"&max-results=100";
    	aq.progress(R.id.progress).ajax(url, XmlDom.class, 0, this, "photosCb");
    }
    
    
    public void photosCb(String url, XmlDom xml, AjaxStatus status){
    	
    	if(xml != null){
    		
    		photos = convertAll(xml);
    		render(photos);
    		
    		
    	}
    	
    }
	
    private void render(List<Photo> entries){
    	
    	AQUtility.debug("render setup");
    	
		ArrayAdapter<Photo> aa = new ArrayAdapter<Photo>(getActivity(), R.layout.grid_item2, entries){
			
			public View getView(int position, View convertView, ViewGroup parent) {
				
				if(convertView == null){
					convertView = aq.inflate(convertView, R.layout.grid_item2, parent);
				}
				
				Photo photo = getItem(position);
				
				AQuery aq = aq2.recycle(convertView);
				
				String tbUrl = photo.tb;
				
				//Bitmap placeholder = aq.getCachedImage(R.drawable.image_ph);
				
				if(aq.shouldDelay(position, convertView, parent, tbUrl)){
							
					//aq.id(R.id.tb).image(placeholder);
					aq.id(R.id.tb).clear();
					
				}else{
					
					aq.id(R.id.tb).image(tbUrl, true, true, 200, R.drawable.image_missing, null, 0, 0);
				}
				
				return convertView;
				
			}
			
			
		};
		
		aq.id(R.id.grid).adapter(aa);
    	
    	
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
			//tb = tbs.get(0).attr("url");
			tb = tbs.get(tbs.size() - 1).attr("url");
		}
		
		tb = tb.replaceAll("https:", "http:");
		
		Photo photo = new Photo();
		photo.url = url;
		photo.tb = tb;
		photo.title = title;
		photo.author = author;
		
		return photo;
	}
    
	class Photo{
		
		String tb;
		String url;
		String title;
		String author;
	}
}
