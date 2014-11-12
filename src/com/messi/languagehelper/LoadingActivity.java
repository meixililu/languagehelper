package com.messi.languagehelper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.baidu.mobstat.StatService;
import com.messi.languagehelper.util.ADUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.ShortCut;
import com.messi.languagehelper.wxapi.WXEntryActivity;

public class LoadingActivity extends Activity {
	
		// 缓存，保存当前的引擎参数到下一次启动应用程序使用.
		private SharedPreferences mSharedPreferences;
		private View app_logo,subtitle;
		private LinearLayout middle_ad;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.loading_activity);
			mSharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
			boolean isShowLoading = Settings.isTodayShow(mSharedPreferences);
//			if(!isShowLoading){
				init();
				new WaitTask().execute();
//			}else{
//				toNextPage();
//			}
		}
		
		private void init(){
			ShortCut.addShortcut(this, mSharedPreferences);
			app_logo = (View)findViewById(R.id.app_logo);
			subtitle = (View)findViewById(R.id.subtitle);
			middle_ad = (LinearLayout)findViewById(R.id.middle_ad);
			ADUtil.initQuanPingAD(this, middle_ad);
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
					Thread.sleep(3000);
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
