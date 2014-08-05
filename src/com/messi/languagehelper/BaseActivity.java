package com.messi.languagehelper;

import android.os.Bundle;
import android.view.KeyEvent;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.baidu.mobstat.StatService;
import com.messi.languagehelper.util.AudioTrackUtil;

public class BaseActivity extends SherlockFragmentActivity {

	public ActionBar mActionBar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		super.onCreate(savedInstanceState);
		mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(getResources().getDrawable(R.color.load_blue));
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setHomeButtonEnabled(true);
        setSupportProgressBarIndeterminateVisibility(false);
		setSupportProgressBarVisibility(false);
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
