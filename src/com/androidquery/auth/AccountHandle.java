package com.androidquery.auth;

import java.util.LinkedHashSet;

import org.apache.http.HttpRequest;

import android.content.Context;

import com.androidquery.callback.AbstractAjaxCallback;

public abstract class AccountHandle {

	
	private LinkedHashSet<AbstractAjaxCallback<?, ?>> callbacks;
	
	public synchronized void auth(AbstractAjaxCallback<?, ?> cb){		
		
		if(callbacks == null){
			callbacks = new LinkedHashSet<AbstractAjaxCallback<?,?>>();
			callbacks.add(cb);
			auth();
		}else{
			callbacks.add(cb);
		}
				
	}
	
	
	public abstract boolean authenticated();
	
	protected void success(Context context){
		callback(context);
	}
	
	private synchronized void callback(Context context){
		
		if(callbacks != null){
			
			for(AbstractAjaxCallback<?, ?> cb: callbacks){
				cb.async(context);
			}
			
			callbacks = null;
		}
		
	}
	
	protected synchronized void failure(Context context, int code, String message){		
		
		if(callbacks != null){
			
			for(AbstractAjaxCallback<?, ?> cb: callbacks){
				cb.failure(code, message);
			}
			
			callbacks = null;
		}
		
	}
	
	
	protected abstract void auth();
	
	public abstract boolean expired(int code);
	
	public abstract boolean reauth(AbstractAjaxCallback<?, ?> cb);
	
	public void applyToken(AbstractAjaxCallback<?, ?> cb, HttpRequest request){		
	}
	
	public String getNetworkUrl(String url){
		return url;
	}
	
	public String getCacheUrl(String url){
		return url;
	}
	
	public void unauth(){		
	}
	
}
