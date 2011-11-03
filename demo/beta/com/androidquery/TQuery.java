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
    	
    	boolean done = false;//setupWebview(wv, url);
    	
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
    				pv.setVisibility(View.GONE);
    			}
    			
    		}
    		
    		@Override
    		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
    			
    			if(pv != null){
    				pv.setVisibility(View.GONE);
    			}
    		}
    	});
    	
    	return this;
    	
    }
    
    //11-01 19:38:42.823: W/AQuery(3429): <html><meta name="viewport" content="target-densitydpi=device-dpi,initial-scale=1, minimum-scale=1,user-scalable=1"><body style="margin:0px;padding:0px;"><div style="vertical-align:middle;text-align:center;display:table-cell;width:854px;height:686px;"><img height="686" src="http://photos-h.ak.fbcdn.net/hphotos-ak-ash4/297103_211318432274011_127037124035476_528812_1631110115_n.jpg" /></div></body></html>

    
    private boolean setupWebview(WebView wv, String url){
    	
    	int width = wv.getWidth();
    	int height = wv.getHeight();
    	
    	wv.setInitialScale(100);
    	
    	if(width <= 0 || height <= 0 || url == null) return false;
    	
    	AQUtility.debug("webview", width + "x" + height);
    	
    	View parent = (View) wv.getParent();
    	AQUtility.debug("parent", parent.getWidth() + "x" + parent.getHeight());
    	
    	
    	String meta = "<meta name=\"viewport\" content=\"target-densitydpi=device-dpi,initial-scale=1, minimum-scale=1,user-scalable=1\">";    	
    	String body = "<body style=\"margin:0px;padding:0px;background:black;\"><div style=\"vertical-align:middle;text-align:center;display:table-cell;width:@widthpx;height:@heightpx;\"><img @dim src=\"@src\" /></div></body>";
    	
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
