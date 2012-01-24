package com.androidquery.auth;

import org.apache.http.HttpRequest;

import com.androidquery.callback.AbstractAjaxCallback;
import com.androidquery.callback.AjaxStatus;

public class BasicHandle extends AccountHandle{

	private String username;
	private String password;
	
	public BasicHandle(String username, String password){
		
		this.username = username;
		this.password = password;
		
	}
	
	@Override
	public boolean authenticated() {
		return true;
	}

	@Override
	protected void auth() {
		
	}

	@Override
	public boolean expired(AbstractAjaxCallback<?, ?> cb, AjaxStatus status) {
		int code = status.getCode();
		return code == 401;
	}

	@Override
	public boolean reauth(AbstractAjaxCallback<?, ?> cb) {
		
		return false;
	}

	@Override	
	public void applyToken(AbstractAjaxCallback<?, ?> cb, HttpRequest request){		
	
		
	}

}
