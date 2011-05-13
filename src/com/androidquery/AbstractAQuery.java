package com.androidquery;

import java.lang.reflect.Method;

import android.app.Activity;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.*;

public abstract class AbstractAQuery<T extends AbstractAQuery<T>> {

	private View root;
	private Activity act;
	
	protected View view;
	
	protected abstract T create(View view);
	
	public AbstractAQuery(Activity act){
		this.act = act;
		this.view = root;
	}
	
	public AbstractAQuery(View root){
		this.root = root;
		this.view = root;
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
	
	
	public T find(int id){
		View view = findView(id);
		return create(view);
	}
	
	@SuppressWarnings("unchecked")
	private T t(){
		return (T) this;
	}

	public View getView(){
		return view;
	}
	
	public T id(int id){
		view = findView(id);
		return t();
	}
	
	
	public T text(CharSequence text){
		
		
		if(view != null){			
			TextView tv = (TextView) view;
			tv.setText(text);
		}
		return t();
	}
	
	public T text(Spanned text){
		
		
		if(view != null){			
			TextView tv = (TextView) view;
			tv.setText(text);
		}
		return t();
	}
	
	public T image(int id){
		
		if(view != null){
			ImageView iv = (ImageView) view;
			if(id == 0){
				iv.setImageBitmap(null);
			}else{				
				iv.setImageResource(id);
			}
		}
		
		return t();
	}
	
	public T transparent(boolean transparent){
		
		if(view != null){
			UIUtility.transparent(view, transparent);
		}
		
		return t();
	}
	
	public T enabled(boolean enable){
		
		if(view != null){
			view.setEnabled(enable);
		}
		
		return t();
	}
	
	
	public T gone(){
		
		if(view != null){
			view.setVisibility(View.GONE);
		}
		
		return t();
	}
	
	public T hide(){
		
		if(view != null){
			view.setVisibility(View.INVISIBLE);
		}
		
		return t();
	}
	
	public T show(){
		
		if(view != null){
			view.setVisibility(View.VISIBLE);
		}
		
		return t();
	}
	

	public T background(int id){
		
		if(view != null){
		
			if(id != 0){
				view.setBackgroundResource(id);
			}else{
				view.setBackgroundDrawable(null);
			}
		
		}
		
		return t();
	}
	
	public T dataChanged(){
		
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
		
		
		return t();
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
	
	public T bind(Object obj, String method){
		
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
		
		return t();
	}
	
	public T clear(){
		
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
		
		return t();
	}
	
}
