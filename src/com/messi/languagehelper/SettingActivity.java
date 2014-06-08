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

import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.util.SharedPreferencesUtil;
import com.messi.languagehelper.util.ToastUtil;

public class SettingActivity extends BaseActivity implements OnClickListener,SeekBar.OnSeekBarChangeListener {

	private TextView seekbar_text;
	private SeekBar seekbar;
	private FrameLayout speak_yueyu;
	private FrameLayout clear_all_except_favorite,clear_all;
	private CheckBox speak_yueyu_cb;
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
        speak_yueyu_cb = (CheckBox) findViewById(R.id.speak_yueyu_cb);
        clear_all_except_favorite = (FrameLayout) findViewById(R.id.setting_clear_all_except_favorite);
        clear_all = (FrameLayout) findViewById(R.id.setting_clear_all);
        
        seekbar.setOnSeekBarChangeListener(this);
        speak_yueyu.setOnClickListener(this);
        clear_all_except_favorite.setOnClickListener(this);
        clear_all.setOnClickListener(this);
	}
	
	private void initData(){
		seekbar_text.setText(this.getResources().getString(R.string.play_speed_text) + MainFragment.speed);
		seekbar.setProgress(MainFragment.speed);
		boolean checked = mSharedPreferences.getBoolean(SharedPreferencesUtil.SpeakPutonghuaORYueyu, false);
		speak_yueyu_cb.setChecked(checked);
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
			SharedPreferencesUtil.saveBoolean(mSharedPreferences, SharedPreferencesUtil.SpeakPutonghuaORYueyu,
					speak_yueyu_cb.isChecked());
			break;
		case R.id.setting_clear_all_except_favorite:
			new DataBaseUtil(SettingActivity.this).clearExceptFavorite();
			MainFragment.isRefresh = true;
			ToastUtil.diaplayMesShort(SettingActivity.this, this.getResources().getString(R.string.clear_success));
			break;
		case R.id.setting_clear_all:
			new DataBaseUtil(SettingActivity.this).clearAll();
			MainFragment.isRefresh = true;
			CollectedFragment.isRefresh = true;
			ToastUtil.diaplayMesShort(SettingActivity.this, this.getResources().getString(R.string.clear_success));
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		SharedPreferencesUtil.saveInt(mSharedPreferences, 
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
