package com.androidquery.auth;

import java.net.HttpURLConnection;

import org.apache.http.HttpRequest;

import android.net.Uri;

import com.androidquery.callback.AbstractAjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;

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
		return false;
	}

	@Override
	public boolean reauth(AbstractAjaxCallback<?, ?> cb) {		
		return false;
	}

	@Override	
	public void applyToken(AbstractAjaxCallback<?, ?> cb, HttpRequest request){		
	
		String cred = username + ":" + password;
		byte[] data = cred.getBytes();
		
		String auth = "Basic " + new String(AQUtility.encode64(data, 0, data.length));
		
		Uri uri = Uri.parse(cb.getUrl());
		
		String host = uri.getHost();
		request.addHeader("Host", host);
		request.addHeader("Authorization", auth);
		
	}
	
	@Override	
	public void applyToken(AbstractAjaxCallback<?, ?> cb, HttpURLConnection conn){
		
		String cred = username + ":" + password;
		byte[] data = cred.getBytes();
		
		String auth = "Basic " + new String(AQUtility.encode64(data, 0, data.length));
		
		Uri uri = Uri.parse(cb.getUrl());
		
		String host = uri.getHost();
		conn.setRequestProperty("Host", host);
		conn.setRequestProperty("Authorization", auth);
		
	}
	

}
