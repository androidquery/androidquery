/*
 * Copyright 2011 - AndroidQuery.com (tinyeeliu@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.androidquery.service;

import java.util.Locale;

import org.json.JSONObject;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Html;
import android.text.Html.TagHandler;

import com.androidquery.AQuery;
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
	private long expire = 12 * 60 * 1000;
	
	private String version;
	private boolean fetch;
	private boolean completed;
	private int level = REVISION;
	
	/** Update check level REVISION. */
	public static final int REVISION = 0;
	
	/** Update check level MINOR. */
	public static final int MINOR = 1;
	
	/** Update check level MAJOR. */
	public static final int MAJOR = 2;
	
	/**
	 * Instantiates a new MarketService.
	 *
	 * @param act Current activity.
	 */
	
	public MarketService(Activity act) {
		this.act = act;
		this.aq = new AQuery(act);
		this.handler = new Handler();
		this.locale = Locale.getDefault().toString();
		this.rateUrl = getMarketUrl();
		this.updateUrl = rateUrl;
	}
	
	/**
	 * Set the destination url of the default rate/review button.
	 *
	 * @param url url
	 * @return self
	 */
	public MarketService rateUrl(String url){
		this.rateUrl = url;
		return this;
	}
	
	/**
	 * Set the update check granularity level. Default is REVISION.
	 * 
	 * <br>
	 * 
	 * Can be REVISION, MINOR, or MAJOR.
	 *
	 * <br>
	 *
	 * App version format: MAJOR.MINOR.REVISION
	 * 
	 * <br>
	 * 
	 * Example:
	 * 
	 * <br>
	 * Current app version: 3.1.2
	 * <br>
	 * Newest app version: 3.1.4
	 * <br>
	 * Update notice will show if level is REVISION, because the revision code is higher.
	 * <br>
	 * Update notice will NOT show if level is MINOR, because the minor code is equal (or higher).
	 * 
	 *
	 * @param level granularity level
	 * @return self
	 */
	public MarketService level(int level){
		this.level = level;
		return this;
	}
	
	
	/**
	 * Set the destination url of the default update button.
	 *
	 * @param url url
	 * @return self
	 */
	public MarketService updateUrl(String url){
		this.updateUrl = url;
		return this;
	}
	
	
	/**
	 * Force the update dialog to a specific locale. Example: en_US, ja_JP.
	 *
	 * @param locale interface locale
	 * @return self
	 */
	
	public MarketService locale(String locale){
		this.locale = locale;
		return this;
	}
	
	/**
	 * Display a progress view during version check.
	 *
	 * @param id view id
	 * @return self
	 */
	public MarketService progress(int id){
		this.progress = id;
		return this;
	}
	
	/**
	 * Force a version check against the AQuery server and show a dialog regardless of versions.
	 *
	 * @param force force an update check
	 * @return self
	 */
	public MarketService force(boolean force){
		this.force = force;
		return this;
	}
	
	/**
	 * The time duration which last version check expires. Default is 10 hours.
	 *
	 * @param expire expire time in milliseconds
	 * @return self
	 */
	
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
		
		return "https://androidquery.appspot.com";
	}
	
	private String getQueryUrl(){
		String appId = getAppId();		
		String url = getHost() + "/api/market?app=" + appId + "&locale=" + locale + "&version=" + getVersion() + "&code=" + getVersionCode() + "&aq=" + AQuery.VERSION;
		if(force){
			url += "&force=true";
		}
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
	
	/**
	 * Perform a version check.
	 *
	 * 
	 */
	
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
    	String id = getAppId();
    	return "market://details?id=" + id; 	
    }
    
    protected void callback(String url, JSONObject jo, AjaxStatus status){
    	
    	
    	if(jo == null) return;
    	
    	String latestVer = jo.optString("version", "0");
		int latestCode = jo.optInt("code", 0);
		
		AQUtility.debug("version", getVersion() + "->" + latestVer + ":" + getVersionCode() + "->" + latestCode);
		AQUtility.debug("outdated", outdated(latestVer, latestCode));
		
		if(force || outdated(latestVer, latestCode)){
			showUpdateDialog(jo);
		}
		
		
    	
    }
    
    
    private boolean outdated(String latestVer, int latestCode){
    	
    	String skip = getSkipVersion(act);
    	
    	if(latestVer.equals(skip)){
    		return false;
    	}
    	
    	String version = getVersion();
    	int code = getVersionCode();
    	
    	if(!version.equals(latestVer)){
    		if(code <= latestCode){
    			//return true;
    			return requireUpdate(version, latestVer, level);
    		}
    	}
    	
    	return false;
    }
    
    private boolean requireUpdate(String existVer, String latestVer, int level){
    	
    	if(existVer.equals(latestVer)) return false;
    	
    	try{
    	
	    	String[] evs = existVer.split("\\.");
	    	String[] lvs = latestVer.split("\\.");
	    	
	    	if(evs.length < 3 || lvs.length < 3) return true;
	    	
	    	switch(level){
	    		case REVISION:
	    			if(!evs[evs.length - 1].equals(lvs[lvs.length - 1])){
	    				return true;
	    			}
	    		case MINOR:
	    			if(!evs[evs.length - 2].equals(lvs[lvs.length - 2])){
	    				return true;
	    			}
	    		case MAJOR:
	    			if(!evs[evs.length - 3].equals(lvs[lvs.length - 3])){
	    				return true;
	    			}
	    			return false;
	    		default:
	    			return true;
	    	}
    	
    	}catch(Exception e){
    		AQUtility.report(e);
    		return true;
    	}
    	
    }
    
    
	protected void showUpdateDialog(JSONObject jo){
		
		if(jo == null || version != null) return; 
		
		if(!isActive()) return;
		
		JSONObject dia = jo.optJSONObject("dialog");
		
		String update = dia.optString("update", "Update");
		String skip = dia.optString("skip", "Skip");
		String rate = dia.optString("rate", "Rate");
		//String message = dia.optString("body", "");
		String body = dia.optString("wbody", "");
		String title = dia.optString("title", "Update Available");
		
		AQUtility.debug("wbody", body);
		
		version = jo.optString("version", null);
		
		Drawable icon = getAppIcon();
		
		Context context = act;
		
		final AlertDialog dialog = new AlertDialog.Builder(context)
        .setIcon(icon)
		.setTitle(title)
		.setPositiveButton(rate, handler)
        .setNeutralButton(skip, handler)
        .setNegativeButton(update, handler)
        .create();
		
		dialog.setMessage(Html.fromHtml(patchBody(body), null, handler));
		
		
		aq.show(dialog);
		
		
		return;
		
	}

    
	private static String patchBody(String body){
		return "<small>" + body + "</small>";
	}

	private static final String SKIP_VERSION = "aqs.skip";
	
	private static void setSkipVersion(Context context, String version){
	
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString(SKIP_VERSION, version).commit();		
	}

	private static String getSkipVersion(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context).getString(SKIP_VERSION, null);
	}
	
	private boolean isActive(){
		if(act.isFinishing()) return false;
		return true;
	}
	
	
	private static final String BULLET = "•";
	
	private class Handler implements DialogInterface.OnClickListener, TagHandler{
        
		@SuppressWarnings("unused")
		public void marketCb(String url, JSONObject jo, AjaxStatus status){
			
			if(act.isFinishing()) return;
			
			if(jo != null){
				
				String s = jo.optString("status");
				
				if("1".equals(s)){
				
					if(jo.has("dialog")){
						cb(url, jo, status);
					}
					
					if(!fetch && jo.optBoolean("fetch", false) && status.getSource() == AjaxStatus.NETWORK){
						
						fetch = true;
						
						String marketUrl = jo.optString("marketUrl", null);
						
						AjaxCallback<String> cb = new AjaxCallback<String>();
						cb.url(marketUrl).type(String.class).handler(this, "detailCb");				
						aq.progress(progress).ajax(cb);
						
					}		
					
				}else if("0".equals(s)){
					status.invalidate();
				}else{
					cb(url, jo, status);
				}
				
			}else{
				cb(url, jo, status);
			}
		}
		
		private void cb(String url, JSONObject jo, AjaxStatus status){
			
			if(!completed){			
				completed = true;			
				progress = 0;
				callback(url, jo, status);
			}
		}
		
		@SuppressWarnings("unused")
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
			
			if("li".equals(tag)){
				
				if(opening){
					output.append("  ");
					output.append(BULLET);
					output.append("  ");
				}else{
					output.append("\n");
				}
				
				
			}
		}
		
    }
	
	
}
