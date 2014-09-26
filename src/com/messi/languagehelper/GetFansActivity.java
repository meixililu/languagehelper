package com.messi.languagehelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import com.messi.languagehelper.util.Settings;

public class GetFansActivity extends BaseActivity implements OnClickListener {

	private FrameLayout layout_1, layout_2, layout_3, layout_4, layout_6;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_fans_activity);
		initViews();
	}

	private void initViews(){
		mActionBar.setTitle(getResources().getString(R.string.fighting));
		layout_1 = (FrameLayout)findViewById(R.id.layout_1);
		layout_2 = (FrameLayout)findViewById(R.id.layout_2);
		layout_3 = (FrameLayout)findViewById(R.id.layout_3);
		layout_4 = (FrameLayout)findViewById(R.id.layout_4);
		layout_6 = (FrameLayout)findViewById(R.id.layout_6);
		layout_1.setOnClickListener(this);
		layout_2.setOnClickListener(this);
		layout_3.setOnClickListener(this);
		layout_4.setOnClickListener(this);
		layout_6.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.layout_1:
			break;
		case R.id.layout_2:
			break;
		case R.id.layout_3:
			break;
		case R.id.layout_4:
			Settings.contantUs(GetFansActivity.this);
			break;
		case R.id.layout_6:
			toInvestorListActivity();
			break;
		default:
			break;
		}
	}
	
	private void toInvestorListActivity(){
		Intent intent = new Intent(this,InvestmentActivity.class);
		startActivity(intent);
	}
	
}
