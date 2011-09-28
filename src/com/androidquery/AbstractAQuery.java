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
import java.lang.reflect.Constructor;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidquery.callback.AjaxCallback;
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
	private View progress;

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
		progress = null;
		return self();
	}
	
	/**
	 * Find the first view with first id, under that view, find again with 2nd id, etc...
	 *
	 * @param path The id path.
	 * @return self
	 */
	public T id(int... path){
		view = findView(path);	
		progress = null;		
		return self();
	}
	
	/**
	 * Find the progress bar and show the progress for the next ajax/image request. 
	 * Once ajax or image is called, current progress view is consumed.
	 * Subsequent ajax/image calls won't show progress view unless progress is called again.
	 *
	 * If a file or network requests is required, the progress bar is set to be "VISIBLE".
	 * Once the requests completes, progress bar is set to "GONE".
	 *
	 * @param id the id of the progress bar to be shown
	 * @return self
	 */
	public T progress(int id){
		progress = findView(id);		
		return self();
	}
	
	//dequeue the progress, each progress bar only used once per ajax request
	private View dqProgress(){
		View result = progress;
		progress = null;
		return result;
	}
	
	/**
	 * Set the text of a TextView.
	 *
	 * @param resid the resid
	 * @return self
	 */
	public T text(int resid){
		
		if(view instanceof TextView){			
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
			
		if(view instanceof TextView){			
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
		
		
		if(view instanceof TextView){			
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
		
		if(view instanceof TextView){			
			TextView tv = (TextView) view;
			tv.setTextColor(color);
		}
		return self();
	}
	
	/**
	 * Set the adapter of an AdapterView.
	 *
	 * @param adapter adapter
	 * @return self
	 */
	
	@SuppressWarnings({"unchecked", "rawtypes" })
	public T adapter(Adapter adapter){
		
		if(view instanceof AdapterView){
			AdapterView av = (AdapterView) view;
			av.setAdapter(adapter);
		}
		
		return self();
	}
	
	/**
	 * Set the image of an ImageView.
	 *
	 * @param resid the resource id
	 * @return self
	 * 
	 * @see testImage1
	 */
	public T image(int resid){
		
		if(view instanceof ImageView){
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
	 * 
	 * @see testImage2
	 * 
	 */
	public T image(Drawable drawable){
		
		if(view instanceof ImageView){
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
	 * 
	 * @see testImage3
	 */
	public T image(Bitmap bm){
		
		if(view instanceof ImageView){
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
	 * 
	 * @see testImage4
	 */
	
	public T image(String url){
		return image(url, true, true, 0, 0);
	}
	
	/**
	 * Set the image of an ImageView.
	 *
	 * @param url The image url.
	 * @param memCache Use memory cache.
	 * @param fileCache Use file cache.
	 * @return self
	 * 
	 * @see testImage5
	 */
	public T image(String url, boolean memCache, boolean fileCache){		
		return image(url, memCache, fileCache, 0, 0);
	}
	
	
	/**
	 * Set the image of an ImageView.
	 *
	 * @param url The image url.
	 * @param memCache Use memory cache.
	 * @param fileCache Use file cache.
	 * @param targetWidth Target width for down sampling when reading large images. 0 = no downsampling.
	 * @param fallbackId Fallback image if result is network fetch and image convert failed. 0 = no fallback. 
	 * @return self
	 * 
	 * @see testImage6
	 */
	public T image(String url, boolean memCache, boolean fileCache, int targetWidth, int fallbackId){
		
		return image(url, memCache, fileCache, targetWidth, fallbackId, null, 0);
	}
	
	
	
	/**
	 * Set the image of an ImageView.
	 *
	 * @param url The image url.
	 * @param memCache Use memory cache.
	 * @param fileCache Use file cache.
	 * @param targetWidth Target width for down sampling when reading large images. 0 = no downsampling.
	 * @param fallbackId Fallback image if result is network fetch and image convert failed. 0 = no fallback. 
	 * @param preset Default image to show before real image loaded. null = no preset.
	 * @param animId Apply this animation when image is loaded. 0 = no animation. Also accept AQuery.FADE_IN as a predefined 500ms fade in animation.
	 * @return self
	 * 
	 * @see testImage7
	 * 
	 */
	public T image(String url, boolean memCache, boolean fileCache, int targetWidth, int fallbackId, Bitmap preset, int animId){
		
		return image(url, memCache, fileCache, targetWidth, fallbackId, preset, animId, 0);
	}
	
	/**
	 * Set the image of an ImageView.
	 *
	 * @param url The image url.
	 * @param memCache Use memory cache.
	 * @param fileCache Use file cache.
	 * @param targetWidth Target width for down sampling when reading large images. 0 = no downsampling.
	 * @param fallbackId Fallback image if result is network fetch and image convert failed. 0 = no fallback. 
	 * @param preset Default image to show before real image loaded. null = no preset.
	 * @param animId Apply this animation when image is loaded. 0 = no animation. Also accept AQuery.FADE_IN as a predefined 500ms fade in animation.
	 * @param ratio The desired aspect ratio of the imageview. Ratio is height / width, or AQuery.RATIO_PRESERVE to preserve the original aspect ratio of the image.
	 * @return self
	 * 
	 * @see testImage12
	 * 
	 */
	
	
	public T image(String url, boolean memCache, boolean fileCache, int targetWidth, int fallbackId, Bitmap preset, int animId, float ratio){
		
		BitmapAjaxCallback cb = new BitmapAjaxCallback();		
		cb.url(url).memCache(memCache).fileCache(fileCache).targetWidth(targetWidth).fallback(fallbackId).preset(preset).animation(animId).ratio(ratio);	
		
		return image(cb);
	}
	
	
	/**
	 * Set the image of an ImageView with a custom callback.
	 *
	 * @param callback Callback handler for setting the image.
	 * @return self
	 * 
	 * @see testImage8
	 */
	
	public T image(BitmapAjaxCallback callback){
		
		if(view instanceof ImageView || view instanceof TextView){			
			callback.view(view).progress(dqProgress()).async(getContext());			
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
	 * 
	 * @see testImage9
	 */
	public T image(String url, boolean memCache, boolean fileCache, int targetWidth, int resId, BitmapAjaxCallback callback){
		
		callback.targetWidth(targetWidth).fallback(resId)
		.url(url).memCache(memCache).fileCache(fileCache);
		
		return image(callback);
	}
	
	/**
	 * Set the image of an ImageView from a file. 
	 *
	 * @param file The image file.
	 * @param targetWidth Target width for down sampling when reading large images.
	 * @return self
	 * 
	 * @see testImage10
	 */
	
	public T image(File file, int targetWidth){		
		return image(file, true, targetWidth, null);
	}
	
	
	
	
	/**
	 * Set the image of an ImageView from a file with a custom callback.
	 *
	 * @param file The image file.
	 * @param memCache Use memory cache.
	 * @param targetWidth Target width for down sampling when reading large images.
	 * @param callback Callback handler for setting the image.
	 * @return self
	 * 
	 * @see testImage11
	 */
	public T image(File file, boolean memCache, int targetWidth, BitmapAjaxCallback callback){
		
		if(callback == null) callback = new BitmapAjaxCallback();		
		callback.file(file);	
		
		String url = null;
		if(file != null) url = file.getAbsolutePath();
		return image(url, memCache, true, targetWidth, 0, callback);
	
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
	public T enabled(boolean enabled){
		
		if(view != null){
			view.setEnabled(enabled);
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
		
		if(view instanceof CompoundButton){
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
		
		if(view instanceof CompoundButton){
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
		
		if(view instanceof AdapterView){
			
			AdapterView<?> av = (AdapterView<?>) view;
			Adapter a = av.getAdapter();
			
			if(a instanceof BaseAdapter){
				BaseAdapter ba = (BaseAdapter) a;
				ba.notifyDataSetChanged();
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
			result = view.getTag();
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
	 * Gets the current view as a spinner.
	 *
	 * @return Spinner
	 */
	public Spinner getSpinner(){
		return (Spinner) view;
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
	
	/**
	 * Gets the selected item if current view is an adapter view.
	 *
	 * @return selected
	 */
	public Object getSelectedItem(){
		
		Object result = null;
		
		if(view instanceof AdapterView<?>){
			result = ((AdapterView<?>) view).getSelectedItem();
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
		
		/*
		if(view != null){			
			
			Common common = new Common().forward(handler, method, true, ON_CLICK_SIG);
			view.setOnClickListener(common);
			
		}
		
		return self();
		*/
		
		Common common = new Common().forward(handler, method, true, ON_CLICK_SIG);
		return clicked(common);
		
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
	
	private static Class<?>[] ON_ITEM_SIG = {AdapterView.class, View.class, int.class, long.class};
	
	/**
	 * Register a callback method for when an item is clicked in the ListView. Method must have signature of method(AdapterView<?> parent, View v, int pos, long id).
	 *
	 * @param handler The handler that has the public callback method.
	 * @param method The method name of the callback.
	 * @return self
	 */
	public T itemClicked(Object handler, String method){
		
		/*
		if(view instanceof AdapterView){
		
			AdapterView<?> av = (AdapterView<?>) view;
			
			Common common = new Common().forward(handler, method, true, ON_ITEM_CLICK_SIG);
			av.setOnItemClickListener(common);
			
		}
		
		return self();
		*/
		
		Common common = new Common().forward(handler, method, true, ON_ITEM_SIG);
		return itemClicked(common);
		
	}
	
	/**
	 * Register a callback method for when an item is clicked in the ListView.
	 *
	 * @param listener The callback method.
	 * @return self
	 */
	public T itemClicked(OnItemClickListener listener){
		
		if(view instanceof AdapterView){
		
			AdapterView<?> alv = (AdapterView<?>) view;
			alv.setOnItemClickListener(listener);
		
		}
		
		return self();
		
	}	
	
	/**
	 * Register a callback method for when an item is selected. Method must have signature of method(AdapterView<?> parent, View v, int pos, long id).
	 *
	 * @param handler The handler that has the public callback method.
	 * @param method The method name of the callback.
	 * @return self
	 */
	public T itemSelected(Object handler, String method){
				
		Common common = new Common().forward(handler, method, true, ON_ITEM_SIG);
		return itemSelected(common);
		
	}
	
	/**
	 * Register a callback method for when an item is selected. 
	 *
	 * @param listener The item selected listener.
	 * @return self
	 */
	public T itemSelected(OnItemSelectedListener listener){
		
		if(view instanceof AdapterView){		
			AdapterView<?> alv = (AdapterView<?>) view;
			alv.setOnItemSelectedListener(listener);		
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
		
		if(view instanceof AbsListView){
		
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
		
		if(view instanceof TextView){			
		
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
	 * Call the setOverScrollMode of the view. Only applies when device API is 9+.
	 *
	 * @param mode AQuery.OVER_SCROLL_ALWAYS, AQuery.OVER_SCROLL_ALWAYS, AQuery.OVER_SCROLL_IF_CONTENT_SCROLLS
	 * @return self
	 */
	public T setOverScrollMode9(int mode){
		
		if(view instanceof AbsListView){
			AQUtility.invokeHandler(view, "setOverScrollMode", false, OVER_SCROLL_SIG, mode);
		}
		
		return self();
	}
	
	private static Class<?>[] LAYER_TYPE_SIG = {int.class, Paint.class};
	
	/**
	 * Call the setLayerType of the view. Only applies when device API is 11+.
	 *
	 * Type must be AQuery.LAYER_TYPE_SOFTWARE or AQuery.LAYER_TYPE_HARDWARE.
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
	 * Invoke the method on the current view.
	 *
	 * @param method The name of the method
	 * @param sig The signature of the method
	 * @param params Input parameters
	 * @return object The returning object of the method. Null if no such method or return void.
	 */
	public Object invoke(String method, Class<?>[] sig, Object... params){
		
		Object handler = view;
		if(handler == null) handler = act;
		
		return AQUtility.invokeHandler(handler, method, false, sig, params);
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
			
				Context context = getContext();
					
				int left = AQUtility.dip2pixel(context, leftDip);
				int top = AQUtility.dip2pixel(context, topDip);
				int right = AQUtility.dip2pixel(context, rightDip);
				int bottom = AQUtility.dip2pixel(context, bottomDip);
				
				((MarginLayoutParams) lp).setMargins(left, top, right, bottom);
				view.setLayoutParams(lp);
			}
		
		}
		
		return self();
	}
	
	/**
	 * Set the width of a view. Notes all parameters are in DIP, not in pixel. 
	 * Input can also be ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, or ViewGroup.LayoutParams.MATCH_PARENT.
	 *
	 * @param dip width in dip
	 * @return self
	 */
	
	public T width(int dip){		
		size(true, dip);		
		return self();
	}
	
	/**
	 * Set the height of a view. Notes all parameters are in DIP, not in pixel. 
	 * Input can also be ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, or ViewGroup.LayoutParams.MATCH_PARENT.
	 *
	 * @param dip height in dip
	 * @return self
	 */
	
	public T height(int dip){		
		size(false, dip);
		return self();
	}
	
	
	private void size(boolean width, int dip){
		
		if(view != null){
		
			LayoutParams lp = view.getLayoutParams();
			
			Context context = getContext();
				
			if(dip > 0){
				dip = AQUtility.dip2pixel(context, dip);
			}
			
			if(width){
				lp.width = dip;
			}else{
				lp.height = dip;
			}
			
			view.setLayoutParams(lp);
		
		}
		
	}
	
	
	
	/**
	 * Return the context of activity or view.
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
	 * Advanced Ajax callback. User must manually prepare the callback object settings (url, type, etc...) by using its methods.
	 *
	 * @param callback callback handler
	 * @return self
	 * 
	 * @see testAjax1
	 */
	
	public <K> T ajax(AjaxCallback<K> callback){
				
		callback.progress(dqProgress()).async(getContext());
		
		return self();
	}	
	
	/**
	 * Ajax call with various callback data types.
	 *
	 * @param url url
	 * @param type data type
	 * @param callback callback handler
	 * @return self
	 * 
	 * @see testAjax2
	 */
	
	public <K> T ajax(String url, Class<K> type, AjaxCallback<K> callback){
		
		callback.type(type).url(url);
		return ajax(callback);
	}
	
	/**
	 * Ajax call with various callback data types with file caching.
	 * 
	 * The expire param is the duration to consider cached data expired (if hit).
	 * For example, expire = 15 * 60 * 1000 means if the cache data is within 15 minutes old, 
	 * return cached data immediately, otherwise go fetch the source again.
	 * 
	 *
	 * @param url url
	 * @param type data type
	 * @param expire duration in millseconds, 0 = always use cache
	 * @param callback callback handler
	 * @return self
	 * 
	 * @see testAjax6
	 */
	
	public <K> T ajax(String url, Class<K> type, long expire, AjaxCallback<K> callback){
		
		callback.type(type).url(url).fileCache(true).expire(expire);
		
		return ajax(callback);
	}
	
	
	
	
	/**
	 * Ajax call with various callback data types.
	 *
	 * The handler signature must be (String url, <K> object, AjaxStatus status)
	 *
	 * @param url url
	 * @param type data type
	 * @param handler the handler object with the callback method to be called
	 * @param callback callback method name
	 * @return self
	 * 
	 * @see testAjax3
	 */
	
	public <K> T ajax(String url, Class<K> type, Object handler, String callback){
		
		
		AjaxCallback<K> cb = new AjaxCallback<K>();
		cb.type(type).weakHandler(handler, callback);
		
		return ajax(url, type, cb);
		
	}
	
	
	/**
	 * Ajax call with various callback data types with file caching.
	 * 
	 * The expire param is the duration to consider cache data expired (if hit).
	 * For example, expire = 15 * 60 * 1000 means if the cache data is within 15 minutes old, 
	 * return cached data immediately, otherwise go fetch the source again.
	 * 
	 *
	 * @param url url
	 * @param type data type
	 * @param expire duration in millseconds, 0 = always use cache
	 * @param handler the handler object with the callback method to be called
	 * @param callback callback method name
	 * @return self
	 * 
	 * @see testAjax7
	 */
	
	public <K> T ajax(String url, Class<K> type, long expire, Object handler, String callback){
		
		AjaxCallback<K> cb = new AjaxCallback<K>();
		cb.type(type).weakHandler(handler, callback).fileCache(true).expire(expire);
		
		return ajax(url, type, cb);
	}
	
	/**
	 * Ajax call with POST method.
	 *
	 * The handler signature must be (String url, <K> object, AjaxStatus status)
	 *
	 * @param url url
	 * @param params 
	 * @param type data type
	 * @param callback callback method name
	 * @return self
	 * 
	 * @see testAjax4
	 */
	
	public <K> T ajax(String url, Map<String, Object> params, Class<K> type, AjaxCallback<K> callback){
		
		callback.type(type).url(url).params(params);
		return ajax(callback);
	}
	
	
	/**
	 * Ajax call with POST method.
	 *
	 * The handler signature must be (String url, <K> object, AjaxStatus status)
	 *
	 * @param url url
	 * @param params 
	 * @param type data type
	 * @param callback callback method name
	 * @return self
	 * 
	 * @see testAjax5
	 */
	
	public <K> T ajax(String url, Map<String, Object> params, Class<K> type, Object handler, String callback){
		
		
		AjaxCallback<K> cb = new AjaxCallback<K>();
		cb.type(type).weakHandler(handler, callback);
		
		return ajax(url, params, type, cb);
		
	}
	
	/**
	 * Cache the url to file cache without any callback.
	 *
	 *
	 * @param url url to cache
	 * @param expire duration in millseconds, 0 = never consider cached data as expired
	 * 
	 * @return self
	 * 
	 * @see testCache
	 */
	public T cache(String url, long expire){		
		return ajax(url, byte[].class, expire, null, null);		
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
	 * Return file cached by ajax or image requests. Returns null if url is not cached.
	 *
	 * @param url 
	 * @return File
	 */
	public File getCachedFile(String url){
		
		return AQUtility.getExistedCacheByUrl(AQUtility.getCacheDir(getContext()), url);
		
	}
	
	/**
	 * Return bitmap cached by image requests. Returns null if url is not cached.
	 *
	 * @param url 
	 * @return Bitmap
	 */
	
	public Bitmap getCachedImage(String url){
		return getCachedImage(url, 0);
	}
	
	/**
	 * Return bitmap cached by image requests. Returns null if url is not cached.
	 *
	 * @param url 
	 * @param targetWidth The desired downsampled width.
	 * 
	 * @return Bitmap
	 */
	public Bitmap getCachedImage(String url, int targetWidth){
		
		Bitmap result = BitmapAjaxCallback.getMemoryCached(url, targetWidth);
		if(result == null){
			File file = getCachedFile(url);
			if(file != null){
				result = BitmapAjaxCallback.getResizedImage(file.getAbsolutePath(), null, targetWidth);
			}
		}
		
		return result;
	}
	
	/**
	 * Starts an animation on the view.
	 * @param animId Id of the desired animation.
	 * @return self
	 */
	public T startAnimation(int animId){
		Animation anim = AnimationUtils.loadAnimation(view.getContext(), animId);
		return startAnimation(anim);
	}
	
	/**
	 * Starts an animation on the view.
	 * @param animId Id of the desired animation.
	 * @param listener The listener to recieve notifications from the animation on its events.
	 * @return
	 */
	public T startAnimation(int animId, AnimationListener listener){
		Animation anim = AnimationUtils.loadAnimation(view.getContext(), animId);
		anim.setAnimationListener(listener);
		return startAnimation(anim);
	}
	
	/**
	 * Starts an animation on the view.
	 * @param anim The desired animation.
	 * @return
	 */
	public T startAnimation(Animation anim){
		if(anim != null){
			view.startAnimation(anim);
		}
		return self();
	}
	
}
