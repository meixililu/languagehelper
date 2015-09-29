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
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.widgets.Dialog;
import com.messi.languagehelper.adapter.EvaluationCategoryListAdapter;
import com.messi.languagehelper.util.AVOUtil;
import com.messi.languagehelper.util.NumberUtil;
import com.messi.languagehelper.util.ToastUtil;

public class EvaluationCategoryListActivity extends BaseActivity implements OnClickListener{

	private ListView studylist_lv;
	private EvaluationCategoryListAdapter mAdapter;
	private List<AVObject> avObjects;
	private String ECCode;
	private ButtonFlat mButtonFlat;
	private String categoryContent;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.evaluation_list_activity);
		initSwipeRefresh();
		initViews();
		new QueryTask().execute();
	}

	private void initViews(){
		avObjects = new ArrayList<AVObject>();
		ECCode = getIntent().getStringExtra(AVOUtil.EvaluationCategory.ECCode);
		studylist_lv = (ListView) findViewById(R.id.studylist_lv);
		mButtonFlat = (ButtonFlat) findViewById(R.id.submit_btn);
		mAdapter = new EvaluationCategoryListAdapter(this, avObjects, ECCode);
		studylist_lv.setAdapter(mAdapter);
		mButtonFlat.setOnClickListener(this);
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
			AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.EvaluationCategoryList.EvaluationCategoryList);
//			query.setSkip(10);
			query.setLimit(20);
			query.whereEqualTo(AVOUtil.EvaluationCategoryList.ECLIsValid, "1");
			query.orderByDescending(AVOUtil.EvaluationCategoryList.ECLCode);
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
			initData();
		}
	}
	
	long mECLCode;
	long mECLOrder;
	private void initData(){
		AVObject lastObject = avObjects.get(0);
		String lECLCode = lastObject.getString(AVOUtil.EvaluationCategoryList.ECLCode);
		String lECLOrder = lastObject.getString(AVOUtil.EvaluationCategoryList.ECLOrder);
		mECLCode = NumberUtil.StringToLong(lECLCode);
		mECLOrder = NumberUtil.StringToLong(lECLOrder);
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
		Dialog dialog = new Dialog(this, "提交内容", "多条数据");
		dialog.addAcceptButton("确定");
		dialog.setOnAcceptButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				insetTen();
			}
		});;
		dialog.show();
		
	}
	
	
	
	private int index = 0;
	private long edcode = 1000957;
	private void insetTen(){
		String[] datas = {
				"Where is the tourist information centre?#请问旅游问讯处在哪里？",
				"Is there an airport bus to the city?#这里有从机场去市中心的巴士吗 ？",
				"Where is the bus stop (taxi stand)?#巴士车站在哪里 ？",
				"How much does it cost to the city centre by taxi?#乘计程车到市中心需要多少钱 ？",
				"How can I get to Hilton hotel?#去希尔顿酒店怎么走 ？",
				"May I have a city map?#请给我一张市区地图？",
				"Can I reserve a hotel here?#我可以在这里预订酒店吗 ？",
				"How much is it?#多少钱 ？",
				"Keep the change, please.#不用找钱了",
				"Take me to this address, please.#请拉我去这个地址",
				"How long does it take to go to the city centre?#到市中心需要多长时间？",
				"Stop here, please.#请停下来。",
				"What time does it leave?#几点发车？",
				"Where can I get a ticket?#在哪里卖票？",
				"Could you tell me when we get there?#请问几点能够到达那里。"
				};
		if(index < datas.length){
			categoryContent = datas[index];
			findDataIsExist();
		}else{
			ToastUtil.diaplayMesShort(EvaluationCategoryListActivity.this, "所有数据插入完成");
		}
	}
	
	private void findDataIsExist(){
//		String name = categoryContent.split("#")[0];
//		AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.EvaluationCategoryList.EvaluationCategoryList);
//		query.whereEqualTo(AVOUtil.EvaluationCategoryList.ECLName, name);
//		showProgressbar();
//		query.findInBackground(new FindCallback<AVObject>() {
//		    public void done(List<AVObject> avObjects, AVException e) {
//		    	hideProgressbar();
//		        if (avObjects != null && avObjects.size() == 0) {
//		        	ToastUtil.diaplayMesShort(EvaluationCategoryListActivity.this, "没有重复数据，执行插入!");
		        	submitData();
//		        } else {
//		        	ToastUtil.diaplayMesShort(EvaluationCategoryListActivity.this,"查询到" + avObjects.size() + " 条符合条件的数据");
//		        }
//		    }
//		});
	}
	
	private void submitData(){
		if(categoryContent.contains("#")){
			String name = categoryContent.split("#")[0];
			String eccode = String.valueOf(++mECLCode);
			String eCLOrder = String.valueOf(--mECLOrder);
			String eDCode = String.valueOf(++edcode);
			showProgressbar();
			AVObject newObject = new AVObject(AVOUtil.EvaluationCategoryList.EvaluationCategoryList);
			newObject.put(AVOUtil.EvaluationCategoryList.ECCode, ECCode);
			newObject.put(AVOUtil.EvaluationCategoryList.ECLCode, eccode);
			newObject.put(AVOUtil.EvaluationCategoryList.ECLName, name);
			newObject.put(AVOUtil.EvaluationCategoryList.ECLOrder, eCLOrder);
			newObject.saveInBackground(new SaveCallback() {
				@Override
				public void done(AVException arg0) {
					hideProgressbar();
					if (arg0 == null) {
						ToastUtil.diaplayMesShort(EvaluationCategoryListActivity.this, "保存成功");
					} else {
						ToastUtil.diaplayMesShort(EvaluationCategoryListActivity.this, "保存失败:"+arg0.getMessage());
					}
				}
			});
			
			AVObject detailObject = new AVObject(AVOUtil.EvaluationDetail.EvaluationDetail);
			detailObject.put(AVOUtil.EvaluationDetail.ECCode, ECCode);
			detailObject.put(AVOUtil.EvaluationDetail.ECLCode, eccode);
			detailObject.put(AVOUtil.EvaluationDetail.EDContent, categoryContent);
			detailObject.put(AVOUtil.EvaluationDetail.EDCode, eDCode);
			detailObject.saveInBackground(new SaveCallback() {
				@Override
				public void done(AVException arg0) {
					hideProgressbar();
					if (arg0 == null) {
						ToastUtil.diaplayMesShort(EvaluationCategoryListActivity.this, "保存成功");
						index++;
						insetTen();
					} else {
						ToastUtil.diaplayMesShort(EvaluationCategoryListActivity.this, "保存失败:"+arg0.getMessage());
					}
				}
			});
		}else{
			ToastUtil.diaplayMesShort(EvaluationCategoryListActivity.this, "没有#分隔符");
		}
	}
}
