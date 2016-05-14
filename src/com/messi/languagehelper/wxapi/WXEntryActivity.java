package com.messi.languagehelper.wxapi;


import com.avos.avoscloud.AVAnalytics;
import com.baidu.autoupdatesdk.AppUpdateInfo;
import com.baidu.autoupdatesdk.AppUpdateInfoForInstall;
import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.CPCheckUpdateCallback;
import com.baidu.autoupdatesdk.CPUpdateDownloadCallback;
import com.gc.materialdesign.widgets.Dialog;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.messi.languagehelper.BaseActivity;
import com.messi.languagehelper.CollectedActivity;
import com.messi.languagehelper.LoadingActivity;
import com.messi.languagehelper.MoreActivity;
import com.messi.languagehelper.R;
import com.messi.languagehelper.WebViewFragment;
import com.messi.languagehelper.adapter.MainPageAdapter;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.impl.FragmentProgressbarListener;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.NumberUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.views.PagerSlidingTabStrip;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class WXEntryActivity extends BaseActivity implements OnClickListener,FragmentProgressbarListener {
	
	private ViewPager viewPager;
	private PagerSlidingTabStrip indicator;
	private MainPageAdapter mAdapter;
	
	private long exitTime = 0;
	private Bundle bundle;
	private boolean isRespondWX;
	public static int currentIndex = 0;
	private SharedPreferences mSharedPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.content_frame);
			initDatas();
			initViews();
			checkUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void checkUpdate(){
		BDAutoUpdateSDK.cpUpdateCheck(this, new MyCPCheckUpdateCallback());
	}
	
	private class MyCPCheckUpdateCallback implements CPCheckUpdateCallback {

		@Override
		public void onCheckUpdateCallback(AppUpdateInfo info, AppUpdateInfoForInstall infoForInstall) {
			if(info != null || infoForInstall != null){
				showUpdateDialog(info, infoForInstall);
			}
		}
	}
	
	private void showUpdateDialog(final AppUpdateInfo info, final AppUpdateInfoForInstall infoForInstall){
		String updateInfo = "有更丰富的内容，更快的速度，更好的体验，快快更新吧！";
		if(info != null){
			updateInfo = info.getAppChangeLog();
		}else if(infoForInstall != null){
			updateInfo = infoForInstall.getAppChangeLog();
		}
		if(updateInfo.contains("<br>")){
			updateInfo = updateInfo.replace("<br>", "\n");
		}
		Dialog dialog = new Dialog(WXEntryActivity.this, "更新啦,更新啦!", updateInfo);
		dialog.addAcceptButton("好的");
		dialog.addCancelButton("稍后");
		dialog.setOnAcceptButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(infoForInstall != null && !TextUtils.isEmpty(infoForInstall.getInstallPath())) {
					BDAutoUpdateSDK.cpUpdateInstall(getApplicationContext(), infoForInstall.getInstallPath());
				}else if(info != null) {
					BDAutoUpdateSDK.cpUpdateDownload(WXEntryActivity.this, info, new UpdateDownloadCallback());
				}
			}
		});
		dialog.show();
	}
	
	private class UpdateDownloadCallback implements CPUpdateDownloadCallback {

		@Override
		public void onDownloadComplete(String apkPath) {
			BDAutoUpdateSDK.cpUpdateInstall(getApplicationContext(), apkPath);
		}
		@Override
		public void onStart() {
		}

		@Override
		public void onPercent(int percent, long rcvLen, long fileSize) {
		}

		@Override
		public void onFail(Throwable error, String content) {
		}

		@Override
		public void onStop() {
		}
		
	}
	
	private void initDatas(){
		bundle = getIntent().getExtras();
		SpeechUtility.createUtility(this, SpeechConstant.APPID + "=" +getString(R.string.app_id));
	}
	
	private void initViews(){
		mSharedPreferences = getSharedPreferences(this.getPackageName(), Activity.MODE_PRIVATE);
		if (toolbar != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		}
        
		viewPager = (ViewPager) findViewById(R.id.pager);
		indicator = (PagerSlidingTabStrip) findViewById(R.id.indicator);
		mAdapter = new MainPageAdapter(this.getSupportFragmentManager(),bundle,this);
		viewPager.setAdapter(mAdapter);
		viewPager.setOffscreenPageLimit(4);
		indicator.setViewPager(viewPager);
		
        setLastTimeSelectTab();
	}
	
	private void setLastTimeSelectTab(){
		int index = mSharedPreferences.getInt(KeyUtil.LastTimeSelectTab, 0);
		viewPager.setCurrentItem(index);
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_more:
			toMoreActivity();
			break;
		case R.id.action_collected:
			toCollectedActivity();
			break;
		}
       return true;
	}
	
	private void toCollectedActivity(){
		Intent intent = new Intent(this, CollectedActivity.class); 
		startActivity(intent); 
		AVAnalytics.onEvent(this, "index_pg_to_collectedpg");
	}
	
	private void toMoreActivity(){
		Intent intent = new Intent(this, MoreActivity.class); 
		startActivity(intent); 
		AVAnalytics.onEvent(this, "index_pg_to_morepg");
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			 toMoreActivity();
			 return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onBackPressed() {
    	if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(getApplicationContext(), this.getResources().getString(R.string.exit_program), 0).show();
			exitTime = System.currentTimeMillis();
		} else {
			finish();
		}
	}
	
	@Override
	public void onClick(View v) {
	}
	
	private void saveSelectTab(){
		int index = viewPager.getCurrentItem();
		LogUtil.DefalutLog("WXEntryActivity---onDestroy---saveSelectTab---index:"+index);
		Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.LastTimeSelectTab,index);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		saveSelectTab();
		WebViewFragment.mMainFragment = null;
		boolean AutoClear = mSharedPreferences.getBoolean(KeyUtil.AutoClear, false);
		if(AutoClear){
			DataBaseUtil.getInstance().clearExceptFavorite();
		}
	}

}
