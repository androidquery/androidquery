package com.androidquery.test;

import java.io.File;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

public abstract class AbstractTest<T extends Activity> extends ActivityInstrumentationTestCase2<T> {

	protected AQuery aq;
	
	protected String ICON_URL = "http://www.androidquery.com/z/images/vikispot/android-w.png";
	protected String LAND_URL = "http://farm6.static.flickr.com/5035/5802797131_a729dac808_b.jpg";
	protected String INVALID_URL = "http://www.androidquery.com/z/images/vikispot/xyz.png";
	
	
	public AbstractTest(Class cls){
		super("com.androidquery.test", cls);
		AQUtility.setDebug(true);
	}

	protected void setUp() throws Exception {
        super.setUp();
        aq = new AQuery(getActivity());
        AjaxCallback.setSimulateError(false);
        //AjaxCallback.setProxy(null, 0, null, null);
        AjaxCallback.setProxyHandle(null);
        AQUtility.TEST_IO_EXCEPTION = false;
        AQUtility.debug("new act", getActivity() + ":" + getActivity().isFinishing());
    }
	
	protected void log(Object msg, Object msg2){
		AQUtility.debug(msg, msg2);
	}
	
	protected void log(Object msg){
		AQUtility.debug(msg);
	}
	
	protected void waitAsync(){		
		AQUtility.debugWait(10000);		
	}
	
	protected void waitAsync(long wait){		
		AQUtility.debugWait(10000);		
		waitSec(wait);
	}
	
	protected void clearCache(){
		
		BitmapAjaxCallback.clearCache();
		
		File cacheDir = AQUtility.getCacheDir(getActivity());
		AQUtility.cleanCache(cacheDir, 0, 0);
		
		waitSec(2000);
	}
	
	protected void waitSec(){
		waitSec(1000);
	}
	
	
	protected void waitSec(long time){
		
		synchronized(this){
			try {
				wait(time);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	protected void done(){
		
		AQUtility.debugNotify();
		
	}
	
}
