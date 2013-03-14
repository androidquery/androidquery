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

package com.androidquery.auth;

import java.io.IOException;

import org.apache.http.HttpRequest;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.androidquery.AQuery;
import com.androidquery.callback.AbstractAjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;

/**
 * AQuery internal use only. Handle account, account manager related tasks.
 * 
 */

public class GoogleHandle extends AccountHandle implements DialogInterface.OnClickListener, OnCancelListener{

	private AccountManager am;
	private Account acc;
	private String type;
	private Activity act;
	private String email;
	private Account[] accs;
	private String token;
	
	public GoogleHandle(Activity act, String type, String email){
	
		if(AQuery.ACTIVE_ACCOUNT.equals(email)){
			email = getActiveAccount(act);
		}
		
		this.act = act;
		this.type = type.substring(2);
		this.email = email;
		this.am = AccountManager.get(act);
		
	}
	
	@Override
	protected void auth(){
		
		if(email == null){
			accountDialog();
		}else{
	        Account[] accounts = am.getAccountsByType("com.google");
	        for(int i = 0; i < accounts.length; i++) {
	        	Account account = accounts[i];
	            if(email.equals(account.name)) {
	            	auth(account);
	            	return;
	            }
	        }
		}
	}
	
	
	public boolean reauth(AbstractAjaxCallback<?, ?> cb){
		
		am.invalidateAuthToken(acc.type, token);
		
		try {
			token = am.blockingGetAuthToken(acc, type, true);
			AQUtility.debug("re token", token);
		} catch (Exception e) {			
			AQUtility.debug(e);
			token = null;
		} 
		
		return token != null;
		
	}
	
	public String getType(){
		return type;
	}
	
	private void accountDialog() {
	    
        AlertDialog.Builder builder = new AlertDialog.Builder(act);
        //builder.setTitle("Select a Google account");
        accs = am.getAccountsByType("com.google");
        int size = accs.length;
        
        if(size == 1){
        	auth(accs[0]);
        }else{
        
	        String[] names = new String[size];
	        for(int i = 0; i < size; i++) {
	        	names[i] = accs[i].name;
	        }
	        builder.setItems(names, this);
	        builder.setOnCancelListener(this);
	        
	        AlertDialog dialog = builder.create();//.show();
	        new AQuery(act).show(dialog);
        }
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		
		Account acc = accs[which];
		AQUtility.debug("acc", acc.name);
		
		setActiveAccount(act, acc.name);		
		auth(acc);
	}
	
	public static void setActiveAccount(Context context, String account){
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString(AQuery.ACTIVE_ACCOUNT, account).commit();		
	}

	public static String getActiveAccount(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context).getString(AQuery.ACTIVE_ACCOUNT, null);
	}
	
	private void auth(Account account){
		
		this.acc = account;
		
		Task task = new Task();
		task.execute();
	}
	
	private class Task extends AsyncTask<String, String, Bundle>{
	
		@Override
		protected Bundle doInBackground(String... params) {
	
			AccountManagerFuture<Bundle> future = am.getAuthToken(acc, type, null, act, null, null);
			
			Bundle bundle = null;
			
			try {
				bundle = future.getResult();
			} catch (OperationCanceledException e) {
			} catch (AuthenticatorException e) {
				AQUtility.debug(e);
			} catch (IOException e) {
				AQUtility.debug(e);
			}
			
			return bundle;
		}
		
		
		@Override
		protected void onPostExecute(Bundle bundle) {
			
			if(bundle != null && bundle.containsKey(AccountManager.KEY_AUTHTOKEN)) {
	          	token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
	          	//AQUtility.debug("stored auth", token);        	
	          	success(act);
			}else{
	        	failure(act, AjaxStatus.AUTH_ERROR, "rejected");
	        }
			
		}
	
		
	
	}
	

	@Override
	public void onCancel(DialogInterface dialog) {		
		failure(act, AjaxStatus.AUTH_ERROR, "cancel");
	}
	
	@Override
	public boolean expired(AbstractAjaxCallback<?, ?> cb, AjaxStatus status) {
		int code = status.getCode();
		return code == 401 || code == 403;
	}
	
	@Override
	public void applyToken(AbstractAjaxCallback<?, ?> cb, HttpRequest request) {
		
		//AQUtility.debug("apply token", token);
		
		request.addHeader("Authorization", "GoogleLogin auth=" + token);
	}

	@Override
	public String getCacheUrl(String url){
		return url + "#" + token;
	}


	@Override
	public boolean authenticated() {
		return token != null;
	}

	
	
}
