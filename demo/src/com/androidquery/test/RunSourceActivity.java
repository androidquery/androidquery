package com.androidquery.test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.androidquery.util.XmlDom;

public class RunSourceActivity extends Activity {

	protected AQuery aq;
	protected String type;
	
	protected int getContainer(){
		return R.layout.source_activity;
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		if(AQuery.SDK_INT >= 11){
			//setTheme(0x0103006b);
			//setTheme(0x0103006e);
		}
		
		super.onCreate(savedInstanceState);
				
		
		aq = new AQuery(this);
		
		type = getIntent().getStringExtra("type");
		
		setContentView(getContainer());
		
		String source = getSource();
		
		String title = getSourceTitle();
		
		aq.id(R.id.name).text(title);
		aq.id(R.id.code).text(source);
		aq.id(R.id.go_run).clicked(this, "runSource");
		
		//AQUtility.debug("source", source);
		TestUtility.flurryEvent(this, type);
	}
	
	
	public void runSource(View view){
		
		AQUtility.debug("run");
		
		runSource();
	}
	
	protected void runSource(){
		
	}
	
	/*
	protected void showProgress(boolean show){
		
		aq.id(R.id.progress);
		
		if(show){
			aq.visible();
		}else{
			aq.gone();
		}
		
	}*/
	
	protected void showResult(JSONObject result){
		showResult(result, null);
	}
	
	protected void showResult(JSONObject result, AjaxStatus status){
		
		String str = null;
		
		if(result != null){
			try {
				str = result.toString(2);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		showMeta(status);
		
		aq.id(R.id.result).visible().text(str);
		
	}
	
	private static String[] SOURCE = {"", "NETWORK", "DATASTORE", "FILE", "MEMORY"};
	protected void showMeta(AjaxStatus status){
		if(status != null){
			String meta = "Source:" + SOURCE[status.getSource()] + "\nResponse Code:" + status.getCode() + "\nDuration:" + status.getDuration() + "ms";
			if(status.getCode() != 200){
				meta = meta + "\nMessage:" + status.getMessage();
			}
			showTextResult(meta);
		}
	}
	
	
	
	protected void showResult(JSONArray result, AjaxStatus status){
		
		String str = null;
		
		if(result != null){
			try {
				str = result.toString(2);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		showMeta(status);
		
		aq.id(R.id.result).visible().text(str);
		
	}
	
	protected void showTextResult(Object result){
		
		aq.id(R.id.text_result).visible().text(result + "");
	}
	
	protected void showResult(XmlDom xml, AjaxStatus status){
		showMeta(status);
		if(xml != null){
			aq.id(R.id.result).visible().text(xml.toString(2));
		}
	}
	
	protected void appendResult(Object result){
		
		aq.id(R.id.result).visible();
		String str = aq.getText().toString();
		
		aq.text(str + " " + result);
	}
	
	
	protected void showResult(Object result, AjaxStatus status){
		showMeta(status);
		aq.id(R.id.result).visible().text(result + "");
	}
	
	protected void showResult(int code, Object msg){
		
		showResult(code + ":" + msg, null);
	}
	
	private static Map<String, String> titleMap;
	
	private String getSourceTitle(){
		
		String name = getIntent().getStringExtra("type");
		
		if(titleMap == null){
			titleMap = loadMap();
		}
		
		return titleMap.get(name);
	}
	
	private String getSource(){
		
		String source = "Failed to load source.";
		
		try{
			String name = getIntent().getStringExtra("type");
		
			InputStream is = getClassLoader().getResourceAsStream("com/androidquery/test/source/" + name);
		
			if(is != null){
				source = new String(AQUtility.toBytes(is));
			}
			
			//AQUtility.debug(name, source);
			
		}catch(Exception e){
			//e.printStackTrace();
		}
		
		return source;
		
	}
	
	
	private Map<String, String> loadMap(){
		
		String[] names = getResources().getStringArray(R.array.source_keys);
		String[] code = getResources().getStringArray(R.array.source_title);
		
		Map<String, String> result = new HashMap<String, String>();
		
		for(int i = 0; i < names.length; i++){
			result.put(names[i], code[i]);
		}
		
		return result;
	}
	
	
	public void onStart(){
	   super.onStart();
	   TestUtility.flurryStart(this);
	}
	
	public void onStop(){
	   super.onStop();
	   TestUtility.flurryStop(this);
	}
	
	@Override
	public void onDestroy(){
		
		aq.dismiss();
		
		super.onDestroy();
	}
}
