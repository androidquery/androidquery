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

import java.net.HttpURLConnection;
import java.net.Proxy;
import java.util.LinkedHashSet;

import org.apache.http.HttpRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;

import com.androidquery.callback.AbstractAjaxCallback;
import com.androidquery.callback.AjaxStatus;

public abstract class ProxyHandle {

	
	public abstract void applyProxy(AbstractAjaxCallback<?, ?> cb, HttpRequest request, DefaultHttpClient client);
	
	public abstract Proxy makeProxy(AbstractAjaxCallback<?, ?> cb);
	
	
	
}
