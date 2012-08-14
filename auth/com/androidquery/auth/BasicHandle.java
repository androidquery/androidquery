package com.androidquery.auth;

import java.net.HttpURLConnection;

import org.apache.http.HttpRequest;

import android.net.Uri;

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
		
		String auth = "Basic " + new String(encode(data, 0, data.length));
		
		Uri uri = Uri.parse(cb.getUrl());
		
		String host = uri.getHost();
		request.addHeader("Host", host);
		request.addHeader("Authorization", auth);
		
	}
	
	@Override	
	public void applyToken(AbstractAjaxCallback<?, ?> cb, HttpURLConnection conn){
		
		String cred = username + ":" + password;
		byte[] data = cred.getBytes();
		
		String auth = "Basic " + new String(encode(data, 0, data.length));
		
		Uri uri = Uri.parse(cb.getUrl());
		
		String host = uri.getHost();
		conn.setRequestProperty("Host", host);
		conn.setRequestProperty("Authorization", auth);
		
	}
	
	// Mapping table from 6-bit nibbles to Base64 characters.
	private static final char[] map1 = new char[64];
	   static {
	      int i=0;
	      for (char c='A'; c<='Z'; c++) map1[i++] = c;
	      for (char c='a'; c<='z'; c++) map1[i++] = c;
	      for (char c='0'; c<='9'; c++) map1[i++] = c;
	      map1[i++] = '+'; map1[i++] = '/'; }

	// Mapping table from Base64 characters to 6-bit nibbles.
	private static final byte[] map2 = new byte[128];
	   static {
	      for (int i=0; i<map2.length; i++) map2[i] = -1;
	      for (int i=0; i<64; i++) map2[map1[i]] = (byte)i; }
	
	//Source: http://www.source-code.biz/base64coder/java/Base64Coder.java.txt
	public static char[] encode (byte[] in, int iOff, int iLen) {
		
	   int oDataLen = (iLen*4+2)/3;       // output length without padding
	   int oLen = ((iLen+2)/3)*4;         // output length including padding
	   char[] out = new char[oLen];
	   int ip = iOff;
	   int iEnd = iOff + iLen;
	   int op = 0;
	   while (ip < iEnd) {
	      int i0 = in[ip++] & 0xff;
	      int i1 = ip < iEnd ? in[ip++] & 0xff : 0;
	      int i2 = ip < iEnd ? in[ip++] & 0xff : 0;
	      int o0 = i0 >>> 2;
	      int o1 = ((i0 &   3) << 4) | (i1 >>> 4);
	      int o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
	      int o3 = i2 & 0x3F;
	      out[op++] = map1[o0];
	      out[op++] = map1[o1];
	      out[op] = op < oDataLen ? map1[o2] : '='; op++;
	      out[op] = op < oDataLen ? map1[o3] : '='; op++; }
	   return out; 
	}

}
