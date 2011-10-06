package com.androidquery.test.async;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.TQuery;
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
	
		PreferenceManager.getDefaultSharedPreferences(this).edit().putString("aqs.skip", null).commit();
		
		super.onCreate(savedInstanceState);
		
		type = getIntent().getStringExtra("type");
			
		if(type.equals("service_version_locale")){			
			aq.id(R.id.spinner).visible().setSelection(1).itemSelected(this, "localeSelected");	
			aq.id(R.id.go_run).gone();
		}
		
		showResult("The update message is fetched from the 'Recent Changes' field of your Android Market's development console with the correponsing locale.", null);
		
		
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
	    
		MarketService ms = new MarketService(this);
		ms.locale(Locale.getDefault().toString()).force(true).progress(R.id.progress).checkVersion();
	        
	}	
	
	public void service_version_locale(){
		
		String locale = aq.id(R.id.spinner).getSelectedItem().toString();
		
		MarketService ms = new MarketService(this);
		ms.locale(locale).force(true).progress(R.id.progress).checkVersion();
	    
	}
	
	public void service_version_auto(){
		
		MarketService ms = new MarketService(this);
		ms.progress(R.id.progress).checkVersion();
	    
		showResult("Nothing happends if current version is up to date.", null);
		
	}	
	
	
	
	public void stringCb(String url, String html, AjaxStatus status){
		
		showResult(html, status);
		
	}
	
	
	@Override
	public void onDestroy(){
		
		TQuery tq = new TQuery(this);
		tq.dismissDialogs();
		
		super.onDestroy();
	}
	
}
