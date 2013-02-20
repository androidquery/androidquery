package com.androidquery.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.androidquery.callback.ImageOptions;
import com.androidquery.util.AQUtility;

public class AdhocActivity extends Activity {

	private AQuery aq;
	private int number = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.adhoc_activity);
		
		aq = new AQuery(this);
		
		aq.id(R.id.button).clicked(this, "goClicked");
	}
	
	
	
	private void work() throws IOException{
		
		String url = "";
		
		BitmapAjaxCallback cb = new BitmapAjaxCallback();
		
		cb.url(url).ratio(AQuery.RATIO_PRESERVE).expire(3600*1000);
		
		aq.id(R.id.image).image(cb);
		
	}
	
	public void goClicked(View view){
		
		AQUtility.debug("clicked");
		
		String url = "http://farm6.static.flickr.com/5035/5802797131_a729dac808_b.jpg";
		
		aq.ajax(url, File.class, 0, new AjaxCallback<File>(){
			
			@Override
			public void callback(String url, File object, AjaxStatus status){
				
				try{
					photoCb(url, object, status);
				}catch(Throwable e){
					AQUtility.debug(e);
					finish();
				}
			}
			
		}.uiCallback(false));
		
	}
	
	private Map<String, Bitmap> cache = new HashMap<String, Bitmap>();
	
	public void photoCb(String url, File file, AjaxStatus status) throws Exception{
		
		if(file == null) return;
		
		AQUtility.debug("file cb", file.length());
		AQUtility.debug("ui", AQUtility.isUIThread());
		
		for(int i = 0; i < 50; i++){
			
			String tag = "image#" + (i + 1);
			
			AQUtility.time(tag);
			Bitmap bm = decode(file);
			
			cache.put(i +"", bm);
			
			
			AQUtility.timeEnd(tag, 0);
			
		}
		
	}
	
	private Bitmap decode(File file) throws Exception{
		
		BitmapFactory.Options options = new Options();
		
		options.inInputShareable = true;
		options.inPurgeable = true;
		
		FileInputStream fis = new FileInputStream(file);
		
		//Bitmap result = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
		
		Bitmap result = BitmapFactory.decodeFileDescriptor(fis.getFD(), null, options);
		
		AQUtility.debug("bm", result.getWidth() + "x" + result.getHeight());
		
		fis.close();
		
		return result;
		
	}
	
	
	protected int getContainer(){
		return R.layout.adhoc_activity;
	}
	
	
	

}
