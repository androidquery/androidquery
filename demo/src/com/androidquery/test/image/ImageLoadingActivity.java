package com.androidquery.test.image;

import static com.googlecode.charts4j.Color.GREEN;
import static com.googlecode.charts4j.Color.RED;
import static com.googlecode.charts4j.Color.SKYBLUE;
import static com.googlecode.charts4j.Color.WHITE;

import java.io.File;
import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.callback.ImageOptions;
import com.androidquery.test.RunSourceActivity;
import com.androidquery.util.AQUtility;
import com.googlecode.charts4j.AxisLabels;
import com.googlecode.charts4j.AxisLabelsFactory;
import com.googlecode.charts4j.AxisStyle;
import com.googlecode.charts4j.AxisTextAlignment;
import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.Data;
import com.googlecode.charts4j.Fills;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.Line;
import com.googlecode.charts4j.LineChart;
import com.googlecode.charts4j.LineStyle;
import com.googlecode.charts4j.LinearGradientFill;
import com.googlecode.charts4j.Plots;
import com.googlecode.charts4j.Shape;

public class ImageLoadingActivity extends RunSourceActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		aq.id(R.id.result).gone();
		
		
		if("image_access_file".equals(type) || "image_access_memory".equals(type)){
			image_simple();
		}else if("image_file".equals(type) || "image_file_custom".equals(type)){
			aq.cache("http://farm6.static.flickr.com/5035/5802797131_a729dac808_b.jpg", 0);
		}else if("image_preload".equals(type)){
			String small = "http://farm6.static.flickr.com/5035/5802797131_a729dac808_s.jpg";		
			aq.cache(small, 0);
			aq.id(R.id.image).width(250).height(250).image(0).visible();
		}else if("image_ratio".equals(type)){
			aq.id(R.id.image).width(250);
		}else if("image_pre_cache".equals(type)){
			pre_cache();
		}else if("image_button".equals(type)){
			aq.id(R.id.button).visible();
			aq.id(R.id.go_run).gone();
			image_button();
		}else if("image_send".equals(type)){
			aq.cache("http://www.androidquery.com/z/images/vikispot/android-w.png", 0);
		}
			
			
		
			
	}
	
	private void pre_cache(){
		aq.cache("http://farm3.static.flickr.com/2199/2218403922_062bc3bcf2.jpg", 0);
	}
	
	@Override
	protected void runSource(){
		AQUtility.debug(type);
		AQUtility.invokeHandler(this, type, false, false, null);
	}
	
	public void image_simple(){
		
		String url = "http://www.androidquery.com/z/images/vikispot/android-w.png";
		aq.id(R.id.image).progress(R.id.progress).image(url);
		
	}
	
	public void image_cache(){
		
		boolean memCache = false;
		boolean fileCache = true;

		String url = "http://www.androidquery.com/z/images/vikispot/android-w.png";
		aq.id(R.id.image).progress(R.id.progress).image(url, memCache, fileCache);
	}
	
	
	public void image_down(){
		
		String imageUrl = "http://farm6.static.flickr.com/5035/5802797131_a729dac808_b.jpg";            
		aq.id(R.id.image).progress(R.id.progress).image(imageUrl, true, true, 200, 0);

	}
	
	
	public void image_fallback(){
		
		String imageUrl = "http://www.androidquery.com/z/images/vikispot/xyz.png";
		aq.id(R.id.image).progress(R.id.progress).image(imageUrl, true, true, 0, R.drawable.image_missing);
		
	}
	
	
	
	public void image_preload(){
		
		String thumbnail = "http://farm6.static.flickr.com/5035/5802797131_a729dac808_s.jpg";	
		Bitmap preset = aq.getCachedImage(thumbnail);
		
		String imageUrl = "http://farm6.static.flickr.com/5035/5802797131_a729dac808_b.jpg";		
		aq.id(R.id.image).progress(R.id.progress).image(imageUrl, false, false, 0, 0, preset, 0, AQuery.RATIO_PRESERVE);
		
	}
	
	
	public void image_progress(){
		
		aq.id(R.id.image).clear();
		
		String imageUrl = "http://farm6.static.flickr.com/5035/5802797131_a729dac808_b.jpg";		
		aq.id(R.id.image).progress(R.id.progress).image(imageUrl, false, false);
		
		
	}
	
	public void image_progress_dialogbar(){
		
		ProgressDialog dialog = new ProgressDialog(this);
		
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle("Loading...");
		 
		aq.id(R.id.image).clear();
		
		String imageUrl = "http://farm6.static.flickr.com/5035/5802797131_a729dac808_b.jpg";		
		aq.id(R.id.image).progress(dialog).image(imageUrl, false, false);
		
		
	}
	
	public void image_progress_bar(){
		
		aq.id(R.id.progress_bar).visible();
		
		aq.id(R.id.image).clear();
		
		String imageUrl = "http://farm6.static.flickr.com/5035/5802797131_a729dac808_b.jpg";		
		aq.id(R.id.image).progress(R.id.progress_bar).image(imageUrl, false, false);
		
		//aq.id(R.id.image).progress(R.id.progress_box).image(imageUrl, false, false);
	}

	public void image_animation(){
	
		String imageUrl = "http://www.androidquery.com/z/images/vikispot/android-w.png";					
		aq.id(R.id.image).progress(R.id.progress).image(imageUrl, true, true, 0, 0, null, AQuery.FADE_IN);
		
	}
	
	
	public void image_animation2(){
		
		String imageUrl = "http://www.androidquery.com/z/images/vikispot/android-w.png";		
		aq.id(R.id.image).progress(R.id.progress).image(imageUrl, true, true, 0, 0, null, R.anim.slide_in_left);
		
	}
	
	public void image_ratio(){
		
		String imageUrl = "http://farm3.static.flickr.com/2199/2218403922_062bc3bcf2.jpg";	
		aq.id(R.id.image).progress(R.id.progress).image(imageUrl, true, true, 0, 0, null, AQuery.FADE_IN, AQuery.RATIO_PRESERVE);
	
	}
	
	public void image_round(){
		
		String url = "http://www.androidquery.com/z/images/vikispot/android-w.png";
		
		ImageOptions options = new ImageOptions();
		options.round = 15;
		options.fallback = R.drawable.image_missing;
		
		aq.id(R.id.image).image(url, options);
		
	}
	
	public void image_file(){
		
		String imageUrl = "http://farm6.static.flickr.com/5035/5802797131_a729dac808_b.jpg";
		File file = aq.getCachedFile(imageUrl);
		
		if(file != null){
			aq.id(R.id.image).progress(R.id.progress).image(file, 300);
		}
		
	}
	
	public void image_custom(){
		
		String imageUrl = "http://www.androidquery.com/z/images/vikispot/android-w.png";
		final int tint = 0x77AA0000;

		aq.id(R.id.image).progress(R.id.progress).visible().image(imageUrl, true, true, 0, 0, new BitmapAjaxCallback(){

	        @Override
	        public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status){
	           
	        	iv.setBackgroundColor(0xffcc0000);
                iv.setImageBitmap(bm);
                //iv.setColorFilter(tint, PorterDuff.Mode.SRC_ATOP);
	                
                showMeta(status);
	        }
		        
		}.round(10));
	}
	
	public void image_file_custom(){
		
		String imageUrl = "http://farm6.static.flickr.com/5035/5802797131_a729dac808_b.jpg";
		File file = aq.getCachedFile(imageUrl);
		final int tint = 0x77AA0000;
		
		if(file != null){
			
			aq.id(R.id.image).progress(R.id.progress).visible().image(file, true, 300, new BitmapAjaxCallback(){

		        @Override
		        public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status){
		           
	                iv.setImageBitmap(bm);
	                iv.setColorFilter(tint, PorterDuff.Mode.SRC_ATOP);
		                
	                showMeta(status);
		        }
			        
			});
			
		}
		
	}
	
	
	public void image_dup(){
		
		String imageUrl = "http://www.androidquery.com/z/images/vikispot/android-w.png";
		aq.id(R.id.image).image(imageUrl, false, false);

		//no network fetch for 2nd request, image will be shown when first request is completed
		aq.id(R.id.image2).image(imageUrl, false, false);
		
	}
	
	public void image_button(){
			
		String tb = "http://androidquery.appspot.com/z/demo/button.png";
		aq.id(R.id.button).image(tb);
		
	}
	
	public void image_advance(){
		
		aq.id(R.id.image).width(LayoutParams.FILL_PARENT);
		
		String imageUrl = "http://farm3.static.flickr.com/2199/2218403922_062bc3bcf2.jpg";	
	
		BitmapAjaxCallback cb = new BitmapAjaxCallback();
		cb.url(imageUrl).animation(AQuery.FADE_IN).ratio(1.0f);
		
		aq.id(R.id.image).image(cb);
		
	}
	
	public void image_access_file(){
		
		String imageUrl = "http://www.androidquery.com/z/images/vikispot/android-w.png";
		File file = aq.getCachedFile(imageUrl);
		
		if(file != null){
			showResult("File:" + file + " Length:" + file.length(), null);
		}
		
	}
	
	public void image_access_memory(){
		
		String imageUrl = "http://www.androidquery.com/z/images/vikispot/android-w.png";			
		Bitmap bm = aq.getCachedImage(imageUrl);
		
		if(bm != null){
			showResult("Dimension:" + bm.getWidth() + "x" + bm.getHeight(), null);
		}
		
	}
	
	public void image_pre_cache(){
		
		String imageUrl = "http://farm3.static.flickr.com/2199/2218403922_062bc3bcf2.jpg";
		
		File file = aq.getCachedFile(imageUrl);
		
		if(file != null){
			showTextResult("File cached:" + file.getAbsolutePath() + " Length:" + file.length());
		}
		
		aq.id(R.id.image).image(imageUrl);
		
	}
	
	
	public void image_nocache(){
		
		String url = "http://www.androidquery.com/z/images/vikispot/android-w.png";
		
		//force a network refetch without any caching
		aq.id(R.id.image).image(url, false, false);
		
		//force no proxy cache by appending a random number 
		String url2 = url + "?t=" + System.currentTimeMillis();
		aq.id(R.id.image2).image(url2, false, false);
		
	}
	
	public void image_clear_mem(){
		BitmapAjaxCallback.clearCache();
		showTextResult("Bitmap Memcache cleared");
	}
	
	public void image_clear_disk(){
		
		AQUtility.cleanCacheAsync(this, 0, 0);
		showTextResult("File cache cleared");
	}
	
	public void image_cache_dir(){
		
		File ext = Environment.getExternalStorageDirectory();
		File cacheDir = new File(ext, "myapp");
		
		AQUtility.setCacheDir(cacheDir);
		
		AQUtility.debug("cache dir exist", cacheDir.exists());
		
		String url = "http://www.androidquery.com/z/images/vikispot/android-w.png";
		aq.cache(url, 0);
		
		File file = AQUtility.getCacheFile(AQUtility.getCacheDir(this, AQuery.CACHE_DEFAULT), url);
		
		showTextResult(file.getAbsolutePath());
	}
	
	private static final int SEND_REQUEST = 12;
	
	public void image_send(){
		
		String url = "http://www.androidquery.com/z/images/vikispot/android-w.png";		
		File file = aq.makeSharedFile(url, "android.png");
		
		if(file != null){		
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("image/jpeg");
			intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
			startActivityForResult(Intent.createChooser(intent, "Share via:"), SEND_REQUEST);
		}
	}
	
	
	public void image_chart(){
		
        // EXAMPLE from http://code.google.com/p/charts4j/
        
        final int NUM_POINTS = 25;
        final double[] competition = new double[NUM_POINTS];
        final double[] mywebsite = new double[NUM_POINTS];
        for (int i = 0; i < NUM_POINTS; i++) {
            competition[i] = 100-(Math.cos(30*i*Math.PI/180)*10 + 50)*i/20;
            mywebsite[i] = (Math.cos(30*i*Math.PI/180)*10 + 50)*i/20;
        }
        Line line1 = Plots.newLine(Data.newData(mywebsite), Color.newColor("CA3D05"), "My Website.com");
        line1.setLineStyle(LineStyle.newLineStyle(3, 1, 0));
        line1.addShapeMarkers(Shape.DIAMOND, Color.newColor("CA3D05"), 12);
        line1.addShapeMarkers(Shape.DIAMOND, Color.WHITE, 8);
        Line line2 = Plots.newLine(Data.newData(competition), SKYBLUE, "Competition.com");
        line2.setLineStyle(LineStyle.newLineStyle(3, 1, 0));
        line2.addShapeMarkers(Shape.DIAMOND, SKYBLUE, 12);
        line2.addShapeMarkers(Shape.DIAMOND, Color.WHITE, 8);


        // Defining chart.
        LineChart chart = GCharts.newLineChart(line1, line2);
        chart.setSize(600, 450);
        
        String dummy = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
		String title = "Very long title " + dummy + dummy + dummy + dummy + dummy;
		
		chart.setTitle(title);
		
		chart.addHorizontalRangeMarker(40, 60, Color.newColor(RED, 30));
        chart.addVerticalRangeMarker(70, 90, Color.newColor(GREEN, 30));
        chart.setGrid(25, 25, 3, 2);

        // Defining axis info and styles
        AxisStyle axisStyle = AxisStyle.newAxisStyle(WHITE, 12, AxisTextAlignment.CENTER);
        AxisLabels xAxis = AxisLabelsFactory.newAxisLabels("Nov", "Dec", "Jan", "Feb", "Mar");
        xAxis.setAxisStyle(axisStyle);
        AxisLabels xAxis2 = AxisLabelsFactory.newAxisLabels("2007", "2007", "2008", "2008", "2008");
        xAxis2.setAxisStyle(axisStyle);
        AxisLabels yAxis = AxisLabelsFactory.newAxisLabels("", "25", "50", "75", "100");
        AxisLabels xAxis3 = AxisLabelsFactory.newAxisLabels("Month", 50.0);
        xAxis3.setAxisStyle(AxisStyle.newAxisStyle(WHITE, 14, AxisTextAlignment.CENTER));
        yAxis.setAxisStyle(axisStyle);
        AxisLabels yAxis2 = AxisLabelsFactory.newAxisLabels("Hits", 50.0);
        yAxis2.setAxisStyle(AxisStyle.newAxisStyle(WHITE, 14, AxisTextAlignment.CENTER));
        yAxis2.setAxisStyle(axisStyle);

        // Adding axis info to chart.
        chart.addXAxisLabels(xAxis);
        chart.addXAxisLabels(xAxis2);
        chart.addXAxisLabels(xAxis3);
        chart.addYAxisLabels(yAxis);
        chart.addYAxisLabels(yAxis2);

        // Defining background and chart fills.
        chart.setBackgroundFill(Fills.newSolidFill(Color.newColor("1F1D1D")));
        LinearGradientFill fill = Fills.newLinearGradientFill(0, Color.newColor("363433"), 100);
        fill.addColorAndOffset(Color.newColor("2E2B2A"), 0);
        chart.setAreaFill(fill);
        String url = chart.toURLString();
		
        showTextResult("Image URL:" + url);
        
		aq.id(R.id.image).progress(R.id.progress).image(url);
		
	}
	
	public void image_auto_rotate(){
		
		String imageUrl = "http://res.dbkon.co.kr/resource/201302091360376386575001.jpg";            
		
		//imageUrl = "http://www.androidquery.com/z/images/vikispot/android-w.png";
		
		BitmapAjaxCallback cb = new BitmapAjaxCallback();
		cb.url(imageUrl).targetWidth(300).rotate(true);
		
		aq.id(R.id.image).image(cb);
		
	}
	
	
}
