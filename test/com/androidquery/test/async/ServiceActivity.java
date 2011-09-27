package com.androidquery.test.async;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

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
			
		if(type.equals("service_version_locale")){
			aq.id(R.id.spinner).visible().itemSelected(this, "localeSelected");			
		}
	}
	
	
	public void localeSelected(AdapterView<?> parent, View v, int pos, long id) {
        
		service_version_locale();
	}
	
	
	@Override
	protected int getContainer(){
		return R.layout.service_activity;
	}
	
	
	@Override
	protected void runSource(){
		
		AQUtility.debug("run", type);
		
		AQUtility.invokeHandler(this, type, false, null);
	}
	
	public void service_version_force(){
	    		
		showProgress(true);
		
		MarketService ms = new MarketService(this){
			
			@Override
			protected void callback(String url, JSONObject jo, AjaxStatus status) {
				showProgress(false);
				super.callback(url, jo, status);
			}
			
		};
		
		ms.force(true).checkVersion();
	        
	}	
	
	public void service_version_locale(){
		
		String locale = aq.id(R.id.spinner).getSelectedItem().toString();
		
		MarketService ms = new MarketService(this);
		ms.locale(locale).force(true).checkVersion();
	      
	}
	
	public void service_version_auto(){
		
		showProgress(true);
		
		MarketService ms = new MarketService(this){
			
			@Override
			protected void callback(String url, JSONObject jo, AjaxStatus status) {
				showProgress(false);
				super.callback(url, jo, status);
			}
			
		};
		
		ms.checkVersion();
	    
		showResult("Nothing happends if current version is up to date.", null);
		
	}	
	
	
	
	public void stringCb(String url, String html, AjaxStatus status){
		
		showResult(html, status);
		
	}
	
}
