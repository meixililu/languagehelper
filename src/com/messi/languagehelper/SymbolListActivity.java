package com.messi.languagehelper;

import java.util.ArrayList;
import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.gc.materialdesign.widgets.Dialog;
import com.messi.languagehelper.adapter.SymbolListAdapter;
import com.messi.languagehelper.dao.SymbolListDao;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.util.ADUtil;
import com.messi.languagehelper.util.AVOUtil;
import com.messi.languagehelper.util.DownLoadUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.ToastUtil;
import com.messi.languagehelper.util.XFYSAD;
import com.messi.languagehelper.views.GridViewWithHeaderAndFooter;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

public class SymbolListActivity extends BaseActivity {

	private GridViewWithHeaderAndFooter category_lv;
	private SymbolListAdapter mAdapter;
	private List<SymbolListDao> mSymbolListDao;
	private XFYSAD mXFYSAD;
	private int downloadCount;
	
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
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				mAdapter.notifyDataSetChanged();
			}
		}, 1000);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.symbol_detail_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_info:  
			showXunFeiDialog();
//			StatService.onEvent(this, "show_xunfi_evaluation_dialog", "显示讯飞口语评测说明", 1);
			break;
		}
       return super.onOptionsItemSelected(item);
	}
	
	private void showXunFeiDialog(){
		Dialog dialog = new Dialog(this, "温馨提示", "离线所有音标MP3");
		dialog.addAcceptButton("确定");
		dialog.addCancelButton("取消");
		dialog.setOnAcceptButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DownLoadUtil.downloadSymbolMp3(SymbolListActivity.this, mSymbolListDao, mHandler);
			}
		});
		dialog.show();
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 1){
				downloadCount++;
				LogUtil.DefalutLog("downloadCount:"+downloadCount);
				if(downloadCount == 48){
					ToastUtil.diaplayMesShort(SymbolListActivity.this, "离线完成，您可以离线学习音标了！");
					downloadCount = 0;
				}
			}
		}
	};
	
	@Override
	public void onResume() {
		super.onResume();
		if(mXFYSAD != null){
    		mXFYSAD.startPlayImg();
    	}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(mXFYSAD != null){
    		mXFYSAD.canclePlayImg();
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
}
