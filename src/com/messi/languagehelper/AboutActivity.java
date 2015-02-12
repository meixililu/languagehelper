package com.messi.languagehelper;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.messi.languagehelper.util.Settings;

public class AboutActivity extends BaseActivity implements OnClickListener {

	private TextView email_layout;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		init();
	}

	private void init() {
		getSupportActionBar().setTitle(getResources().getString(R.string.title_about));
        email_layout = (TextView) findViewById(R.id.email_layout);
        email_layout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.email_layout:
			Settings.contantUs(AboutActivity.this);
			StatService.onEvent(this, "1.6_about_email", "发送email", 1);
			break;
		default:
			break;
		}
	}
}
