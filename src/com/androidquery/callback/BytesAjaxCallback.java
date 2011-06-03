package com.androidquery.callback;


public abstract class BytesAjaxCallback extends AjaxCallback<byte[]>{

	@Override
	public byte[] transform(byte[] data) {
		return data;
	}
	
}
