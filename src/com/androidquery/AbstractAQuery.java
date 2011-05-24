package com.androidquery;

import java.lang.reflect.Method;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.webkit.WebView;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public abstract class AbstractAQuery<T extends AbstractAQuery<T>> implements Constants {

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
	private T self(){
		return (T) this;
	}

	public View getView(){
		return view;
	}
	
	public T id(int id){
		view = findView(id);
		return self();
	}
	
	
	public T text(CharSequence text){
				
		if(view != null){			
			TextView tv = (TextView) view;
			tv.setText(text);
		}
		return self();
	}
	
	public T text(Spanned text){
		
		
		if(view != null){			
			TextView tv = (TextView) view;
			tv.setText(text);
		}
		return self();
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
		
		return self();
	}
	
	public T image(Drawable drawable){
		
		if(view != null){
			ImageView iv = (ImageView) view;
			iv.setImageDrawable(drawable);
		}
		
		return self();
	}
	
	public T image(Bitmap bm){
		
		if(view != null){
			ImageView iv = (ImageView) view;
			iv.setImageBitmap(bm);
		}
		
		return self();
	}
	
	public T transparent(boolean transparent){
		
		if(view != null){
			UIUtility.transparent(view, transparent);
		}
		
		return self();
	}
	
	public T enabled(boolean enable){
		
		if(view != null){
			view.setEnabled(enable);
		}
		
		return self();
	}
	
	
	public T gone(){
		
		if(view != null){
			view.setVisibility(View.GONE);
		}
		
		return self();
	}
	
	public T invisible(){
		
		if(view != null){
			view.setVisibility(View.INVISIBLE);
		}
		
		return self();
	}
	
	public T visible(){
		
		if(view != null){
			view.setVisibility(View.VISIBLE);
		}
		
		return self();
	}
	

	public T background(int id){
		
		if(view != null){
		
			if(id != 0){
				view.setBackgroundResource(id);
			}else{
				view.setBackgroundDrawable(null);
			}
		
		}
		
		return self();
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
		
		
		return self();
	}
	
	private void invokeHandler(Object handler, String callback, Class<?>[] cls, Object... params){
    	try{   
			Method method = handler.getClass().getMethod(callback, cls);
			if(method != null){
				method.invoke(handler, params);			
			}
		}catch(Exception e){
			e.printStackTrace();
		}
    }
	
	
	public boolean isExist(){
		return view != null;
	}
	
	public Object getTag(){
		Object result = null;
		if(view != null){
			view.getTag();
		}
		return result;
	}
	
	public ImageView getImageView(){
		return (ImageView) view;
	}
	
	private static Class<?>[] ON_CLICK_SIG = {View.class};
	public T clicked(Object obj, String method){
		
		if(view != null){			
			
			final Object o = obj;
			final String callback = method;
			
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					invokeHandler(o, callback, ON_CLICK_SIG, v);
				}
			});
		}
		
		return self();
	}
	
	private static Class<?>[] ON_ITEM_CLICK_SIG = {AdapterView.class, View.class, int.class, long.class};
	public T itemClicked(Object obj, String method){
		
		if(view != null && view instanceof AbsListView){
		
			final Object o = obj;
			final String callback = method;
			
			
			AbsListView alv = (AbsListView) view;
			alv.setOnItemClickListener(new OnItemClickListener() {
	
				@Override
				public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
						
					invokeHandler(o, callback, ON_ITEM_CLICK_SIG, parent, v, pos, id);
					
				}
			});
		
		}
		
		return self();
		
	}
	
	protected void debug(Object msg){
		System.err.println(msg);
	}
	
	private static Class<?>[] ON_SCROLLED_STATE_SIG = {AbsListView.class, int.class};
	public T scrolledBottom(Object obj, String method){
		
		if(view != null && view instanceof AbsListView){
		
			final Object o = obj;
			final String callback = method;
			
			AbsListView lv = (AbsListView) view;
			
	        lv.setOnScrollListener(new OnScrollListener() {
				
				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					
					
					int cc = view.getCount();
					int last = view.getLastVisiblePosition();
					
					if(scrollState == OnScrollListener.SCROLL_STATE_IDLE && cc == last + 1){
						
						invokeHandler(o, callback, ON_SCROLLED_STATE_SIG, view, scrollState);
						
						
					}
					
				}
				
				@Override
				public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
					
				}
			});
			
		}
		
		return self();
	}
	
	private static Class<?>[] PENDING_TRANSITION_SIG = {int.class, int.class};
	public T overridePendingTransition(int enterAnim, int exitAnim){
		
		if(act != null){
			invokeHandler(act, "overridePendingTransition", PENDING_TRANSITION_SIG, enterAnim, exitAnim);
		}
		
		return self();
	}
	
	private static Class<?>[] LAYER_TYPE_SIG = {int.class, Paint.class};
	public T layerType(int type, Paint paint){
		
		if(view != null){
			invokeHandler(view, "setLayerType", LAYER_TYPE_SIG, type, paint);
		}
		
		return self();
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
		
		return self();
	}
	
	private int dip2pixel(float n){
		int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, n, view.getResources().getDisplayMetrics());
		return value;
	}
	
	public T margin(float leftDip, float topDip, float rightDip, float bottomDip){
		
		if(view != null){
		
			LayoutParams lp = view.getLayoutParams();
			
			if(lp instanceof MarginLayoutParams){
				
				int left = dip2pixel(leftDip);
				int top = dip2pixel(topDip);
				int right = dip2pixel(rightDip);
				int bottom = dip2pixel(bottomDip);
				
				((MarginLayoutParams) lp).setMargins(left, top, right, bottom);
				view.setLayoutParams(lp);
			}
		
		}
		
		return self();
	}
	
	
}
