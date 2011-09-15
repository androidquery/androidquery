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

public class AjaxStatus {

	private int code;
	private String message;
	private String redirect;
	private byte[] data;
	private Date time;
	private boolean refresh;
	private DefaultHttpClient client;
	
	public AjaxStatus(int code, String message, String redirect, byte[] data, Date time, boolean refresh){
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

	public boolean getRefresh() {
		return refresh;
	}
	
	protected void setRefresh(boolean refresh) {
		this.refresh = refresh;
	}



	public void setClient(DefaultHttpClient client) {
		this.client = client;
	}



	public DefaultHttpClient getClient() {
		return client;
	}
	
}
