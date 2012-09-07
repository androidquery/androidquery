package com.androidquery.callback;

import com.androidquery.util.Common;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.ProgressBar;

public interface ProgressStrategy {

	public void reset(Object progress);
	public void setBytes(Object progress, int bytes);
	public void increment(Object progress, int delta);
	public void done(Object progress);
	public void showProgress(Object progress, String url, boolean show);
}

/* package */ abstract class StrategyBase implements ProgressStrategy {
	
	protected boolean unknown;
	protected int bytes;
	protected int current;
	protected String url;
	
	@Override
	public void reset(Object progress) {
		unknown = false;
		current = 0;
		bytes = 10000;
	}
	
	@Override
	public void setBytes(Object progress, int bytes) {
		if(bytes <= 0){
			unknown = true;
			bytes = 10000;
		}
		
		this.bytes = bytes;
	}
	
	@Override
	public void increment(Object progress, int delta) {
	}
	
	@Override
	public void done(Object progress) {
	}
	
	@Override
	public void showProgress(Object progress, String url, boolean show) {
		Common.showProgress(progress, url, show);
	}
	
}

/* package */ class ProgressBarStrategy extends StrategyBase {
	
	@Override
	public void reset(Object progress) {
		ProgressBar pb = (ProgressBar) progress;
		pb.setProgress(0);
		pb.setMax(10000);
		super.reset(progress);
	}
	
	@Override
	public void setBytes(Object progress, int bytes) {
		super.setBytes(progress, bytes);
		ProgressBar pb = (ProgressBar) progress;
		pb.setProgress(0);
		pb.setMax(bytes);
	}
	
	@Override
	public void increment(Object progress, int delta) {
		ProgressBar pb = (ProgressBar) progress;
		pb.incrementProgressBy(unknown ? 1 : delta);
	}
	
	@Override
	public void done(Object progress) {
		ProgressBar pb = (ProgressBar) progress;
		pb.setProgress(pb.getMax());
	}
	
}

/* package */ class ProgressDialogStrategy extends StrategyBase {
	
	@Override
	public void reset(Object progress) {
		ProgressDialog pd = (ProgressDialog) progress;
		pd.setProgress(0);
		pd.setMax(10000);
		super.reset(progress);
	}
	
	@Override
	public void setBytes(Object progress, int bytes) {
		super.setBytes(progress, bytes);
		ProgressDialog pd = (ProgressDialog) progress;
		pd.setProgress(0);
		pd.setMax(bytes);
	}
	
	@Override
	public void increment(Object progress, int delta) {
		ProgressDialog pd = (ProgressDialog) progress;
		pd.incrementProgressBy(unknown ? 1 : delta);
	}
	
	@Override
	public void done(Object progress) {
		ProgressDialog pd = (ProgressDialog) progress;
		pd.setProgress(pd.getMax());
	}
	
}

/* package */ class ActivityStrategy extends StrategyBase {
	
	@Override
	public void reset(Object progress) {
		Activity act = (Activity) progress;
		act.setProgress(0);
		super.reset(progress);
	}

	@Override
	public void increment(Object progress, int delta) {
		Activity act = (Activity) progress;
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
	
	@Override
	public void done(Object progress) {
		Activity act = (Activity) progress;
		act.setProgress(9999);
	}
	
}

/* package */ class ViewStrategy extends StrategyBase {
	
}
