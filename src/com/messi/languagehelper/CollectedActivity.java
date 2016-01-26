package com.messi.languagehelper;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.iflytek.cloud.SpeechSynthesizer;
import com.messi.languagehelper.adapter.CollectedActivityAdapter;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.views.PagerSlidingTabStrip;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;

public class CollectedActivity extends BaseActivity implements OnClickListener {

	private PagerSlidingTabStrip indicator;
	private ViewPager viewPager;
	private ProgressBarCircularIndeterminate progressbar;

	// 缓存，保存当前的引擎参数到下一次启动应用程序使用.
	private SharedPreferences mSharedPreferences;
	//合成对象.
	private SpeechSynthesizer mSpeechSynthesizer;
	
	private CollectedActivityAdapter mAdapter;

	private Bundle bundle;
	private int maxNumber = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.collected_main);
		init();
	}
	
	private void init() {
		getSupportActionBar().setTitle(getResources().getString(R.string.title_favorite));
		mSharedPreferences = this.getSharedPreferences(this.getPackageName(), Activity.MODE_PRIVATE);
		viewPager = (ViewPager) findViewById(R.id.pager);
		indicator = (PagerSlidingTabStrip) findViewById(R.id.indicator);
		progressbar = (ProgressBarCircularIndeterminate) findViewById(R.id.collected_progressbar_m);
		mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(this, null);
		mAdapter = new CollectedActivityAdapter(this.getSupportFragmentManager(),bundle,this);
		
		viewPager.setAdapter(mAdapter);
		indicator.setViewPager(viewPager);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.DefalutLog("CollectedFragment-onDestroy");
	}
	
	@Override
	public void onClick(View v) {
		
	}
}
