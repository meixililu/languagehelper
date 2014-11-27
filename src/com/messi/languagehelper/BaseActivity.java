package com.messi.languagehelper;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.Window;

import com.baidu.mobstat.StatService;
import com.messi.languagehelper.util.AudioTrackUtil;

public class BaseActivity extends ActionBarActivity {

	public Toolbar toolbar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			 AudioTrackUtil.adjustStreamVolume(BaseActivity.this,keyCode);
	         return true;
	    case KeyEvent.KEYCODE_VOLUME_DOWN:
	    	 AudioTrackUtil.adjustStreamVolume(BaseActivity.this,keyCode);
		     return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
