package com.messi.languagehelper;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.widgets.Dialog;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.IFLYAdListener;
import com.iflytek.voiceads.IFLYBannerAd;
import com.messi.languagehelper.adapter.EvaluationCategoryAdapter;
import com.messi.languagehelper.util.ADUtil;
import com.messi.languagehelper.util.AVOUtil;
import com.messi.languagehelper.util.NumberUtil;
import com.messi.languagehelper.util.ToastUtil;

public class EvaluationCategoryActivity extends BaseActivity implements OnClickListener{

	private ListView category_lv;
	private EvaluationCategoryAdapter mAdapter;
	private List<AVObject> avObjects;
	private IFLYBannerAd mIFLYBannerAd;
	private LinearLayout ad_view;
	private ButtonFlat mButtonFlat;
	private String categoryContent;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.evaluation_category_activity);
		initSwipeRefresh();
		initViews();
		new QueryTask().execute();
	}
	
	private void initViews(){
		getSupportActionBar().setTitle(getResources().getString(R.string.spokenEnglishTest));
		avObjects = new ArrayList<AVObject>();
		category_lv = (ListView) findViewById(R.id.studycategory_lv);
		ad_view = (LinearLayout) findViewById(R.id.ad_view);
		mButtonFlat = (ButtonFlat) findViewById(R.id.submit_btn);
		mAdapter = new EvaluationCategoryAdapter(this, avObjects);
		category_lv.setAdapter(mAdapter);
		mButtonFlat.setOnClickListener(this);
		addAD();
		
		categoryContent = "出国旅游-游客问讯";
	}
	
	private void addAD(){
		if(ADUtil.isShowAd(this)){
			mIFLYBannerAd = ADUtil.initBannerAD(EvaluationCategoryActivity.this, ad_view, ADUtil.ListADId);
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
					StatService.onEvent(EvaluationCategoryActivity.this, "ad_banner", "点击banner广告", 1);
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
			AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.EvaluationCategory.EvaluationCategory);
			query.whereEqualTo(AVOUtil.EvaluationCategory.ECIsValid, "1");
			query.orderByDescending(AVOUtil.EvaluationCategory.ECOrder);
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
		case R.id.submit_btn:
			showData();
			break;
		}
	}
	
	private void showData(){
		Dialog dialog = new Dialog(this, "提交内容", categoryContent);
		dialog.addAcceptButton("确定");
		dialog.setOnAcceptButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				findDataIsExist();
			}
		});;
		dialog.show();
	}
	
	private void findDataIsExist(){
		AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.EvaluationCategory.EvaluationCategory);
		query.whereEqualTo(AVOUtil.EvaluationCategory.ECName, categoryContent);
		showProgressbar();
		query.findInBackground(new FindCallback<AVObject>() {
		    public void done(List<AVObject> avObjects, AVException e) {
		    	hideProgressbar();
		    	if(avObjects != null){
		    		if (avObjects.size() == 0) {
		    			ToastUtil.diaplayMesShort(EvaluationCategoryActivity.this, "没有重复数据，执行插入!");
		    			submitData();
		    		} else {
		    			ToastUtil.diaplayMesShort(EvaluationCategoryActivity.this,"查询到" + avObjects.size() + " 条符合条件的数据");
		    		}
		    	}
		    }
		});
	}
	
	private void submitData(){
		AVObject lastObject = avObjects.get(avObjects.size()-1);
		String lECCode = lastObject.getString(AVOUtil.EvaluationCategory.ECCode);
		String ECOrder = lastObject.getString(AVOUtil.EvaluationCategory.ECOrder);
		long mECCode = NumberUtil.StringToLong(lECCode);
		long mECOrder = NumberUtil.StringToLong(ECOrder);
		
		showProgressbar();
		AVObject newObject = new AVObject(AVOUtil.EvaluationCategory.EvaluationCategory);
		newObject.put(AVOUtil.EvaluationCategory.ECCode, String.valueOf(mECCode+1));
		newObject.put(AVOUtil.EvaluationCategory.ECName, categoryContent);
		newObject.put(AVOUtil.EvaluationCategory.ETCode, "10002");
		newObject.put(AVOUtil.EvaluationCategory.ECOrder, String.valueOf(mECOrder-1));
		newObject.saveInBackground(new SaveCallback() {
			@Override
			public void done(AVException arg0) {
		    	hideProgressbar();
				if (arg0 == null) {
					ToastUtil.diaplayMesShort(EvaluationCategoryActivity.this, "保存成功");
		        } else {
		        	ToastUtil.diaplayMesShort(EvaluationCategoryActivity.this, "保存失败:"+arg0.getMessage());
		        }
			}
		});
	}
	
	
}
