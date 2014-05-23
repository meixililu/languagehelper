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

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.baidu.mobstat.StatService;
import com.messi.languagehelper.util.SharedPreferencesUtil;

public class SettingActivity extends SherlockFragmentActivity implements OnClickListener,SeekBar.OnSeekBarChangeListener {

	public ActionBar mActionBar;
	private TextView seekbar_text;
	private SeekBar seekbar;
	private FrameLayout speak_yueyu;
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
		mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(getResources().getDrawable(R.color.load_blue));
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setTitle(this.getResources().getString(R.string.title_settings));
        mSharedPreferences = getSharedPreferences(this.getPackageName(), Activity.MODE_PRIVATE);
        
        seekbar_text = (TextView) findViewById(R.id.seekbar_text);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        speak_yueyu = (FrameLayout) findViewById(R.id.speak_yueyu);
        speak_yueyu_cb = (CheckBox) findViewById(R.id.speak_yueyu_cb);
        seekbar.setOnSeekBarChangeListener(this);
        speak_yueyu.setOnClickListener(this);
	}
	
	private void initData(){
		seekbar_text.setText("播放语速调节：" + MainFragment.speed);
		seekbar.setProgress(MainFragment.speed);
		boolean checked = mSharedPreferences.getBoolean(SharedPreferencesUtil.SpeakPutonghuaORYueyu, false);
		speak_yueyu_cb.setChecked(checked);
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:  
			finish();
		}
       return super.onOptionsItemSelected(item);
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
		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		SharedPreferencesUtil.saveInt(mSharedPreferences, getString(R.string.preference_key_tts_speed),
				MainFragment.speed);
		
		super.onDestroy();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		MainFragment.speed = progress;
		seekbar_text.setText("播放语速调节：" + MainFragment.speed);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}
}
