package com.messi.languagehelper;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.messi.languagehelper.adapter.StudyListItemAdapter;

public class StudyActivity extends BaseActivity {

	private ListView studylist_lv;
	private LinearLayout page_navigation,page_content;
	private String[] studylist_part1;
	private StudyListItemAdapter mAdapter;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.study_activity);
		initViews();
	}

	private void initViews(){
		mActionBar.setTitle(getResources().getString(R.string.introduction));
		studylist_part1 = getResources().getStringArray(R.array.studylist_part1);
		studylist_lv = (ListView) findViewById(R.id.studylist_lv);
		page_navigation = (LinearLayout) findViewById(R.id.page_navigation);
		page_content = (LinearLayout) findViewById(R.id.page_content);
		mAdapter = new StudyListItemAdapter(this, studylist_part1);
		studylist_lv.setAdapter(mAdapter);
	}

}
