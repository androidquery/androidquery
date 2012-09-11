
package com.androidquery;

import android.R;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.util.AQUtility;

public class WebDialog extends Dialog {

	private String url;
	private WebViewClient client;
	private WebView wv;
	private LinearLayout ll;
	private String message;
    
    public WebDialog(Context context, String url, WebViewClient client) {
        //super(context, R.style.Theme_Light_NoTitleBar);
    	super(context, R.style.Theme_NoTitleBar);
        this.url = url;
        this.client = client;
        
    }
    
    public void setLoadingMessage(String message){
    	this.message = message;
    }
    
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
   
    	
    	RelativeLayout layout = new RelativeLayout(getContext());
        layout.setBackgroundColor(0xFFFFFFFF);
    	
        setupWebView(layout);        
        setupProgress(layout);
        
        
        FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        addContentView(layout, FILL);
        
        
    }

    private void setupProgress(RelativeLayout layout){
    	
    	Context context = getContext();
    	
    	ll = new LinearLayout(context);
    	
    	ProgressBar progress = new ProgressBar(context);
    	int p = AQUtility.dip2pixel(context, 30);
    	LinearLayout.LayoutParams plp = new LinearLayout.LayoutParams(p, p);
    	ll.addView(progress, plp);
    	
    	if(message != null){
	    	TextView tv = new TextView(context);
	    	LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	    	tlp.leftMargin = AQUtility.dip2pixel(context, 5);
	    	tlp.gravity = 0x10;
	    	tv.setText(message);
	    	ll.addView(tv, tlp);
    	}
    	
    	RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); 
    	lp.addRule(RelativeLayout.CENTER_IN_PARENT);
    	
        layout.addView(ll, lp);
    	
    }
    
    private void setupWebView(RelativeLayout layout) {
    	
    	wv = new WebView(getContext());
    	wv.setVerticalScrollBarEnabled(false);
    	wv.setHorizontalScrollBarEnabled(false);
        
        if(client == null) client = new WebViewClient();
        
        wv.setWebViewClient(new DialogWebViewClient());
        
        WebSettings ws = wv.getSettings();
        ws.setJavaScriptEnabled(true);
        //ws.setSaveFormData(false);
        
        //wv.loadUrl(url);
        
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);       
        layout.addView(wv, lp);
        
        
    }
    
    public void load(){
    	if(wv != null){
    		wv.loadUrl(url);
    	}
    }

    private void showProgress(boolean show){
    	
    	if(ll != null){
    		
    		if(show) ll.setVisibility(View.VISIBLE);
    		else ll.setVisibility(View.GONE);
    	}
    	
    }
    
    @Override
    public void dismiss(){
    	
    	try{
    		super.dismiss();
    	}catch(Exception e){    		
    	}
    	
    }
    
    private class DialogWebViewClient extends WebViewClient{
    	
    	@Override
    	public void onPageFinished(WebView view, String url) {
    		
    		showProgress(false);    		
    		client.onPageFinished(view, url);
    	}
    	
    	@Override
    	public void onPageStarted(WebView view, String url, Bitmap favicon) {
    		client.onPageStarted(view, url, favicon);
    	}
    	
    	@Override
    	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
    		client.onReceivedError(view, errorCode, description, failingUrl);
    	}
    	
    	@Override
    	public boolean shouldOverrideUrlLoading(WebView view, String url) {
    		return client.shouldOverrideUrlLoading(view, url);
    	}
    	
    	
    	
    }

}
