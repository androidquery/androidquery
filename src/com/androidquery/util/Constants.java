/*
 * Copyright 2011 - AndroidQuery.com (tinyeeliu@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.androidquery.util;

public interface Constants {


	public static final String VERSION = "0.24.3";
	
	public static final int LAYER_TYPE_SOFTWARE = 1;
	public static final int LAYER_TYPE_HARDWARE = 2;
	public static final int FLAG_HARDWARE_ACCELERATED = 0x01000000;
	public static final int FLAG_ACTIVITY_NO_ANIMATION = 0x00010000;
	public static final int OVER_SCROLL_ALWAYS = 0;
	public static final int OVER_SCROLL_IF_CONTENT_SCROLLS = 1;
	public static final int OVER_SCROLL_NEVER = 2;
	public static final int INVISIBLE = -1;
	public static final int GONE = -2;
	public static final int PRESET = -3;
	public static final int FADE_IN = -1;
	public static final int FADE_IN_NETWORK = -2;
	public static final int FADE_IN_FILE = -3;
	
	public static final int CACHE_DEFAULT = 0;
	public static final int CACHE_PERSISTENT = 1;
	
	public static final int METHOD_GET = 0;
	public static final int METHOD_POST = 1;
	public static final int METHOD_DELETE = 2;
	public static final int METHOD_PUT = 3;
	public static final int METHOD_DETECT = 4;
	
	public static final int TAG_URL = 0x40FF0001;
	public static final int TAG_SCROLL_LISTENER = 0x40FF0002;
	public static final int TAG_LAYOUT = 0x40FF0003;
	public static final int TAG_NUM = 0x40FF0004;
	
	public static final float RATIO_PRESERVE = Float.MAX_VALUE;
	public static final float ANCHOR_DYNAMIC = Float.MAX_VALUE;
	
	public static final String ACTIVE_ACCOUNT = "aq.account";
	
	public static final String AUTH_READER = "g.reader";
	public static final String AUTH_PICASA = "g.lh2";
	public static final String AUTH_SPREADSHEETS = "g.wise";
	public static final String AUTH_DOC_LIST = "g.writely";
	public static final String AUTH_YOUTUBE = "g.youtube";
	public static final String AUTH_ANALYTICS = "g.analytics";
	public static final String AUTH_BLOGGER = "g.blogger";
	public static final String AUTH_CALENDAR = "g.cl";
	//public static final String AUTH_BUZZ = "g.buzz";
	public static final String AUTH_CONTACTS = "g.cp";
	//public static final String AUTH_FINANCE = "g.finance";
	public static final String AUTH_MAPS = "g.local";

	public static final String POST_ENTITY = "%entity";
	
	public static final int SDK_INT = android.os.Build.VERSION.SDK_INT;
}