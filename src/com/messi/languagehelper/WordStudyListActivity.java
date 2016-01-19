package com.messi.languagehelper;

import java.util.ArrayList;
import java.util.List;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.messi.languagehelper.adapter.WordStudyListAdapter;
import com.messi.languagehelper.dao.WordListType;
import com.messi.languagehelper.util.AVOUtil;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

public class WordStudyListActivity extends BaseActivity {

	private ListView category_lv;
	private WordStudyListAdapter mAdapter;
	private List<WordListType> mWordTypeList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.word_study_list_activity);
		initSwipeRefresh();
		initViews();
		new QueryTask().execute();
	}
	
	private void initViews(){
		getSupportActionBar().setTitle(getResources().getString(R.string.title_word_study));
		mWordTypeList = new ArrayList<WordListType>();
		category_lv = (ListView) findViewById(R.id.studycategory_lv);
		mAdapter = new WordStudyListAdapter(this, mWordTypeList);
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
