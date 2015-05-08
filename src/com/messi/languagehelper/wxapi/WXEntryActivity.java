package com.messi.languagehelper.wxapi;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.messi.languagehelper.BaseActivity;
import com.messi.languagehelper.CollectedActivity;
import com.messi.languagehelper.ImgShareActivity;
import com.messi.languagehelper.R;
import com.messi.languagehelper.SettingActivity;
import com.messi.languagehelper.WebViewFragment;
import com.messi.languagehelper.adapter.MainPageAdapter;
import com.messi.languagehelper.adapter.MenuListItemAdapter;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.impl.FragmentProgressbarListener;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.views.PagerSlidingTabStrip;
import com.umeng.update.UmengUpdateAgent;

public class WXEntryActivity extends BaseActivity implements OnClickListener,FragmentProgressbarListener {
	
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
	private ViewPager viewPager;
	private ProgressBarCircularIndeterminate mProgressbar;
	private PagerSlidingTabStrip indicator;
	private MainPageAdapter mAdapter;
	private View leftMenuHeader;
	
	private String[] mPlanetTitles;
	private long exitTime = 0;
	private Bundle bundle;
	private boolean isRespondWX;
	public static int currentIndex = 0;
	private SharedPreferences mSharedPreferences;
	private LayoutInflater mInflater;

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
		UmengUpdateAgent.setUpdateOnlyWifi(false);
//		UmengUpdateAgent.setSlotId("");
		UmengUpdateAgent.setUpdateCheckConfig(false);
		UmengUpdateAgent.update(this);
	}
	
	private void initDatas(){
		bundle = getIntent().getExtras();
		SpeechUtility.createUtility(this, SpeechConstant.APPID + "=" +getString(R.string.app_id));
	}
	
	private void initViews(){
		mSharedPreferences = getSharedPreferences(this.getPackageName(), Activity.MODE_PRIVATE);
		mPlanetTitles = getResources().getStringArray(R.array.planets_array);
		mInflater = getLayoutInflater(); 
		
		if (toolbar != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(false);
			toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
		}
	    // Inflate a menu to be displayed in the toolbar
		// toolbar.inflateMenu(R.menu.your_toolbar_menu);
		mProgressbar = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndetermininate);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
		viewPager = (ViewPager) findViewById(R.id.pager);
		indicator = (PagerSlidingTabStrip) findViewById(R.id.indicator);
		mAdapter = new MainPageAdapter(this.getSupportFragmentManager(),bundle,this);
		initLeftMenuHeader();
		viewPager.setAdapter(mAdapter);
		viewPager.setOffscreenPageLimit(3);
		indicator.setViewPager(viewPager);
		MenuListItemAdapter adapter = new MenuListItemAdapter(this,mPlanetTitles,mDrawerLayout,mDrawerList);
		mDrawerList.setAdapter(adapter);
        // Set the list's click listener
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mDrawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));
		mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        setLastTimeSelectTab();
	}
	
	private void setLastTimeSelectTab(){
		int index = mSharedPreferences.getInt(KeyUtil.LastTimeSelectTab, 0);
		viewPager.setCurrentItem(index);
	}
	
	private void initLeftMenuHeader(){
		leftMenuHeader = mInflater.inflate(R.layout.left_menu_header, null);
		mDrawerList.addHeaderView(leftMenuHeader);
	}
	
	public void showProgressbar(){
		mProgressbar.setVisibility(View.VISIBLE);
	}
	
	public void hideProgressbar(){
		mProgressbar.setVisibility(View.GONE);
	}
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:  
			menu();
			break;
		case R.id.action_share:
			toShareActivity();
			break;
		case R.id.action_collected:
			toCollectedActivity();
			break;
		}
       return true;
	}
	
	private void menu(){
		if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
        	mDrawerLayout.closeDrawer(mDrawerList);
        } else {
        	mDrawerLayout.openDrawer(mDrawerList);
        }
		StatService.onEvent(this, "home_menu", "主页左上角菜单", 1);
	}

	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        If the nav drawer is open, hide action items related to the content view
//        menu.findItem(R.drawable.menu_d).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
	
	@Override
	public void onClick(View v) {
//		if (v.getId() == R.id.icon_share) {
//			toShareActivity();
//		}
	}
	
	private void toShareActivity(){
		Intent intent = new Intent(this, ImgShareActivity.class); 
		startActivity(intent); 
		StatService.onEvent(this, "menu_to_share_page", "主页右上角去分享页面", 1);
	}
	
	private void toCollectedActivity(){
		Intent intent = new Intent(this, CollectedActivity.class); 
		startActivity(intent); 
		StatService.onEvent(this, "menu_to_favor_page", "主页右上角去收藏页面", 1);
	}
	
	/**
	 * 设置为最大声
	 */
	private void setMiddleVolume(){
		AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);  
		int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
		int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		int middle = max/2;
		if(currentVolume < middle){
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, middle, 0);  
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			 menu();
			 StatService.onEvent(this, "system_menu", "系统菜单按钮", 1);
			 return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onBackPressed() {
		boolean autoplayUnreadDot = mSharedPreferences.getBoolean(KeyUtil.ShowNewFunction, true);
        if(autoplayUnreadDot){
        	if ((System.currentTimeMillis() - exitTime) > 2000) {
        		menu();
    			Toast.makeText(getApplicationContext(), this.getResources().getString(R.string.exit_program), 0).show();
    			exitTime = System.currentTimeMillis();
    		} else {
    			Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.ShowNewFunction,false);
    			finish();
    		}
        }else{
        	if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
        		mDrawerLayout.closeDrawer(mDrawerList);
        	} else{
        		if ((System.currentTimeMillis() - exitTime) > 2000) {
        			Toast.makeText(getApplicationContext(), this.getResources().getString(R.string.exit_program), 0).show();
        			exitTime = System.currentTimeMillis();
        		} else {
        			finish();
        		}
        	}
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
}
