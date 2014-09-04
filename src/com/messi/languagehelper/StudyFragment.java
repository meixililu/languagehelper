package com.messi.languagehelper;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.actionbarsherlock.app.SherlockFragment;

public class StudyFragment extends SherlockFragment implements OnClickListener{

	private View view;
	private FrameLayout study_part1,study_part2,study_part3,study_part4;
	public static StudyFragment mMainFragment;
	
	public static StudyFragment getInstance(){
		if(mMainFragment == null){
			mMainFragment = new StudyFragment();
		}
		return mMainFragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.study_fragment, null);
		initViews();
		return view;
	}
	
	private void initViews(){
		study_part1 = (FrameLayout)view.findViewById(R.id.study_part1);
		study_part2 = (FrameLayout)view.findViewById(R.id.study_part2);
		study_part3 = (FrameLayout)view.findViewById(R.id.study_part3);
		study_part4 = (FrameLayout)view.findViewById(R.id.study_part4);
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
	
	private void toStudyListPage(){
		
	}
	
}
