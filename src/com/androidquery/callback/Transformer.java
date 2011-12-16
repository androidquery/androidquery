package com.androidquery.callback;

import android.graphics.Bitmap;

public interface Transformer{

	public <T> T transform(String url, Class<T> type, String encoding, byte[] data, AjaxStatus status);

	
}
