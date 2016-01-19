package com.messi.languagehelper;

import com.messi.languagehelper.adapter.WordStudyUnitListAdapter;
import com.messi.languagehelper.dao.WordListItem;
import com.messi.languagehelper.util.KeyUtil;

import android.os.Bundle;
import android.widget.GridView;

public class WordStudyUintListActivity extends BaseActivity {

	private GridView category_lv;
	private WordStudyUnitListAdapter mAdapter;
	private WordListItem mAVObject;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.word_study_uint_list_activity);
		initSwipeRefresh();
		initViews();
	}
	
	private void initViews(){
		mAVObject = (WordListItem) BaseApplication.dataMap.get(KeyUtil.DataMapKey);
		category_lv = (GridView) findViewById(R.id.studycategory_lv);
		mAdapter = new WordStudyUnitListAdapter(this, mAVObject);
		category_lv.setAdapter(mAdapter);
	}
	
	@Override
	public void onSwipeRefreshLayoutRefresh() {
		mAdapter.notifyDataSetChanged();
		onSwipeRefreshLayoutFinish();
	}
		
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
}
