package com.messi.languagehelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import com.baidu.mobstat.StatService;
import com.messi.languagehelper.util.KeyUtil;

public class StudyCategoryActivity extends BaseActivity implements OnClickListener{

	private FrameLayout study_part1,study_part2,study_part3,study_part4;
	
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
			toStudyListActivity(PartOne);
			StatService.onEvent(StudyCategoryActivity.this, "19_studylist_part1", "口语练习第1部分", 1);
			break;
		case R.id.study_part2:
			toGetContentActivity();
			StatService.onEvent(StudyCategoryActivity.this, "19_studylist_part2", "口语练习第2部分", 1);
			break;
		case R.id.study_part3:
			toGetContentActivity();
			StatService.onEvent(StudyCategoryActivity.this, "19_studylist_part3", "口语练习第3部分", 1);
			break;
		case R.id.study_part4:
			toGetfansActivity();
			StatService.onEvent(StudyCategoryActivity.this, "19_studylist_part4", "你不是一个人在战斗", 1);
			break;
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
