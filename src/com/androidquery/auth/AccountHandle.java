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
		failed = false;
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
	
	private boolean failed;
	protected void failure(Context context){		
		failed = true;
		callback(context);
	}
	
	public boolean failed(){
		return failed;
	}
	
	
	protected abstract void auth();
	
	public abstract boolean expired(int code);
	
	public abstract boolean reauth(AbstractAjaxCallback<?, ?> cb);
	
	public void applyToken(AbstractAjaxCallback<?, ?> cb, HttpRequest request){		
	}
	
	public String applyToken(String url){
		return url;
	}
	
	
}
