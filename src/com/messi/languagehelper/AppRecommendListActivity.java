package com.messi.languagehelper;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.messi.languagehelper.adapter.AppRecommendListAdapter;
import com.messi.languagehelper.util.AVOUtil;

public class AppRecommendListActivity extends BaseActivity implements OnClickListener{
	
	private ListView category_lv;
	private AppRecommendListAdapter mAdapter;
	private List<AVObject> avObjects;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_recommend_list_activity);
		initSwipeRefresh();
		initViews();
		new QueryTask().execute();
	}
	
	private void initViews(){
		getSupportActionBar().setTitle(getResources().getString(R.string.title_apps));
		avObjects = new ArrayList<AVObject>();
		category_lv = (ListView) findViewById(R.id.studycategory_lv);
		mAdapter = new AppRecommendListAdapter(this, avObjects);
		category_lv.setAdapter(mAdapter);
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
			AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.AppRecommendList.AppRecommendList);
			query.whereEqualTo(AVOUtil.AppRecommendList.IsValid, "1");
			query.orderByAscending(AVOUtil.AppRecommendList.Order);
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
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		default:
			break;
		}
	}
	
}
