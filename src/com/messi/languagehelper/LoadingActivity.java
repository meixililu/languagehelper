package com.messi.languagehelper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.messi.languagehelper.util.ShortCut;
import com.messi.languagehelper.wxapi.WXEntryActivity;

public class LoadingActivity extends Activity {
	
	// 缓存，保存当前的引擎参数到下一次启动应用程序使用.
	private SharedPreferences mSharedPreferences;
	private TextView app_name,subtitle;
	private TranslateAnimation mHideAnimation;
	private Animation fadein;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_activity);
		mSharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
		ShortCut.addShortcut(this, mSharedPreferences);
		initViews();
		new WaitTask().execute();
	}
	
	private void initViews(){
		app_name = (TextView)findViewById(R.id.app_name);
		subtitle = (TextView)findViewById(R.id.subtitle);
		mHideAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,  
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -0.7f);
		fadein = AnimationUtils.loadAnimation(this, R.anim.fade_in);
		mHideAnimation.setDuration(400);
		mHideAnimation.setFillAfter(true);
		fadein.setFillAfter(true);
		mHideAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation arg0) {
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			@Override
			public void onAnimationEnd(Animation arg0) {
				subtitle.setVisibility(View.VISIBLE);
				subtitle.startAnimation(fadein);
			}
		});
		fadein.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				Intent intent = new Intent(LoadingActivity.this, WXEntryActivity.class);
				startActivity(intent);
				finish();
			}
		});
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
	
	class WaitTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			app_name.startAnimation(mHideAnimation);
		}
	}

}
