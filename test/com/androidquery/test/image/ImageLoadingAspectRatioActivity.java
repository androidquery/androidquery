package com.androidquery.test.image;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.test.RunSourceActivity;
import com.androidquery.util.AQUtility;

public class ImageLoadingAspectRatioActivity extends RunSourceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
			
		
		aspectRatio();
	}
	
	protected int getContainer(){
		return R.layout.image_ratio_activity;
	}
	
	
	
	@Override
	protected void runSource(){
		
		//AQUtility.invokeHandler(this, type, false, null);
	}
	
	public void aspectRatio(){
		
		String imageUrl = "http://farm3.static.flickr.com/2199/2218403922_062bc3bcf2.jpg";	
		
		aq.id(R.id.image1).image(imageUrl, true, true, 0, 0, null, 0, AQuery.RATIO_PRESERVE);
		aq.id(R.id.text1).text("Original Aspect Ratio");
		
		aq.id(R.id.image2).image(imageUrl, true, true, 0, 0, null, 0, 1.0f / 1.0f);
		aq.id(R.id.text2).text("1:1");
		
		aq.id(R.id.image3).image(imageUrl, true, true, 0, 0, null, 0, 1.5f / 1.0f);
		aq.id(R.id.text3).text("1.5:1");
		
		aq.id(R.id.image4).image(imageUrl, true, true, 0, 0, null, 0, 9.0f / 16.0f);
		aq.id(R.id.text4).text("9:16");
		
		aq.id(R.id.image5).image(imageUrl, true, true, 0, 0, null, 0, 3.0f / 4.0f);
		aq.id(R.id.text5).text("3:4");
		
		
	}
	
	
}
