package com.androidquery.test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import com.androidquery.util.AQUtility;

public class IOUtility {

	public static final int NET_TIMEOUT = 20000;

	public static byte[] openBytes(String urlPath) throws IOException{
		return openBytes(urlPath, true);
	}
	
	public static byte[] openBytes(String urlPath, boolean retry) throws IOException{
		return openHttpResult(urlPath, retry);
	}
	
	private static final String MOBILE_AGENT = "Mozilla/5.0 (Linux; U; Android 2.2) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533";	
	
	public static byte[] openHttpResult(String urlPath, boolean retry) throws IOException{
		
		AQUtility.debug("net", urlPath);
		
		URL url = new URL(urlPath);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
              
        
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);     
        connection.setConnectTimeout(NET_TIMEOUT);
        
        
        int code = connection.getResponseCode();
       
        if(code == 307 && retry){
        	String redirect = connection.getHeaderField("Location");
        	return openHttpResult(redirect, false);
        }
        
        if(code == -1 && retry){
        	return openHttpResult(urlPath, false);
        }
        
        AQUtility.debug("response", code);
        
        
        if(code == -1 || code < 200 || code >= 300){
        	
        	throw new IOException();
        }
        
        
        byte[] result = AQUtility.toBytes(connection.getInputStream());
        return result;
	}
	
	public static String openString(String urlPath) throws IOException{
		
		byte[] bytes = openBytes(urlPath);		
		return new String(bytes, "UTF-8");
		
	}
	
	
	
	
}
