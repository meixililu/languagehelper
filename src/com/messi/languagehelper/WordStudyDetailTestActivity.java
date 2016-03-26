package com.messi.languagehelper;

import java.util.ArrayList;
import java.util.List;

import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.gc.materialdesign.views.ButtonFloat;
import com.iflytek.cloud.SpeechSynthesizer;
import com.messi.languagehelper.adapter.WordStudyDetailTestAdapter;
import com.messi.languagehelper.dao.WordDetailListItem;
import com.messi.languagehelper.util.AVOUtil;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.SDCardUtil;
import com.messi.languagehelper.util.ToastUtil;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;

public class WordStudyDetailTestActivity extends BaseActivity implements OnClickListener {

	private GridView category_lv;
	private WordStudyDetailTestAdapter mAdapter;
	private List<WordDetailListItem> itemList;
	private ButtonFloat playbtn,previous_btn,next_btn;
	private String class_name;
	private String class_id;
	private int course_id;
	private int course_num;
	private MediaPlayer mPlayer;
	private String audioPath;
	
	private SpeechSynthesizer mSpeechSynthesizer;
	private SharedPreferences mSharedPreferences;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.word_study_detail_test_activity);
		initSwipeRefresh();
		initViews();
		new QueryTask().execute();
	}
	
	private void initViews(){
		mSharedPreferences = this.getSharedPreferences(this.getPackageName(), Activity.MODE_PRIVATE);
		mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(this, null);
		mPlayer = new MediaPlayer();
		
		class_name = getIntent().getStringExtra(KeyUtil.ClassName);
		class_id = getIntent().getStringExtra(KeyUtil.ClassId);
		course_id = getIntent().getIntExtra(KeyUtil.CourseId, 1);
		course_num = getIntent().getIntExtra(KeyUtil.CourseNum, 0);
		setTitle(class_name + course_id +"单元");
		if(!TextUtils.isEmpty(class_id)){
			audioPath = SDCardUtil.WordStudyPath + class_id + SDCardUtil.Delimiter + String.valueOf(course_id) + SDCardUtil.Delimiter;
		}else{
			finish();
		}
		playbtn = (ButtonFloat) findViewById(R.id.playbtn);
		previous_btn = (ButtonFloat) findViewById(R.id.previous_btn);
		next_btn = (ButtonFloat) findViewById(R.id.next_btn);
		itemList = new ArrayList<WordDetailListItem>();
		category_lv = (GridView) findViewById(R.id.studycategory_lv);
		mAdapter = new WordStudyDetailTestAdapter(this, mSharedPreferences, mSpeechSynthesizer,
				itemList, audioPath, mPlayer);
		category_lv.setAdapter(mAdapter);
		
		playbtn.setOnClickListener(this);
		previous_btn.setOnClickListener(this);
		next_btn.setOnClickListener(this);
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
				AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.WordStudyDetail.WordStudyDetail);
				query.whereEqualTo(AVOUtil.WordStudyDetail.class_id, class_id);
				query.whereEqualTo(AVOUtil.WordStudyDetail.course, course_id);
				query.orderByAscending(AVOUtil.WordStudyDetail.item_id);
				List<AVObject> avObjects  = query.find();
				if(avObjects != null){
					itemList.clear();
					for(AVObject mAVObject : avObjects){
						itemList.add( changeData(mAVObject) );
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
			mAdapter.getPlayOrder();
			category_lv.setSelection(0);
		}
	}
	
	private WordDetailListItem changeData(AVObject mAVObject){
		WordDetailListItem mType = new WordDetailListItem();
		mType.setClass_id(mAVObject.getString(AVOUtil.WordStudyDetail.class_id));
		mType.setClass_title(mAVObject.getString(AVOUtil.WordStudyDetail.class_title));
		mType.setCourse(mAVObject.getInt(AVOUtil.WordStudyDetail.course));
		mType.setDesc(mAVObject.getString(AVOUtil.WordStudyDetail.desc));
		mType.setItem_id(mAVObject.getString(AVOUtil.WordStudyDetail.item_id));
		mType.setName(mAVObject.getString(AVOUtil.WordStudyDetail.name));
		mType.setSound(mAVObject.getString(AVOUtil.WordStudyDetail.sound));
		mType.setSymbol(mAVObject.getString(AVOUtil.WordStudyDetail.symbol));
		return mType;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) { 
		case R.id.playbtn:
			playSound();
			break;
		case R.id.previous_btn:
			if(course_id > 1){
				course_id--;
				setTitle(class_name + course_id +"单元");
				new QueryTask().execute();
			}else{
				ToastUtil.diaplayMesShort(WordStudyDetailTestActivity.this, "已经是第一单元了");
			}
			break;
		case R.id.next_btn:
			if(course_id < course_num){
				course_id++;
				setTitle(class_name + course_id +"单元");
				new QueryTask().execute();
			}else{
				ToastUtil.diaplayMesShort(WordStudyDetailTestActivity.this, "已经是最后一单元了");
			}
			break;
		}
	}
	
	private void playSound(){
		if(mAdapter.isPlaying()){
			playbtn.setDrawableIcon(this.getResources().getDrawable(R.drawable.ic_stop_white_48dp));
			mAdapter.onPlayBtnClick();
		}else{
			playbtn.setDrawableIcon(this.getResources().getDrawable(R.drawable.ic_play_arrow_white_48dp));
		}
	}
	
	public void stopPlay(){
		if (mPlayer != null) {   
			mPlayer.stop();  
        } 
		playbtn.setDrawableIcon(this.getResources().getDrawable(R.drawable.ic_play_arrow_white_48dp));
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mPlayer != null) {   
			mPlayer.stop();  
			mPlayer.release();   
			mPlayer = null;   
        } 
		if(mSpeechSynthesizer != null){
			mSpeechSynthesizer.stopSpeaking();
			mSpeechSynthesizer = null;
		}
	}
	
}
