package com.androidquerytest;

import com.androidquery.AQuery;
import com.androidquery.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class ListenerTestActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
				
		setContentView(R.layout.listener_test);
		
		setupTest();
		
	}
	
	private void setupTest(){
		
		AQuery aq = new AQuery(this);
		
		aq.id(R.id.clicked1).clicked(this, "clicked1");
		
		aq.id(R.id.clicked2).clicked(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TestUtility.showToast(ListenerTestActivity.this, "pass");
			}
		});
		
	}
	
	public void clicked1(View view){
		
		TestUtility.showToast(this, "pass");
		
	}
	
}
