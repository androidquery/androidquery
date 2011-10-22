package com.androidquery.test;

import android.os.Bundle;

import com.androidquery.R;

public class AdhocActivity extends RunSourceActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		
	}
	
	
	private void work(){
		
		//runSource();
		
	}
	
	
	protected int getContainer(){
		return R.layout.adhoc_activity;
	}
	
	@Override
	protected void runSource(){
		
		try{
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	

}
