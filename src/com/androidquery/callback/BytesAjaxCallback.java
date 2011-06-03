package com.androidquery.callback;

import org.json.JSONObject;

public abstract class BytesAjaxCallback extends AjaxCallback<byte[]>{

	@Override
	public byte[] transform(byte[] data) {
		return data;
	}
	
}
