package com.messi.languagehelper;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.messi.languagehelper.adapter.StudyListItemAdapter;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.ViewUtil;

public class StudyActivity extends BaseActivity {

	private LinearLayout page_navigation,page_content;
	private String[] studylist_part1;
	private int position;
	private FragmentTransaction fragmentTransaction;
	private FragmentManager fragmentManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.study_activity);
		initViews();
	}

	private void initViews(){
		mActionBar.setTitle(getResources().getString(R.string.practice_spoken_englist));
		position = getIntent().getIntExtra(KeyUtil.PracticeContentKey, 0);
		fragmentManager = getSupportFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		studylist_part1 = getResources().getStringArray(R.array.studylist_part1_content1);
		page_navigation = (LinearLayout) findViewById(R.id.page_navigation);
		page_content = (LinearLayout) findViewById(R.id.page_content);
		addIndicator();
		addFragment();
	}
	
	private void addIndicator(){
		ViewUtil.addIndicator(studylist_part1.length, page_navigation, this);
	}
	
	private void addFragment(){
		PracticeOneFragment fragment = new PracticeOneFragment(studylist_part1[position]);
		fragmentTransaction.add(R.id.page_content, fragment);
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fragmentTransaction.commit();
	}

}
