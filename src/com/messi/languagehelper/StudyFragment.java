package com.messi.languagehelper;

import com.actionbarsherlock.app.SherlockFragment;

public class StudyFragment extends SherlockFragment {

	
	public static MainFragment mMainFragment;
	
	public static MainFragment getInstance(){
		if(mMainFragment == null){
			mMainFragment = new MainFragment();
		}
		return mMainFragment;
	}
	
}
