package com.androidquery.test;

import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.service.MarketService;
import com.androidquery.test.RunSourceActivity;
import com.androidquery.util.AQUtility;

public class AdhocActivity extends RunSourceActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		work();	
	}
	
	private void work(){
		
		AQUtility.debug("adhoc");
		
		AQUtility.debug(Locale.SIMPLIFIED_CHINESE.toString());
		
		ApplicationInfo ai = this.getApplicationInfo();
		
		check("has code", ApplicationInfo.FLAG_HAS_CODE);
		check("debug", ApplicationInfo.FLAG_DEBUGGABLE);
		check("test only", ApplicationInfo.FLAG_TEST_ONLY);
		
		AQUtility.debug("sourceDir", ai.publicSourceDir);
		AQUtility.debug("dataDir", ai.dataDir);
		AQUtility.debug("enabled", ai.enabled);
	}
	
	private void check(String name, int mask){
		ApplicationInfo ai = this.getApplicationInfo();
		AQUtility.debug(name, ai.flags & mask);
		
		
	}
	
	
	protected int getContainer(){
		return R.layout.adhoc_activity;
	}
	
	@Override
	protected void runSource(){
		
	}
	

	
}
