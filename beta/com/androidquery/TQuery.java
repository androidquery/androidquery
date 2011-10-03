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
	
	
	public File makeSharableFile(String url, String filename){
		
		File file = null;
		
		try{
		
			File cached = getCachedFile(url);
			
			if(cached != null){
			
				File temp = AQUtility.getTempDir();
				
				if(temp != null){
				
					file = new File(temp, filename);
					file.createNewFile();
					
					FileInputStream fis = new FileInputStream(cached);
					FileOutputStream fos = new FileOutputStream(file);
					
					try{
						AQUtility.copy(fis, fos);
					}finally{
						fis.close();
						fos.close();
					}
					
				}
			}
		
		}catch(Exception e){
			AQUtility.report(e);
		}
		
		return file;
	}
	
	
}
