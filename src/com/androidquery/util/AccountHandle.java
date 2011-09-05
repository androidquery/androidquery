package com.androidquery.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.os.Bundle;



public class AccountHandle {

	private AccountManager am;
	private Account acc;
	private String authToken;
	private String authType;
		
	public static AccountHandle makeHandle(Context context, String account, String authType){
		
		if(android.os.Build.VERSION.SDK_INT < 5) return null;
		
		AQUtility.time("find account");
		
		AccountManager manager = AccountManager.get(context);
        
		Account[] accounts = manager.getAccountsByType("com.google");
        for(int i = 0; i < accounts.length; i++) {
        	Account acc = accounts[i];
            if(account.equals(acc.name)) {
            	
            	AccountHandle ah = new AccountHandle();
            	
            	ah.am = manager;
            	ah.acc = acc;
            	ah.authType = authType;
            	
            	AQUtility.debug("account ok", acc.name);
            	AQUtility.timeEnd("find account", 0);
            	return ah;
            }
        }
		
        
        AQUtility.debug("account doesn't exist", account);
        return null;
	}
	
	public String setupAuthToken(boolean expired){
		
		if(am == null) return null;
		
		if(expired){
			AQUtility.debug("expired invalidate");
			am.invalidateAuthToken(authType, authToken);
		}
		
		AQUtility.time("auth future");
		
    	AccountManagerFuture<Bundle> future = am.getAuthToken(acc, authType, true, null, null);
		
    	AQUtility.timeEnd("auth future", 0);
    	
		Bundle bundle = null;
		try {
			bundle = future.getResult();
		} catch (Exception e) {
			AQUtility.report(e);
		} 
		
		if(bundle != null && bundle.containsKey(AccountManager.KEY_AUTHTOKEN)){
			String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
			
			AQUtility.debug("tok", token);
			
			//authToken(token);
			authToken = token;
		}
		
		return authToken;
		
	}
	
	
}
