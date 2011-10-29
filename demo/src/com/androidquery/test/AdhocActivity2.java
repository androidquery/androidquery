package com.androidquery.test;

import java.io.UnsupportedEncodingException;

import android.os.Bundle;

import com.androidquery.R;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.androidquery.util.XmlDom;

public class AdhocActivity2 extends RunSourceActivity{

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		work();
	}
	
	
	private void work(){
		
		//runSource();
		
	}
	
	
	protected int getContainer(){
		return R.layout.adhoc_activity2;
	}
	
	@Override
	protected void runSource(){
		
		AQUtility.debug("ad hoc2");
		ajax_encoding();
	}
	
	private void ajax_encoding(){
		
		//String url = "http://www.kyotojp.com/limousine-big5.html";
		String url = "http://192.168.1.222/test/big5.xml";
		
		
		AjaxCallback<XmlDom> cb = new AjaxCallback<XmlDom>();
		//cb.url(url).type(String.class).encoding("Big5").weakHandler(this, "big5cb");
		cb.url(url).type(XmlDom.class).weakHandler(this, "big5cb");
		
		aq.ajax(cb);
		
	}
	
	public void big5cb(String url, String html, AjaxStatus status){
		
		
		AQUtility.debug(html);
		
	}
	
	public void big5cb(String url, XmlDom xml, AjaxStatus status){
		
		
		AQUtility.debug(xml);
		
		if(xml != null){
			String text = xml.text("text");
			AQUtility.debug(text);
		}
		
	}
	
	/*
	public void big5cb(String url, byte[] data, AjaxStatus status){
		
		if(data != null){
			String html = null;
			try {
				html = new String(data, "Big5");
			} catch (UnsupportedEncodingException e) {
			}
			AQUtility.debug(html);
		}
		
		
		
	}
	*/
	
}
