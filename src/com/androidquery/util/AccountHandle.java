package com.androidquery.util;

import java.io.IOException;

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



public class AccountHandle extends AsyncTask<String, String, Bundle> implements DialogInterface.OnClickListener, OnCancelListener{

	private AccountManager am;
	private Account acc;
	private String token;
	private String type;
	private Activity act;
	private String email;
	private Account[] accs;
	private AbstractAjaxCallback<?, ?> cb;	
	
	public AccountHandle(Activity act, String type, String email){
	
		if(AQuery.ACTIVE_ACCOUNT.equals(email)){
			email = getActiveAccount(act);
		}
		
		this.act = act;
		this.type = type;
		this.email = email;
		this.am = AccountManager.get(act);
		
	}
	
	public void async(AbstractAjaxCallback<?, ?> cb){		
		this.cb = cb;
		auth();
	}
	
	public boolean needToken(){
		return cb == null;
	}
	
	private void auth(){
		
		AQUtility.debug("auth", email);
		
		if(email == null){
			accountDialog();
		}else{
	        Account[] accounts = am.getAccountsByType("com.google");
	        for(int i = 0; i < accounts.length; i++) {
	        	Account account = accounts[i];
	            if(email.equals(account.name)) {
	            	AQUtility.debug("account match");
	            	
	            	auth(account);
	            	
	            	
	            	return;
	            }
	        }
		}
	}
	
	
	public String reauth(){
		
		
		am.invalidateAuthToken(acc.type, token);
		
		try {
			token = am.blockingGetAuthToken(acc, type, true);
			AQUtility.debug("re token", token);
		} catch (Exception e) {			
			AQUtility.report(e);
			token = null;
		} 
		
		return token;
		
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
	        builder.create().show();
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
		
		execute();
		
	}
	
	@Override
	protected Bundle doInBackground(String... params) {

		AccountManagerFuture<Bundle> future = am.getAuthToken(acc, type, null, act, null, null);
		
		Bundle bundle = null;
		
		try {
			bundle = future.getResult();
		} catch (OperationCanceledException e) {
		} catch (AuthenticatorException e) {
			AQUtility.report(e);
		} catch (IOException e) {
			AQUtility.report(e);
		}
		
		return bundle;
	}
	
	private void startCb(){
		
		cb.authToken(token);
		cb.async(act);
		
		act = null;
		accs = null;
		cb = null;
	}
	
	public String getToken(){
		return token;
	}
	
	@Override
	protected void onPostExecute(Bundle bundle) {
		
		if(bundle != null && bundle.containsKey(AccountManager.KEY_AUTHTOKEN)) {
          	token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
          	AQUtility.debug("stored auth", token);        	
        }
		
		startCb();
        
		return;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		
		startCb();
	}
	
	
	

	
	
}
