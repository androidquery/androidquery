package com.androidquery.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;

public class IntentListActivity extends ListActivity {

	private AQuery aq;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		AQUtility.setDebug(true);
		
		super.onCreate(savedInstanceState);
				
		aq = new AQuery(this);
		
		
		setContentView(R.layout.empty_list);
		
		aq.id(android.R.id.list).adapter(getAA()).itemClicked(this, "itemClicked");
		

	}
	
	private int[] getResList(){
		
		String type = getIntent().getStringExtra("type");
		
		if("async".equals(type)){
			return new int[]{R.array.async_names, R.array.async_values};
		}else{
			return new int[]{R.array.top_names, R.array.top_values};
		}
		
		
	}
	
	private List<ActivityItem> list;
	
	private ArrayAdapter<ActivityItem> getAA(){
		
		list = new ArrayList<ActivityItem>();
		
		
		int[] ids = getResList();
		
		
		String[] names = getResources().getStringArray(ids[0]);
		String[] values = getResources().getStringArray(ids[1]);
		
		AQUtility.debug("names", Arrays.asList(names));
		AQUtility.debug("values", Arrays.asList(values));
		
		
		for(int i = 0; i < names.length; i++){
			String name = names[i];
			String[] vs = values[i].split(":");
			Class<?> cls = null;
			String type = null;
			try {
				cls = Class.forName(vs[0]);
				if(vs.length > 1){
					type = vs[1];
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			list.add(new ActivityItem(cls, name, type));
		}
		
		
		return new ArrayAdapter<ActivityItem>(this, R.layout.list_item, list);
	}
	
	public void itemClicked(AdapterView<?> parent, View view, int position, long id){
		
		ActivityItem ai = list.get(position);		
		invokeIntent(ai);
	}
	
	private void invokeIntent(ActivityItem ai){
		
		Class<?> cls = ai.getActivityClass();
		String type = ai.getType();
		
		Intent intent = new Intent(this, cls);
		intent.putExtra("type", type);
		
		AQUtility.debug("start", cls + ":" + type);
		
		startActivity(intent);
	}
	
}
