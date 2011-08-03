package com.androidquery.test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;

public class RunSourceActivity extends Activity {

	protected AQuery aq;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
				
		aq = new AQuery(this);
		
		setContentView(R.layout.source_activity);
		
		String source = getSource();
		
		String title = getSourceTitle();
		
		aq.id(R.id.name).text(title);
		aq.id(R.id.code).text(source);
		aq.id(R.id.go_run).clicked(this, "runSource");
		
		AQUtility.debug("source", source);
		
	}
	
	public void runSource(View view){
		
		AQUtility.debug("run");
		
		runSource();
	}
	
	protected void runSource(){
		
	}
	
	protected void showResult(Object result){
		aq.id(R.id.result).text(result + "");
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
			source = new String(AQUtility.toBytes(is));
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
