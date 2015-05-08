package com.messi.languagehelper;

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

import com.baidu.mobstat.StatService;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.SDCardUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.ShareUtil;
import com.messi.languagehelper.util.ToastUtil;

public class SettingActivity extends BaseActivity implements OnClickListener,SeekBar.OnSeekBarChangeListener {

	private TextView seekbar_text;
	private SeekBar seekbar;
	private ImageView autoread_unread_dot,unread_auto_clear_dot;
	private FrameLayout speak_yueyu,auto_play,auto_clear;
	private FrameLayout clear_all_except_favorite,clear_all,invite_friends;
	private CheckBox speak_yueyu_cb,auto_play_cb,auto_clear_cb;
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
        autoread_unread_dot = (ImageView) findViewById(R.id.unread_dot);
        unread_auto_clear_dot = (ImageView) findViewById(R.id.unread_auto_clear_dot);
        speak_yueyu = (FrameLayout) findViewById(R.id.speak_yueyu);
        auto_play = (FrameLayout) findViewById(R.id.setting_auto_play);
        auto_clear = (FrameLayout) findViewById(R.id.setting_auto_clear);
        speak_yueyu_cb = (CheckBox) findViewById(R.id.speak_yueyu_cb);
        auto_clear_cb = (CheckBox) findViewById(R.id.setting_auto_clear_cb);
        auto_play_cb = (CheckBox) findViewById(R.id.setting_auto_play_cb);
        clear_all_except_favorite = (FrameLayout) findViewById(R.id.setting_clear_all_except_favorite);
        clear_all = (FrameLayout) findViewById(R.id.setting_clear_all);
        invite_friends = (FrameLayout) findViewById(R.id.setting_invite_friends);
        
        seekbar.setOnSeekBarChangeListener(this);
        speak_yueyu.setOnClickListener(this);
        clear_all_except_favorite.setOnClickListener(this);
        clear_all.setOnClickListener(this);
        invite_friends.setOnClickListener(this);
        auto_play.setOnClickListener(this);
        auto_clear.setOnClickListener(this);
	}
	
	private void initData(){
		seekbar_text.setText(this.getResources().getString(R.string.play_speed_text) + MainFragment.speed);
		seekbar.setProgress(MainFragment.speed);
		boolean checked = mSharedPreferences.getBoolean(KeyUtil.SpeakPutonghuaORYueyu, false);
		boolean autoplay = mSharedPreferences.getBoolean(KeyUtil.AutoPlayResult, false);
		boolean autoplayUnreadDot = mSharedPreferences.getBoolean(KeyUtil.AutoPlayUnreadDot, false);
		boolean AutoClear = mSharedPreferences.getBoolean(KeyUtil.AutoClear, false);
		boolean AutoClearUnreadDot = mSharedPreferences.getBoolean(KeyUtil.AutoClearUnreadDot, false);
		speak_yueyu_cb.setChecked(checked);
		auto_play_cb.setChecked(autoplay);
		auto_clear_cb.setChecked(AutoClear);
		if(autoplayUnreadDot){
			autoread_unread_dot.setVisibility(View.GONE);
		}
		if(AutoClearUnreadDot){
			unread_auto_clear_dot.setVisibility(View.GONE);
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
			autoread_unread_dot.setVisibility(View.GONE);
			Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.AutoPlayResult,auto_play_cb.isChecked());
			Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.AutoPlayUnreadDot,true);
			StatService.onEvent(this, "setting_page_auto_play", "翻译完成之后自动播放", 1);
			break;
		case R.id.setting_auto_clear:
			auto_clear_cb.setChecked(!auto_clear_cb.isChecked());
			unread_auto_clear_dot.setVisibility(View.GONE);
			MainFragment.isRefresh = true;
			DictionaryFragment.isRefresh = true;
			Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.AutoClear,auto_clear_cb.isChecked());
			Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.AutoClearUnreadDot,true);
			StatService.onEvent(this, "setting_page_auto_clear", "退出自动清除", 1);
			break;
		case R.id.setting_clear_all_except_favorite:
			DataBaseUtil.getInstance().clearExceptFavorite();
			MainFragment.isRefresh = true;
			DictionaryFragment.isRefresh = true;
			ToastUtil.diaplayMesShort(SettingActivity.this, this.getResources().getString(R.string.clear_success));
			StatService.onEvent(this, "setting_page_clear_all_except", "清除收藏以外的记录", 1);
			break;
		case R.id.setting_clear_all:
			DataBaseUtil.getInstance().clearAll();
			MainFragment.isRefresh = true;
			DictionaryFragment.isRefresh = true;
			SDCardUtil.deleteOldFile();
			ToastUtil.diaplayMesShort(SettingActivity.this, this.getResources().getString(R.string.clear_success));
			StatService.onEvent(this, "setting_page_clear_all", "清除所有记录", 1);
			break;
		case R.id.setting_invite_friends:
			ShareUtil.shareLink(SettingActivity.this,SettingActivity.this.getResources().getString(R.string.invite_friends_prompt));
			StatService.onEvent(this, "setting_page_invite_friends", "邀请小伙伴", 1);
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
