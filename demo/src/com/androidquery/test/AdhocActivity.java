package com.androidquery.test;

import java.io.File;
import java.io.IOException;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;

public class AdhocActivity extends Activity {

	private AQuery aq;
	private int number = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.adhoc_activity);
		
		aq = new AQuery(this);
		
		AjaxCallback.setReuseHttpClient(false);
		//aq.id(R.id.text).text("point 1");
		
		try{
			work();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	private void work() throws IOException{
		/*
		String url = "http://farm6.static.flickr.com/5035/5802797131_a729dac808_b.jpg";   
		
		aq.id(new ImageView(this)).image(url, false, true, 200, 0, new BitmapAjaxCallback(){
			
			protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
				AQUtility.debug(bm.getWidth());
			}
			
		});
		*/
		
        AQUtility.setDebug(true);
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle("Sending...");

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                        String url = "http://sssprog.ru";
                        final int num = number;
                        number++;

                        Log.d("sss", "loading started " + num);

                        aq.progress(dialog).ajax(url, String.class, new
                        		
                        	AjaxCallback<String>() {
                            
                        		@Override
	                            public void callback(String url, String html, AjaxStatus status)
                        		{
	                                        Log.i("sss", "ajax loaded " + num + "   " + status.getCode() + "  " + url);
	                                        Log.i("sss", "Res: " + html);
	                                }
	                        });
	                }
	        });
	
		
	}
	
	
	
	protected int getContainer(){
		return R.layout.adhoc_activity;
	}
	
	
	

}
