package com.androidquery;

import java.io.InputStream;

import com.androidquery.util.AQUtility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
	
	
	private String getSource(Context context){
		
		String source = null;
		
		try{
		
			InputStream is = context.getClassLoader().getResourceAsStream("com/androidquery/web_image.html");			
			if(is != null){
				source = new String(AQUtility.toBytes(is));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return source;
		
	}
	
	private static final String PREF_FILE = "WebViewSettings";
	private static final String DOUBLE_TAP_TOAST_COUNT = "double_tap_toast_count";

	private static void fixWebviewTip(Context context){
		SharedPreferences prefs = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
		if (prefs.getInt(DOUBLE_TAP_TOAST_COUNT, 1) > 0) {
		    prefs.edit().putInt(DOUBLE_TAP_TOAST_COUNT, 0).commit();
		}
	}
	
	
    public TQuery webImage(String url){
    	
    	if(!(view instanceof WebView)){
    		return this;
    	}
    	
    	WebView wv = (WebView) view;
    	
    	if(url.equals(wv.getTag(AQuery.TAG_URL))){
    		return this;
    	}
    	
    	wv.setTag(AQuery.TAG_URL, url);
    	
    	final View pv = progress;
    	progress = null;
    	
    	fixWebviewTip(wv.getContext());
    	
		wv.loadData("<html></html>", "text/html", "utf-8");
		wv.setBackgroundColor(Color.parseColor("#000000"));
		
		//state, "init", "fetch"
		wv.setTag(AQuery.TAG_LAYOUT, "init");
		
		wv.setPictureListener(new PictureListener() {
			
			//private int lastWidth;
			
			@Override
			public void onNewPicture(WebView view, Picture picture) {
				
				String url = (String) view.getTag(AQuery.TAG_URL);
				String state = (String) view.getTag(AQuery.TAG_LAYOUT);
				
				AQUtility.debug("pic fin", view.getTag(AQuery.TAG_LAYOUT));
				AQUtility.debug("width", view.getWidth());
				
				//int width = view.getWidth();
				
				if("init".equals(state)){
					view.setTag(AQuery.TAG_LAYOUT, "fetch");
					//lastWidth = width;
					setupWebview(view, url);					
				}else if("fetch".equals(state)){
    				view.setVisibility(View.VISIBLE);
					view.setPictureListener(null);
				}
				
			}
			
			
		});
    		
    	
    	//aq.id(R.id.progress).visible();
    	if(pv != null){
    		pv.setVisibility(View.VISIBLE);
    	}
    	
    	
    	wv.setWebViewClient(new WebViewClient(){
    		
    		
    		@Override
    		public void onPageFinished(WebView view, String url) {
    			
    			AQUtility.debug("fin", view.getTag(AQuery.TAG_LAYOUT));
    			
    			if(("fetch".equals(view.getTag(AQuery.TAG_LAYOUT)))){
    				if(pv != null){
    					pv.setVisibility(View.GONE);
    				}
    				view.setWebViewClient(null);
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
    	
    	String html = getSource(wv.getContext());
    	html = html.replaceAll("@width", width + "").replaceAll("@height", height + "").replaceAll("@src", url);
    	
    	String dim;
    	
    	boolean landscape = width >= height;
    	
    	if(!landscape){
    		dim = "width:" + width + "px;";
    	}else{
    		dim = "height:" + height + "px;";
    	}
    	html = html.replaceAll("@dim", dim).replaceAll("@scape", landscape + "");	
    	
    	//AQUtility.debug(html);
    	
    	WebSettings ws = wv.getSettings();
    	ws.setSupportZoom(true);
    	ws.setBuiltInZoomControls(true);
    	ws.setJavaScriptEnabled(true);
    	
    	wv.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
    	
    	wv.setBackgroundColor(Color.parseColor("#000000"));
    	
    	return true;
    }
	
}
