package com.messi.languagehelper;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.messi.languagehelper.adapter.SymbolListAdapter;
import com.messi.languagehelper.util.ADUtil;
import com.messi.languagehelper.util.AVOUtil;
import com.messi.languagehelper.util.XFYSAD;
import com.messi.languagehelper.views.GridViewWithHeaderAndFooter;

public class SymbolListActivity extends BaseActivity implements OnClickListener{

	private GridViewWithHeaderAndFooter category_lv;
	private SymbolListAdapter mAdapter;
	private List<AVObject> avObjects;
	private XFYSAD mXFYSAD;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.symbol_list_activity);
		initSwipeRefresh();
		initViews();
		new QueryTask().execute();
	}
	
	private void initViews(){
		getSupportActionBar().setTitle(getResources().getString(R.string.symbolStudy));
		avObjects = new ArrayList<AVObject>();
		category_lv = (GridViewWithHeaderAndFooter) findViewById(R.id.studycategory_lv);
		View headerView = LayoutInflater.from(this).inflate(R.layout.xunfei_ysad_item, null);
		category_lv.addHeaderView(headerView);
		mAdapter = new SymbolListAdapter(this, avObjects);
		category_lv.setAdapter(mAdapter);
		
		mXFYSAD = new XFYSAD(this, headerView, ADUtil.MRYJYSNRLAd);
		mXFYSAD.showAD();
		mXFYSAD.startPlayImg();
		mAdapter.notifyDataSetChanged();
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
			AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.SymbolList.SymbolList);
			query.whereEqualTo(AVOUtil.SymbolList.SLIsValid, "1");
			query.orderByAscending(AVOUtil.SymbolList.SLOrder);
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
		if(mXFYSAD != null){
    		mXFYSAD.canclePlayImg();
    		mXFYSAD = null;
    	}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		default:
			break;
		}
	}
	
}
