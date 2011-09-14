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

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.util.AQUtility;

public class RunSourceActivity extends Activity {

	protected AQuery aq;
	
	protected int getContainer(){
		return R.layout.source_activity;
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
				
		aq = new AQuery(this);
		
		setContentView(getContainer());
		
		String source = getSource();
		
		String title = getSourceTitle();
		
		aq.id(R.id.name).text(title);
		aq.id(R.id.code).text(source);
		aq.id(R.id.go_run).clicked(this, "runSource");
		
		//AQUtility.debug("source", source);
		
	}
	
	public void runSource(View view){
		
		AQUtility.debug("run");
		
		runSource();
	}
	
	protected void runSource(){
		
	}
	
	protected void showProgress(boolean show){
		
		aq.id(R.id.progress);
		
		if(show){
			aq.visible();
		}else{
			aq.gone();
		}
		
	}
	
	protected void showResult(JSONObject result){
		
		String str = null;
		
		if(result != null){
			try {
				str = result.toString(2);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		aq.id(R.id.result).visible().text(str);
		
	}
	
	protected void showResult(JSONArray result){
		
		String str = null;
		
		if(result != null){
			try {
				str = result.toString(2);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		aq.id(R.id.result).visible().text(str);
		
	}
	
	protected void showResult(Object result){
		
		aq.id(R.id.result).visible().text(result + "");
	}
	
	protected void showResult(int code, Object msg){
		
		showResult(code + ":" + msg);
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
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return source;
		
		
		/*
		String name = getIntent().getStringExtra("type");
		
		if(sourceMap == null){
			sourceMap = loadMap();			
		}
		
		return sourceMap.get(name);
		
		*/
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
	
}
