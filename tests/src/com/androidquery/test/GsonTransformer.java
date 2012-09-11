package com.androidquery.test;

import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.Transformer;
import com.google.gson.Gson;

public class GsonTransformer implements Transformer{

	@Override
	public <T> T transform(String url, Class<T> type, String encoding, byte[] data, AjaxStatus status) {
		
		Gson g = new Gson();
		return g.fromJson(new String(data), type);
		
		
	}


}
