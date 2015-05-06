package com.messi.languagehelper;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.LinearLayout;

import com.baidu.mobstat.StatService;
import com.iflytek.cloud.SpeechSynthesizer;
import com.messi.languagehelper.impl.PracticeProgressListener;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.SDCardUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.ViewUtil;

public class StudyActivity extends BaseActivity implements PracticeProgressListener {

	public static String vedioPath = "";

	private LinearLayout page_navigation, page_content;
	private String[] studylist_part1_content;
	private FragmentManager fragmentManager;
	private int studylist_position;// second level
	public int pageIndex;// third level
	private String level;
	private SharedPreferences mSharedPreferences;
	private Fragment mContent;
	private SpeechSynthesizer mSpeechSynthesizer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.study_activity);
		initViews();
	}

	private void initViews() {
		mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(this,null);
		mSharedPreferences = Settings.getSharedPreferences(this);
		fragmentManager = getSupportFragmentManager();
		studylist_position = getIntent().getIntExtra(KeyUtil.PracticeContentKey, 0);
		level = getIntent().getStringExtra(KeyUtil.LevelKey);
		vedioPath = SDCardUtil.PracticePath + level + SDCardUtil.Delimiter + studylist_position + SDCardUtil.Delimiter;
		getStudyContent();

		page_navigation = (LinearLayout) findViewById(R.id.page_navigation);
		page_content = (LinearLayout) findViewById(R.id.page_content);
		addIndicator();
		addFragment();
	}

	private void getStudyContent() {
		if (studylist_position == 0) {
			studylist_part1_content = getResources().getStringArray(R.array.studylist_part1_content1);
		} else if(studylist_position == 1){
			studylist_part1_content = getResources().getStringArray(R.array.studylist_part1_content2);
		} else if(studylist_position == 2){
			studylist_part1_content = getResources().getStringArray(R.array.studylist_part1_content3);
		} else if(studylist_position == 3){
			studylist_part1_content = getResources().getStringArray(R.array.studylist_part1_content4);
		} else if(studylist_position == 4){
			studylist_part1_content = getResources().getStringArray(R.array.studylist_part1_content5);
		} else if(studylist_position == 5){
			studylist_part1_content = getResources().getStringArray(R.array.studylist_part1_content6);
		} else if(studylist_position == 6){
			studylist_part1_content = getResources().getStringArray(R.array.studylist_part1_content7);
		} else if(studylist_position == 7){
			studylist_part1_content = getResources().getStringArray(R.array.studylist_part1_content8);
		} else if(studylist_position == 8){
			studylist_part1_content = getResources().getStringArray(R.array.studylist_part1_content9);
		} else if(studylist_position == 9){
			studylist_part1_content = getResources().getStringArray(R.array.studylist_part1_content10);
		} else if(studylist_position == 10){
			studylist_part1_content = getResources().getStringArray(R.array.studylist_part1_content11);
		} else if(studylist_position == 11){
			studylist_part1_content = getResources().getStringArray(R.array.studylist_part1_content12);
		} else if(studylist_position == 12){
			studylist_part1_content = getResources().getStringArray(R.array.studylist_part1_content13);
		}
	}

	private void addIndicator() {
		ViewUtil.addIndicator(studylist_part1_content.length, page_navigation,this);
	}

	private void addFragment() {
		String[]contents = studylist_part1_content[pageIndex].split("#");
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
		if (pageIndex < studylist_part1_content.length) {
			String[]contents = studylist_part1_content[pageIndex].split("#");
			String type = contents[contents.length-1];
			getSupportActionBar().setTitle( getActionbarTitle(type) );
			Fragment mpramf = getStudyType(type);
			setFragment(mpramf);
		}else {
			getSupportActionBar().setTitle(getResources().getString(R.string.practice_spoken_englist_finish));
			FinishFragment mpramf = new FinishFragment(this);
			setFragment(mpramf);
			StatService.onEvent(this, "study_page_finish", "口语练习完成", 1);
		}
	}
	
	private Fragment getStudyType(String type){
		Fragment mpramf = new Fragment();
		type = type.toLowerCase();
		if(type.equals(KeyUtil.Study_Every.toLowerCase())){
			mpramf = new PracticeEveryFragment(studylist_part1_content[pageIndex], this,
					vedioPath + KeyUtil.Study_Every + SDCardUtil.Delimiter,mSharedPreferences,mSpeechSynthesizer);
			
			StatService.onEvent(this, "study_page_studyevery", "学单词", 1);
		}else if(type.equals(KeyUtil.Practice_FourInOne.toLowerCase())){
			mpramf = new PracticeFourChooseOneFragment(studylist_part1_content[pageIndex], this,
					vedioPath + KeyUtil.Practice_FourInOne + SDCardUtil.Delimiter,mSharedPreferences,mSpeechSynthesizer);
			
			StatService.onEvent(this, "study_page_fourinone", "口语练习四选一", 1);
		}else if(type.equals(KeyUtil.Practice_ReadAfterMe.toLowerCase())){
			mpramf = new PracticeReadAfterMeFragment(studylist_part1_content[pageIndex], this, 
					vedioPath + KeyUtil.Practice_ReadAfterMe + SDCardUtil.Delimiter,mSharedPreferences,mSpeechSynthesizer);
			
			StatService.onEvent(this, "study_page_readafterme", "口语练习跟我读", 1);
		}else if(type.equals(KeyUtil.Practice_Translate.toLowerCase())){
			mpramf = new PracticeWriteFragment(studylist_part1_content[pageIndex], this, 
					vedioPath + KeyUtil.Practice_Translate + SDCardUtil.Delimiter,mSharedPreferences,mSpeechSynthesizer);
			
			StatService.onEvent(this, "study_page_write", "口语练习书写校验", 1);
		}else if(type.equals(KeyUtil.Practice_SpeakAfterMe.toLowerCase())){
			mpramf = new PracticeReadAfterMeFragment(studylist_part1_content[pageIndex], this, 
					vedioPath + KeyUtil.Practice_SpeakAfterMe + SDCardUtil.Delimiter,mSharedPreferences,mSpeechSynthesizer);
			
			StatService.onEvent(this, "study_page_speakafterme", "口语练习跟我说", 1);
		}else{
			mpramf = new FinishFragment(this);
		}
		return mpramf;
	}
	
	private String getActionbarTitle(String type){
		String mpramf = "";
		if(type.equals(KeyUtil.Study_Every)){
			mpramf = getResources().getString(R.string.practice_spoken_englist_style_fourinone);
		}else if(type.equals(KeyUtil.Practice_FourInOne)){
			mpramf = getResources().getString(R.string.practice_spoken_englist_style_fourinone);
		}else if(type.equals(KeyUtil.Practice_ReadAfterMe)){
			mpramf = getResources().getString(R.string.practice_spoken_englist_style_readafterme);
		}else if(type.equals(KeyUtil.Practice_Translate)){
			mpramf = getResources().getString(R.string.practice_spoken_englist_style_write);
		}else if(type.equals(KeyUtil.Practice_SpeakAfterMe)){
			mpramf = getResources().getString(R.string.practice_spoken_englist_style_speakafterme);
		}else{
			mpramf = getResources().getString(R.string.practice_spoken_englist_finish);
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

	private void showProgressbar() {
		setSupportProgressBarIndeterminateVisibility(true);
		setSupportProgressBarVisibility(true);
	}

	private void hideProgressbar() {
		setSupportProgressBarIndeterminateVisibility(false);
		setSupportProgressBarVisibility(false);
	}

	@Override
	public void finishActivity() {
		this.finish();
	}

}
