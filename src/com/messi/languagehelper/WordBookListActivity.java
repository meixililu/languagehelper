package com.messi.languagehelper;

import java.util.List;

import com.messi.languagehelper.adapter.WordBookListAdapter;
import com.messi.languagehelper.dao.WordListItem;
import com.messi.languagehelper.util.ADUtil;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.XFYSAD;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

public class WordBookListActivity extends BaseActivity {

	private ListView category_lv;
	private WordBookListAdapter mAdapter;
	private List<WordListItem> child_list;
	private XFYSAD mXFYSAD;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.study_category_activity);
		initSwipeRefresh();
		initViews();
	}
	
	private void initViews(){
		child_list = (List<WordListItem>) BaseApplication.dataMap.get(KeyUtil.DataMapKey);
		BaseApplication.dataMap.clear();
		if(child_list != null){
			category_lv = (ListView) findViewById(R.id.studycategory_lv);
			View headerView = LayoutInflater.from(this).inflate(R.layout.xunfei_ysad_item, null);
			category_lv.addHeaderView(headerView);
			mAdapter = new WordBookListAdapter(this, child_list);
			category_lv.setAdapter(mAdapter);
			
			mXFYSAD = new XFYSAD(this, headerView, ADUtil.MRYJYSNRLAd);
			mXFYSAD.showAD();
			mAdapter.notifyDataSetChanged();
			mXFYSAD.startPlayImg();
		}
	}
	
	public void onSwipeRefreshLayoutRefresh() {
		super.onSwipeRefreshLayoutRefresh();
		mAdapter.notifyDataSetChanged();
		onSwipeRefreshLayoutFinish();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mXFYSAD != null){
    		mXFYSAD.canclePlayImg();
    		mXFYSAD = null;
    	}
	}

	
}
