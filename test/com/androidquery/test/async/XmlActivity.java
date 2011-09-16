package com.androidquery.test.async;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import android.os.Bundle;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.test.RunSourceActivity;
import com.androidquery.util.AQUtility;
import com.androidquery.util.XmlDom;

public class XmlActivity extends RunSourceActivity {

	private String type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		type = getIntent().getStringExtra("type");
			
	}
	
	@Override
	protected void runSource(){
		
		AQUtility.debug("run", type);
		
		AQUtility.invokeHandler(this, type, false, null);
	}
	
	public void xml_ajax(){
		
		showProgress(true);		
		
		String url = "https://picasaweb.google.com/data/feed/base/featured?max-results=8";		
		aq.ajax(url, XmlDom.class, this, "picasaCb");
		
	}
	
	public void picasaCb(String url, XmlDom xml, AjaxStatus status){
		
		showProgress(false);
		showResult(xml);		
		if(xml == null) return;

		List<XmlDom> entries = xml.tags("entry");
		
		List<String> titles = new ArrayList<String>();
		
		String imageUrl = null;
		
		for(XmlDom entry: entries){
			titles.add(entry.text("title"));
			imageUrl = entry.tag("content", "type", "image/jpeg").attr("src");
			
		}
		
		showTextResult(titles);
		
		aq.id(R.id.image).image(imageUrl);
		
		
		
	}
	
	
	public void xml_resource() throws SAXException{
		
		InputStream is = getResources().openRawResource(R.raw.code);
		
		XmlDom xml = new XmlDom(is);		
		String code1 = xml.tag("entry", "name", "snippet1").text();
		
		showTextResult(code1);		
		showResult(xml);
		
	}
	
}
