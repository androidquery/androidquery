package com.androidquery.util;

public class HttpResult {
	
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
