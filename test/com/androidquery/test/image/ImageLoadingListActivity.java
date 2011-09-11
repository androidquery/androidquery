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

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.test.RunSourceActivity;

public class ImageLoadingListActivity extends RunSourceActivity {

	private String type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		
		type = getIntent().getStringExtra("type");
		
		async_json();	
	}
	
	protected int getContainer(){
		return R.layout.image_list_activity;
	}
	
	public void async_json(){
	    
        String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0&rsz=8";        
        aq.ajax(url, JSONObject.class, this, "renderNews");
	        
	}
	
	public View inflate(Context context, int id){
		
		View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(id,null);
		return view;
		
	}
	
	
	public void renderNews(String url, JSONObject json, AjaxStatus status) {
	
		if(json == null) return;
		
		JSONArray ja = json.optJSONObject("responseData").optJSONArray("results");
		if(ja == null) return;
		
		List<JSONObject> items = new ArrayList<JSONObject>();
		for(int i = 0 ; i < ja.length(); i++){
			JSONObject jo = ja.optJSONObject(i);
			if(jo.has("image")){
				items.add(jo);
			}
		}
		
		ArrayAdapter<JSONObject> aa = new ArrayAdapter<JSONObject>(this, R.layout.content_item_s, items){
			
			@Override
			public View getView(int position, View view, ViewGroup parent) {
				
				if(view == null){
					view = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.content_item_s, null);
				}
				
				JSONObject jo = getItem(position);
				
				AQuery aq = new AQuery(view);
				aq.id(R.id.name).text(jo.optString("titleNoFormatting", "No Title"));
				aq.id(R.id.meta).text(jo.optString("publisher", ""));
				
				JSONObject image = jo.optJSONObject("image");
				String tb = null;
				if(image != null){
					tb = image.optString("tbUrl");
					aq.id(R.id.tb).image(tb, true, true, 0, 0, null, AQuery.FADE_IN, 1.0f);
				}else{
					aq.id(R.id.tb).clear();
				}
				
				return view;
				
			}
		};
		
		aq.id(R.id.list).adapter(aa);
		
	}
	
	
	@Override
	protected void runSource(){
		
		//AQUtility.invokeHandler(this, type, false, null);
	}
	
	public void image_simple(){
		
	}
	
	
}
