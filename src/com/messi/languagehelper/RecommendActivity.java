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
import com.messi.languagehelper.adapter.AppRecommendDetailAdapter;
import com.messi.languagehelper.util.AVOUtil;

public class RecommendActivity extends BaseActivity implements OnClickListener {

	private ListView category_lv;
	private AppRecommendDetailAdapter mAdapter;
	private List<AVObject> avObjects;
	private String mAppTypeCode;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recommend_activity);
		initSwipeRefresh();
		initViews();
		new QueryTask().execute();
	}
	
	private void initViews(){
		mAppTypeCode = getIntent().getStringExtra(AVOUtil.AppRecommendList.AppTypeCode);
		avObjects = new ArrayList<AVObject>();
		category_lv = (ListView) findViewById(R.id.studycategory_lv);
		mAdapter = new AppRecommendDetailAdapter(this, avObjects);
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
			AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.AppRecommendDetail.AppRecommendDetail);
			query.whereEqualTo(AVOUtil.AppRecommendDetail.AppTypeCode, mAppTypeCode);
			query.whereEqualTo(AVOUtil.AppRecommendDetail.IsValid, "1");
			query.orderByAscending(AVOUtil.AppRecommendDetail.Order);
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
	public void onClick(View v) {
	}

}
