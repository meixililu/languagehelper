package com.messi.languagehelper;

import java.util.ArrayList;
import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.messi.languagehelper.adapter.EvaluationTypeAdapter;
import com.messi.languagehelper.impl.FragmentProgressbarListener;
import com.messi.languagehelper.util.ADUtil;
import com.messi.languagehelper.util.AVOUtil;
import com.messi.languagehelper.util.XFYSAD;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

public class SpokenEnglishPractiseFragment extends BaseFragment{

	private ListView category_lv;
	private EvaluationTypeAdapter mAdapter;
	private List<AVObject> avObjects;
	private XFYSAD mXFYSAD;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mProgressbarListener = (FragmentProgressbarListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement FragmentProgressbarListener");
        }
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.spoken_english_practice_fragment, container, false);
		initSwipeRefresh(view);
		initViews(view);
		new QueryTask().execute();
		return view;
	}
	
	private void initViews(View view){
		avObjects = new ArrayList<AVObject>();
		category_lv = (ListView) view.findViewById(R.id.studycategory_lv);
		View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.xunfei_ysad_item, null);
		category_lv.addHeaderView(headerView);
		mAdapter = new EvaluationTypeAdapter(getActivity(), avObjects);
		category_lv.setAdapter(mAdapter);
		
		mXFYSAD = new XFYSAD(getActivity(), headerView, ADUtil.MRYJYSNRLAd);
		mXFYSAD.showAD();
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
        	if(mXFYSAD != null){
        		mXFYSAD.startPlayImg();
        	}
        }else{
        	if(mXFYSAD != null){
        		mXFYSAD.canclePlayImg();
        	}
        }
	}
	
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
	public void onDestroy() {
		super.onDestroy();
		if(mXFYSAD != null){
			mXFYSAD.canclePlayImg();
			mXFYSAD = null;
		}
	}
}
