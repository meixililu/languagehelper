package com.messi.languagehelper;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

public class StudyListActivity extends BaseActivity implements OnClickListener {

	private FrameLayout study_part1,study_part2,study_part3,study_part4;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.study_list_fragment);
		initViews();
	}

	private void initViews(){
		mActionBar.setTitle(getResources().getString(R.string.title_about));
		study_part1 = (FrameLayout)findViewById(R.id.study_part1);
		study_part2 = (FrameLayout)findViewById(R.id.study_part2);
		study_part3 = (FrameLayout)findViewById(R.id.study_part3);
		study_part4 = (FrameLayout)findViewById(R.id.study_part4);
		study_part1.setOnClickListener(this);
		study_part2.setOnClickListener(this);
		study_part3.setOnClickListener(this);
		study_part4.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.study_part1:
			break;
		case R.id.study_part2:
			break;
		case R.id.study_part3:
			break;
		case R.id.study_part4:
			
			break;
		default:
			break;
		}
	}
	
}
