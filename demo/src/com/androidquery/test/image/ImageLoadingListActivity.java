package com.androidquery.test.image;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SlidingDrawer;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.test.RunSourceActivity;

public class ImageLoadingListActivity extends RunSourceActivity {

	protected AQuery listAq;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		work();	
	}
	
	protected int getContainer(){
		return R.layout.image_list_activity;
	}
	
	public void work(){
	    
        String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0&rsz=8";        
        aq.progress(R.id.progress).ajax(url, JSONObject.class, this, "renderNews");
	        
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
		addItems(ja, items);
		addItems(ja, items);
		addItems(ja, items);
		addItems(ja, items);
		
		listAq = new AQuery(this);
		
		ArrayAdapter<JSONObject> aa = new ArrayAdapter<JSONObject>(this, R.layout.content_item_s, items){
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				
				if(convertView == null){
					convertView = getLayoutInflater().inflate(R.layout.content_item_s, null);
				}
				
				JSONObject jo = getItem(position);
				
				AQuery aq = listAq.recycle(convertView);
				aq.id(R.id.name).text(jo.optString("titleNoFormatting", "No Title"));
				aq.id(R.id.meta).text(jo.optString("publisher", ""));
				
				String tb = jo.optJSONObject("image").optString("tbUrl");
				aq.id(R.id.tb).progress(R.id.progress).image(tb, true, true, 0, 0, null, AQuery.FADE_IN_NETWORK, 1.0f);
				
				
				return convertView;
				
			}
		};
		
		aq.id(R.id.list).adapter(aa);
		
	}
	
	
	@Override
	protected void runSource(){
		
		//AQUtility.invokeHandler(this, type, false, null);
	}
	
	@Override
	public void onBackPressed(){
		
		SlidingDrawer sd = (SlidingDrawer) findViewById(R.id.slidingDrawer);
		if(sd != null && sd.isOpened()){
			sd.animateClose();
			return;
		}else{
			super.onBackPressed();
		}
	}
	
	
}
