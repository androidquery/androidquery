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

	
	/*
	private View root;
	private Activity act;
	
	protected View view;
	
	
	public AQuery(Activity act){
		this.act = act;
		this.view = root;
	}
	
	public AQuery(View root){
		this.root = root;
		this.view = root;
	}
	
	public View getView(){
		return view;
	}
	
	private View findView(int id){
		View result = null;
		if(root != null){
			result = root.findViewById(id);
		}else if(act != null){
			result = act.findViewById(id);
		}
		return result;
	}
	
	public AQuery find(int id){
		View view = findView(id);
		return new AQuery(view);
	}
	

	public  id(int id){
		view = findView(id);
		return this;
	}
	
	public AQuery text(CharSequence text){
		
		
		if(view != null){			
			TextView tv = (TextView) view;
			tv.setText(text);
		}
		return this;
	}
	
	public AQuery text(Spanned text){
		
		
		if(view != null){			
			TextView tv = (TextView) view;
			tv.setText(text);
		}
		return this;
	}
	
	public AQuery image(int id){
		
		if(view != null){
			ImageView iv = (ImageView) view;
			if(id == 0){
				iv.setImageBitmap(null);
			}else{				
				iv.setImageResource(id);
			}
		}
		
		return this;
	}
	
	public AQuery transparent(boolean transparent){
		
		if(view != null){
			UIUtility.transparent(view, transparent);
		}
		
		return this;
	}
	
	public AQuery enabled(boolean enable){
		
		if(view != null){
			view.setEnabled(enable);
		}
		
		return this;
	}
	
	
	public AQuery gone(){
		
		if(view != null){
			view.setVisibility(View.GONE);
		}
		
		return this;
	}
	
	public AQuery hide(){
		
		if(view != null){
			view.setVisibility(View.INVISIBLE);
		}
		
		return this;
	}
	
	public AQuery show(){
		
		if(view != null){
			view.setVisibility(View.VISIBLE);
		}
		
		return this;
	}
	

	public AQuery background(int id){
		
		if(view != null){
		
			if(id != 0){
				view.setBackgroundResource(id);
			}else{
				view.setBackgroundDrawable(null);
			}
		
		}
		
		return this;
	}
	
	public AQuery dataChanged(){
		
		if(view != null){
			
			if(view instanceof ListView){
				
				ListView lv = (ListView) view;
				ListAdapter la = lv.getAdapter();
				
				if(la instanceof BaseAdapter){
					BaseAdapter ba = (BaseAdapter) la;
					ba.notifyDataSetChanged();
				}
				
			}
			
		}
		
		
		return this;
	}
	
	private void invokeHandler(View view, Object handler, String callback){
    	try{   
			Method method = handler.getClass().getMethod(callback, View.class);
			if(method != null){
				method.invoke(handler, view);
			}
		}catch(Exception e){
			//Utility.report(e);
			e.printStackTrace();
		}
    }
	
	public AQuery bind(Object obj, String method){
		
		final Object o = obj;
		final String callback = method;
		
		if(view != null){			
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					invokeHandler(v, o, callback);
				}
			});
		}
		
		return this;
	}
	
	public AQuery clear(){
		
		if(view != null){
			
			if(view instanceof ImageView){
				ImageView iv = ((ImageView) view);
				iv.setImageBitmap(null);
				iv.setTag(null);
			}else if(view instanceof WebView){
				WebView wv = ((WebView) view);
				wv.stopLoading();
				wv.clearView();
				wv.setTag(null);
			}
			
			
		}
		
		return this;
	}
	
	*/
}
