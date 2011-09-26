package com.androidquery.service;

import java.util.Locale;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;

import com.androidquery.AQuery;
import com.androidquery.AbstractAQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;

public class MarketService{

	private Activity act;
	private AQuery aq;
	private Handler handler;
	private String locale;
	private String rateUrl;
	private String updateUrl;
	private boolean force;
	
	public MarketService(Activity act) {
		this.act = act;
		this.aq = new AQuery(act);
		this.handler = new Handler();
		this.locale = Locale.getDefault().toString();
		this.rateUrl = getMarketUrl();
		this.updateUrl = rateUrl;
	}
	
	public MarketService rateUrl(String url){
		this.rateUrl = url;
		return this;
	}
	
	public MarketService updateUrl(String url){
		this.updateUrl = url;
		return this;
	}
	
	public MarketService locale(String locale){
		this.locale = locale;
		return this;
	}
	
	public MarketService force(boolean force){
		this.force = force;
		return this;
	}
	
	private String getHost(){
		//return "http://192.168.1.222";
		return "https://androidquery.appspot.com";
	}
	
	private String getQueryUrl(){
		String url = getHost() + "/api/market?app=" + getAppId() + "&locale=" + locale;
		return url;
	}
	
	private String getAppId(){
		ApplicationInfo info = act.getApplicationInfo();		
		String appId = info.packageName;
		return appId;
	}
	
	private Drawable getAppIcon(){
		Drawable d = act.getApplicationInfo().loadIcon(act.getPackageManager());
		return d;
	}
	
	private String getVersion(){
		
		String appId = getAppId();
		
		String version = null;
		
		PackageInfo info;
		try {
			info = act.getPackageManager().getPackageInfo(appId, 0);
			version = info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		return version;
	}
	
	
	public void checkVersion(){
		
		String url = getQueryUrl();
		
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class).handler(handler, "marketCb");
		
		aq.ajax(cb);
		
	}
	
	
	

    private static boolean openUrl(Activity act, String url) {
    
    	
    	try{
   
	    	if(url == null) return false;
	    	
	    	Uri uri = Uri.parse(url);
	    	Intent intent = new Intent(Intent.ACTION_VIEW, uri);	    	
	    	act.startActivity(intent);
    	
	    	return true;
    	}catch(Exception e){
    		return false;
    	}
    }
    
    private String getMarketUrl(){
    	return "market://details?id=" + getAppId();
    	
    }
    
    protected void callback(String url, JSONObject jo, AjaxStatus status){
    	
    	if(jo == null) return;
    	
    	String latest = jo.optString("version", null);
		String version = getVersion();
		
		if(latest != null && version != null){
			
			AQUtility.debug("version", version + "->" + latest);
			
			if(force || !latest.equals(version)){
				showUpdateDialog(jo);
			}
			
		}
    	
    }
    
	protected void showUpdateDialog(JSONObject jo){
		
		if(jo == null) return; 
		
		JSONObject dia = jo.optJSONObject("dialog");
		
		String update = dia.optString("update", "Update");
		String skip = dia.optString("skip", "Skip");
		String rate = dia.optString("rate", "Rate");
		String body = dia.optString("body", jo.optString("recent", "N/A"));
		String title = dia.optString("title", "Update Available");
		
		Drawable icon = getAppIcon();
		
		Dialog dialog = new AlertDialog.Builder(act)
        .setIcon(icon)
		.setTitle(title)
        .setMessage(body)        
		.setPositiveButton(rate, handler)
        .setNeutralButton(skip, handler)
        .setNegativeButton(update, handler)
        .create();
		
		dialog.show();
		
		return;
		
	}
    
	
	protected class Handler implements DialogInterface.OnClickListener{
        
		public void marketCb(String url, JSONObject jo, AjaxStatus status) {
			
			marketCb(url, jo, status, false);
			
		}
		
		public void marketFetchedCb(String url, JSONObject jo, AjaxStatus status) {
			
			marketCb(url, jo, status, true);
			
		}
		
		private void marketCb(String url, JSONObject jo, AjaxStatus status, boolean fetched){
			
			//AQUtility.debug(jo);
			
			if(jo != null && "1".equals(jo.optString("status"))){
				
				if(!fetched && jo.optBoolean("fetch", false)){
					
					String marketUrl = jo.optString("marketUrl");
					
					AjaxCallback<String> cb = new AjaxCallback<String>();
					cb.url(marketUrl).type(String.class).handler(this, "detailCb");				
					aq.ajax(cb);
					
				}else{					
					callback(url, jo, status);
					
				}
				
			}else{
				callback(url, jo, status);				
			}
		}
		
		public void detailCb(String url, String html, AjaxStatus status){
			
			if(html != null && html.length() > 1000){
				
				String qurl = getQueryUrl();
				
				AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
				cb.url(qurl).type(JSONObject.class).handler(this, "marketFetchedCb");
				cb.param("html", html);
				
				aq.ajax(cb);
				
			}
			
			
		}
		
		


		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			switch (which) {
				case AlertDialog.BUTTON_POSITIVE:
					openUrl(act, rateUrl);
					break;
				case AlertDialog.BUTTON_NEGATIVE:
					openUrl(act, updateUrl);
					break;
				case AlertDialog.BUTTON_NEUTRAL:
				
					break;
			}
			
			
			
		}
		
    }
	
	
}
