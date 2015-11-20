package com.messi.languagehelper;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.messi.languagehelper.adapter.EvaluationCategoryListAdapter;
import com.messi.languagehelper.util.AVOUtil;

public class EvaluationCategoryListActivity extends BaseActivity {

	private ListView studylist_lv;
	private EvaluationCategoryListAdapter mAdapter;
	private List<AVObject> avObjects;
	private String ECCode;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.evaluation_list_activity);
		initSwipeRefresh();
		initViews();
		new QueryTask().execute();
	}

	private void initViews(){
		avObjects = new ArrayList<AVObject>();
		ECCode = getIntent().getStringExtra(AVOUtil.EvaluationCategory.ECCode);
		studylist_lv = (ListView) findViewById(R.id.studylist_lv);
		mAdapter = new EvaluationCategoryListAdapter(this, avObjects, ECCode);
		studylist_lv.setAdapter(mAdapter);
	}

	@Override
	public void onSwipeRefreshLayoutRefresh() {
		super.onSwipeRefreshLayoutRefresh();
		new QueryTask().execute();
	}
	
	private class QueryTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressbar();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.EvaluationDetail.EvaluationDetail);
			query.whereEqualTo(AVOUtil.EvaluationDetail.ECCode, ECCode);
			query.whereEqualTo(AVOUtil.EvaluationDetail.EDIsValid, "1");
			query.orderByAscending(AVOUtil.EvaluationDetail.ECLCode);
			try {
				List<AVObject> avObject  = query.find();
				if(avObject != null){
					avObjects.clear();
					avObjects.addAll(avObject);
				}
			} catch (AVException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			hideProgressbar();
			onSwipeRefreshLayoutFinish();
			mAdapter.notifyDataSetChanged();
		}
	}
}
