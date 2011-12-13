/*
 * Copyright 2011 - AndroidQuery.com (tinyeeliu@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.androidquery.callback;

import java.util.Date;

import org.apache.http.impl.client.DefaultHttpClient;

import com.androidquery.util.AQUtility;

/**
 * AjaxStatus contains meta information of an AjaxCallback callback.
 */
public class AjaxStatus {

	/** Source NETWORK. */
	public static final int NETWORK = 1;
	
	/** Source DATASTORE. */
	public static final int DATASTORE = 2;
	
	/** Source FILE. */
	public static final int FILE = 3;
	
	/** Source MEMORY. */
	public static final int MEMORY = 4;
	
	public static final int DEVICE = 5;
	
	public static final int NETWORK_ERROR = -101;
	public static final int AUTH_ERROR = -102;
	public static final int TRANSFORM_ERROR = -103;
	
	
	private int code = 200;
	private String message = "OK";
	private String redirect;
	private byte[] data;
	private Date time = new Date();
	private boolean refresh;
	private DefaultHttpClient client;
	private long duration;
	private int source = NETWORK;
	private long start = System.currentTimeMillis();
	private boolean done;
	private boolean invalid;
	private boolean reauth;
	private String error;
	
	public AjaxStatus(){		
	}
	
	public AjaxStatus(int code, String message){
		this.code = code;
		this.message = message;
	}
	
	protected AjaxStatus source(int source){
		this.source = source;
		return this;
	}
	
	protected AjaxStatus code(int code){
		this.code = code;
		return this;
	}
	
	protected AjaxStatus error(String error){
		this.error = error;
		return this;
	}
	
	protected AjaxStatus message(String message){
		this.message = message;
		return this;
	}
	
	protected AjaxStatus redirect(String redirect){
		this.redirect = redirect;
		return this;
	}
	
	protected AjaxStatus time(Date time){
		this.time = time;
		return this;
	}
	
	protected AjaxStatus refresh(boolean refresh){
		this.refresh = refresh;
		return this;
	}
	
	protected AjaxStatus reauth(boolean reauth){
		this.reauth = reauth;
		return this;
	}
	
	protected AjaxStatus client(DefaultHttpClient client){
		this.client = client;
		return this;
	}
	
	protected AjaxStatus done(){
		this.duration = System.currentTimeMillis() - start;
		this.done = true;
		this.reauth = false;
		return this;
	}
	
	
	protected AjaxStatus data(byte[] data){
		this.data = data;
		return this;
	}
	
	public AjaxStatus invalidate(){
		this.invalid = true;
		return this;
	}
	
	protected boolean getDone() {
		return done;
	}
	
	protected boolean getReauth() {
		return reauth;
	}
	
	protected boolean getInvalid() {
		return invalid;
	}
	
	/**
	 * Gets the http response code.
	 *
	 * @return code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Gets the http response message.
	 *
	 * @return message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Gets the redirected url. Returns original url if no redirection.
	 *
	 * @return redirect url
	 */
	public String getRedirect() {
		return redirect;
	}

	protected byte[] getData() {
		return data;
	}
	
	/**
	 * Gets the object fetched time. Returns original fetch time when url is file cached.
	 *
	 * @return original fetch time
	 */
	public Date getTime(){
		return time;
	}

	/**
	 * Gets the refresh param.
	 *
	 * @return refresh
	 */
	public boolean getRefresh() {
		return refresh;
	}
	
	/**
	 * Gets the http client used to fetch the url. User can access other resources like response headers and cookies.
	 * Returns null if object is cached (source is not AjaxStatus.NETWORK).
	 *
	 * @return http client
	 */
	public DefaultHttpClient getClient() {
		return client;
	}

	/**
	 * Gets the duration of the ajax request in millseconds.
	 *
	 * @return duration
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * Gets the source type. Can be AjaxStatus.NETWORK, AjaxStatus.DATASTORE, AjaxStatus.FILE, or AjaxStatus.MEMORY.
	 *
	 * @return source
	 */
	public int getSource() {
		return source;
	}
	
	/**
	 * Gets the error response as a string. For http response code that's not 200-299.
	 *
	 * @return source
	 */
	public String getError() {
		return error;
	}
	
	/**
	 * Test if the response is expired against current time, given the expire duration in milliseconds.
	 * If the ajax source is NETWORK, it's never considered expired.
	 *
	 * @return expire Expire duration in milliseconds.
	 */
	
	public boolean expired(long expire){
		
		long mod = time.getTime();
		long now = System.currentTimeMillis();		
		long diff = now - mod;
		
		if(diff > expire && getSource() != NETWORK){
			return true;
		}
		
		return false;
	}
	
}
