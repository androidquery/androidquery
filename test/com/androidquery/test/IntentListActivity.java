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
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.service.MarketService;
import com.androidquery.util.AQUtility;
import com.flurry.android.FlurryAgent;

public class IntentListActivity extends ListActivity {

	private boolean debug = false;
	private AQuery aq;
	private String type;
	
	private static boolean init;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		
		
		if(!init){
			AQUtility.setDebug(true);
			BitmapAjaxCallback.setPixelLimit(600 * 600);
			init = true;
		}
		
		super.onCreate(savedInstanceState);
		
		type = getIntent().getStringExtra("type");
		
		
		aq = new AQuery(this);		
		setContentView(R.layout.empty_list);
		aq.id(android.R.id.list).adapter(getAA()).itemClicked(this, "itemClicked");
		
		if(type == null && debug){
			forward();
		}

		if(isTaskRoot()){			
			MarketService ms = new MarketService(this);
			ms.checkVersion();
		}
	}
	
	public void onStart(){
	   super.onStart();
	   TestUtility.flurryStart(this);
	}
	
	public void onStop(){
	   super.onStop();
	   TestUtility.flurryStop(this);
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
		}else if("xml".equals(type)){
			return new int[]{R.array.xml_names, R.array.xml_values};
		}else if("service".equals(type)){
			return new int[]{R.array.service_names, R.array.service_values};
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
		
		//AQUtility.debug("count", names.length);
		
		
		for(int i = 0; i < names.length; i++){
			String name = names[i];
			String value = values[i];
			
			AQUtility.debug("name", name);
			AQUtility.debug("value", values[i]);
			
			
			if(value.startsWith("http")){
				list.add(new ActivityItem(null, name, value));
			}else{
				String[] vs = value.split(":");
				list.add(makeActivity(vs[0], name, vs[1]));
			}
			
			
		}
		
		
		return new ArrayAdapter<ActivityItem>(this, R.layout.list_item, list);
	}
	
	private ActivityItem makeActivity(String cls, String name, String type){
		
		Class c = null;
		
		try {
			c = Class.forName(cls);			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ActivityItem(c, name, type);
	}
	
	
	public void itemClicked(AdapterView<?> parent, View view, int position, long id){
		
		ActivityItem ai = list.get(position);	
	
		if(ai.isLink()){
			//openBrowser(this, "http://android-query.googlecode.com/svn/trunk/javadoc/com/androidquery/AbstractAQuery.html");
			openBrowser(this, ai.getType());
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
