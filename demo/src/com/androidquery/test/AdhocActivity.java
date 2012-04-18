package com.androidquery.test;

import java.io.File;
import java.io.IOException;

import org.json.JSONObject;

import android.app.Activity;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;

public class AdhocActivity extends Activity {

	private AQuery aq;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.adhoc_activity);
		
		aq = new AQuery(this);
		
		aq.id(R.id.text).text("point 1");
		
		try{
			work();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/*
	private void work(){
		
		String url = "http://www.google.com";
		File file = AQUtility.getCacheFile(AQUtility.getCacheDir(this), url);
		
		String content = "<html>HELLO</html>";
		byte[] data = content.getBytes();
		
		AQUtility.storeAsync(file, data, 0);
		
	}
	*/
	
	private void work() throws IOException{
		
		String url = "http://www.google.com";
		
		File ext = Environment.getExternalStorageDirectory();
		File tempDir = new File(ext, "myapps");	
		tempDir.mkdirs();
		
		File target = new File(tempDir, "myfile.html");
		target.createNewFile();
		
		
		download(url, target);
	}
	
	private void download(String url, final File target){
		
		
		aq.ajax(url, byte[].class, -1, new AjaxCallback<byte[]>(){
			
			@Override
			public void callback(String url, byte[] data, AjaxStatus status) {
				
				if(data != null){
					
					
					AQUtility.storeAsync(target, data, 0);
					
					
					
				}else{
					//error
				}
				
			}
			
		});
		
	}
	
	
	protected int getContainer(){
		return R.layout.adhoc_activity;
	}
	
	
	

}
