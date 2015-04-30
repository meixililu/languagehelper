package com.messi.languagehelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import com.baidu.mobstat.StatService;
import com.messi.languagehelper.util.KeyUtil;

public class GetContentActivity extends BaseActivity implements OnClickListener {

	private FrameLayout layout_1,layout_2;
	private String title;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_content_activity);
		initViews();
	}

	private void initViews(){
		title = getIntent().getStringExtra(KeyUtil.ActionbarTitle);
		getSupportActionBar().setTitle(title);
		layout_1 = (FrameLayout)findViewById(R.id.layout_1);
		layout_2 = (FrameLayout)findViewById(R.id.layout_2);
		layout_1.setOnClickListener(this);
		layout_2.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.layout_1:
			break;
		case R.id.layout_2:
			Intent intent = new Intent(GetContentActivity.this, GetFansActivity.class);
			startActivity(intent);
			StatService.onEvent(this, "19_getcontent_email", "邀请用户发邮件提出内容要求", 1);
			break;
		default:
			break;
		}
	}
	
}
