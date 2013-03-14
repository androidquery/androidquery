package com.androidquery.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.view.View;
import android.widget.ProgressBar;

import com.androidquery.AQuery;

public class Progress implements Runnable{

	private ProgressBar pb;
	private ProgressDialog pd;
	private Activity act;
	private View view;
	private boolean unknown;
	private int bytes;
	private int current;
	private String url;
	
	public Progress(Object p){
		
		if(p instanceof ProgressBar){
			pb = (ProgressBar) p;
		}else if(p instanceof ProgressDialog){
			pd = (ProgressDialog) p;
		}else if(p instanceof Activity){
			act = (Activity) p;
		}else if(p instanceof View){
			view = (View) p;
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

	@Override
	public void run() {
		dismiss(url);
	}
	
	public void show(String url){
		
		reset();
		
		if(pd != null){
			AQuery aq = new AQuery(pd.getContext());			
			aq.show(pd);
		}
		
		if(act != null){
			act.setProgressBarIndeterminateVisibility(true);
			act.setProgressBarVisibility(true);
		}
		
		if(pb != null){
			pb.setTag(AQuery.TAG_URL, url);
			pb.setVisibility(View.VISIBLE);
		}
		
		if(view != null){
			view.setTag(AQuery.TAG_URL, url);
			view.setVisibility(View.VISIBLE);
		}
		
	}
	
	public void hide(String url){
		
		if(AQUtility.isUIThread()){
			dismiss(url);
		}else{
			this.url = url;
			AQUtility.post(this);
		}
		
	}
	
	private void dismiss(String url){
		
		if(pd != null){
			AQuery aq = new AQuery(pd.getContext());			
			aq.dismiss(pd);
		}
		
		if(act != null){
			act.setProgressBarIndeterminateVisibility(false);
			act.setProgressBarVisibility(false);
		}
		
		if(pb != null){
			pb.setTag(AQuery.TAG_URL, url);
			pb.setVisibility(View.VISIBLE);
		}
		
		View pv = pb;
		if(pv == null){
			pv = view;
		}
		
		if(pv != null){
			
			Object tag = pv.getTag(AQuery.TAG_URL);
			if(tag == null || tag.equals(url)){
				pv.setTag(AQuery.TAG_URL, null);	
				
				if(pb != null && pb.isIndeterminate()){
					pv.setVisibility(View.GONE);						
				}
			}
		}
		
	}
	
	
	private void showProgress(Object p, String url, boolean show){
		
		if(p != null){
			
			if(p instanceof View){				

				View pv = (View) p;
				
				ProgressBar pbar = null;
				
				if(p instanceof ProgressBar){
					pbar = (ProgressBar) p;
				}
				
				if(show){
					pv.setTag(AQuery.TAG_URL, url);
					pv.setVisibility(View.VISIBLE);
					if(pbar != null){
						pbar.setProgress(0);	
						pbar.setMax(100);
					}
					
				}else{
					Object tag = pv.getTag(AQuery.TAG_URL);
					if(tag == null || tag.equals(url)){
						pv.setTag(AQuery.TAG_URL, null);	
						
						if(pbar != null && pbar.isIndeterminate()){
							pv.setVisibility(View.GONE);						
						}
					}
				}
			}else if(p instanceof Dialog){
				
				Dialog pd = (Dialog) p;
				
				AQuery aq = new AQuery(pd.getContext());
				
				if(show){
					aq.show(pd);
				}else{
					aq.dismiss(pd);
				}
				
			}else if(p instanceof Activity){
				
				Activity act = (Activity) p;;
				act.setProgressBarIndeterminateVisibility(show);
				act.setProgressBarVisibility(show);
			
				if(show){
					act.setProgress(0);
				}
			}
		}
		
	}
	
	
}
