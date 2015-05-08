package com.messi.languagehelper;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.messi.languagehelper.adapter.StudyCategoryListItemAdapter;

public class StudyCategoryActivity extends BaseActivity implements OnClickListener{

	
	private ListView category_lv;
	private String[] study_category_list;
	private StudyCategoryListItemAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.study_category_activity);
		initViews();
	}
	
	private void initViews(){
		getSupportActionBar().setTitle(getResources().getString(R.string.title_Practice));
		study_category_list = getResources().getStringArray(R.array.study_category);
		category_lv = (ListView) findViewById(R.id.studycategory_lv);
		mAdapter = new StudyCategoryListItemAdapter(this, study_category_list);
		category_lv.setAdapter(mAdapter);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		
		default:
			break;
		}
	}
	
}
