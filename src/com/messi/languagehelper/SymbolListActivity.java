package com.messi.languagehelper;

import java.util.ArrayList;
import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.messi.languagehelper.adapter.SymbolListAdapter;
import com.messi.languagehelper.dao.SymbolListDao;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.util.ADUtil;
import com.messi.languagehelper.util.AVOUtil;
import com.messi.languagehelper.util.XFYSAD;
import com.messi.languagehelper.views.GridViewWithHeaderAndFooter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

public class SymbolListActivity extends BaseActivity {

	private GridViewWithHeaderAndFooter category_lv;
	private SymbolListAdapter mAdapter;
	private List<SymbolListDao> mSymbolListDao;
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
		mSymbolListDao = new ArrayList<SymbolListDao>();
		category_lv = (GridViewWithHeaderAndFooter) findViewById(R.id.studycategory_lv);
		View headerView = LayoutInflater.from(this).inflate(R.layout.xunfei_ysad_item, null);
		category_lv.addHeaderView(headerView);
		mAdapter = new SymbolListAdapter(this, mSymbolListDao);
		category_lv.setAdapter(mAdapter);
		
		mXFYSAD = new XFYSAD(this, headerView, ADUtil.MRYJYSNRLAd);
		mXFYSAD.showAD();
		mAdapter.notifyDataSetChanged();
		mXFYSAD.startPlayImg();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mAdapter.notifyDataSetChanged();
			}
		}, 800);
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
			try {
				long localSize = DataBaseUtil.getInstance().getSymbolListSize();
				mSymbolListDao.clear();
				if(localSize == 0){
					AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.SymbolDetail.SymbolDetail);
					query.whereEqualTo(AVOUtil.SymbolDetail.SDIsValid, "1");
					query.orderByAscending(AVOUtil.SymbolDetail.SDCode);
					List<AVObject> avObjects  = query.find();
					if(avObjects != null){
						for(AVObject mAVObject : avObjects){
							mSymbolListDao.add( changeDataType(mAVObject) );
						}
						DataBaseUtil.getInstance().insert(mSymbolListDao);
					}
				}else{
					mSymbolListDao.addAll(DataBaseUtil.getInstance().getSymbolList());
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
	
	private SymbolListDao changeDataType(AVObject avobject){
		SymbolListDao mBean = new SymbolListDao();
		mBean.setSDCode(avobject.getString(AVOUtil.SymbolDetail.SDCode));	
		mBean.setSDName(avobject.getString(AVOUtil.SymbolDetail.SDName));	
		mBean.setSDDes(avobject.getString(AVOUtil.SymbolDetail.SDDes));	
		mBean.setSDInfo(avobject.getString(AVOUtil.SymbolDetail.SDInfo));	
		AVFile SDAudioMp3File = avobject.getAVFile(AVOUtil.SymbolDetail.SDAudioMp3);
		mBean.setSDAudioMp3Url(SDAudioMp3File.getUrl());	
		AVFile SDTeacherMp3File = avobject.getAVFile(AVOUtil.SymbolDetail.SDTeacherMp3);
		mBean.setSDTeacherMp3Url(SDTeacherMp3File.getUrl());	
		return mBean;	
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mXFYSAD != null){
    		mXFYSAD.canclePlayImg();
    		mXFYSAD = null;
    	}
	}
}
