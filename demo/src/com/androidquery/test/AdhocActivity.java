package com.androidquery.test;

import org.json.JSONObject;

import android.app.Activity;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

public class AdhocActivity extends Activity {

	private AQuery aq;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.adhoc_activity);
		
		aq = new AQuery(this);
		
		aq.id(R.id.text).text("point 1");
		
		work();
		
	}
	
	private void work(){
		
		Log.i("test", "point 1");
		
		String url = "http://www.google.com/uds/GnewsSearch?q=Obama&v=1.0";
		aq.ajax(url, JSONObject.class, this, "jsonCb");
		
	}
	
	public void jsonCb(String url, JSONObject jo, AjaxStatus status){
		
		Log.i("test", "point 2");
		
		aq.id(R.id.text).text("point 2");
		
	}
	
	protected int getContainer(){
		return R.layout.adhoc_activity;
	}
	
	
	

}
