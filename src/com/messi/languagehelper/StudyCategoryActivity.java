package com.messi.languagehelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import com.baidu.mobstat.StatService;
import com.messi.languagehelper.util.KeyUtil;

public class StudyCategoryActivity extends BaseActivity implements OnClickListener{

	
	public static final String PartOne = "part_one";
	public static final String PartTwo = "part_two";
	public static final String PartThree = "part_three";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.study_category_activity);
		initViews();
	}
	
	private void initViews(){
		
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		
		default:
			break;
		}
	}
	
	private void toGetfansActivity(){
		Intent intent = new Intent(this,GetFansActivity.class);
		startActivity(intent);
	}
	private void toStudyListActivity(String level){
		Intent intent = new Intent(this,StudyListActivity.class);
		intent.putExtra(KeyUtil.LevelKey, level);
		startActivity(intent);
	}
	private void toGetContentActivity(){
		Intent intent = new Intent(this,GetContentActivity.class);
		startActivity(intent);
	}
	
}
