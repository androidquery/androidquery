package com.androidquery.callback;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.androidquery.util.Utility;

public abstract class JSONAjaxCallback extends AjaxCallback<JSONObject>{

	@Override
	public JSONObject transform(byte[] data) {
		
		JSONObject result = null;
    	
    	try {    		
    		String str = new String(data);
			result = (JSONObject) new JSONTokener(str).nextValue();
		} catch (Exception e) {	  		
			Utility.report(e);
		}
		return result;
	}
	
}
