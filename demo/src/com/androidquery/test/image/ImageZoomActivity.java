package com.androidquery.test.image;

import android.os.Bundle;

import com.androidquery.R;
import com.androidquery.test.RunSourceActivity;

public class ImageZoomActivity extends RunSourceActivity {
    
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        
    	debug();
    	
        image_zoom();
        
    }
    
	private void debug(){
		
		/*
		WebView wv = aq.id(R.id.web).getWebView();
		
		wv.setWebChromeClient(new WebChromeClient() {
			  public boolean onConsoleMessage(ConsoleMessage cm) {
			    AQUtility.debug(cm.message() + " -- From line "
			                         + cm.lineNumber() + " of "
			                         + cm.sourceId() );
			    return true;
			  }
			});
			*/
	}
	
	protected int getContainer(){
		return R.layout.image_zoom_activity;
	}
    
    private void image_zoom(){
    
    	String url = "http://farm4.static.flickr.com/3531/3769416703_b76406f9de.jpg";    	
    	aq.id(R.id.text).text("Try pinch zoom with finger.");
    	aq.id(R.id.web).progress(R.id.progress).webImage(url);
    	//aq.id(R.id.web).progress(R.id.progress).webImage(url, true, true, 0xFF000000);
    }
   
    
}