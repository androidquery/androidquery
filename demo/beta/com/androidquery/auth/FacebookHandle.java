package com.androidquery.auth;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.androidquery.AQuery;
import com.androidquery.WebDialog;
import com.androidquery.callback.AbstractAjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;

public class FacebookHandle extends AccountHandle{

	private String appId;
	private Activity act;
	private WebDialog dialog;
	private String token;
	private String permissions;
	private String message;

	private static final String OAUTH_ENDPOINT = "https://graph.facebook.com/oauth/authorize";	
	private static final String REDIRECT_URI = "https://www.facebook.com/connect/login_success.html";
	
	private static final String CANCEL_URI = "fbconnect:cancel";
	//private static final String TOKEN = "access_token";
	//private static final String EXPIRES = "expires_in";
	
	//private static final String DISPLAY_STRING = "touch";

	private boolean first;
	
	public FacebookHandle(Activity act, String appId, String permissions) {
		this.appId = appId;
		this.act = act;
		this.permissions = permissions;
		token = fetchToken();
		first = token == null;
	}

    public void setLoadingMessage(String message){
    	this.message = message;
    }
    
    public void setLoadingMessage(int resId){
    	this.message = act.getString(resId);
    }

	private void dismiss(){
		if(dialog != null){
			new AQuery(act).dismiss(dialog);
			dialog = null;
		}
	}
	
	private void show(){
		if(dialog != null){		
			new AQuery(act).show(dialog);
		}
	}
	
	private void failure(){
		dismiss();
		failure(act, AjaxStatus.AUTH_ERROR, "cancel");
	}
	
	
	protected void auth() {

		Bundle parameters = new Bundle();
		parameters.putString("client_id", appId);
		parameters.putString("type", "user_agent");
		if(permissions != null){
			parameters.putString("scope", permissions);
		}
		
		parameters.putString("redirect_uri", REDIRECT_URI);
		String url = OAUTH_ENDPOINT + "?" + encodeUrl(parameters);
		
		FbWebViewClient client = new FbWebViewClient();
		
		dialog = new WebDialog(act, url, client);	
		dialog.setLoadingMessage(message);
		dialog.setOnCancelListener(client);
		
		show();
		
		if(!first || token != null){
			dialog.hide();
		}
		
	}
	
	private static final String FB_TOKEN = "aq.fb.token";
	
	private String fetchToken(){
		return PreferenceManager.getDefaultSharedPreferences(act).getString(FB_TOKEN, null);	
	}
	
	private void storeToken(String token){
		PreferenceManager.getDefaultSharedPreferences(act).edit().putString(FB_TOKEN, token).commit();	
	}
	

	private class FbWebViewClient extends WebViewClient implements OnCancelListener {
		
		private boolean checkDone(String url){
			
			if(url.startsWith(REDIRECT_URI)) {
				
				Bundle values = parseUrl(url);
				
				String error = values.getString("error_reason");
				
				AQUtility.debug("error", error);
				
				
				if(error == null) {
					token = extractToken(url);					
				}
				
				if(token != null){
					dismiss();
					storeToken(token);
					first = false;
					success(act);
				}else{
					failure();
				}
				
				return true;
			}else if(url.startsWith(CANCEL_URI)) {
				AQUtility.debug("cancelled");
				failure();
				return true;
			} 
			
			return false;
		}
		
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			
			AQUtility.debug("return url: " + url);
			
			return checkDone(url);
			
		}
		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			
			AQUtility.debug("started", url);
			
			if(checkDone(url)){
			}else{			
				super.onPageStarted(view, url, favicon);
			}
			
		}
		
		@Override
		public void onPageFinished(WebView view, String url) {
			AQUtility.debug("finished", url);
			super.onPageFinished(view, url);
			
			//if(dialog != null){
			//	dialog.showProgress(false);
			//}
			
			show();
		}
		
		
		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			failure();
		}
		

		@Override
		public void onCancel(DialogInterface dialog) {
			failure();
		}
		
	}

	
	private String extractToken(String url) {

		Uri uri = Uri.parse(url.replace('#', '?'));

		String token = uri.getQueryParameter("access_token");

		AQUtility.debug("token", token);

		return token;
		
		
	}

	private static String encodeUrl(Bundle parameters) {
		if (parameters == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String key : parameters.keySet()) {
			if (first)
				first = false;
			else
				sb.append("&");
			sb.append(key + "=" + parameters.getString(key));
		}
		return sb.toString();
	}

	private static Bundle decodeUrl(String s) {
		Bundle params = new Bundle();
		if (s != null) {
			String array[] = s.split("&");
			for (String parameter : array) {
				String v[] = parameter.split("=");
				params.putString(v[0], v[1]);
			}
		}
		return params;
	}

	
	private static Bundle parseUrl(String url) {
		
		try {
			URL u = new URL(url);
			Bundle b = decodeUrl(u.getQuery());
			b.putAll(decodeUrl(u.getRef()));
			return b;
		} catch (MalformedURLException e) {
			return new Bundle();
		}
	}

	
	@Override
	public boolean expired(int code) {
		
		return code == 400 || code == 401 || code == 403;
	}

	@Override
	public boolean reauth(final AbstractAjaxCallback<?, ?> cb) {
		
		token = null;
		storeToken(null);
		
		AQUtility.post(new Runnable() {
			
			@Override
			public void run() {
				auth(cb);
			}
		});
		
		return false;
	}

	
	@Override
	public String getNetworkUrl(String url){

		if(url.indexOf('?') == -1){
			url = url + "?";
		}else{
			url = url + "&";
		}
		
		url = url + "access_token=" + token;
		return url;
	}
	
	@Override
	public String getCacheUrl(String url){
		return getNetworkUrl(url);
	}


	@Override
	public boolean authenticated() {
		return token != null;
	}
	
	@Override
	public void unauth(){
		
		token = null;
		
		CookieSyncManager.createInstance(act);
		CookieManager.getInstance().removeAllCookie();	
		storeToken(null);
	}
	
}
