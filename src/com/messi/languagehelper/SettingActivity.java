package com.messi.languagehelper;

import com.avos.avoscloud.AVAnalytics;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.SDCardUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.ToastUtil;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class SettingActivity extends BaseActivity implements OnClickListener,SeekBar.OnSeekBarChangeListener {

	private TextView seekbar_text;
	private SeekBar seekbar;
	private ImageView unread_auto_play;
	private FrameLayout speak_yueyu,auto_play,auto_clear;
	private FrameLayout clear_all_except_favorite,clear_all;
	private FrameLayout auto_clear_after_finish;
	private CheckBox speak_yueyu_cb,auto_play_cb,auto_clear_cb;
	private CheckBox auto_clear_cb_after_finish;
	private SharedPreferences mSharedPreferences;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		init();
		initData();
	}

	private void init() {
		mSharedPreferences = getSharedPreferences(this.getPackageName(), Activity.MODE_PRIVATE);
		getSupportActionBar().setTitle(this.getResources().getString(R.string.title_settings));
        seekbar_text = (TextView) findViewById(R.id.seekbar_text);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        unread_auto_play = (ImageView) findViewById(R.id.unread_auto_play);
        speak_yueyu = (FrameLayout) findViewById(R.id.speak_yueyu);
        auto_play = (FrameLayout) findViewById(R.id.setting_auto_play);
        auto_clear = (FrameLayout) findViewById(R.id.setting_auto_clear);
        auto_clear_after_finish = (FrameLayout) findViewById(R.id.setting_auto_clear_after_finish);
        speak_yueyu_cb = (CheckBox) findViewById(R.id.speak_yueyu_cb);
        auto_clear_cb = (CheckBox) findViewById(R.id.setting_auto_clear_cb);
        auto_play_cb = (CheckBox) findViewById(R.id.setting_auto_play_cb);
        auto_clear_cb_after_finish = (CheckBox) findViewById(R.id.setting_auto_clear_cb_after_finish);
        clear_all_except_favorite = (FrameLayout) findViewById(R.id.setting_clear_all_except_favorite);
        clear_all = (FrameLayout) findViewById(R.id.setting_clear_all);
        
        seekbar.setOnSeekBarChangeListener(this);
        speak_yueyu.setOnClickListener(this);
        clear_all_except_favorite.setOnClickListener(this);
        clear_all.setOnClickListener(this);
        auto_play.setOnClickListener(this);
        auto_clear.setOnClickListener(this);
        auto_clear_after_finish.setOnClickListener(this);
	}
	
	private void initData(){
		seekbar_text.setText(this.getResources().getString(R.string.play_speed_text) + MainFragment.speed);
		seekbar.setProgress(MainFragment.speed);
		boolean checked = mSharedPreferences.getBoolean(KeyUtil.SpeakPutonghuaORYueyu, false);
		boolean autoplay = mSharedPreferences.getBoolean(KeyUtil.AutoPlayResult, false);
		boolean AutoClearInputAfterFinish = mSharedPreferences.getBoolean(KeyUtil.AutoClearInputAfterFinish, true);
		boolean AutoPlayUnreadDot = mSharedPreferences.getBoolean(KeyUtil.AutoPlayUnreadDot, false);
		boolean AutoClear = mSharedPreferences.getBoolean(KeyUtil.AutoClear, false);
		speak_yueyu_cb.setChecked(checked);
		auto_play_cb.setChecked(autoplay);
		auto_clear_cb.setChecked(AutoClear);
		auto_clear_cb_after_finish.setChecked(AutoClearInputAfterFinish);
		if(AutoPlayUnreadDot){
			unread_auto_play.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.speak_yueyu:
			if(speak_yueyu_cb.isChecked()){
				speak_yueyu_cb.setChecked(false);
			}else{
				speak_yueyu_cb.setChecked(true);
			}
			MainFragment.isSpeakYueyuNeedUpdate = true;
			Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.SpeakPutonghuaORYueyu, speak_yueyu_cb.isChecked());
			break;
		case R.id.setting_auto_play:
			auto_play_cb.setChecked(!auto_play_cb.isChecked());
			unread_auto_play.setVisibility(View.GONE);
			Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.AutoPlayUnreadDot,true);
			Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.AutoPlayResult,auto_play_cb.isChecked());
			AVAnalytics.onEvent(this, "setting_pg_auto_play");
			break;
		case R.id.setting_auto_clear_after_finish:
			auto_clear_cb_after_finish.setChecked(!auto_clear_cb_after_finish.isChecked());
			Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.AutoClearInputAfterFinish,auto_clear_cb_after_finish.isChecked());
			AVAnalytics.onEvent(this, "setting_pg_auto_clear_input");
			break;
		case R.id.setting_auto_clear:
			auto_clear_cb.setChecked(!auto_clear_cb.isChecked());
			MainFragment.isRefresh = true;
			DictionaryFragment.isRefresh = true;
			Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.AutoClear,auto_clear_cb.isChecked());
			AVAnalytics.onEvent(this, "setting_pg_auto_clear");
			break;
		case R.id.setting_clear_all_except_favorite:
			DataBaseUtil.getInstance().clearExceptFavorite();
			MainFragment.isRefresh = true;
			DictionaryFragment.isRefresh = true;
			ToastUtil.diaplayMesShort(SettingActivity.this, this.getResources().getString(R.string.clear_success));
			AVAnalytics.onEvent(this, "setting_pg_clear_all_except");
			break;
		case R.id.setting_clear_all:
			DataBaseUtil.getInstance().clearAll();
			MainFragment.isRefresh = true;
			DictionaryFragment.isRefresh = true;
			SDCardUtil.deleteOldFile();
			ToastUtil.diaplayMesShort(SettingActivity.this, this.getResources().getString(R.string.clear_success));
			AVAnalytics.onEvent(this, "setting_pg_clear_all");
			break;
		default:
			break;
		}
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Settings.saveSharedPreferences(mSharedPreferences, 
				getString(R.string.preference_key_tts_speed),
				MainFragment.speed);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		MainFragment.speed = progress;
		seekbar_text.setText(this.getResources().getString(R.string.play_speed_text) + MainFragment.speed);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}
}
