package com.androidquery.auth;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.androidquery.AQuery;
import com.androidquery.WebDialog;
import com.androidquery.callback.AbstractAjaxCallback;
import com.androidquery.callback.AjaxCallback;
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
	
	private boolean first;
	private boolean sso;
	private int requestId;
	
	public FacebookHandle(Activity act, String appId, String permissions) {
		this(act, appId, permissions, null);
	}
	
	public FacebookHandle(Activity act, String appId, String permissions, String accessToken) {
		
		this.appId = appId;
		this.act = act;
		this.permissions = permissions;
		
		this.token = accessToken;
				
		if(token == null && permissionOk(permissions, fetchPermission())){
			token = fetchToken();
		}
		
		first = token == null;
	}
	
	public String getToken(){
		return token;
	}
	
	public static String getToken(Context context){
		
		return PreferenceManager.getDefaultSharedPreferences(context).getString(FB_TOKEN, null);
		
	}
	
	public FacebookHandle sso(int requestId){
		this.sso = true;
		this.requestId = requestId;
		return this;
	}
	
	
	private boolean permissionOk(String permissions, String old){
		
		if(permissions == null) return true;
		if(old == null) return false;
		
		String[] splits = old.split("[,\\s]+");
		Set<String> oldSet = new HashSet<String>(Arrays.asList(splits));
		
		splits = permissions.split("[,\\s]+");
		
		
		for(int i = 0; i < splits.length; i++){
			if(!oldSet.contains(splits[i])){
				AQUtility.debug("perm mismatch");
				return false;
			}
		}
		
		return true;
		
	}
	

    public FacebookHandle message(String message){
    	this.message = message;
    	return this;
    }
    
    public FacebookHandle setLoadingMessage(int resId){
    	this.message = act.getString(resId);
    	return this;
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
	
	private void hide(){
		if(dialog != null){
			try{
				dialog.hide();
			}catch(Exception e){
				AQUtility.debug(e);
			}
		}
	}
	
	private void failure(){
		failure("cancel");
	}
	
	
	private void failure(String message){
		dismiss();
		failure(act, AjaxStatus.AUTH_ERROR, message);
	}
	
	protected void auth() {
		
		if(act.isFinishing()) return;
		
		
		boolean ok = sso();
		
		AQUtility.debug("authing", ok);
		
		
		if(!ok){
			webAuth();
		}
		
	}
	
	private boolean sso(){
		
		if(!sso) return false;
		return startSingleSignOn(act, appId, permissions, requestId);
		
	}
	
	private void webAuth() {

		AQUtility.debug("web auth");
		
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
			
			AQUtility.debug("auth hide");
			hide();
			
		}
		
		dialog.load();
		
		AQUtility.debug("auth started");
	}
	
	private static final String FB_TOKEN = "aq.fb.token";
	private static final String FB_PERMISSION = "aq.fb.permission";
	
	private String fetchToken(){
		return PreferenceManager.getDefaultSharedPreferences(act).getString(FB_TOKEN, null);	
	}
	
	private String fetchPermission(){
		return PreferenceManager.getDefaultSharedPreferences(act).getString(FB_PERMISSION, null);	
	}
	
	private void storeToken(String token, String permission){
		Editor editor = PreferenceManager.getDefaultSharedPreferences(act).edit();
		editor.putString(FB_TOKEN, token).putString(FB_PERMISSION, permission);//.commit();	
		AQUtility.apply(editor);
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
					storeToken(token, permissions);
					first = false;
					authenticated(token);
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
			
			super.onPageFinished(view, url);			
			show();
			
			AQUtility.debug("finished", url);
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

	//03-17 17:05:40.594: W/AQuery(23190): error:{"error":{"message":"Error validating access token: User 1318428934 has not authorized application 155734287864315.","type":"OAuthException","code":190}}

	@Override
	public boolean expired(AbstractAjaxCallback<?, ?> cb, AjaxStatus status) {
		
		int code = status.getCode();
		if(code == 200) return false;
		
		String error = status.getError();
		if(error != null && error.contains("OAuthException")){
			AQUtility.debug("fb token expired");
			return true;
		}
		
		String url = cb.getUrl();
		
		if(code == 400 && (url.endsWith("/likes") || url.endsWith("/comments") || url.endsWith("/checkins"))){
			return false;
		}
		
		if(code == 403 && (url.endsWith("/feed") || url.contains("method=delete"))){
			return false;
		}
		
		return code == 400 || code == 401 || code == 403;
	}

	@Override
	public boolean reauth(final AbstractAjaxCallback<?, ?> cb) {
		
		AQUtility.debug("reauth requested");
		
		token = null;
		
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
		storeToken(null, null);
	}
	
    private boolean startSingleSignOn(Activity activity, String applicationId, String permissions, int activityCode) {
        
    	boolean didSucceed = true;
        Intent intent = new Intent();

        intent.setClassName("com.facebook.katana", "com.facebook.katana.ProxyAuth");
        intent.putExtra("client_id", applicationId);
        
        if(permissions != null) {
            intent.putExtra("scope", permissions);
        }

        if(!validateAppSignatureForIntent(activity, intent)){
            return false;
        }

        try {
            activity.startActivityForResult(intent, activityCode);
        } catch (ActivityNotFoundException e) {
            didSucceed = false;
        }

        return didSucceed;
    }
    
    private static Boolean hasSSO;
    
    public boolean isSSOAvailable(){
    	
    	if(hasSSO == null){
    		Intent intent = new Intent();
    		intent.setClassName("com.facebook.katana", "com.facebook.katana.ProxyAuth");
    		hasSSO = validateAppSignatureForIntent(act, intent);
    	}
    	
    	return hasSSO;
    }
    
	protected void authenticated(String token){
		
	}
    
	public void ajaxProfile(AjaxCallback<JSONObject> cb){
		ajaxProfile(cb, 0);
	}

    public void ajaxProfile(AjaxCallback<JSONObject> cb, long expire){
    	
		String url = "https://graph.facebook.com/me";
		
		AQuery aq = new AQuery(act);	
		aq.auth(this).ajax(url, JSONObject.class, expire, cb);
    
    }
    
    private boolean validateAppSignatureForIntent(Context context, Intent intent) {

    	PackageManager pm = context.getPackageManager();
    	
        ResolveInfo resolveInfo = pm.resolveActivity(intent, 0);
        if(resolveInfo == null){
            return false;
        }

        String packageName = resolveInfo.activityInfo.packageName;
        PackageInfo packageInfo;
        try {
            packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
        } catch (NameNotFoundException e) {
            return false;
        }

        for(Signature signature : packageInfo.signatures) {
            if(signature.toCharsString().equals(FB_APP_SIGNATURE)) {
                return true;
            }
        }
        return false;
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        
    	AQUtility.debug("on result", resultCode);
    	
        // Successfully redirected.
        if (resultCode == Activity.RESULT_OK) {

            // Check OAuth 2.0/2.10 error code.
            String error = data.getStringExtra("error");
            if (error == null) {
                error = data.getStringExtra("error_type");
            }

           
            // A Facebook error occurred.
            if(error != null) {
            	AQUtility.debug("error", error);
            	if(error.equals("service_disabled") || error.equals("AndroidAuthKillSwitchException")) {
            		webAuth();
                }else{                	
                	
                	String description = data.getStringExtra("error_description");
                    AQUtility.debug("fb error", description);
                    Log.e("fb error", description);       	
                	failure(description);
                }
            // No errors.
            }else{
            	
            	token = data.getStringExtra("access_token");
            	
            	AQUtility.debug("onComplete", token);
				if(token != null){
					storeToken(token, permissions);
					first = false;
					authenticated(token);
					success(act);
				}else{
					failure();
				}
            	
            }

        // An error occurred before we could be redirected.
        }else if (resultCode == Activity.RESULT_CANCELED) {
            failure();
        }
    
    }
	
    
    public static final String FB_APP_SIGNATURE =
        "30820268308201d102044a9c4610300d06092a864886f70d0101040500307a310"
        + "b3009060355040613025553310b30090603550408130243413112301006035504"
        + "07130950616c6f20416c746f31183016060355040a130f46616365626f6f6b204"
        + "d6f62696c653111300f060355040b130846616365626f6f6b311d301b06035504"
        + "03131446616365626f6f6b20436f72706f726174696f6e3020170d30393038333"
        + "13231353231365a180f32303530303932353231353231365a307a310b30090603"
        + "55040613025553310b30090603550408130243413112301006035504071309506"
        + "16c6f20416c746f31183016060355040a130f46616365626f6f6b204d6f62696c"
        + "653111300f060355040b130846616365626f6f6b311d301b06035504031314466"
        + "16365626f6f6b20436f72706f726174696f6e30819f300d06092a864886f70d01"
        + "0101050003818d0030818902818100c207d51df8eb8c97d93ba0c8c1002c928fa"
        + "b00dc1b42fca5e66e99cc3023ed2d214d822bc59e8e35ddcf5f44c7ae8ade50d7"
        + "e0c434f500e6c131f4a2834f987fc46406115de2018ebbb0d5a3c261bd97581cc"
        + "fef76afc7135a6d59e8855ecd7eacc8f8737e794c60a761c536b72b11fac8e603"
        + "f5da1a2d54aa103b8a13c0dbc10203010001300d06092a864886f70d010104050"
        + "0038181005ee9be8bcbb250648d3b741290a82a1c9dc2e76a0af2f2228f1d9f9c"
        + "4007529c446a70175c5a900d5141812866db46be6559e2141616483998211f4a6"
        + "73149fb2232a10d247663b26a9031e15f84bc1c74d141ff98a02d76f85b2c8ab2"
        + "571b6469b232d8e768a7f7ca04f7abe4a775615916c07940656b58717457b42bd"
        + "928a2";
}
