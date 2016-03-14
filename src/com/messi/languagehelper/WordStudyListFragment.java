package com.messi.languagehelper;

import java.util.ArrayList;
import java.util.List;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.messi.languagehelper.adapter.WordStudyListAdapter;
import com.messi.languagehelper.dao.WordListType;
import com.messi.languagehelper.impl.FragmentProgressbarListener;
import com.messi.languagehelper.util.AVOUtil;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

public class WordStudyListFragment extends BaseFragment{

	private ListView category_lv;
	private WordStudyListAdapter mAdapter;
	private List<WordListType> mWordTypeList;
	
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
		View view = inflater.inflate(R.layout.word_study_list_fragment, container, false);
		initSwipeRefresh(view);
		initViews(view);
		new QueryTask().execute();
		return view;
	}
	
	private void initViews(View view){
		mWordTypeList = new ArrayList<WordListType>();
		category_lv = (ListView) view.findViewById(R.id.studycategory_lv);
		mAdapter = new WordStudyListAdapter(getActivity(), mWordTypeList);
		category_lv.setAdapter(mAdapter);
	}
	
	@Override
	public void onSwipeRefreshLayoutRefresh() {
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
				AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.WordStudyType.WordStudyType);
				query.whereEqualTo(AVOUtil.WordStudyType.is_valid, "1");
				query.orderByAscending(AVOUtil.WordStudyType.type_id);
				List<AVObject> avObjects  = query.find();
				if(avObjects != null){
					mWordTypeList.clear();
					for(AVObject mAVObject : avObjects){
						mWordTypeList.add( changeData(mAVObject) );
					}
				}
			} catch (Exception e) {
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
	
	private WordListType changeData(AVObject mAVObject){
		WordListType mType = new WordListType();
		mType.setType_id(mAVObject.getString(AVOUtil.WordStudyType.type_id));
		mType.setTitle(mAVObject.getString(AVOUtil.WordStudyType.title));
		mType.setCourse_num(mAVObject.getString(AVOUtil.WordStudyType.course_num));
		mType.setWord_num(mAVObject.getString(AVOUtil.WordStudyType.word_num));
		mType.setListJson(mAVObject.getString(AVOUtil.WordStudyType.child_list_json));
		mType.setList();
		AVFile mImgFile = mAVObject.getAVFile(AVOUtil.WordStudyType.img);
		if(mImgFile != null){
			mType.setImg_url(mImgFile.getUrl());
		}
		return mType;
	}
}
