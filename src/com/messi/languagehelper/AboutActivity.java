package com.messi.languagehelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.baidu.mobstat.StatService;

public class AboutActivity extends BaseActivity implements OnClickListener {

	private TextView email_layout;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		init();
	}

	private void init() {
        mActionBar.setTitle(getResources().getString(R.string.title_about));
        email_layout = (TextView) findViewById(R.id.email_layout);
        email_layout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.email_layout:
			contantUs(AboutActivity.this);
			StatService.onEvent(this, "1.6_about_email", "发送email", 1);
			break;
		default:
			break;
		}
	}
	
	public static void contantUs(Context mContext){
		try {
			Intent emailIntent = new Intent(Intent.ACTION_SEND);
			emailIntent.setType("message/rfc822");
			emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[] { "meixililulu@163.com" });
			mContext.startActivity(emailIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
