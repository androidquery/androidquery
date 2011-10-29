package com.androidquery;

import com.androidquery.util.AQUtility;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Picture;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebView.PictureListener;

public class TQuery extends AbstractAQuery<TQuery>{

	public TQuery(Activity act) {
		super(act);
	}

	public TQuery(View view) {
		super(view);
	}
	

    public TQuery webImage(String url){
    	
    	if(!(view instanceof WebView)){
    		return this;
    	}
    	
    	final View pv = progress;
    	progress = null;
    	
    	WebView wv = (WebView) view;
    	
    	boolean done = setupWebview(wv, url);
    	
    	if(!done){
    		wv.loadData("<html></html>", "text/html", "utf-8");
    		wv.setBackgroundColor(Color.parseColor("#000000"));
    		
    		wv.setTag(AQuery.TAG_URL, url);
    		
    		wv.setPictureListener(new PictureListener() {
				
				@Override
				public void onNewPicture(WebView view, Picture picture) {
					
					String url = (String) view.getTag(AQuery.TAG_URL);
					view.setTag(AQuery.TAG_URL, null);
					view.setPictureListener(null);
					setupWebview(view, url);
				}
			});
    		
    	}
    	
    	//aq.id(R.id.progress).visible();
    	if(pv != null){
    		pv.setVisibility(View.VISIBLE);
    	}
    	
    	
    	wv.setWebViewClient(new WebViewClient(){
    		
    		
    		@Override
    		public void onPageFinished(WebView view, String u) {
    			
    			if(view.getTag(AQuery.TAG_URL) == null && pv != null){
    				//aq.id(R.id.progress).gone();
    				pv.setVisibility(View.GONE);
    			}
    			
    		}
    		
    		@Override
    		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
    			//aq.id(R.id.progress).gone();
    			if(pv != null){
    				//aq.id(R.id.progress).gone();
    				pv.setVisibility(View.GONE);
    			}
    		}
    	});
    	
    	return this;
    	
    }
    
    private boolean setupWebview(WebView wv, String url){
    	
    	int width = wv.getWidth();
    	int height = wv.getHeight();
    	
    	wv.setInitialScale(100);
    	
    	if(width <= 0 || height <= 0 || url == null) return false;
    	
    	String meta = "<meta name=\"viewport\" content=\"target-densitydpi=device-dpi,initial-scale=1, minimum-scale=1,user-scalable=1\">";    	
    	String body = "<body style=\"margin:0px;padding:0px;\"><div style=\"vertical-align:middle;text-align:center;display:table-cell;width:@widthpx;height:@heightpx;\"><img @dim src=\"@src\" /></div></body>";
    	
    	body = body.replaceAll("@width", width + "").replaceAll("@height", height + "").replaceAll("@src", url);
    	
    	String dim;
    	if(width < height){
    		dim = "width=\"" + width + "\"";
    	}else{
    		dim = "height=\"" + height + "\"";
    	}
    	
    	body = body.replaceAll("@dim", dim);
    	
    	String html = "<html>" + meta + body + "</html>";
    			
    	AQUtility.debug(html);
    	
    	WebSettings ws = wv.getSettings();
    	ws.setSupportZoom(true);
    	ws.setBuiltInZoomControls(true);
    	
    	wv.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
    	
    	wv.setBackgroundColor(Color.parseColor("#000000"));
    	
    	return true;
    }
	
}
