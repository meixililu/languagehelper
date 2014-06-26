package com.messi.languagehelper.wxapi;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.baidu.mobstat.StatService;
import com.iflytek.cloud.speech.SpeechUser;
import com.messi.languagehelper.AboutActivity;
import com.messi.languagehelper.BaseActivity;
import com.messi.languagehelper.HelpActivity;
import com.messi.languagehelper.ImgShareActivity;
import com.messi.languagehelper.R;
import com.messi.languagehelper.RecommendActivity;
import com.messi.languagehelper.SettingActivity;
import com.messi.languagehelper.adapter.MainPageAdapter;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.ToastUtil;
import com.messi.languagehelper.util.WechatUtil;
import com.messi.languagehelper.views.PagerSlidingTabStrip;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler,OnClickListener {
	
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
	private ViewPager viewPager;
	private PagerSlidingTabStrip indicator;
	private MainPageAdapter mAdapter;
	
	private String[] mPlanetTitles;
	private IWXAPI api;
	private long exitTime = 0;
	private Bundle bundle;
	private boolean isRespondWX;
	public static int currentIndex = 0;
	public static WXEntryActivity mWXEntryActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.content_frame);
			mWXEntryActivity = this;
			initDatas();
			initViews();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initDatas(){
		setMiddleVolume();
		api = WXAPIFactory.createWXAPI(this, WechatUtil.APP_ID, true);
		bundle = getIntent().getExtras();
		api.handleIntent(getIntent(), this);
		SpeechUser.getUser().login(this, null, null, "appid=" + getString(R.string.app_id), null);
	}
	
	private void initViews(){
		mPlanetTitles = getResources().getStringArray(R.array.planets_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
		viewPager = (ViewPager) findViewById(R.id.pager);
		indicator = (PagerSlidingTabStrip) findViewById(R.id.indicator);
		mAdapter = new MainPageAdapter(this.getSupportFragmentManager(),bundle,this);
		viewPager.setAdapter(mAdapter);
		viewPager.setOffscreenPageLimit(3);
		indicator.setViewPager(viewPager);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_item, mPlanetTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
            	mActionBar.setTitle(WXEntryActivity.this.getResources().getString(R.string.app_name));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
            	mActionBar.setTitle(WXEntryActivity.this.getResources().getString(R.string.title_more));
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView parent, View view, int position, long id) {
	    	try {
				if(position == 2){
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("market://details?id=com.messi.languagehelper"));
					startActivity(intent);
					StatService.onEvent(WXEntryActivity.this, "1.6_commend", "吐槽评价按钮", 1);
				}else if(position == 5){
					
					StatService.onEvent(WXEntryActivity.this, "1.8_contantus", "联系我们按钮", 1);
				}else{
					Intent intent = new Intent();
					if(position == 0){
						intent.setClass(WXEntryActivity.this, SettingActivity.class);
						StatService.onEvent(WXEntryActivity.this, "1.6_settingbtn", "应用设置按钮", 1);
					}else if(position == 1){
						intent.setClass(WXEntryActivity.this, RecommendActivity.class);
						StatService.onEvent(WXEntryActivity.this, "1.6_recommendbtn", "推荐应用按钮", 1);
					}else if(position == 3){
						intent.setClass(WXEntryActivity.this, HelpActivity.class);
						StatService.onEvent(WXEntryActivity.this, "1.7_help", "使用帮助按钮", 1);
					}else if(position == 4){
						intent.setClass(WXEntryActivity.this, AboutActivity.class);
						StatService.onEvent(WXEntryActivity.this, "1.6_aboutus", "关于我们按钮", 1);
					}
					WXEntryActivity.this.startActivity(intent);
					if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
						mDrawerLayout.closeDrawer(mDrawerList);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,0,0,this.getResources().getString(R.string.menu_share)).setIcon(R.drawable.icon_share).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//		menu.add(0,1,1,this.getResources().getString(R.string.menu_settings)).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:  
			menu();
			StatService.onEvent(this, "1.6_homemenu", "主页左上角菜单", 1);
			break;
		case 0:  
			toShareActivity();
			StatService.onEvent(this, "1.8_menu_to_share_activity", "去自定义分享页面", 1);
			break;
//		case 1:  
//			toSettingActivity();
//			StatService.onEvent(this, "1.8_menu_to_settings", "语速调节", 1);
//			break;
		}
       return true;
	}
	
	private void menu(){
		if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
        	mDrawerLayout.closeDrawer(mDrawerList);
        } else {
        	mDrawerLayout.openDrawer(mDrawerList);
        }
	}

	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
//        menu.findItem(R.drawable.menu_d).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
	
	@Override
	public void onClick(View v) {
//		if (v.getId() == R.id.top_menu_btn) {
//			
//		}
	}
	
	private void toShareActivity(){
		Intent intent = new Intent(this, ImgShareActivity.class); 
		startActivity(intent); 
	}
	
	private void toSettingActivity(){
		Intent intent = new Intent(this, SettingActivity.class); 
		startActivity(intent); 
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
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
		switch (req.getType()) {
		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
			isRespondWX = true;	
			LogUtil.DefalutLog("respond wx");
			break;
		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
			isRespondWX = false;	
			LogUtil.DefalutLog("show message wx");
			break;
		}
	}

	@Override
	public void onResp(BaseResp resp) {
		int result = 0;
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			result = R.string.errcode_success;
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = R.string.errcode_cancel;
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = R.string.errcode_deny;
			break;
		default:
			result = R.string.errcode_unknown;
			break;
		}
		ToastUtil.diaplayMesShort(this, result);
		LogUtil.DefalutLog("onResp");
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_MENU:
			 menu();
			 StatService.onEvent(this, "1.6_xitongmenu", "系统菜单按钮", 1);
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
}
