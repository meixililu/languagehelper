package com.messi.languagehelper;

import android.os.Bundle;
import android.widget.ListView;

import com.messi.languagehelper.adapter.InvestmentListItemAdapter;
import com.messi.languagehelper.util.KeyUtil;

public class InvestmentActivity extends BaseActivity {

	private ListView studylist_lv;
	private String[] studylist_part1;
	private InvestmentListItemAdapter mAdapter;
	private String level;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.investment_list_activity);
		initViews();
	}

	private void initViews(){
		getSupportActionBar().setTitle(getResources().getString(R.string.invest_activity_title));
		level = getIntent().getStringExtra(KeyUtil.LevelKey);
//		studylist_part1 = getResources().getStringArray(R.array.investors);
		studylist_lv = (ListView) findViewById(R.id.studylist_lv);
		mAdapter = new InvestmentListItemAdapter(this, studylist_part1, level);
		studylist_lv.setAdapter(mAdapter);
	}

}
