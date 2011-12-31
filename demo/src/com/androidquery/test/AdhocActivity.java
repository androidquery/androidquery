package com.androidquery.test;

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
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
import com.androidquery.callback.BitmapAjaxCallback;
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
	
	private int count = 0;
	
	private void work(){
		
		aq.id(R.id.button).clicked(this, "changeClicked");
		
		change();
    	
	}
	
	public void changeClicked(){
		
		change();
		
	}
	
	private Options reuse = new Options();
	private void change(){
		
		String[] pics = new String[]{
				"http://lh3.ggpht.com/36jZOjOQhiKEww7HYQ0vuIgpoZPXPABUnpaKvJW3oB-ICKsNje2wQrWMpAiecPm4O9ds0YsIGGCLmNvJzctb=w720",	
				"http://lh4.ggpht.com/mERAL6RTzcaqMYeO6yluQNLv4eQQh0Sw8aOI2EPYdiEbBpoAYry_MJJGrNsVnqe-Aglkzn-9FZiwOYRjmC54aQ=w720",
				"http://lh6.ggpht.com/K5iNBUb9q2z1dB8ydKGaM6xxOidaUm0HW843Z63IcxU1Yeth6PlN9liTGN49apSoJdjDFvhieHm6mVsudp1IXA=w720",
				"http://lh6.ggpht.com/bgdvMgCjpRWUHgl72reqwpHIl75aloTORrTi_Zn4LzcNXnIR6M7E42PcW2A7SAtlYE7H87cxpg0DAJvS0Vinsw=w720",
				"http://lh5.ggpht.com/-9vDPE6RQmqoC-YRaamuOpBO1nKVr_ulgGZqK-ZSL5CsTe8n0ygx5UkjAz9MhFgBMyFZSremw5mOkuZWHkwCOA=w720",
				"http://lh6.ggpht.com/c5vZpr2XY6Jkswd1maDJK2FgSVsfhZd02ZE8k6LR0LXl_5u7UEm8Lkv2UacXUJBRspng9p5BPtv-jg81jyY-Jw=w720",
				"http://lh3.ggpht.com/3nQCVQfQToBvde-vG4DzpW0DEXlTi0TbH822NFANDuqgc3ch8YT-Z-8eivO6tfKAVu5KNHQJMRJu5BYb6Qe2=w720",
				"http://lh4.ggpht.com/3Dwdi493BqhW99pMJV7Xt--qnXdofcpqPEh70P5PljI-BSydDtDbM50QxyFaSgsMz9axf6z0P9OXK8KpEn5Hpw=w720"
		};
		
		String url = pics[count++ % pics.length];
		
		BitmapAjaxCallback cb = new BitmapAjaxCallback();
		cb.url(url).ratio(AQuery.RATIO_PRESERVE).reuse(reuse);
		
		aq.id(R.id.image1).image(cb);
		
		//AQUtility.debug("reuse", reuse.inBitmap);
		
		/*
		//aq.id(R.id.image1).image(url, true, true, 0, 0, null, AQuery.FADE_IN_NETWORK, AQuery.RATIO_PRESERVE);
		
		File file = aq.getCachedFile(url);
		
		//AQUtility.debug("cached", file.length());
		if(file == null || !file.exists()){
			aq.id(R.id.image1).image(url, true, true, 0, 0, null, AQuery.FADE_IN_NETWORK, AQuery.RATIO_PRESERVE);
			
		}else{
		
			Options os = new Options();
			os.inBitmap = reuse;
			os.inMutable = true;
			os.inSampleSize = 1;
			
			
			AQUtility.debug("reuse", os.inBitmap);
			
			Bitmap bm = BitmapFactory.decodeFile(file.getAbsolutePath(), os);
			
			AQUtility.debug("same bitmap?", bm == reuse);
			
			reuse = bm;
			
			AQUtility.debug("mut", bm.isMutable());
			
			aq.id(R.id.image1).image(bm, AQuery.RATIO_PRESERVE);
		}
		*/
	}
	
	
	protected int getContainer(){
		return R.layout.adhoc_activity;
	}
	
	
	

}
