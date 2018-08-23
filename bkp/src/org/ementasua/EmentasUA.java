package org.ementasua;

import java.io.File;

import utilclass.EmentasGetter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.TextView;

public class EmentasUA extends FragmentActivity {

	private static final String [] TITLES = {"Santiago","Crasto","SnackBar"};
	
	private SwipeyTabs mTabs;
	private ViewPager mViewPager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int toShow;

		setContentView(R.layout.activity_swipeytab);

		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mTabs = (SwipeyTabs) findViewById(R.id.swipeytabs);

		SwipeyTabsPagerAdapter adapter = new SwipeyTabsPagerAdapter(this, getSupportFragmentManager());
		mViewPager.setAdapter(adapter);
		mTabs.setAdapter(adapter);
		mViewPager.setOnPageChangeListener(mTabs);
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		if(!sharedPreferences.getBoolean("log_show", false)){
			File f = new File(getFilesDir(), "EmentasUA");
			if(f.exists())
				f.delete();
			startActivity(new Intent(getApplicationContext(), LogChanges.class));
		}
		
		toShow = Integer.parseInt(sharedPreferences.getString("cantInicial", "0"));
				
		mViewPager.setCurrentItem(toShow);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.optionsmenu, menu);
		return true; 
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()) {
			case R.id.refresh:
				EmentasGetter.deleteFile();
				startActivity(new Intent(getApplicationContext(), Start.class));
				finish();
				return true;
			case R.id.about:
				startActivity(new Intent(this, About.class));
				return true;
			case R.id.prefs:
				startActivity(new Intent(this, Prefs.class));
				return true;
		}
		return false; 
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}
	
	private class SwipeyTabsPagerAdapter extends FragmentPagerAdapter implements
			SwipeyTabsAdapter {
		
		private final Context mContext;

		public SwipeyTabsPagerAdapter(Context context, FragmentManager fm) {
			super(fm);

			this.mContext = context;
		}

		@Override
		public Fragment getItem(int position) {
			return SwipeyTabFragment.newInstance(TITLES[position]);
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		public TextView getTab(final int position, SwipeyTabs root) {
			final TextView view = (TextView) LayoutInflater.from(mContext).inflate(R.layout.swipey_tab_indicator, root, false);
			view.setText(TITLES[position]);
			
			view.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					mViewPager.setCurrentItem(position);
				}
			});
			
			view.setOnLongClickListener(new OnLongClickListener() {
				public boolean onLongClick(View v) {
					
					Intent i = new Intent(getApplicationContext(), InfoCanteens.class);
					i.putExtra("canteens", view.getText());
					
					startActivity(i);
					
					return true;
				}
			});
			
			return view;
		}

	}

}