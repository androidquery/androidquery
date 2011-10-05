package com.androidquery;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;

import com.androidquery.AQuery;
import com.androidquery.AbstractAQuery;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;
import com.androidquery.util.Common;

public class TQuery extends AbstractAQuery<TQuery>{

	public TQuery(Activity act) {
		super(act);
	}

	public TQuery(View view) {
		super(view);
	}
	
	
	public void dismissDialogs(Activity act){
		
		/*
		Window win = act.getWindow();
		
		AQUtility.debug("window", act.getWindow());
		WindowManager wm = act.getWindowManager();
		
		
		View view = win.findViewById(android.R.id.message);
		AQUtility.debug("dia view", view);
		
		View code = win.findViewById(R.id.code);
		AQUtility.debug("code view", code);
		
		View code2 = id(R.id.code).getView();
		AQUtility.debug("code2 view", code2);
		*/
	}
	
	
}
