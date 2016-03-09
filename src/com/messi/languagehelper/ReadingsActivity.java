package com.messi.languagehelper;

import java.util.ArrayList;
import java.util.List;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.messi.languagehelper.adapter.ReadingListAdapter;
import com.messi.languagehelper.util.AVOUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.ScreenUtil;
import com.messi.languagehelper.util.ToastUtil;

import android.os.AsyncTask;
import android.os.Bundle;
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
		getSupportActionBar().setTitle(this.getResources().getString(R.string.reading));
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
            }  
        });
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
			query.addDescendingOrder(AVOUtil.Reading.publish_time);
			query.addDescendingOrder(AVOUtil.Reading.item_id);
			query.skip(skip);
			query.limit(20);
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
					avObjects.addAll(avObject);
					mAdapter.notifyDataSetChanged();
					skip += 20;
					showFooterview();
				}
			}
		}
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
					maxRandom = query.count();
					maxRandom /= 20; 
					LogUtil.DefalutLog("maxRandom:"+maxRandom);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	
}
