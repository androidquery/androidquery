package com.androidquery.callback;

public class AjaxStatus {

	private int code;
	private String message;
	private String redirect;
	private byte[] data;
	
	protected AjaxStatus(int code, String message, String redirect, byte[] data){
		this.code = code;
		this.message = message;
		this.redirect = redirect;
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public String getRedirect() {
		return redirect;
	}

	protected byte[] getData() {
		return data;
	}
	
	
	
}
