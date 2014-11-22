package com.messi.languagehelper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
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

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.loading_activity);
			mSharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
			boolean isShowLoading = Settings.isEnoughTime(mSharedPreferences,IntervalTime);
			if(isShowLoading){
				init();
				new WaitTask().execute();
			}else{
				toNextPage();
			}
		}
		
		private void init(){
			ShortCut.addShortcut(this, mSharedPreferences);
			middle_ad = (LinearLayout)findViewById(R.id.middle_ad);
			final IFLYFullScreenAdView fullScreenAd = ADUtil.initQuanPingAD(this, middle_ad);
			fullScreenAd.setAdListener(new IFLYAdListener() {
				@Override
				public void onReceiveAd() {
					fullScreenAd.showAd();
				}
				@Override
				public void onPresentScreen() {
				}
				@Override
				public void onFailedToReceiveAd(String arg0) {
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
		}
}
