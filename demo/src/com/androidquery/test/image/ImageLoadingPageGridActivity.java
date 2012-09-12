package com.androidquery.test.image;

import com.androidquery.AQuery;
import com.androidquery.R;
import com.androidquery.util.AQUtility;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public class ImageLoadingPageGridActivity extends FragmentActivity{

	private AQuery aq = new AQuery(this);
	private ViewPager pager;
	private PageAdapter adapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.image_page_grid_activity);
		
		pager = (ViewPager) findViewById(R.id.pager);
		
		adapter = new PageAdapter(getSupportFragmentManager());

		pager = (ViewPager) findViewById(R.id.pager);		
		pager.setAdapter(adapter);
		
		pager.setOffscreenPageLimit(2);
		
	}
	
	
	private static String[] topics = {"dog", "cat", "bird", "panda", "horse", "elephant", "bear", "butterfly", "monkey", "fish", "tiger", "chicken", "pig"};
	
	private class PageAdapter extends FragmentStatePagerAdapter{


		public PageAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int pos) {
			
			AQUtility.debug("primary", topics[pos]);
			
			Bundle args = new Bundle();
			
			args.putString("topic", topics[pos]);
			
			ImageGridFragment fragment = (ImageGridFragment) Fragment.instantiate(ImageLoadingPageGridActivity.this, ImageGridFragment.class.getName(), args);
			
			return fragment;
		}

		@Override
		public int getCount() {
			return topics.length;
		}
		
		@Override
        public CharSequence getPageTitle (int pos) {
            return topics[pos];
        }
		
		@Override
		public void setPrimaryItem(ViewGroup container, int position, Object object){
			
			AQUtility.debug("primary", topics[position]);
			
			ImageGridFragment fragment = (ImageGridFragment) object;
			fragment.init();
			
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object){
			AQUtility.debug("destroyItem", topics[position]);
		}
		
		
	}
	
}
