package com.messi.languagehelper;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.baidu.mobstat.StatService;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.IFLYAdListener;
import com.iflytek.voiceads.IFLYBannerAd;
import com.messi.languagehelper.adapter.EnglishWebsiteListAdapter;
import com.messi.languagehelper.util.ADUtil;
import com.messi.languagehelper.util.AVOUtil;
import com.messi.languagehelper.util.XFYSAD;

public class EnglishWebsiteRecommendActivity extends BaseActivity implements OnClickListener {

	private ListView category_lv;
	private EnglishWebsiteListAdapter mAdapter;
	private List<AVObject> avObjects;
	private XFYSAD mXFYSAD;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.english_website_recommend_activity);
		initSwipeRefresh();
		initViews();
		new QueryTask().execute();
	}
	
	private void initViews(){
		getSupportActionBar().setTitle(getResources().getString(R.string.title_website));
		avObjects = new ArrayList<AVObject>();
		category_lv = (ListView) findViewById(R.id.studycategory_lv);
		View headerView = LayoutInflater.from(this).inflate(R.layout.xunfei_ysad_item, null);
		category_lv.addHeaderView(headerView);
		mAdapter = new EnglishWebsiteListAdapter(this, avObjects);
		category_lv.setAdapter(mAdapter);
		
		mXFYSAD = new XFYSAD(this, headerView, ADUtil.MRYJYSNRLAd);
		mXFYSAD.showAD();
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
			AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.EnglishWebsite.EnglishWebsite);
			query.whereEqualTo(AVOUtil.EnglishWebsite.IsValid, "1");
			query.orderByDescending(AVOUtil.EnglishWebsite.Order);
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
	
	public void onDestroy() {
		super.onDestroy();
		if(mXFYSAD != null){
    		mXFYSAD.canclePlayImg();
    		mXFYSAD = null;
    	}
	}
}
