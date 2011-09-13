package com.androidquery.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.util.AQUtility;

public class IntentListActivity extends ListActivity {

	private boolean debug = false;
	private AQuery aq;
	private String type;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		AQUtility.setDebug(true);
		
		super.onCreate(savedInstanceState);
		
		type = getIntent().getStringExtra("type");
		
		
		aq = new AQuery(this);		
		setContentView(R.layout.empty_list);
		aq.id(android.R.id.list).adapter(getAA()).itemClicked(this, "itemClicked");
		
		if(type == null && debug){
			forward();
		}

	}
	
	private void forward(){
		
		Intent intent = new Intent(this, AdhocActivity.class);
		this.startActivity(intent);
	}
	
	
	private int[] getResList(){
		
		
		if("async".equals(type)){
			return new int[]{R.array.async_names, R.array.async_values};
		}else if("image".equals(type)){
			return new int[]{R.array.image_names, R.array.image_values};
		}else if("auth".equals(type)){
			return new int[]{R.array.auth_names, R.array.auth_values};
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
		
		if("javadoc".equalsIgnoreCase(ai.getName())){
			openBrowser(this, "http://android-query.googlecode.com/svn/trunk/javadoc/com/androidquery/AbstractAQuery.html");
		}else{		
			invokeIntent(ai);
		}
	}
	
    public static boolean openBrowser(Activity act, String url) {
    
    	
    	try{
   
	    	if(url == null) return false;
	    	
	    	Uri uri = Uri.parse(url);
	    	Intent intent = new Intent(Intent.ACTION_VIEW, uri);	    	
	    	act.startActivity(intent);
    	
	    	return true;
    	}catch(Exception e){
    		return false;
    	}
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
