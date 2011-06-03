package com.androidquery.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class IOUtility {

	private static int NET_TIMEOUT = 30000;
	
	public static HttpResult openBytes(String urlPath, boolean retry) throws IOException{
				
		URL url = new URL(urlPath);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
           
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);     
        connection.setConnectTimeout(NET_TIMEOUT);
        
        int code = connection.getResponseCode();
       
        if(code == -1 && retry){
        	Utility.debug("code -1", urlPath);
        	return openBytes(urlPath, false);
        }
        
        byte[] data = null;
        String redirect = urlPath;
        if(code == -1 || code < 200 || code >= 300){        	
        	//throw new IOException();
        }else{
        	data = DataUtility.toBytes(connection.getInputStream());
        	redirect = connection.getURL().toExternalForm();
        }
        
        
        HttpResult result = new HttpResult();
        result.setData(data);
        result.setCode(code);
        result.setMessage(connection.getResponseMessage());
        result.setRedirect(redirect);
        
        return result;
	}
	
	
}

class HttpResult{
	
	private byte[] data;
	private int code;
	private String redirect;
	private String message;
	
	
	public void setCode(int code) {
		this.code = code;
	}
	public int getCode() {
		return code;
	}
	public void setRedirect(String redirect) {
		this.redirect = redirect;
	}
	public String getRedirect() {
		return redirect;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public byte[] getData() {
		return data;
	}
	
}
