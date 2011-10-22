package com.androidquery.test;

import android.os.Bundle;

import com.androidquery.R;
import com.androidquery.util.AQUtility;

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
		
	}
	
}
