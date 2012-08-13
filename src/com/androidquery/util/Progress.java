package com.androidquery.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.ProgressBar;

public class Progress {

	private ProgressBar pb;
	private ProgressDialog pd;
	private Activity act;
	private boolean unknown;
	private int bytes;
	private int current;
	
	public Progress(Object p){
		
		if(p instanceof ProgressBar){
			pb = (ProgressBar) p;
		}else if(p instanceof ProgressDialog){
			pd = (ProgressDialog) p;
		}else if(p instanceof Activity){
			act = (Activity) p;
		}
		
	}
	
	public void reset(){
		
		if(pb != null){
			pb.setProgress(0);
			pb.setMax(10000);
		}
		if(pd != null){
			pd.setProgress(0);
			pd.setMax(10000);
		}
		
		if(act != null){
			act.setProgress(0);
		}

		unknown = false;
		current = 0;
		bytes = 10000;
		
	}
	
	public void setBytes(int bytes){
		
		if(bytes <= 0){
			unknown = true;
			bytes = 10000;
		}
		
		this.bytes = bytes;
		
		if(pb != null){
			pb.setProgress(0);
			pb.setMax(bytes);
		}
		if(pd != null){
			pd.setProgress(0);
			pd.setMax(bytes);
		}
		
		
	}
	
	public void increment(int delta){
		
		if(pb != null){		
			pb.incrementProgressBy(unknown ? 1 : delta);
		}
		
		if(pd != null){
			pd.incrementProgressBy(unknown ? 1 : delta);
		}
		
		if(act != null){
			int p;
			if(unknown){
				p = current++;
			}else{
				current+= delta;
				p = (10000 * current) / bytes;
			}
			if(p > 9999){
				p = 9999;
			}
			act.setProgress(p);
		}
		
	}
	
	
	public void done(){
		
		if(pb != null){
			pb.setProgress(pb.getMax());
		}
		if(pd != null){
			pd.setProgress(pd.getMax());
		}
		
		if(act != null){
			act.setProgress(9999);
		}
		
	}
	
	/*
	     public static void copy(InputStream in, OutputStream out, int max, ProgressDialog dialog, ProgressBar bar) throws IOException {
       
    	AQUtility.debug("max", max);
    	
    	if(max <= 0) max = 100;
    	
    	if(dialog != null){
    		if(dialog.isIndeterminate()){
    			dialog = null;
    		}else{
    			dialog.setMax(max);
    			dialog.setProgress(0);
    		}
    	}
    	
    	if(bar != null){
    		
    		if(bar.isIndeterminate()){
    			bar = null;
    		}else{
    			bar.setMax(max);
    			bar.setProgress(0);
    		}
    	}
    	
    	byte[] b = new byte[IO_BUFFER_SIZE];
        int read;
        while((read = in.read(b)) != -1){
            out.write(b, 0, read);
            if(dialog != null) dialog.incrementProgressBy(read);        	
            if(bar != null) bar.incrementProgressBy(read);
            //AQUtility.debug("inc", read);
        }
        
        if(dialog != null) dialog.setProgress(max);
        if(bar != null) bar.setProgress(max);
        
    }
	 */
	
	
}
