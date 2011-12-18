package com.androidquery.callback;


public interface Transformer{

	public <T> T transform(String url, Class<T> type, String encoding, byte[] data, AjaxStatus status);

	
}
