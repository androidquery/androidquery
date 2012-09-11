package com.androidquery.test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.view.View;
import android.widget.TextView;


public class AQueryMiscTest extends AbstractTest<AQueryTestActivity> {

	
	public AQueryMiscTest() {		
		super(AQueryTestActivity.class);
    }

	public void testTempFile() {
		
		
		clearCache();
		
		AQUtility.post(new Runnable() {
			
			@Override
			public void run() {
				aq.id(R.id.image).image(ICON_URL);
			}
		});
		
		waitAsync(2000);
		
		assertNotNull(aq.getImageView().getDrawable());
		
		File file = aq.getCachedFile(ICON_URL);
		assertNotNull(file);
		
		AQUtility.time("move");
		
		File temp = aq.makeSharedFile(ICON_URL, "hello.png");
		
		AQUtility.timeEnd("move", 0);
		
		assertTrue(temp.exists());	
		assertTrue(temp.length() > 1000);
		assertTrue(temp.getName().equals("hello.png"));
		
		clearCache();
		
		assertFalse(temp.exists());
		
		
		File ghost = aq.getCachedFile("http://www.abc.com");
		assertNull(ghost);
		
    }
	
	
	public void testAdhoc(){
		
		String str = "eka_2322";
		String[] splits = str.split("\\_");
		
		AQUtility.debug(Arrays.asList(splits));
		
	}
}
