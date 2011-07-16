/*
 * Copyright 2011 - AndroidQuery.com (tinyeeliu@gmail.com)
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

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;

import android.app.Activity;
import android.content.Context;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.AQUtility;
import com.androidquery.util.Common;
import com.androidquery.util.Constants;


/**
 * The core class of AQuery. Contains all the methods available from an AQuery object.
 *
 * @param <T> the generic type
 */
public abstract class AbstractAQuery<T extends AbstractAQuery<T>> implements Constants {

	private View root;
	private Activity act;
	private Context context;
	
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
	}
	
	/**
	 * Instantiates a new AQuery object.
	 *
	 * @param root View container that's the parent of the to-be-operated views.
	 */
	public AbstractAQuery(View root){
		this.root = root;
		this.view = root;
	}
	
	/**
	 * Instantiates a new AQuery object.
	 *
	 * @param context Context that will be used in async operations.
	 */
	
	public AbstractAQuery(Context context){
		this.context = context;
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
	
	private View findView(int... path){
		
		View result = findView(path[0]);
		
		for(int i = 1; i < path.length && result != null; i++){
			result = result.findViewById(path[i]);
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
	 * Find the first view with first id, under that view, find again with 2nd id, etc...
	 *
	 * @param id the id
	 * @return self
	 */
	public T id(int... path){
		view = findView(path);			
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
	 * @param resid the resource id
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
	 * @param bm Bitmap
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
	 * Set the image of an ImageView.
	 *
	 * @param url Image url.
	 * @return self
	 */
	
	public T image(String url){
		return image(url, true, true);
	}
	
	/**
	 * Set the image of an ImageView.
	 *
	 * @param url The image url.
	 * @param memCache Use memory cache.
	 * @param fileCache Use file cache.
	 * @return self
	 */
	public T image(String url, boolean memCache, boolean fileCache){
		
		
		if(view != null){
			ImageView iv = (ImageView) view;
			BitmapAjaxCallback.async(getContext(), iv, url, memCache, fileCache, 0, 0);
		}
		
		return self();
	}
	
	
	/**
	 * Set the image of an ImageView.
	 *
	 * @param url The image url.
	 * @param memCache Use memory cache.
	 * @param fileCache Use file cache.
	 * @param targetWidth Target width for down sampling when reading large images.
	 * @param resId Fallback image if result is network fetch and image convert failed. 
	 * @return self
	 */
	public T image(String url, boolean memCache, boolean fileCache, int targetWidth, int resId){
		
		
		if(view != null){
			ImageView iv = (ImageView) view;			
			BitmapAjaxCallback.async(getContext(), iv, url, memCache, fileCache, targetWidth, resId);
		}
		
		return self();
	}
	
	
	/**
	 * Set the image of an ImageView with a custom callback.
	 *
	 * @param url The image url.
	 * @param memCache Use memory cache.
	 * @param fileCache Use file cache.
	 * @param targetWidth Target width for down sampling when reading large images.
	 * @param resId Fallback image if result is network fetch and image convert failed. 
	 * @param callback Callback handler for setting the image.
	 * @return self
	 */
	public T image(String url, boolean memCache, boolean fileCache, int targetWidth, int resId, BitmapAjaxCallback callback){
		
		if(view != null){
			ImageView iv = (ImageView) view;
			callback.setImageView(url, iv);
			callback.setTargetWidth(targetWidth);
			callback.setFallback(resId);
			callback.async(getContext(), url, memCache, fileCache, false);
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
			AQUtility.transparent(view, transparent);
		}
		
		return self();
	}
	
	/**
	 * Enable a view.
	 *
	 * @param enabled state
	 * @return self
	 */
	public T enabled(boolean enable){
		
		if(view != null){
			view.setEnabled(enable);
		}
		
		return self();
	}
	
	/**
	 * Set checked state of a compound button.
	 *
	 * @param checked state
	 * @return self
	 */
	public T checked(boolean checked){
		
		if(view != null && view instanceof CompoundButton){
			CompoundButton cb = (CompoundButton) view;
			cb.setChecked(checked);
		}
		
		return self();
	}
	
	/**
	 * Get checked state of a compound button.
	 *
	 * @return checked
	 */
	public boolean isChecked(){
		
		boolean checked = false;
		
		if(view != null && view instanceof CompoundButton){
			CompoundButton cb = (CompoundButton) view;
			checked = cb.isChecked();
		}
		
		return checked;
	}
	
	/**
	 * Set clickable for a view.
	 *
	 * @param clickable
	 * @return self
	 */
	public T clickable(boolean clickable){
		
		if(view != null){
			view.setClickable(clickable);
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
	
	
	
	
	private static final Class<?>[] ON_CLICK_SIG = {View.class};
	
	/**
	 * Register a callback method for when the view is clicked. Method must have signature of method(View view).
	 *
	 * @param handler The handler that has the public callback method.
	 * @param method The method name of the callback.
	 * @return self
	 */
	public T clicked(Object handler, String method){
		
		if(view != null){			
			
			Common common = new Common().forward(handler, method, true, ON_CLICK_SIG);
			view.setOnClickListener(common);
			
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
	 * @param handler The handler that has the public callback method.
	 * @param method The method name of the callback.
	 * @return self
	 */
	public T itemClicked(Object handler, String method){
		
		if(view != null && view instanceof AdapterView){
		
			AdapterView<?> av = (AdapterView<?>) view;
			
			Common common = new Common().forward(handler, method, true, ON_ITEM_CLICK_SIG);
			av.setOnItemClickListener(common);
			
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
	
	
	
	private static Class<?>[] ON_SCROLLED_STATE_SIG = {AbsListView.class, int.class};
	
	/**
	 * Register a callback method for when a list is scrolled to bottom. Method must have signature of method(AbsListView view, int scrollState).
	 *
	 * @param handler The handler that has the public callback method.
	 * @param method The method name of the callback.
	 * @return self
	 */
	public T scrolledBottom(Object handler, String method){
		
		if(view != null && view instanceof AbsListView){
		
			AbsListView lv = (AbsListView) view;
			Common common = new Common().forward(handler, method, true, ON_SCROLLED_STATE_SIG);
			lv.setOnScrollListener(common);
			
		}
		
		return self();
	}
	
	private static final Class<?>[] TEXT_CHANGE_SIG = {CharSequence.class, int.class, int.class, int.class};
	
	/**
	 * Register a callback method for when a textview text is changed. Method must have signature of method(CharSequence s, int start, int before, int count)).
	 *
	 * @param handler The handler that has the public callback method.
	 * @param method The method name of the callback.
	 * @return self
	 */
	public T textChanged(Object handler, String method){
		
		if(view != null && view instanceof TextView){			
		
			TextView tv = (TextView) view;
			Common common = new Common().forward(handler, method, true, TEXT_CHANGE_SIG);
			tv.addTextChangedListener(common);
			
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
			AQUtility.invokeHandler(act, "overridePendingTransition", false, PENDING_TRANSITION_SIG, enterAnim, exitAnim);
		}
		
		return self();
	}
	
	private static final Class<?>[] OVER_SCROLL_SIG = {int.class};
	
	/**
	 * Call the overridePendingTransition of the activity. Only applies when device API is 5+.
	 *
	 * @param mode AQuery.OVER_SCROLL_ALWAYS, AQuery.OVER_SCROLL_ALWAYS, AQuery.OVER_SCROLL_IF_CONTENT_SCROLLS
	 * @return self
	 */
	public T setOverScrollMode9(int mode){
		
		if(view != null && view instanceof AbsListView){
			AQUtility.invokeHandler(view, "setOverScrollMode", false, OVER_SCROLL_SIG, mode);
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
			
			AQUtility.invokeHandler(view, "setLayerType", false, LAYER_TYPE_SIG, type, paint);
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
	
	/**
	 * Return the context of activity of view.
	 *	 
	 * @return Context
	 */
	
	public Context getContext(){
		if(act != null){
			return act;
		}
		if(root != null){
			return root.getContext();
		}
		return context;
	}
	
	/**
	 * Ajax call with various callback data types.
	 *
	 * @param url url
	 * @param type data type
	 * @param callback callback handler
	 * @return self
	 */
	
	public <K> T ajax(String url, Class<K> type, AjaxCallback<K> callback){
		
		callback.setType(type);
		callback.async(getContext(), url);
		
		return self();
	}
	
	/**
	 * Ajax call with various callback data types.
	 *
	 * The handler signature must be (String url, <K> object, AjaxStatus status)
	 *
	 * @param url url
	 * @param type data type
	 * @param callback callback method name
	 * @return self
	 */
	
	public <K> T ajax(String url, Class<K> type, Object handler, String callback){
		
		
		AjaxCallback<K> cb = new AjaxCallback<K>();
		cb.setCallback(handler, callback);
		cb.setType(type);
		cb.async(getContext(), url);
		
		return self();
	}
	
	
	/**
	 * Stop all ajax activities. Should be called when current activity is to be destroy.
	 *
	 * 
	 * @return self
	 */
	public T ajaxCancel(){
		
		AjaxCallback.cancel();
		
		return self();
	}
	
	/**
	 * Return file cached by image requests. Returns null if url is not cached.
	 *
	 * 
	 * @return File
	 */
	public File getCachedFile(String url){
		
		return AQUtility.getExistedCacheByUrl(AQUtility.getCacheDir(getContext()), url);
		
	}
	
}
