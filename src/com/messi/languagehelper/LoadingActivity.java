package com.messi.languagehelper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

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

public class LoadingActivity extends Activity {
	
		public static final long IntervalTime = 1000 * 60 * 30;
//		public static final long IntervalTime = 1000 * 60 * 1;
		// 缓存，保存当前的引擎参数到下一次启动应用程序使用.
		private SharedPreferences mSharedPreferences;
		private LinearLayout middle_ad;
		private IFLYFullScreenAd fullScreenAd;
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			setContentView(R.layout.loading_activity);
			try {
				mSharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
//				boolean isShowLoading = Settings.isEnoughTime(mSharedPreferences,IntervalTime);
//				if(isShowLoading){
					init();
					new WaitTask().execute();
//				}else{
//					toNextPage();
//				}
			} catch (Exception e) {
				toNextPage();
				e.printStackTrace();
			}
		}
		
		private void init(){
			try {
				ShortCut.addShortcut(this, mSharedPreferences);
				middle_ad = (LinearLayout)findViewById(R.id.middle_ad);
				fullScreenAd = ADUtil.initQuanPingAD(this, middle_ad);
//				if(showNewFunction()){
					fullScreenAd.loadAd(new IFLYAdListener() {
						@Override
						public void onAdReceive() {
							if(fullScreenAd != null){
								fullScreenAd.showAd();
							}
							LogUtil.DefalutLog("LoadingActivity---fullScreenAd---onAdReceive");
						}
						@Override
						public void onAdClose() {
						}
						@Override
						public void onAdClick() {
						}
						@Override
						public void onAdFailed(AdError arg0) {
							LogUtil.DefalutLog("LoadingActivity---onAdFailed:"+arg0.getErrorCode()+"---"+arg0.getErrorDescription());
						}
					});
//				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private boolean showNewFunction(){
			int IsCanShowAD_Loading = mSharedPreferences.getInt(KeyUtil.IsCanShowAD_Loading, 0);
	        if(IsCanShowAD_Loading > 0){
	        	return true;
	        }else{
	        	IsCanShowAD_Loading++;
	        	Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.IsCanShowAD_Loading,IsCanShowAD_Loading);
	        	return false;
	        }
		}
		
		private void toNextPage(){
			Intent intent = new Intent(LoadingActivity.this, WXEntryActivity.class);
			startActivity(intent);
			finish();
		}

		class WaitTask extends AsyncTask<Void, Void, Void>{

			@Override
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				toNextPage();
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
			middle_ad.removeAllViews();
			if(fullScreenAd != null){
				fullScreenAd = null;
			}
		}
}
