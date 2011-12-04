package com.androidquery.service;

import java.util.Locale;

import org.json.JSONObject;
import org.xml.sax.XMLReader;

import android.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.Html;
import android.text.Html.TagHandler;
import android.widget.TextView;

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
	private long expire = 10 * 60 * 1000;
	
	private String version;
	private boolean fetch;
	private boolean completed;
	private int level = REVISION;
	
	public static final int REVISION = 0;
	public static final int MINOR = 1;
	public static final int MAJOR = 2;
	
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
	
	public MarketService level(int level){
		this.level = level;
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
		
		//return "http://0-2-6.androidquery.appspot.com";
	}
	
	private String getQueryUrl(){
		String appId = getAppId();		
		String url = getHost() + "/api/market?app=" + appId + "&locale=" + locale + "&version=" + getVersion() + "&code=" + getVersionCode() + "&aq=" + AQuery.VERSION;
		return url;
	}
	
	private String getAppId(){
		
		return getApplicationInfo().packageName;
	}
	
	
	private Drawable getAppIcon(){
		Drawable d = getApplicationInfo().loadIcon(act.getPackageManager());
		
		AQUtility.debug("icon", d.getIntrinsicHeight());
		
		//Drawable s = new ScaleDrawable(d, 0x11, 0.5f, 0.5f);
		//s.setLevel(1);
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
		String message = dia.optString("body", "");
		String body = dia.optString("wbody", "");
		String title = dia.optString("title", "Update Available");
		
		AQUtility.debug("wbody", body);
		
		
		version = jo.optString("version", null);
		
		Drawable icon = getAppIcon();
		
		
		Context context = act;
		
		//getDialogStyle(act);
		
		
		final AlertDialog dialog = new AlertDialog.Builder(context)
        .setIcon(icon)
		.setTitle(title)
		//.setMessage(message)
		.setPositiveButton(rate, handler)
        .setNeutralButton(skip, handler)
        .setNegativeButton(update, handler)
        .create();
		
		/*
		if(color == null || rec == null){			
			dialog.setMessage(message);			
			aq.show(dialog);
		}else{
			WebView wv = new WebView(act);	
			aq.id(wv).setLayerType11(AQuery.LAYER_TYPE_SOFTWARE, null);			
			String wbody = patchWBody(body, color);		
			
			wv.setBackgroundColor(0);
			int margin = Math.max(10, Math.min(20, rec.left));
			
			//dialog.setView(wv, margin, margin, margin, margin); 
			dialog.setMessage(Html.fromHtml(patchBody(body), null, handler));
			
			
			

			wv.loadDataWithBaseURL(null, wbody, "text/html", "utf-8", null);
		}
		*/
		//AQUtility.debug("method finished", System.currentTimeMillis());
		
		dialog.setMessage(Html.fromHtml(patchBody(body), null, handler));
		
		
		aq.show(dialog);
		
		
		return;
		
	}

	
	private static String patchWBody(String body, int color){
		String wbody = "<html><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><div style=\"color:#" +  Integer.toHexString(color) + ";\">" + body + "</div></html>"; 
		return wbody;
	}
    
	private static String patchBody(String body){
		return "<small>" + body + "</small>";
	}
	
	private static Integer color;
	private static Rect rec;
	
	private static void getDialogStyle(Activity act){
		
		if(color == null){
			
			AlertDialog dialog = new AlertDialog.Builder(act).setMessage(" ").create();
			
			dialog.show();
			dialog.hide();
			
			TextView tv = (TextView) dialog.findViewById(R.id.message);
			
			if(tv != null){
				int c = tv.getTextColors().getDefaultColor();
				c = c & 0xffffff;
				color = c;	
				rec = new Rect(tv.getPaddingLeft(), tv.getPaddingTop(), tv.getPaddingRight(), tv.getPaddingBottom());				
			}
			
			dialog.dismiss();
			
		}
		
		
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
	
	protected class Handler implements DialogInterface.OnClickListener, TagHandler{
        
		public void marketCb(String url, JSONObject jo, AjaxStatus status){
			
			if(act.isFinishing()) return;
			
			AQUtility.debug(jo);
			
			if(jo != null){
				
				if("1".equals(jo.optString("status"))){
				
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
				progress = 0;
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
