package com.messi.languagehelper;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.messi.languagehelper.util.Settings;

public class GetFansActivity extends BaseActivity implements OnClickListener {

	private FrameLayout layout_1, layout_2, layout_3;
	private TextView mail_txt;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_fans_activity);
		initViews();
	}

	private void initViews(){
		getSupportActionBar().setTitle(getResources().getString(R.string.fighting));
		mail_txt = (TextView)findViewById(R.id.mail_txt);
		mail_txt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.mail_txt:
			Settings.contantUs(GetFansActivity.this);
			StatService.onEvent(this, "getfans_email", "邀请用户发邮件提出想法", 1);
			break;
		default:
			break;
		}
	}
	
	
}
