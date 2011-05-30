/*
 * Copyright 2011 - Peter Liu (tinyeeliu@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.androidquery;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Editable;
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

/**
 * The core class of AQuery. Contains all the methods available from an AQuery object.
 *
 * @param <T> the generic type
 */
public abstract class AbstractAQuery<T extends AbstractAQuery<T>> implements Constants {

	private View root;
	private Activity act;
	
	protected View view;
	

	private T create(View view){
		
		T result = null;
		
		try{
			Constructor<T> c = getConstructor();
			result = (T) c.newInstance(view);
		}catch(Exception e){
			//should never happen
			e.printStackTrace();
		}
		return result;
		
	}
	
	private Constructor<T> constructor;
	@SuppressWarnings("unchecked")
	private Constructor<T> getConstructor(){
		
		if(constructor == null){
		
			try{
				constructor = (Constructor<T>) getClass().getConstructor(View.class);
			}catch(Exception e){
				//should never happen
				e.printStackTrace();
			}
		}
		
		return constructor;
	}
	
	/**
	 * Instantiates a new AQuery object.
	 *
	 * @param act Activity that's the parent of the to-be-operated views.
	 */
	public AbstractAQuery(Activity act){
		this.act = act;
		this.view = root;
	}
	
	/**
	 * Instantiates a new AQuery object.
	 *
	 * @param root The view container that's the parent of the to-be-operated views..
	 */
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
	
	
	/**
	 * Return a new AQuery object that uses the found view as a root.
	 *
	 * @param id the id
	 * @return self
	 */
	public T find(int id){
		View view = findView(id);
		
		return create(view);
	}
	
	@SuppressWarnings("unchecked")
	private T self(){
		return (T) this;
	}

	/**
	 * Return the current operating view.
	 *
	 * @return the view
	 */
	public View getView(){
		return view;
	}
	
	/**
	 * Points the current operating view to the first view found with the id under the root.
	 *
	 * @param id the id
	 * @return self
	 */
	public T id(int id){
		view = findView(id);		
		return self();
	}
	
	/**
	 * Set the text of a TextView.
	 *
	 * @param resid the resid
	 * @return self
	 */
	public T text(int resid){
		
		if(view != null){			
			TextView tv = (TextView) view;
			tv.setText(resid);
		}
		return self();
	}
	
	/**
	 * Set the text of a TextView.
	 *
	 * @param text the text
	 * @return self
	 */
	public T text(CharSequence text){
				
		if(view != null){			
			TextView tv = (TextView) view;
			tv.setText(text);
		}
		return self();
	}
	
	/**
	 * Set the text of a TextView.
	 *
	 * @param text the text
	 * @return self
	 */
	public T text(Spanned text){
		
		
		if(view != null){			
			TextView tv = (TextView) view;
			tv.setText(text);
		}
		return self();
	}
	
	/**
	 * Set the text color of a TextView.
	 *
	 * @param color the color
	 * @return self
	 */
	public T textColor(int color){
		
		if(view != null){			
			TextView tv = (TextView) view;
			tv.setTextColor(color);
		}
		return self();
	}
	
	/**
	 * Set the image of an ImageView.
	 *
	 * @param id the id
	 * @return self
	 */
	public T image(int resid){
		
		if(view != null){
			ImageView iv = (ImageView) view;
			if(resid == 0){
				iv.setImageBitmap(null);
			}else{				
				iv.setImageResource(resid);
			}
		}
		
		return self();
	}
	
	/**
	 * Set the image of an ImageView.
	 *
	 * @param drawable the drawable
	 * @return self
	 */
	public T image(Drawable drawable){
		
		if(view != null){
			ImageView iv = (ImageView) view;
			iv.setImageDrawable(drawable);
		}
		
		return self();
	}
	
	/**
	 * Set the image of an ImageView.
	 *
	 * @param bm the bm
	 * @return self
	 */
	public T image(Bitmap bm){
		
		if(view != null){
			ImageView iv = (ImageView) view;
			iv.setImageBitmap(bm);
		}
		
		return self();
	}
	
	/**
	 * Set a view to be transparent.
	 *
	 * @param transparent the transparent
	 * @return self
	 */
	public T transparent(boolean transparent){
		
		if(view != null){
			UIUtility.transparent(view, transparent);
		}
		
		return self();
	}
	
	/**
	 * Enable a view.
	 *
	 * @param enable the enable
	 * @return self
	 */
	public T enabled(boolean enable){
		
		if(view != null){
			view.setEnabled(enable);
		}
		
		return self();
	}
	
	
	/**
	 * Set view visibility to View.GONE.
	 *
	 * @return self
	 */
	public T gone(){
		
		if(view != null){
			view.setVisibility(View.GONE);
		}
		
		return self();
	}
	
	/**
	 * Set view visibility to View.INVISIBLE.
	 *
	 * @return self
	 */
	public T invisible(){
		
		if(view != null){
			view.setVisibility(View.INVISIBLE);
		}
		
		return self();
	}
	
	/**
	 * Set view visibility to View.VISIBLE.
	 *
	 * @return self
	 */
	public T visible(){
		
		if(view != null){
			view.setVisibility(View.VISIBLE);
		}
		
		return self();
	}
	

	/**
	 * Set view background.
	 *
	 * @param id the id
	 * @return self
	 */
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
	
	/**
	 * Notify a ListView that the data of it's adapter is changed.
	 *
	 * @return self
	 */
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
		}catch(NoSuchMethodException e){
			//no such method, do nothing
		}catch(Exception e){		
			e.printStackTrace();
		}
    }
	
	
	/**
	 * Checks if the current view exist.
	 *
	 * @return true, if is exist
	 */
	public boolean isExist(){
		return view != null;
	}
	
	/**
	 * Gets the tag of the view.
	 *
	 * @return tag
	 */
	public Object getTag(){
		Object result = null;
		if(view != null){
			view.getTag();
		}
		return result;
	}
	
	/**
	 * Gets the current view as an image view.
	 *
	 * @return ImageView
	 */
	public ImageView getImageView(){
		return (ImageView) view;
	}
	
	/**
	 * Gets the current view as a text view.
	 *
	 * @return TextView
	 */
	public TextView getTextView(){
		return (TextView) view;
	}
	
	/**
	 * Gets the current view as an edit text.
	 *
	 * @return EditText
	 */
	public EditText getEditText(){
		return (EditText) view;
	}
	
	/**
	 * Gets the current view as an progress bar.
	 *
	 * @return ProgressBar
	 */
	public ProgressBar getProgressBar(){
		return (ProgressBar) view;
	}
	
	/**
	 * Gets the current view as a button.
	 *
	 * @return Button
	 */
	public Button getButton(){
		return (Button) view;
	}
	
	/**
	 * Gets the current view as a checkbox.
	 *
	 * @return CheckBox
	 */
	public CheckBox getCheckBox(){
		return (CheckBox) view;
	}
	
	/**
	 * Gets the current view as a listview.
	 *
	 * @return ListView
	 */
	public ListView getListView(){
		return (ListView) view;
	}
	
	/**
	 * Gets the current view as a gridview.
	 *
	 * @return GridView
	 */
	public GridView getGridView(){
		return (GridView) view;
	}
	
	/**
	 * Gets the current view as a webview.
	 *
	 * @return WebView
	 */
	public WebView getWebView(){
		return (WebView) view;
	}
	
	
	/**
	 * Gets the editable.
	 *
	 * @return the editable
	 */
	public Editable getEditable(){
		
		Editable result = null;
		
		if(view instanceof EditText){
			result = ((EditText) view).getEditableText();
		}
		
		return result;
	}
	
	
	
	
	private static Class<?>[] ON_CLICK_SIG = {View.class};
	
	/**
	 * Register a callback method for when the view is clicked. Method must have signature of method(View view).
	 *
	 * @param obj The handler that has the public callback method.
	 * @param method The method name of the callback.
	 * @return self
	 */
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
	
	
	/**
	 * Register a callback method for when the view is clicked. 
	 *
	 * @param listener The callback method.
	 * @return self
	 */
	public T clicked(OnClickListener listener){
		
		if(view != null){						
			view.setOnClickListener(listener);
		}
		
		return self();
	}
	
	private static Class<?>[] ON_ITEM_CLICK_SIG = {AdapterView.class, View.class, int.class, long.class};
	
	/**
	 * Register a callback method for when an item is clicked in the ListView. Method must have signature of method(AdapterView<?> parent, View v, int pos, long id).
	 *
	 * @param obj The handler that has the public callback method.
	 * @param method The method name of the callback.
	 * @return self
	 */
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
	
	/**
	 * Register a callback method for when an item is clicked in the ListView.
	 *
	 * @param listener The callback method.
	 * @return self
	 */
	public T itemClicked(OnItemClickListener listener){
		
		if(view != null && view instanceof AbsListView){
		
			AbsListView alv = (AbsListView) view;
			alv.setOnItemClickListener(listener);
		
		}
		
		return self();
		
	}	
	
	protected void debug(Object msg){
		System.err.println(msg);
	}
	
	private static Class<?>[] ON_SCROLLED_STATE_SIG = {AbsListView.class, int.class};
	
	/**
	 * Register a callback method for when a list is scrolled to bottom. Method must have signature of method(Object obj, String method).
	 *
	 * @param obj The handler that has the public callback method.
	 * @param method The method name of the callback.
	 * @return self
	 */
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
	
	/**
	 * Call the overridePendingTransition of the activity. Only applies when device API is 5+.
	 *
	 * @param enterAnim the enter animation
	 * @param exitAnim the exit animation
	 * @return self
	 */
	public T overridePendingTransition5(int enterAnim, int exitAnim){
		
		if(act != null){
			invokeHandler(act, "overridePendingTransition", PENDING_TRANSITION_SIG, enterAnim, exitAnim);
		}
		
		return self();
	}
	
	private static Class<?>[] LAYER_TYPE_SIG = {int.class, Paint.class};
	
	/**
	 * Call the setLayerType of the view. Only applies when device API is 11+.
	 *
	 * @param type the type
	 * @param paint the paint
	 * @return self
	 */
	public T setLayerType11(int type, Paint paint){
		
		if(view != null){
			
			invokeHandler(view, "setLayerType", LAYER_TYPE_SIG, type, paint);
		}
		
		return self();
	}
	
	
	/**
	 * Set the activity to be hardware accelerated. Only applies when device API is 11+.
	 *
	 * @return self
	 */
	public T hardwareAccelerated11(){
		
		if(act != null){
			
			act.getWindow().setFlags(AQuery.FLAG_HARDWARE_ACCELERATED, AQuery.FLAG_HARDWARE_ACCELERATED);
		}
		
		return self();
	}
	
	
	
	
	/**
	 * Clear a view. Applies to ImageView, WebView, and TextView.
	 *
	 * @return self
	 */
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
			}else if(view instanceof TextView){
				TextView tv = ((TextView) view);
				tv.setText("");
			}
			
			
		}
		
		return self();
	}
	
	private int dip2pixel(float n){
		int value = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, n, view.getResources().getDisplayMetrics());
		return value;
	}
	
	/**
	 * Set the margin of a view. Notes all parameters are in DIP, not in pixel.
	 *
	 * @param leftDip the left dip
	 * @param topDip the top dip
	 * @param rightDip the right dip
	 * @param bottomDip the bottom dip
	 * @return self
	 */
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
