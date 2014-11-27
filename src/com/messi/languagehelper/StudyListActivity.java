package com.messi.languagehelper;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.messi.languagehelper.adapter.StudyListItemAdapter;
import com.messi.languagehelper.util.KeyUtil;

public class StudyListActivity extends BaseActivity {

	private ListView studylist_lv;
	private String[] studylist_part1;
	private StudyListItemAdapter mAdapter;
	private String level;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.study_list_fragment);
		initViews();
	}

	private void initViews(){
		toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setTitle(getResources().getString(R.string.introduction));
		}
		level = getIntent().getStringExtra(KeyUtil.LevelKey);
		studylist_part1 = getResources().getStringArray(R.array.studylist_part1);
		studylist_lv = (ListView) findViewById(R.id.studylist_lv);
		mAdapter = new StudyListItemAdapter(this, studylist_part1, level);
		studylist_lv.setAdapter(mAdapter);
	}

}
