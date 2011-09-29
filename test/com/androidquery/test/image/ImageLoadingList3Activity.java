package com.androidquery.test.image;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.test.RunSourceActivity;
import com.androidquery.util.AQUtility;

public class ImageLoadingList3Activity extends RunSourceActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		async_json();	
	}
	
	protected int getContainer(){
		return R.layout.image_list_activity3;
	}
	
	public void async_json(){
	    
        String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0&rsz=8";        
        aq.progress(R.id.progress).ajax(url, JSONObject.class, 0, this, "renderNews");
	        
	}
	
	
	private void addItems(JSONArray ja, List<JSONObject> items){
		for(int i = 0 ; i < ja.length(); i++){
			JSONObject jo = ja.optJSONObject(i);
			if(jo.has("image")){
				items.add(jo);
			}
		}
	}
	
	
	public void renderNews(String url, JSONObject json, AjaxStatus status) {
	
		if(json == null) return;
		
		JSONArray ja = json.optJSONObject("responseData").optJSONArray("results");
		if(ja == null) return;
		
		List<JSONObject> items = new ArrayList<JSONObject>();
		for(int i = 0; i < 10; i++){
			addItems(ja, items);
		}
		
		ArrayAdapter<JSONObject> aa = new ArrayAdapter<JSONObject>(this, R.layout.content_item_s, items){
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				
				ViewHolder holder;
				
				if(convertView == null){
					holder = new ViewHolder();
                 
					//convertView = mInflater.inflate(R.layout.photoitem, null);
					convertView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.content_item_s, null);
					 
					holder.imageview = (ImageView) convertView.findViewById(R.id.tb);
	                holder.progress = (ProgressBar) convertView.findViewById(R.id.progress);
	
	                convertView.setTag(holder);
	            }else{
	            	holder = (ViewHolder) convertView.getTag();
	            }
				
				/*
				if(view == null){
					view = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.content_item_s, null);
				}
				*/
				
				
				JSONObject jo = getItem(position);
				
				AQuery aq = new AQuery(convertView);
				aq.id(R.id.name).text(jo.optString("titleNoFormatting", "No Title"));
				aq.id(R.id.meta).text(jo.optString("publisher", ""));
				
				String tb = jo.optJSONObject("image").optString("tbUrl");
				aq.id(R.id.tb).progress(R.id.progress).image(tb, true, true, 0, 0, null, 0, 1.0f);
				
				//AQUtility.debug("iv", aq.id(R.id.tb).getView());
				//AQUtility.debug("pb", aq.id(R.id.progress).getView());
				
				/*
				AQuery aq = new AQuery(view);
				aq.id(R.id.name).text(jo.optString("titleNoFormatting", "No Title"));
				aq.id(R.id.meta).text(jo.optString("publisher", ""));
				
				String tb = jo.optJSONObject("image").optString("tbUrl");
				aq.id(R.id.tb).progress(R.id.progress).image(tb, true, true, 0, 0, null, AQuery.FADE_IN, 1.0f);
				*/
				
				return convertView;
				
			}
		};
		
		aq.id(R.id.list).adapter(aa);
		
	}
	
	class ViewHolder {
        ImageView imageview;
        ProgressBar progress;
	}
	
	@Override
	protected void runSource(){
		
		//AQUtility.invokeHandler(this, type, false, null);
	}
	
	
	
	
}
