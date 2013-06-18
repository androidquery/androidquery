package com.androidquery.test;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.service.MarketService;
import com.androidquery.util.AQUtility;

public class IntentListActivity extends ListActivity {

	private boolean debug = false;
	private AQuery aq;
	private String type;
	
	private static boolean init;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		if(!init){
			AQUtility.setDebug(true);
			AQUtility.setCacheDir(null);
			BitmapAjaxCallback.setPixelLimit(600 * 600);
			BitmapAjaxCallback.setCacheLimit(200);
			BitmapAjaxCallback.setIconCacheLimit(100);
			BitmapAjaxCallback.setMaxPixelLimit(10000000);
			init = true;
			ErrorReporter.installReporter(getApplicationContext());
		}
		
		super.onCreate(savedInstanceState);
		
		type = getIntent().getStringExtra("type");
		
		
		aq = new AQuery(this);		
		setContentView(R.layout.empty_list);
		aq.id(android.R.id.list).adapter(getAA()).itemClicked(this, "itemClicked");
		
		if(type == null && debug){
			forward();
		}

		AQUtility.debug("on create");
		
		
		if(isTaskRoot()){			
		    AQUtility.debug("task root start version check");
			MarketService ms = new MarketService(this);
			ms.level(MarketService.MINOR).checkVersion();
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
		}else if("location".equals(type)){
			return new int[]{R.array.location_names, R.array.location_values};
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
		
		for(int i = 0; i < names.length; i++){
			String name = names[i];
			String value = values[i];
			if(value.startsWith("http")){
				list.add(new ActivityItem(null, name, value, null));
			}else{
				String[] vs = value.split(":");
				String meta = null;
				if(vs.length > 2){
					meta = vs[2];
				}
				list.add(makeActivity(vs[0], name, vs[1], meta));
			}
			
			
		}
		
		if(type == null && (TestUtility.isTestDevice(this) || TestUtility.isEmulator())){
			
			list.add(makeActivity("com.androidquery.test.AdhocActivity", "Ad Hoc Debug", "", null));
			list.add(makeActivity("com.androidquery.test.AdhocActivity2", "Ad Hoc Debug2", "", null));
		}
		
		
		ArrayAdapter<ActivityItem> result = new ArrayAdapter<ActivityItem>(this, R.layout.list_item, list){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				
				if(convertView == null){
					convertView = getLayoutInflater().inflate(R.layout.list_item, null);
				}
				
				ActivityItem ai = (ActivityItem) getItem(position);
				AQuery aq = new AQuery(convertView);
				
				String text = ai.getName();
				String meta = ai.getMeta();
				
				if(meta != null){
					text += "   <small><small><font color=\"red\">" + meta + "</font></small></small>";
				}
				
				Spanned span = Html.fromHtml(text);
				
				aq.id(R.id.name).text(span);
				//aq.id(R.id.meta).text(ai.getMeta());
				
				return convertView;
			}
		};
		
		
		return result;
	}
	
	private ActivityItem makeActivity(String cls, String name, String type, String meta){
		
		Class c = null;
		
		try {
			c = Class.forName(cls);			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ActivityItem(c, name, type, meta);
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
    
	@Override
	public void onDestroy(){
		
		aq.dismiss();
		
		super.onDestroy();
	}
	
	
	
	private void invokeIntent(ActivityItem ai){
		
		Class<?> cls = ai.getActivityClass();
		String type = ai.getType();
		
		Intent intent = new Intent(this, cls);
		intent.putExtra("type", type);
		
		startActivity(intent);
	}
	
}
