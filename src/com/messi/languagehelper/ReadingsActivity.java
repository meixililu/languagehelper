package com.messi.languagehelper;

import java.util.ArrayList;
import java.util.List;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.IFLYNativeAd;
import com.iflytek.voiceads.IFLYNativeListener;
import com.iflytek.voiceads.NativeADDataRef;
import com.messi.languagehelper.adapter.ReadingListAdapter;
import com.messi.languagehelper.util.ADUtil;
import com.messi.languagehelper.util.AVOUtil;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.NumberUtil;
import com.messi.languagehelper.util.ScreenUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.ToastUtil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class ReadingsActivity extends BaseActivity implements OnClickListener{

	private ListView listview;
	private ReadingListAdapter mAdapter;
	private List<AVObject> avObjects;
	private View footerview;
	private int skip = 0;
	private int maxRandom;
	private String category;
	private IFLYNativeAd nativeAd;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reading_activity);
		initViews();
		initFooterview();
		new QueryTask().execute();
		getMaxPageNumberBackground();
	}
	
	private void initViews(){
		category = getIntent().getStringExtra(KeyUtil.Category);
		LogUtil.DefalutLog("category:"+category);
		avObjects = new ArrayList<AVObject>();
		listview = (ListView) findViewById(R.id.listview);
		initSwipeRefresh();
		mAdapter = new ReadingListAdapter(this, avObjects);
		listview.setAdapter(mAdapter);
		setListOnScrollListener();
	}
	
	private void random(){
		skip = (int) Math.round(Math.random()*maxRandom);
		LogUtil.DefalutLog("skip:"+skip);
	}
	
	public void setListOnScrollListener(){
		listview.setOnScrollListener(new OnScrollListener() {  
            private int lastItemIndex;//当前ListView中最后一个Item的索引  
            @Override  
            public void onScrollStateChanged(AbsListView view, int scrollState) { 
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && lastItemIndex == mAdapter.getCount() - 1) {  
                	new QueryTask().execute();
                }  
            }  
            @Override  
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) { 
                lastItemIndex = firstVisibleItem + visibleItemCount - 2; 
                isADInList(view,firstVisibleItem,visibleItemCount);
            }  
        });
	}
	
	private void isADInList(AbsListView view,int first, int vCount){
		if(avObjects.size() > 3){
			for(int i=first;i< (first+vCount);i++){
				if(i < avObjects.size()){
					AVObject mAVObject = avObjects.get(i);
					if(mAVObject != null && mAVObject.get(KeyUtil.ADKey) != null){
						NativeADDataRef mNativeADDataRef = (NativeADDataRef) mAVObject.get(KeyUtil.ADKey);
						mNativeADDataRef.onExposured(view.getChildAt(i%vCount));
					}
				}
			}
		}
	}
	
	private void initFooterview(){
		footerview = LayoutInflater.from(this).inflate(R.layout.footerview, null, false);
		listview.addFooterView(footerview);
		hideFooterview();
	}
	
	private void showFooterview(){
		footerview.setVisibility(View.VISIBLE);  
		footerview.setPadding(0, ScreenUtil.dip2px(this, 15), 0, ScreenUtil.dip2px(this, 15));  
	}
	
	private void hideFooterview(){
		footerview.setVisibility(View.GONE);  
		footerview.setPadding(0, - (footerview.getHeight()+80), 0, 0);  
	}
	
	@Override
	public void onSwipeRefreshLayoutRefresh() {
		hideFooterview();
		random();
		avObjects.clear();
		mAdapter.notifyDataSetChanged();
		new QueryTask().execute();
	}
	
	private class QueryTask extends AsyncTask<Void, Void, List<AVObject>> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressbar();
		}
		
		@Override
		protected List<AVObject> doInBackground(Void... params) {
			AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.Reading.Reading);
			if(!TextUtils.isEmpty(category)){
				query.whereEqualTo(AVOUtil.Reading.category, category);
			}
			query.addDescendingOrder(AVOUtil.Reading.publish_time);
			query.addDescendingOrder(AVOUtil.Reading.item_id);
			query.skip(skip);
			query.limit(Settings.page_size);
			try {
				List<AVObject> avObject  = query.find();
				return avObject;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<AVObject> avObject) {
			hideProgressbar();
			onSwipeRefreshLayoutFinish();
			if(avObject != null){
				if(avObject.size() == 0){
					ToastUtil.diaplayMesShort(ReadingsActivity.this, "没有了！");
					hideFooterview();
				}else{
					loadAD();
					avObjects.addAll(avObject);
					mAdapter.notifyDataSetChanged();
					skip += Settings.page_size;
					showFooterview();
				}
			}else{
				ToastUtil.diaplayMesShort(ReadingsActivity.this, "加载失败，下拉可刷新");
			}
		}
	}
	
	private void loadAD(){
		nativeAd = new IFLYNativeAd(this, ADUtil.XXLAD, new IFLYNativeListener() {
			@Override
			public void onAdFailed(AdError arg0) {
				LogUtil.DefalutLog("onAdFailed---"+arg0.getErrorCode()+"---"+arg0.getErrorDescription());
			}
			@Override
			public void onADLoaded(List<NativeADDataRef> adList) {
				if(adList != null && adList.size() > 0){
					NativeADDataRef nad = adList.get(0);
					AVObject mAVObject = new AVObject();
					mAVObject.put(KeyUtil.ADKey, nad);
					int index = avObjects.size() - Settings.page_size + NumberUtil.randomNumberRange(3, 7);
					if(index < 0){
						index = 0;
					}
					LogUtil.DefalutLog("index:"+index);
					avObjects.add(index,mAVObject);
					mAdapter.notifyDataSetChanged();
				}
			}
		});
		nativeAd.loadAd(1);
	}
	
	@Override
	public void onClick(View v) {
	}
	
	private void getMaxPageNumberBackground(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.Reading.Reading);
					if(!TextUtils.isEmpty(category)){
						query.whereEqualTo(AVOUtil.Reading.category, category);
					}
					maxRandom = query.count();
					maxRandom /= Settings.page_size; 
					LogUtil.DefalutLog("maxRandom:"+maxRandom);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	
}
