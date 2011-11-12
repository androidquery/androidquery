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

public class ImageZoomActivity extends Activity {
    
	private AQuery aq;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.image_zoom_activity);
        
        aq = new AQuery(this);
        
        initView();
        
    }
    
    private void initView(){
    
    	String url = "http://farm4.static.flickr.com/3531/3769416703_b76406f9de.jpg";
    	//String url = "http://192.168.1.222/test/test.htm";
    	
    	aq.id(R.id.text).text("Try pinch zoom with finger.");
    	
    	TQuery aq = new TQuery(this);
    	aq.id(R.id.web).progress(R.id.progress).webImage(url);
    	
    	/*
    	WebView wv = aq.id(R.id.web).getWebView();
    	
    	WebSettings ws = wv.getSettings();
    	ws.setJavaScriptEnabled(true);
    	ws.setSupportZoom(true);
    	ws.setBuiltInZoomControls(true);
    	
    	wv.loadUrl(url);
    	*/
    }
   
    
}