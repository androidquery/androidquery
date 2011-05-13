package com.androidquery;

import java.lang.reflect.Method;

import android.app.Activity;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class AQuery extends AbstractAQuery<AQuery>{

	public AQuery(Activity act) {
		super(act);
	}
	
	public AQuery(View view) {
		super(view);
	}

	@Override
	protected AQuery create(View view) {
		return new AQuery(view);
	}

}
