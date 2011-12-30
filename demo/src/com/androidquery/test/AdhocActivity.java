package com.androidquery.test;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;

public class AdhocActivity extends Activity {

	private AQuery aq;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.adhoc_activity);
		
		aq = new AQuery(this);
		
		work();
		
	}
	
	private void work(){
		
		//12-30 22:18:58.612: W/AQuery(8517): {"data":{"id":14,"picture":"http:\/\/lh6.ggpht.com\/bHvkpWmmOMFL4c5N_WRVA1OGyz6pPSxN1ojf7nHMQOFIClCp3JA0YisLwHQsdrDVPrr4zFKg9CYPL9icOjCC","desc":"坊間的洋蔥鴨一般釀入大量洋蔥，而且燉至肉質軟腍、入口即溶。有次宴客因太趕不夠時間預備，賓客到齊但洋蔥鴨還未燉夠火侯，出乎意料地鴨肉的質感及味道卻比之前還好吃，你不妨試試。","price":"200.0","name":"洋蔥鴨","link":"http:\/\/aigens-app.appspot.com\/dish\/14#洋蔥鴨"},"status":"1","comments":[]}

		String url = "http://www.vikispot.com/z/images/vikispot/android-w.png";
		
		url = "http://lh6.ggpht.com/bHvkpWmmOMFL4c5N_WRVA1OGyz6pPSxN1ojf7nHMQOFIClCp3JA0YisLwHQsdrDVPrr4zFKg9CYPL9icOjCC";
		
		aq.id(R.id.image2).image(url, true, true);
    	
    	aq.id(R.id.image3).image(url, true, true, 0, 0, null, AQuery.FADE_IN_NETWORK, 1.0f);
    	
    	AQUtility.debug("ad hoc done");
    	
	}
	
	
	protected int getContainer(){
		return R.layout.adhoc_activity;
	}
	
	
	

}
