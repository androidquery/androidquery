package com.androidquery.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;
import com.androidquery.util.XmlDom;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class AQueryXmlTest extends AbstractTest<AQueryTestActivity> {

	private XmlDom xml;
	
	private String url;
	private Object result;
	private AjaxStatus status;
	
	public void done(String url, Object result, AjaxStatus status){
		
		this.url = url;
		this.result = result;
		this.status = status;

		log("done", result);
		
		assertTrue(AQUtility.isUIThread());
		
		done();
		
	}
	
	public AQueryXmlTest() throws SAXException {		
		super(AQueryTestActivity.class);
		
		
    }
	
	protected void setUp() throws Exception {
        super.setUp();
        
        InputStream is = this.getActivity().getResources().openRawResource(R.raw.xml_test);
		xml = new XmlDom(is);
    }
	
	public void testGetElement() {
		
		assertNotNull(xml.getElement());
		
    }
	
	
	public void testTag(){
		
		assertNotNull(xml.tag("id"));
		assertNotNull(xml.tag("entry"));
		assertNull(xml.tag("what"));
		assertNull(xml.tag("what"));
	
		assertEquals("3479", xml.tag("openSearch:totalResults").text());
	}
	
	public void testTag2(){
		
		assertNotNull(xml.tag("link", null, null));		
		assertNotNull(xml.tag("link", "rel", null));
		assertNotNull(xml.tag("link", "rel", "self"));
		assertNull(xml.tag("link", "what", null));		
		assertNull(xml.tag("link", "rel", "what"));
		
	}
	
	public void testTags(){
		
		assertEquals(0, xml.tags("what").size());
		assertEquals(8, xml.tags("entry").size());
		
	}
	
	public void testTags2(){
		
		XmlDom entry = xml.tag("entry");
		
		assertEquals(5, entry.tags("link", null, null).size());
		assertEquals(5, entry.tags("link", "rel", null).size());
		assertEquals(1, entry.tags("link", "rel", "self").size());
	}
	
	public void testChild(){
		
		assertNotNull(xml.child("link"));
		
	}
	
	public void testChild2(){
		
		assertNotNull(xml.child("link", null, null));
		assertEquals("application/atom+xml", xml.child("link", "rel", "self").attr("type"));
	}
	
	public void testChildren(){
		
		assertEquals(3, xml.children("link").size());
		assertEquals(0, xml.children("what").size());
	}
	
	public void testChildren2(){
		
		assertEquals(3, xml.children("link", null, null).size());
		assertEquals(3, xml.children("link", "type", null).size());
		assertEquals(0, xml.children("link", "what", null).size());
		assertEquals(1, xml.children("link", "rel", "self").size());
		assertEquals(0, xml.children("link", "rel", "what").size());
		
	}
	
	public void testText(){
		
		assertEquals("https://picasaweb.google.com/data/feed/base/featured", xml.child("id").text());
	}

	public void testText2(){
		
		assertEquals("https://picasaweb.google.com/data/feed/base/featured", xml.text("id"));
		assertEquals("EricJamesPhoto_10.jpg", xml.child("entry").text("title"));
	}
	
	public void testAttr(){
		
		assertEquals("application/atom+xml", xml.child("link").attr("type"));
	}
	
	public void testToString() throws SAXException{
		
		InputStream is = this.getActivity().getResources().openRawResource(R.raw.colors);
		xml = new XmlDom(is);
		
		assertTrue(xml.toString().length() > 300);
		
	}
	
	public void testToString2() throws SAXException{
		
		InputStream is = this.getActivity().getResources().openRawResource(R.raw.colors);
		xml = new XmlDom(is);
		
		String str = xml.toString(4);
		assertTrue(str.length() > 300);
		
		String str2 = xml.tag("entry").toString(4);
		assertTrue(str2.length() > 10);
	
		AQUtility.debug("script", str2);
	}	
	
	public void testText3() throws Exception{
		
		InputStream is = this.getActivity().getResources().openRawResource(R.raw.colors);
		xml = new XmlDom(is);
		
		String text = xml.tag("resources").text();
		
		assertEquals(0, text.length());
		
		String text2 = xml.tag("script").text();
		assertTrue(text2.length() > 20);
		
		AQUtility.debug("script", text2);
		
	}
	
	public void testMalformXml(){
		
		String url = "http://fotbollskanalen.apps.tv4.se/news/topnews";
		
		AjaxCallback<XmlDom> cb = new AjaxCallback<XmlDom>(){
			@Override
			public void callback(String url, XmlDom object, AjaxStatus status) {
				done(url, object, status);
			}
		};
		cb.url(url).type(XmlDom.class);
			
        aq.ajax(cb);
        
        waitAsync();
        
        XmlDom xml = (XmlDom) result;
       
        assertNotNull(xml);
        
        AQUtility.debug(xml);
        
		
	}
	
}
