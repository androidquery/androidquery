package com.androidquery.callback;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.androidquery.util.Utility;

public abstract class HTMLAjaxCallback extends AjaxCallback<String>{

	@Override
	public String transform(byte[] data) {
		
		String result = null;
    	
    	try {    		
    		result = new String(data, "UTF-8");
		} catch (Exception e) {	  		
			Utility.report(e);
		}
		return result;
	}
	
}
