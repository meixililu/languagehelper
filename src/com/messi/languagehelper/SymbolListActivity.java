package com.messi.languagehelper;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.baidu.mobstat.StatService;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.IFLYAdListener;
import com.iflytek.voiceads.IFLYBannerAd;
import com.messi.languagehelper.adapter.SymbolListAdapter;
import com.messi.languagehelper.util.ADUtil;
import com.messi.languagehelper.util.AVOUtil;

public class SymbolListActivity extends BaseActivity implements OnClickListener{

	private GridView category_lv;
	private SymbolListAdapter mAdapter;
	private List<AVObject> avObjects;
	private IFLYBannerAd mIFLYBannerAd;
	private LinearLayout ad_view;
	
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
		category_lv = (GridView) findViewById(R.id.studycategory_lv);
		ad_view = (LinearLayout) findViewById(R.id.ad_view);
		mAdapter = new SymbolListAdapter(this, avObjects);
		category_lv.setAdapter(mAdapter);
		addAD();
	}
	
	private void addAD(){
		if(ADUtil.isShowAd(this)){
			mIFLYBannerAd = ADUtil.initBannerAD(SymbolListActivity.this, ad_view, ADUtil.ListADId);
			mIFLYBannerAd.loadAd(new IFLYAdListener() {
				@Override
				public void onAdReceive() {
					if(mIFLYBannerAd != null){
						mIFLYBannerAd.showAd();
					}
				}
				@Override
				public void onAdFailed(AdError arg0) {
				}
				@Override
				public void onAdClose() {
				}
				@Override
				public void onAdClick() {
					StatService.onEvent(SymbolListActivity.this, "ad_banner", "点击banner广告", 1);
				}
			});
		}
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
		if(mIFLYBannerAd != null){
			mIFLYBannerAd.destroy();
			mIFLYBannerAd = null;
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
