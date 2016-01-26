package com.messi.languagehelper;

import com.messi.languagehelper.adapter.JokePageAdapter;
import com.messi.languagehelper.impl.FragmentProgressbarListener;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

public class JokeActivity extends BaseActivity implements FragmentProgressbarListener{

	private TabLayout tablayout;
	private ViewPager viewpager;
	private JokePageAdapter pageAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.joke_activity);
		initViews();
	}
	
	private void initViews(){
		getSupportActionBar().setTitle(getResources().getString(R.string.leisuer_duanzi));
		tablayout = (TabLayout) findViewById(R.id.tablayout);
		viewpager = (ViewPager) findViewById(R.id.viewpager);
		
		pageAdapter = new JokePageAdapter(getSupportFragmentManager(),this);
		viewpager.setAdapter(pageAdapter);
		viewpager.setOffscreenPageLimit(3);
		tablayout.setTabsFromPagerAdapter(pageAdapter);
		tablayout.setupWithViewPager(viewpager);
	}
	
	
}
