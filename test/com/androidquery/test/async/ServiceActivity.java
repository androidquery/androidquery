package com.androidquery.test.async;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.service.MarketService;
import com.androidquery.test.RunSourceActivity;
import com.androidquery.util.AQUtility;
import com.androidquery.util.XmlDom;

public class ServiceActivity extends RunSourceActivity {

	private String type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		type = getIntent().getStringExtra("type");
			
	}
	
	@Override
	protected void runSource(){
		
		AQUtility.debug("run", type);
		
		AQUtility.invokeHandler(this, type, false, null);
	}
	
	public void service_version(){
	    		
		showProgress(true);
		
		MarketService ms = new MarketService(this){
			
			@Override
			protected void callback(String url, JSONObject jo, AjaxStatus status) {
				showProgress(false);
				super.callback(url, jo, status);
			}
			
		};
		
		ms.checkVersion();
	        
	}	
	
	public void service_version2(){
		
		//MarketService ms = new MarketService(this);
		//ms.locale(Locale.TRADITIONAL_CHINESE.toString()).checkVersion();
	      
		String url = "https://market.android.com/details?id=com.androidquery&hl=zh-TW";
		
		aq.ajax(url, String.class, this, "stringCb");
	}	
	
	public void stringCb(String url, String html, AjaxStatus status){
		
		showResult(html);
		
	}
	
}
