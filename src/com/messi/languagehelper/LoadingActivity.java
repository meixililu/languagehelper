package com.messi.languagehelper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;

import com.baidu.mobstat.StatService;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.IFLYAdListener;
import com.iflytek.voiceads.IFLYFullScreenAd;
import com.messi.languagehelper.util.ADUtil;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.ShortCut;
import com.messi.languagehelper.wxapi.WXEntryActivity;

public class LoadingActivity extends Activity implements OnClickListener{
	
	private SharedPreferences mSharedPreferences;
	private ImageView forward_img;
	private IFLYFullScreenAd fullScreenAd;
	private Handler mHandler;
	private boolean isAdExposure;
	private boolean isAdClicked;
	
	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			mHandler.removeCallbacks(m3Runnable);
			mHandler.removeCallbacks(mRunnableFinal);
			toNextPage();
		}
	};
	
	private Runnable mRunnableFinal = new Runnable() {
		@Override
		public void run() {
			LogUtil.DefalutLog("LoadingActivity---mRunnableFinal");
			toNextPage();
		}
	};
	
	private Runnable m3Runnable = new Runnable() {
		@Override
		public void run() {
			LogUtil.DefalutLog("LoadingActivity---m3Runnable---isAdExposure:"+isAdExposure);
			if(!isAdExposure){
				mHandler.removeCallbacks(mRunnableFinal);
				toNextPage();
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			TransparentStatusbar();
			setContentView(R.layout.loading_activity);
			init();
		} catch (Exception e) {
			onError();
			e.printStackTrace();
		}
	}
	
	private void TransparentStatusbar(){
		if(VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            //透明状态栏 透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}else{
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}
	
	private void init(){
		mSharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
		mHandler = new Handler();
		forward_img = (ImageView) findViewById(R.id.forward_img);
		forward_img.setOnClickListener(this);
		ShortCut.addShortcut(this, mSharedPreferences);
		addToShowAdTimes();
		if(ADUtil.isShowAd(this)){
			fullScreenAd = ADUtil.initQuanPingAD(this);
			fullScreenAd.loadAd(new IFLYAdListener() {
				@Override
				public void onAdReceive() {
					if(fullScreenAd != null){
						fullScreenAd.showAd();
						LogUtil.DefalutLog("LoadingActivity---fullScreenAd---onAdReceive");
					}
				}
				@Override
				public void onAdClose() {
					if(!isAdClicked){
						toNextPage();
					}
					LogUtil.DefalutLog("LoadingActivity---fullScreenAd---onAdClose");
				}
				@Override
				public void onAdClick() {
					onClickAd();
					LogUtil.DefalutLog("LoadingActivity---fullScreenAd---onAdClick");
				}
				@Override
				public void onAdFailed(AdError arg0) {
					onError();
					LogUtil.DefalutLog("LoadingActivity---fullScreenAd---onAdFailed:"+arg0.getErrorCode()+"---"+arg0.getErrorDescription());
				}
				@Override
				public void onAdExposure() {
					isAdExposure = true;
					mHandler.postDelayed(mRunnableFinal, 3500);
					LogUtil.DefalutLog("LoadingActivity---fullScreenAd---onAdExposure");
				}
			});
		}else{
			onError();
		}
		startTask();
	}
	
	private void onClickAd(){
		isAdClicked = true;
		cancleRunable();
		forward_img.setVisibility(View.VISIBLE);
		StatService.onEvent(LoadingActivity.this, "ad_kaiping", "点击开屏广告", 1);
	}
	
	private void onError(){
		mHandler.postDelayed(mRunnable, 800);
	}
	
	private void startTask(){
		mHandler.postDelayed(m3Runnable, 3000);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtil.DefalutLog("LoadingActivity---onActivityResult");
	}
	
	private void addToShowAdTimes(){
		int IsCanShowAD = mSharedPreferences.getInt(KeyUtil.IsCanShowAD_Loading, 0);
		IsCanShowAD++;
		Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.IsCanShowAD_Loading, IsCanShowAD);
	}
	
	private void toNextPage(){
		try {
			Intent intent = new Intent(LoadingActivity.this, WXEntryActivity.class);
			startActivity(intent);
			finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void cancleRunable(){
		if(m3Runnable != null){
			mHandler.removeCallbacks(mRunnableFinal);
			mHandler.removeCallbacks(m3Runnable);
			mHandler.removeCallbacks(mRunnable);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		StatService.onPause(this);
	}
	@Override
	protected void onStop() {
		super.onStop();
		
	}
	@Override
	protected void onStart() {
		super.onStart();
		if(isAdClicked){
			toNextPage();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LogUtil.DefalutLog("LoadingActivity---onDestroy---destroyAd");
		if(fullScreenAd != null){
			fullScreenAd = null;
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.forward_img:
			toNextPage();
			break;
		}
	}
}
