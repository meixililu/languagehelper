package com.messi.languagehelper;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.messi.languagehelper.util.Settings;

public class AboutActivity extends BaseActivity implements OnClickListener {

	private TextView email_layout, app_version;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		init();
	}

	private void init() {
		getSupportActionBar().setTitle(getResources().getString(R.string.title_about));
		email_layout = (TextView) findViewById(R.id.email_layout);
		app_version = (TextView) findViewById(R.id.app_version);
		email_layout.setOnClickListener(this);
		app_version.setText(getVersion());
	}

	public String getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "2.3";
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.email_layout:
			Settings.contantUs(AboutActivity.this);
			StatService.onEvent(this, "about_page_email", "关于我们发送邮件", 1);
			break;
		default:
			break;
		}
	}
}
