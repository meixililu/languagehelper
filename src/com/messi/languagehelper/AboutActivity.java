package com.messi.languagehelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.baidu.mobstat.StatService;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.WechatUtil;
import com.tencent.mm.sdk.openapi.IWXAPI;

public class AboutActivity extends BaseActivity implements OnClickListener {

	private TextView email_layout;
	public ActionBar mActionBar;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		init();
	}

	private void init() {
		mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(getResources().getDrawable(R.color.load_blue));
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setTitle(getResources().getString(R.string.title_about));
        email_layout = (TextView) findViewById(R.id.email_layout);
        email_layout.setOnClickListener(this);
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
		case R.id.email_layout:
			try {
				Intent emailIntent = new Intent(Intent.ACTION_SEND);
				emailIntent.setType("message/rfc822");
				emailIntent.putExtra(Intent.EXTRA_EMAIL,
						new String[] { "meixililulu@163.com" });
				startActivity(emailIntent);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}
}
