package com.androidquery.service;

import java.util.Locale;

import org.json.JSONObject;
import org.xml.sax.XMLReader;

import android.R;
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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Html;
import android.text.Html.TagHandler;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
	private int progress;
	private long expire = 10 * 60 * 1000;
	
	private String version;
	private boolean fetch;
	private boolean completed;
	
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
	
	public MarketService progress(int id){
		this.progress = id;
		return this;
	}
	
	public MarketService force(boolean force){
		this.force = force;
		return this;
	}
	
	public MarketService expire(long expire){
		this.expire = expire;
		return this;
	}
	
	private static ApplicationInfo ai;
	private ApplicationInfo getApplicationInfo(){
		
		if(ai == null){
			ai = act.getApplicationInfo();			
		}
		
		return ai;
	}
	
	private static PackageInfo pi;
	private PackageInfo getPackageInfo(){
		
		if(pi == null){
			try {
				pi = act.getPackageManager().getPackageInfo(getAppId(), 0);
				
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return pi;
	}
	
	
	private String getHost(){
		//return "http://192.168.1.222";
		return "https://androidquery.appspot.com";
	}
	
	private String getQueryUrl(){
		String url = getHost() + "/api/market?app=" + getAppId() + "&locale=" + locale + "&version=" + getVersion() + "&code=" + getVersionCode();
		return url;
	}
	
	private String getAppId(){
		return getApplicationInfo().packageName;
	}
	
	
	private Drawable getAppIcon(){
		Drawable d = getApplicationInfo().loadIcon(act.getPackageManager());
		return d;
	}
	
	private String getVersion(){		
		return getPackageInfo().versionName;		
	}
	
	private int getVersionCode(){		
		return getPackageInfo().versionCode;		
	}
	
	
	public void checkVersion(){
		
		String url = getQueryUrl();
		
		AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
		cb.url(url).type(JSONObject.class).handler(handler, "marketCb").fileCache(!force).expire(expire);
		
		aq.progress(progress).ajax(cb);
		
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
    	
    	String latestVer = jo.optString("version", null);
		int latestCode = jo.optInt("code", 0);
		
		if(latestVer != null){
			
			AQUtility.debug("version", getVersion() + "->" + latestVer + ":" + getVersionCode() + "->" + latestCode);
			AQUtility.debug("outdated", outdated(latestVer, latestCode));
			
			if(force || outdated(latestVer, latestCode)){
				showUpdateDialog(jo);
			}
			
		}
    	
    }
    
    
    private boolean outdated(String latestVer, int latestCode){
    	
    	String skip = getSkipVersion(act);
    	if(latestVer.equals(skip)){
    		AQUtility.debug("skip!");
    		return false;
    	}
    	
    	String version = getVersion();
    	int code = getVersionCode();
    	
    	if(!version.equals(latestVer)){
    		if(code <= latestCode){
    			return true;
    		}
    	}
    	
    	return false;
    }
    
	protected void showUpdateDialog(JSONObject jo){
		
		if(jo == null || version != null) return; 
		
		JSONObject dia = jo.optJSONObject("dialog");
		
		String update = dia.optString("update", "Update");
		String skip = dia.optString("skip", "Skip");
		String rate = dia.optString("rate", "Rate");
		String body = dia.optString("body", jo.optString("recent", "N/A"));
		String title = dia.optString("title", "Update Available");
		
		body = "<div class=\"doc-whatsnew-container\"><p>What's in this version:</p><ol><li>Added list delay draw examples.</li><li>Added set cache directory example.</li><li>Added share image example.</li></ol></div>";
		
		body += "<small><small>check via AndroidQuery</small></small>";
		
		Drawable icon = getAppIcon();
		
		Context context = act;
		
		
		Dialog dialog = new AlertDialog.Builder(context)
        .setIcon(icon)
		.setTitle(title)
        .setMessage(body)        
		.setPositiveButton(rate, handler)
        .setNeutralButton(skip, handler)
        .setNegativeButton(update, handler)
        .create();
		
		
		version = jo.optString("version", null);
		
		dialog.show();
		
		resetBody(dialog, body);
		
		return;
		
	}
    
	private void resetBody(Dialog dialog, String body){
		
		try{
			TextView messageView = (TextView) dialog.findViewById(R.id.message);
			if(messageView != null){
				messageView.setText(Html.fromHtml(body, null, handler));
			}
		}catch(Exception e){
			AQUtility.report(e);
		}
	}
	
	
	private static final String SKIP_VERSION = "aqs.skip";
	
	private static void setSkipVersion(Context context, String version){
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString(SKIP_VERSION, version).commit();		
	}

	private static String getSkipVersion(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context).getString(SKIP_VERSION, null);
	}
	
	protected class Handler implements DialogInterface.OnClickListener, TagHandler{
        
		public void marketCb(String url, JSONObject jo, AjaxStatus status){
			
			if(act.isFinishing()) return;
			
			AQUtility.debug(jo);
			
			
			if(jo != null){
				
				if("1".equals(jo.optString("status"))){
				
					if(!fetch && jo.optBoolean("fetch", false) && status.getSource() == AjaxStatus.NETWORK){
						
						fetch = true;
						//status.invalidate();
						
						String marketUrl = jo.optString("marketUrl", null);
						
						AjaxCallback<String> cb = new AjaxCallback<String>();
						cb.url(marketUrl).type(String.class).handler(this, "detailCb");				
						aq.progress(progress).ajax(cb);
						
					}				
					
					if(jo.has("dialog")){
						cb(url, jo, status);
					}
					
				}else{
					status.invalidate();
				}
				
			}else{
				cb(url, jo, status);
			}
		}
		
		private void cb(String url, JSONObject jo, AjaxStatus status){
			
			if(!completed){			
				completed = true;			
				callback(url, jo, status);
			}
		}
		
		public void detailCb(String url, String html, AjaxStatus status){
			
			if(html != null && html.length() > 1000){
				
				String qurl = getQueryUrl();
				
				AjaxCallback<JSONObject> cb = new AjaxCallback<JSONObject>();
				cb.url(qurl).type(JSONObject.class).handler(this, "marketCb");
				cb.param("html", html);
				
				aq.progress(progress).ajax(cb);
				
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
					setSkipVersion(act, version);
					break;
			}
			
			
			
		}

		
		@Override
		public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
			
			AQUtility.debug("unknown", tag);
			if("li".equals(tag)){
				
				if(opening){
				}else{
					output.append("\n");
				}
				
				
			}
		}
		
    }
	
	
}
