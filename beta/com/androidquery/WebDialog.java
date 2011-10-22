
package com.androidquery;

import android.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

public class WebDialog extends Dialog {

	private String url;
	private WebViewClient client;
    
    public WebDialog(Context context, String url, WebViewClient client) {
        super(context, R.style.Theme);
        this.url = url;
        this.client = client;
        
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
   
        WebView wv = setUpWebView();
        
        FrameLayout.LayoutParams FILL = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        addContentView(wv, FILL);

    }

    
    private WebView setUpWebView() {
    	
    	WebView mWebView = new WebView(getContext());
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        
        WebViewClient wvc = client;
        if(wvc == null) wvc = new WebViewClient();
        
        mWebView.setWebViewClient(wvc);
        
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
        
        return mWebView;
    }

    @Override
    public void dismiss(){
    	
    	try{
    		super.dismiss();
    	}catch(Exception e){    		
    	}
    	
    }

}
