package com.androidquery.test;

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

public class AQService extends AbstractAQuery<AQService>{

	private Activity act;
	
	public AQService(Activity act) {
		super(act);
		this.act = act;
	}
	
	public AQService(View view) {
		super(view);
	}
	
	public AQService(Context context) {
		super(context);
	}
	
	private String getHost(){
		return "http://192.168.1.222";
	}
	
	private String getQueryUrl(){
		String url = getHost() + "/api/market?app=" + getAppId();
		return url;
	}
	
	private String getAppId(){
		ApplicationInfo info = getContext().getApplicationInfo();		
		String appId = info.packageName;
		return appId;
	}
	
	private Drawable getAppIcon(){
		Drawable d = getContext().getApplicationInfo().loadIcon(getContext().getPackageManager());
		return d;
	}
	
	private String getVersion(){
		
		String appId = getAppId();
		
		String version = null;
		
		PackageInfo info;
		try {
			info = getContext().getPackageManager().getPackageInfo(appId, 0);
			version = info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		return version;
	}
	
	public AQService versionCheck(){
		
		//http://192.168.1.222/api/market?app=com.pekca.vikispot.android
		
				
		String url = getQueryUrl();
		
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class).handler(this, "marketCb");
		
		ajax(cb);
		
		return this;
	}
	
	public void marketCb(String url, JSONObject jo, AjaxStatus status) {
		
		marketCb(url, jo, status, false);
		
	}
	
	public void marketFetchedCb(String url, JSONObject jo, AjaxStatus status) {
		
		marketCb(url, jo, status, true);
		
	}
	
	private void marketCb(String url, JSONObject jo, AjaxStatus status, boolean fetched){
		
		AQUtility.debug(jo);
		
		if(jo != null && "1".equals(jo.optString("status"))){
			
			if(!fetched && jo.optBoolean("fetch", false)){
				
				String marketUrl = jo.optString("marketUrl");
				
				AjaxCallback<String> cb = new AjaxCallback<String>();
				cb.url(marketUrl).type(String.class).handler(this, "detailCb");				
				ajax(cb);
				
			}else{
				
				String latest = jo.optString("version", null);
				String version = getVersion();
				
				if(latest != null && version != null){
					
					AQUtility.debug("version", version + "->" + latest);
					
					if(!latest.equals(version)){
						makeUpdateDialog(jo).show();
					}
					
				}
				
			}
			
		}
	}
	
	public void detailCb(String url, String html, AjaxStatus status){
		
		if(html != null && html.length() > 1000){
			
			String qurl = getQueryUrl();
			
			AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
			cb.url(qurl).type(JSONObject.class).handler(this, "marketFetchedCb");
			cb.param("html", html);
			
			ajax(cb);
			
		}
		
		
	}
	
    public static boolean openBrowser(Activity act, String url) {
    
    	
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
    
    private void openMarket(){
    	
    	openBrowser(act, getMarketUrl());
    	
    }

	private Dialog makeUpdateDialog(JSONObject jo){
		
		JSONObject dia = jo.optJSONObject("dialog");
		
		String update = dia.optString("update", "Update");
		String skip = dia.optString("skip", "Skip");
		String rate = dia.optString("rate", "Rate");
		String body = dia.optString("body", jo.optString("recent", "N/A"));
		String title = dia.optString("title", "Update Available");
		
		Drawable icon = getAppIcon();
		
		return new AlertDialog.Builder(getContext())
        .setIcon(icon)
		.setTitle(title)
        .setMessage(body)        
		.setPositiveButton(rate, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	openMarket();
                /* User clicked OK so do some stuff */
            }
        })
        .setNeutralButton(skip, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                /* User clicked Something so do some stuff */
            }
        })
        .setNegativeButton(update, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                /* User clicked Cancel so do some stuff */
            	openMarket();
            }
        })
        .create();
	}
	
}
