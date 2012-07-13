package com.androidquery.test.image;

import android.os.Bundle;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.callback.ImageOptions;
import com.androidquery.test.RunSourceActivity;

public class ImageLoadingAspectRatioActivity extends RunSourceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		if(type.equals("image_ratio_fixed")){
			aspectRatio();
		}else if(type.equals("image_ratio_anchor")){
			aspectRatioAnchor();
		}
		
		
		
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
		
		
		aq.id(R.id.image1).progress(R.id.progress).image(imageUrl, true, true, 0, 0, null, 0, AQuery.RATIO_PRESERVE);
		//aq.id(R.id.image1).progress(R.id.progress).image("http://www.google.com/a.jpg", true, true, 0, AQuery.GONE, null, 0, AQuery.RATIO_PRESERVE);
		
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
	
	public void aspectRatioAnchor(){
		
		String imageUrl = "http://farm3.static.flickr.com/2199/2218403922_062bc3bcf2.jpg";	
		
		ImageOptions options = new ImageOptions();
		
		options.ratio = AQuery.RATIO_PRESERVE;
		options.anchor = AQuery.ANCHOR_DYNAMIC;
		aq.id(R.id.image1).image(imageUrl, options);
		aq.id(R.id.text1).text("Original Image");
		
		options.ratio = 1;
		options.anchor = 1;
		aq.id(R.id.image2).image(imageUrl, options);
		aq.id(R.id.text2).text("Anchor 1.0 (Top)");
		
		options.ratio = 1;
		options.anchor = -1;
		aq.id(R.id.image3).image(imageUrl, options);
		aq.id(R.id.text3).text("Anchor -1.0 (Bottom)");
		
		options.ratio = 1;
		options.anchor = AQuery.ANCHOR_DYNAMIC;
		aq.id(R.id.image4).image(imageUrl, options);
		aq.id(R.id.text4).text("AQuery.ANCHOR_DYNAMIC");
		
		aq.id(R.id.text5).text("");
		
		
	}
	
}
