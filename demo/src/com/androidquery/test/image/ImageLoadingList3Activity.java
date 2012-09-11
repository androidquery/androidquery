package com.androidquery.test.image;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxStatus;

public class ImageLoadingList3Activity extends ImageLoadingListActivity {

	
	
	public void work(){
	    
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
		
		
		listAq = new AQuery(this);
		
		ArrayAdapter<JSONObject> aa = new ArrayAdapter<JSONObject>(this, R.layout.content_item_s, items){
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				
				ViewHolder holder;
				
				
				
				if(convertView == null){
					holder = new ViewHolder();
					convertView = getLayoutInflater().inflate(R.layout.content_item_s, parent, false);
					holder.imageview = (ImageView) convertView.findViewById(R.id.tb);
	                holder.progress = (ProgressBar) convertView.findViewById(R.id.progress);
	                holder.name = (TextView) convertView.findViewById(R.id.name);
	                holder.meta = (TextView) convertView.findViewById(R.id.meta);
	                convertView.setTag(holder);
	            }else{
	            	holder = (ViewHolder) convertView.getTag();
	            }
				
				JSONObject jo = getItem(position);
				String tb = jo.optJSONObject("image").optString("tbUrl");
				
				AQuery aq = listAq.recycle(convertView);
				aq.id(holder.name).text(jo.optString("titleNoFormatting", "No Title"));
				aq.id(holder.meta).text(jo.optString("publisher", ""));
				aq.id(holder.imageview).progress(holder.progress).image(tb, true, true, 0, 0, null, 0, 1.0f);
				
				/*
				aq.id(R.id.tb).progress(R.id.progress);
				BitmapAjaxCallback cb = new BitmapAjaxCallback();
				cb.url(tb).ratio(AQuery.RATIO_PRESERVE);
				aq.image(cb);
				*/
				
				return convertView;
			}
		};
		
		aq.id(R.id.list).adapter(aa);
		
	}
	
	class ViewHolder {
        ImageView imageview;
        ProgressBar progress;
        TextView name;
        TextView meta;
	}
	
	@Override
	protected void runSource(){
		
		//AQUtility.invokeHandler(this, type, false, null);
	}
	
	
	
	
}
