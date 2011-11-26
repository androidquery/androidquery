package com.androidquery.test.image;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.TQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.test.RunSourceActivity;
import com.androidquery.util.AQUtility;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebView.PictureListener;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;

public class ImageZoomActivity extends RunSourceActivity {
    
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        
        image_zoom();
        
    }
    
	protected int getContainer(){
		return R.layout.image_zoom_activity;
	}
    
    private void image_zoom(){
    
    	String url = "http://farm4.static.flickr.com/3531/3769416703_b76406f9de.jpg";
    	
    	aq.id(R.id.text).text("Try pinch zoom with finger.");
    	
    	aq.id(R.id.web).progress(R.id.progress).webImage(url);
    	
    }
   
    
}