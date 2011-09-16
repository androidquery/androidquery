package com.androidquery.test;

import org.json.JSONArray;
import org.json.JSONObject;

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
		
		
		
		String tb = "http://www.vikispot.com/z/images/vikispot/android-w.png";	
		
		AjaxCallback<Bitmap> cb = new AjaxCallback<Bitmap>(){
			
			@Override
			public void callback(String url, Bitmap bm, AjaxStatus status) {
				
				if(bm != null){
					Button button = aq.id(R.id.button).getButton();					
					button.setCompoundDrawablesWithIntrinsicBounds(new BitmapDrawable(bm), null, null, null);
				}
				
			}
		};
		
		cb.url(tb).type(Bitmap.class).fileCache(true);
		
		aq.ajax(cb);
	}
	
	
	protected int getContainer(){
		return R.layout.adhoc_activity;
	}
	
	@Override
	protected void runSource(){
		
	}
	

	
}
