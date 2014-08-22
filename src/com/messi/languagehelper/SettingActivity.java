package com.messi.languagehelper;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.messi.languagehelper.MainFragment.AutoPlayWaitTask;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.SDCardUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.ToastUtil;

public class SettingActivity extends BaseActivity implements OnClickListener,SeekBar.OnSeekBarChangeListener {

	private TextView seekbar_text;
	private SeekBar seekbar;
	private FrameLayout speak_yueyu,auto_play;
	private FrameLayout clear_all_except_favorite,clear_all;
	private CheckBox speak_yueyu_cb,auto_play_cb;
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
        mActionBar.setTitle(this.getResources().getString(R.string.title_settings));
        seekbar_text = (TextView) findViewById(R.id.seekbar_text);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        speak_yueyu = (FrameLayout) findViewById(R.id.speak_yueyu);
        auto_play = (FrameLayout) findViewById(R.id.setting_auto_play);
        speak_yueyu_cb = (CheckBox) findViewById(R.id.speak_yueyu_cb);
        auto_play_cb = (CheckBox) findViewById(R.id.setting_auto_play_cb);
        clear_all_except_favorite = (FrameLayout) findViewById(R.id.setting_clear_all_except_favorite);
        clear_all = (FrameLayout) findViewById(R.id.setting_clear_all);
        
        seekbar.setOnSeekBarChangeListener(this);
        speak_yueyu.setOnClickListener(this);
        clear_all_except_favorite.setOnClickListener(this);
        clear_all.setOnClickListener(this);
        auto_play.setOnClickListener(this);
	}
	
	private void initData(){
		seekbar_text.setText(this.getResources().getString(R.string.play_speed_text) + MainFragment.speed);
		seekbar.setProgress(MainFragment.speed);
		boolean checked = mSharedPreferences.getBoolean(KeyUtil.SpeakPutonghuaORYueyu, false);
		boolean autoplay = mSharedPreferences.getBoolean(KeyUtil.AutoPlayResult, true);
		speak_yueyu_cb.setChecked(checked);
		auto_play_cb.setChecked(autoplay);
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
			Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.SpeakPutonghuaORYueyu,
					speak_yueyu_cb.isChecked());
			break;
		case R.id.setting_auto_play:
			if(auto_play_cb.isChecked()){
				auto_play_cb.setChecked(false);
			}else{
				auto_play_cb.setChecked(true);
			}
			Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.AutoPlayResult,
					auto_play_cb.isChecked());
			break;
		case R.id.setting_clear_all_except_favorite:
			new DataBaseUtil(SettingActivity.this).clearExceptFavorite();
			MainFragment.isRefresh = true;
			ToastUtil.diaplayMesShort(SettingActivity.this, this.getResources().getString(R.string.clear_success));
			StatService.onEvent(this, "1.8_clear_all_except", "清楚收藏以外的记录", 1);
			break;
		case R.id.setting_clear_all:
			new DataBaseUtil(SettingActivity.this).clearAll();
			MainFragment.isRefresh = true;
			CollectedFragment.isRefresh = true;
			SDCardUtil.deleteOldFile();
			ToastUtil.diaplayMesShort(SettingActivity.this, this.getResources().getString(R.string.clear_success));
			StatService.onEvent(this, "1.8_clear_all", "清楚所有记录", 1);
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
