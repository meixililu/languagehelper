package com.messi.languagehelper;

import java.util.List;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.baidu.mobstat.StatService;
import com.iflytek.cloud.SpeechSynthesizer;
import com.messi.languagehelper.impl.PracticeProgressListener;
import com.messi.languagehelper.util.AVOUtil;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.SDCardUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.ViewUtil;

public class PracticeDetailActivity extends BaseActivity implements PracticeProgressListener {

	public static String vedioPath = "";

	private LinearLayout page_navigation, page_content;
	private TextView error_txt;
	private String[] studyContent;
	private FragmentManager fragmentManager;
	public int pageIndex;// third level
	private String PCCode;
	private String PCLCode;
	private SharedPreferences mSharedPreferences;
	private Fragment mContent;
	private SpeechSynthesizer mSpeechSynthesizer;
	private AVObject avObject;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.study_activity);
		initData();
		initViews();
		new QueryTask().execute();
	}
	
	private void initData(){
		mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(this,null);
		mSharedPreferences = Settings.getSharedPreferences(this);
		fragmentManager = getSupportFragmentManager();
		PCLCode = getIntent().getStringExtra(AVOUtil.PracticeCategoryList.PCLCode);
		PCCode = getIntent().getStringExtra(AVOUtil.PracticeCategory.PCCode);
		vedioPath = SDCardUtil.PracticePath + PCCode + SDCardUtil.Delimiter + PCLCode + SDCardUtil.Delimiter;
	}

	private void initViews() {
		page_navigation = (LinearLayout) findViewById(R.id.page_navigation);
		page_content = (LinearLayout) findViewById(R.id.page_content);
		error_txt = (TextView) findViewById(R.id.error_txt);
		error_txt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new QueryTask().execute();
			}
		});
	}
	
	private void setData(){
		addIndicator();
		addFragment();
	}

	private class QueryTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressbar();
			error_txt.setVisibility(View.GONE);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.PracticeDetail.PracticeDetail);
			query.whereEqualTo(AVOUtil.PracticeDetail.PCCode, PCCode);
			query.whereEqualTo(AVOUtil.PracticeDetail.PCLCode, PCLCode);
			try {
				List<AVObject> avObjects  = query.find();
				if(avObjects != null && avObjects.size() > 0){
					avObject = avObjects.get(0);
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
			if(avObject != null){
				String content = avObject.getString(AVOUtil.PracticeDetail.PDContent);
				if(!TextUtils.isEmpty(content)){
					studyContent = content.split("@");
					setData();
				}else{
					error_txt.setVisibility(View.VISIBLE);
				}
			}else{
				error_txt.setVisibility(View.VISIBLE);
			}
		}
	}

	private void addIndicator() {
		ViewUtil.addIndicator(studyContent.length, page_navigation,this);
	}

	private void addFragment() {
		String[]contents = studyContent[pageIndex].split("#");
		String type = contents[contents.length-1];
		getSupportActionBar().setTitle( getActionbarTitle(type) );
		Fragment mpramf = getStudyType(type);
		getSupportActionBar().setTitle(getResources().getString(R.string.practice_spoken_englist_style_studyevery));
		fragmentManager.beginTransaction()
				.add(R.id.page_content, mpramf)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
				.commit();
	}

	private void setFragment(Fragment mFragment) {
		if (mContent != mFragment) {
			mContent = mFragment;
			fragmentManager
					.beginTransaction()
					.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out)
					.replace(R.id.page_content, mFragment) // 替换Fragment，实现切换
					.commit();
		}
	}

	@Override
	public void toNextPage() {
		LogUtil.DefalutLog("toNextPage");
		ViewUtil.setPracticeIndicator(this, page_navigation, pageIndex);
		pageIndex++;
		if (pageIndex < studyContent.length) {
			String[]contents = studyContent[pageIndex].split("#");
			String type = contents[contents.length-1];
			String mTitle = getActionbarTitle(type);
			/** 根据标题来判断这个版本是否有该功能，没有就跳过 **/
			if(!TextUtils.isEmpty(mTitle)){
				getSupportActionBar().setTitle( mTitle );
				Fragment mpramf = getStudyType(type);
				setFragment(mpramf);
			}else{
				toNextPage();
			}
		}else {
			getSupportActionBar().setTitle(getResources().getString(R.string.practice_spoken_englist_finish));
			FinishFragment mpramf = new FinishFragment(this);
			setFragment(mpramf);
			StatService.onEvent(this, "study_page_finish", "口语练习完成", 1);
		}
	}
	
	private Fragment getStudyType(String type){
		Fragment mpramf = new Fragment();
		if(type.equalsIgnoreCase(KeyUtil.Study_Every)){
			mpramf = new PracticeEveryFragment(studyContent[pageIndex], this,
					vedioPath + KeyUtil.Study_Every + SDCardUtil.Delimiter,mSharedPreferences,mSpeechSynthesizer);
			
			StatService.onEvent(this, "study_page_studyevery", "学单词", 1);
		}else if(type.equalsIgnoreCase(KeyUtil.Practice_FourInOne)){
			mpramf = new PracticeFourChooseOneFragment(studyContent[pageIndex], this,
					vedioPath + KeyUtil.Practice_FourInOne + SDCardUtil.Delimiter,mSharedPreferences,mSpeechSynthesizer);
			
			StatService.onEvent(this, "study_page_fourinone", "口语练习四选一", 1);
		}else if(type.equalsIgnoreCase(KeyUtil.Practice_ReadAfterMe)){
			mpramf = new PracticeReadAfterMeFragment(studyContent[pageIndex], this, 
					vedioPath + KeyUtil.Practice_ReadAfterMe + SDCardUtil.Delimiter,mSharedPreferences,mSpeechSynthesizer);
			
			StatService.onEvent(this, "study_page_readafterme", "口语练习跟我读", 1);
		}else if(type.equalsIgnoreCase(KeyUtil.Practice_Translate)){
			mpramf = new PracticeWriteFragment(studyContent[pageIndex], this, 
					vedioPath + KeyUtil.Practice_Translate + SDCardUtil.Delimiter,mSharedPreferences,mSpeechSynthesizer);
			
			StatService.onEvent(this, "study_page_write", "口语练习书写校验", 1);
		}else if(type.equalsIgnoreCase(KeyUtil.Practice_SpeakAfterMe)){
			mpramf = new PracticeReadAfterMeFragment(studyContent[pageIndex], this, 
					vedioPath + KeyUtil.Practice_SpeakAfterMe + SDCardUtil.Delimiter,mSharedPreferences,mSpeechSynthesizer);
			
			StatService.onEvent(this, "study_page_speakafterme", "口语练习跟我说", 1);
		}else{
			mpramf = new FinishFragment(this);
		}
		return mpramf;
	}
	
	private String getActionbarTitle(String type){
		String mpramf = "";
		if(type.equalsIgnoreCase(KeyUtil.Study_Every)){
			mpramf = getResources().getString(R.string.practice_spoken_englist_style_fourinone);
		}else if(type.equalsIgnoreCase(KeyUtil.Practice_FourInOne)){
			mpramf = getResources().getString(R.string.practice_spoken_englist_style_fourinone);
		}else if(type.equalsIgnoreCase(KeyUtil.Practice_ReadAfterMe)){
			mpramf = getResources().getString(R.string.practice_spoken_englist_style_readafterme);
		}else if(type.equalsIgnoreCase(KeyUtil.Practice_Translate)){
			mpramf = getResources().getString(R.string.practice_spoken_englist_style_write);
		}else if(type.equalsIgnoreCase(KeyUtil.Practice_SpeakAfterMe)){
			mpramf = getResources().getString(R.string.practice_spoken_englist_style_speakafterme);
		}else{
			mpramf = "";
		}
		return mpramf;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		pageIndex = 0;
		vedioPath = "";
		if(mSpeechSynthesizer != null){
			mSpeechSynthesizer.destroy();
			mSpeechSynthesizer = null;
		}
	}
	
	@Override
	public void onLoading() {
		showProgressbar();
	}

	@Override
	public void onCompleteLoading() {
		hideProgressbar();
	}

	@Override
	public void finishActivity() {
		this.finish();
	}

}
