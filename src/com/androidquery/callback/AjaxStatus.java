package com.androidquery.callback;

import java.util.Date;

public class AjaxStatus {

	private int code;
	private String message;
	private String redirect;
	private byte[] data;
	private Date time;
	
	protected AjaxStatus(int code, String message, String redirect, byte[] data, Date time){
		this.code = code;
		this.message = message;
		this.redirect = redirect;
		this.data = data;
		this.time = time;
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
	
	public Date getTime(){
		return time;
	}
	
	
}
