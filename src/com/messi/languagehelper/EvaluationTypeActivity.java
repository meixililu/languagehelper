package com.messi.languagehelper;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.baidu.mobstat.StatService;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.IFLYAdListener;
import com.iflytek.voiceads.IFLYBannerAd;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.IFLYNativeListener;
import com.iflytek.voiceads.NativeADDataRef;
import com.messi.languagehelper.adapter.EvaluationTypeAdapter;
import com.messi.languagehelper.util.ADUtil;
import com.messi.languagehelper.util.AVOUtil;
import com.messi.languagehelper.util.LogUtil;
import com.squareup.picasso.Picasso;

public class EvaluationTypeActivity extends BaseActivity implements OnClickListener{

	private ListView category_lv;
	private EvaluationTypeAdapter mAdapter;
	private List<AVObject> avObjects;
	
	private IFLYNativeAd nativeAd;
	private FrameLayout ad_layout;
	private ImageView ad_img;
	private List<NativeADDataRef> adList;
	private int index;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.evaluation_type_activity);
		initSwipeRefresh();
		initViews();
		new QueryTask().execute();
	}
	
	private void initViews(){
		getSupportActionBar().setTitle(getResources().getString(R.string.spokenEnglishTest));
		avObjects = new ArrayList<AVObject>();
		category_lv = (ListView) findViewById(R.id.studycategory_lv);
		View headerView =  LayoutInflater.from(this).inflate(R.layout.xunfei_ysad_item, null);
		ad_layout = (FrameLayout) headerView.findViewById(R.id.ad_layout);
		ad_img = (ImageView) headerView.findViewById(R.id.ad_img);
		category_lv.addHeaderView(headerView);
		mAdapter = new EvaluationTypeAdapter(this, avObjects);
		category_lv.setAdapter(mAdapter);
		showAD();
	}
	
	private void showAD(){
		if(ADUtil.isShowAd(this)){
			nativeAd = new IFLYNativeAd(this, ADUtil.MRYJYSNRLAd,
					new IFLYNativeListener() {
				@Override
				public void onAdFailed(AdError arg0) {
					LogUtil.DefalutLog("onAdFailed---"+arg0.getErrorCode()+"---"+arg0.getErrorDescription());
				}
				@Override
				public void onADLoaded(List<NativeADDataRef> arg0) {
					if(arg0 != null && arg0.size() > 0){
						adList = arg0;
						setAdData();
					}
				}
			});
			nativeAd.loadAd(1);
		}
	}
	
	private void setAdData(){
		Picasso.with(this)
		.load(adList.get(index).getImage())
		.tag(this)
		.into(ad_img);
		adList.get(index).onExposured(ad_layout);
		ad_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				adList.get(index).onClicked(v);
			}
		});
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
			AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.EvaluationType.EvaluationType);
			query.whereEqualTo(AVOUtil.EvaluationType.ETIsValid, "1");
			query.orderByAscending(AVOUtil.EvaluationType.ETOrder);
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
