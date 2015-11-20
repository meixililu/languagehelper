package com.messi.languagehelper;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.messi.languagehelper.adapter.PracticeCategoryListAdapter;
import com.messi.languagehelper.util.AVOUtil;

public class PracticeCategoryListActivity extends BaseActivity {

	private ListView studylist_lv;
	private PracticeCategoryListAdapter mAdapter;
	private List<AVObject> avObjects;
	private String PCCode;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.study_list_fragment);
		initSwipeRefresh();
		initViews();
		new QueryTask().execute();
	}

	private void initViews(){
		avObjects = new ArrayList<AVObject>();
		PCCode = getIntent().getStringExtra(AVOUtil.PracticeCategory.PCCode);
		studylist_lv = (ListView) findViewById(R.id.studylist_lv);
		mAdapter = new PracticeCategoryListAdapter(this, avObjects, PCCode);
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
			AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.PracticeCategoryList.PracticeCategoryList);
			query.whereEqualTo(AVOUtil.PracticeCategoryList.PCCode, PCCode);
			query.whereEqualTo(AVOUtil.PracticeCategoryList.PCLIsValid, "1");
			query.orderByDescending(AVOUtil.PracticeCategoryList.PCLOrder);
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
