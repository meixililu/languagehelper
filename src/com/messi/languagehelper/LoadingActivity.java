package com.messi.languagehelper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.baidu.mobstat.StatService;
import com.iflytek.adserving.IFLYFullScreenAdView;
import com.iflytek.adserving.request.IFLYAdListener;
import com.messi.languagehelper.util.ADUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.ShortCut;
import com.messi.languagehelper.wxapi.WXEntryActivity;

public class LoadingActivity extends Activity {
	
//		public static final long IntervalTime = 1000 * 60 * 60 * 3;
		public static final long IntervalTime = 1000 * 60 * 1;
		// 缓存，保存当前的引擎参数到下一次启动应用程序使用.
		private SharedPreferences mSharedPreferences;
		private LinearLayout middle_ad;
		private IFLYFullScreenAdView fullScreenAd;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.loading_activity);
			mSharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
			boolean isShowLoading = Settings.isEnoughTime(mSharedPreferences,IntervalTime);
//			if(isShowLoading){
				init();
				new WaitTask().execute();
//			}else{
//				toNextPage();
//			}
		}
		
		private void init(){
			ShortCut.addShortcut(this, mSharedPreferences);
			middle_ad = (LinearLayout)findViewById(R.id.middle_ad);
			fullScreenAd = ADUtil.initQuanPingAD(this, middle_ad);
			fullScreenAd.loadAd(new IFLYAdListener() {
				@Override
				public void onAdReceive() {
					if(fullScreenAd != null){
						fullScreenAd.showAd();
					}
				}
				@Override
				public void onAdFailed(int arg0, String arg1) {
				}
				@Override
				public void onAdClose() {
				}
				@Override
				public void onAdClick() {
				}
			});
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
					Thread.sleep(2500);
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
			if(fullScreenAd != null){
				fullScreenAd.destroy();
				fullScreenAd = null;
			}
		}
}
