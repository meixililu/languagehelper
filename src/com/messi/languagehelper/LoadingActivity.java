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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
	private LinearLayout middle_ad;
	private RelativeLayout parent_layout;
	private ImageView forward_img;
	private IFLYFullScreenAd fullScreenAd;
	private boolean isStopToGoNext;
	
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
		parent_layout = (RelativeLayout) findViewById(R.id.parent_layout);
		forward_img = (ImageView) findViewById(R.id.forward_img);
		middle_ad = (LinearLayout)findViewById(R.id.middle_ad);
		forward_img.setOnClickListener(this);
		ShortCut.addShortcut(this, mSharedPreferences);
		if(isEnoughTimeToShowAd()){
			fullScreenAd = ADUtil.initQuanPingAD(this, middle_ad);
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
					toNextPage();
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
			});
		}else{
			onError();
		}
	}
	
	private void onClickAd(){
		isStopToGoNext = true;
		forward_img.setVisibility(View.VISIBLE);
		parent_layout.setOnClickListener(this);
		StatService.onEvent(LoadingActivity.this, "ad_kaiping", "点击开屏广告", 1);
	}
	
	private void onError(){
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				toNextPage();
			}
		}, 800);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtil.DefalutLog("LoadingActivity---onActivityResult");
	}
	
	private boolean isEnoughTimeToShowAd(){
		if(ADUtil.IsShowAdImmediately){
			return true;
		}else{
			int IsCanShowAD_Loading = mSharedPreferences.getInt(KeyUtil.IsCanShowAD_Loading, 0);
			if(IsCanShowAD_Loading > 0){
				return true;
			}else{
				IsCanShowAD_Loading++;
				Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.IsCanShowAD_Loading,IsCanShowAD_Loading);
				return false;
			}
		}
	}
	
	private void toNextPage(){
		try {
			if(!isStopToGoNext){
				Intent intent = new Intent(LoadingActivity.this, WXEntryActivity.class);
				startActivity(intent);
				finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
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
	protected void onDestroy() {
		super.onDestroy();
		if(middle_ad != null){
			middle_ad.removeAllViews();
		}
		if(fullScreenAd != null){
			fullScreenAd = null;
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.parent_layout:
			isStopToGoNext = false;
			toNextPage();
			break;
		case R.id.forward_img:
			isStopToGoNext = false;
			toNextPage();
			break;
		}
	}
}
