package com.messi.languagehelper.wxapi;


import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.widgets.Dialog;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.messi.languagehelper.BaseActivity;
import com.messi.languagehelper.CollectedActivity;
import com.messi.languagehelper.MoreActivity;
import com.messi.languagehelper.R;
import com.messi.languagehelper.WebViewFragment;
import com.messi.languagehelper.adapter.MainPageAdapter;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.impl.FragmentProgressbarListener;
import com.messi.languagehelper.util.AVOUtil;
import com.messi.languagehelper.util.AppDownloadUtil;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.SDCardUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.views.PagerSlidingTabStrip;

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
		AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.UpdateInfo.UpdateInfo);
		query.whereEqualTo(AVOUtil.UpdateInfo.AppCode, "zyhy");
		query.whereEqualTo(AVOUtil.UpdateInfo.IsValid, "1");
		query.findInBackground(new FindCallback<AVObject>() {
		    public void done(List<AVObject> avObjects, AVException e) {
		    	if (avObjects != null && avObjects.size() > 0) {
		    		AVObject mAVObject = avObjects.get(0);
		    		showUpdateDialog(mAVObject);
		    	}
		    }
		});
	}
	
	private void showUpdateDialog(final AVObject mAVObject){
        	int newVersionCode = mAVObject.getInt(AVOUtil.UpdateInfo.VersionCode);
        	int oldVersionCode = Settings.getVersion(WXEntryActivity.this);
        	if(newVersionCode > oldVersionCode){
        		String updateInfo = mAVObject.getString(AVOUtil.UpdateInfo.AppUpdateInfo);
        		String downloadType = mAVObject.getString(AVOUtil.UpdateInfo.DownloadType);
        		String apkUrl = "";
        		if(downloadType.equals("apk")){
        			AVFile avFile = mAVObject.getAVFile(AVOUtil.UpdateInfo.Apk);
        			apkUrl = avFile.getUrl();
        		}else{
        			apkUrl = mAVObject.getString(AVOUtil.UpdateInfo.APPUrl);
        		}
        		final String downloadUrl = apkUrl;
        		LogUtil.DefalutLog("apkUrl:"+apkUrl);
    			Dialog dialog = new Dialog(WXEntryActivity.this, "更新啦,更新啦!", updateInfo);
    			dialog.addAcceptButton("好的");
    			dialog.addCancelButton("稍后");
    			dialog.setOnAcceptButtonClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						new AppDownloadUtil(WXEntryActivity.this,
								downloadUrl,
								mAVObject.getString(AVOUtil.UpdateInfo.AppName),
								mAVObject.getObjectId(),
								SDCardUtil.apkUpdatePath
								).DownloadFile();
					}
				});
    			dialog.show();
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
//			toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
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
		StatService.onEvent(this, "menu_to_share_page", "主页右上角去分享页面", 1);
	}
	
	private void toMoreActivity(){
		Intent intent = new Intent(this, MoreActivity.class); 
		startActivity(intent); 
		StatService.onEvent(this, "menu_to_more_page", "主页右上角去更多页面", 1);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			 toMoreActivity();
			 StatService.onEvent(this, "system_menu", "系统菜单按钮", 1);
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
	protected void onDestroy() {
		super.onDestroy();
		saveSelectTab();
		WebViewFragment.mMainFragment = null;
		boolean AutoClear = mSharedPreferences.getBoolean(KeyUtil.AutoClear, false);
		if(AutoClear){
			DataBaseUtil.getInstance().clearExceptFavorite();
		}
	}
	
	private void saveSelectTab(){
		int index = viewPager.getCurrentItem();
		LogUtil.DefalutLog("WXEntryActivity---onDestroy---saveSelectTab---index:"+index);
		Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.LastTimeSelectTab,index);
	}

	@Override
	public void onClick(View v) {
		
	}
}
