/*******************************************************************************
 * Copyright 2012 AndroidQuery (tinyeeliu@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Additional Note:
 * 1. You cannot use AndroidQuery's Facebook app account in your own apps.
 * 2. You cannot republish the app as is with advertisements.
 ******************************************************************************/
package com.androidquery.test;


import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;




@SuppressLint("NewApi")
public class MainApplication extends Application{

	
	public static final String MOBILE_AGENT = "Mozilla/5.0 (Linux; U; Android 2.2) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533";		
	
	
	@Override
    public void onCreate() {     
        
		if(AQuery.SDK_INT >= 9 && TestUtility.isTestDevice(this)) {
			AQUtility.debug("enable strict mode!");
			
	         StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
             .detectAll()
             .penaltyLog()
             .build());
	         
	         StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
             .detectAll()
             .penaltyLog()
             //.penaltyDeath()
             .build());
	    }
        
        super.onCreate();
        
        //AjaxCallback.setNetworkLimit(1);
        
    }
	
	
	
	
	@Override
	public void onLowMemory(){	
    	BitmapAjaxCallback.clearCache();
    }


	
	

}
