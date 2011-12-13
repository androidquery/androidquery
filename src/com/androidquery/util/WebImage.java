package com.androidquery.util;

import java.io.InputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.androidquery.AQuery;

public class WebImage extends WebViewClient{

	private View progress;
	private static String template;
	
	private static String getSource(Context context){
		
		if(template == null){
		
			try{
				InputStream is = context.getClassLoader().getResourceAsStream("com/androidquery/util/web_image.html");			
				template = new String(AQUtility.toBytes(is));
			}catch(Exception e){
				AQUtility.debug(e);
			}
			
		}
		
		return template;
		
	}
	
	private static final String PREF_FILE = "WebViewSettings";
	private static final String DOUBLE_TAP_TOAST_COUNT = "double_tap_toast_count";

	private static void fixWebviewTip(Context context){
		SharedPreferences prefs = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
		if (prefs.getInt(DOUBLE_TAP_TOAST_COUNT, 1) > 0) {
		    prefs.edit().putInt(DOUBLE_TAP_TOAST_COUNT, 0).commit();
		}
	}
	
	public void webImage(WebView wv, String url, View progress, boolean zoom, boolean control, int color){
		
		if(url.equals(wv.getTag(AQuery.TAG_URL))){
    		return;
    	}
		
		this.progress = progress;
    	
    	wv.setTag(AQuery.TAG_URL, url);
    	fixWebviewTip(wv.getContext());
		
		setupWebview(wv, url, zoom, control, color);
		
	}
	
    private void setupWebview(WebView wv, String url, boolean zoom, boolean control, int color){
    	
    	wv.setInitialScale(100);
    	
    	String html = getSource(wv.getContext());
    	html = html.replace("@src", url).replace("@color", Integer.toHexString(color));
    	
    
    	WebSettings ws = wv.getSettings();
    	ws.setSupportZoom(zoom);
    	ws.setBuiltInZoomControls(zoom);
    	
    	if(!control){
    		disableZoomControl(wv);
    	}
    	
    	ws.setJavaScriptEnabled(true);
    	wv.setWebViewClient(this);
    	
    	if(progress != null){
    		progress.setVisibility(View.VISIBLE);
    	}
    	
    	wv.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
    	
    	//wv.setBackgroundColor(Color.parseColor("#000000"));
    	
    	wv.setBackgroundColor(color);
    	
    }
	
    private void done(WebView view){
    	if(progress != null){
			progress.setVisibility(View.GONE);
			view.setVisibility(View.VISIBLE);
		}
		view.setWebViewClient(null);
    }
    
    @Override
	public void onPageFinished(WebView view, String url) {
    	done(view);
	}
	
	@Override
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		done(view);
	}
	
	private static void disableZoomControl(WebView wv){
		
		if(android.os.Build.VERSION.SDK_INT < 11) return;
		
		WebSettings ws = wv.getSettings();		
		AQUtility.invokeHandler(ws, "setDisplayZoomControls", false, false, new Class[]{boolean.class}, false);
		
	}
	
	/*
    public static void webImage(WebView wv, String url, final View progress){
    	
    	if(url.equals(wv.getTag(AQuery.TAG_URL))){
    		return;
    	}
    	
    	wv.setTag(AQuery.TAG_URL, url);
    	
    	
    	fixWebviewTip(wv.getContext());
    	
		wv.loadData("<html></html>", "text/html", "utf-8");
		wv.setBackgroundColor(Color.parseColor("#000000"));
		
		//state, "init", "fetch"
		wv.setTag(AQuery.TAG_LAYOUT, "init");
		
		wv.setPictureListener(new PictureListener() {
			
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
    		
    	
    	if(progress != null){
    		progress.setVisibility(View.VISIBLE);
    	}
    	
    	
    	wv.setWebViewClient(new WebViewClient(){
    		
    		
    		@Override
    		public void onPageFinished(WebView view, String url) {
    			
    			AQUtility.debug("fin", view.getTag(AQuery.TAG_LAYOUT));
    			
    			if(("fetch".equals(view.getTag(AQuery.TAG_LAYOUT)))){
    				if(progress != null){
    					progress.setVisibility(View.GONE);
    				}
    				view.setWebViewClient(null);
    			}
    			
    		}
    		
    		@Override
    		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
    			
    			if(progress != null){
    				progress.setVisibility(View.GONE);
    			}
    			
    		}
    	});
    	
    	
    }
    
    private static boolean setupWebview(WebView wv, String url){
    	
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
    	
    	//ws.setDisplayZoomControls(false);
    	
    	ws.setJavaScriptEnabled(true);
    	
    	wv.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
    	
    	wv.setBackgroundColor(Color.parseColor("#000000"));
    	
    	return true;
    }
	*/
}
