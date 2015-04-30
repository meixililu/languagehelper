package com.messi.languagehelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import com.baidu.mobstat.StatService;
import com.messi.languagehelper.util.Settings;

public class GetFansActivity extends BaseActivity implements OnClickListener {

	private FrameLayout layout_1, layout_2, layout_3;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_fans_activity);
		initViews();
	}

	private void initViews(){
		getSupportActionBar().setTitle(getResources().getString(R.string.fighting));
//		layout_1 = (FrameLayout)findViewById(R.id.layout_1);
//		layout_2 = (FrameLayout)findViewById(R.id.layout_2);
//		layout_1.setOnClickListener(this);
//		layout_2.setOnClickListener(this);
//		layout_3.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
//		case R.id.layout_4:
//			Settings.contantUs(GetFansActivity.this);
//			StatService.onEvent(this, "19_getfans_email", "邀请用户发邮件提出想法", 1);
//			break;
		default:
			break;
		}
	}
	
	
}
