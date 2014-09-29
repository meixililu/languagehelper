package com.messi.languagehelper;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.LinearLayout;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.study_activity);
		initViews();
	}

	private void initViews() {
		mActionBar.setTitle(getResources().getString(
				R.string.practice_spoken_englist_style1));
		mSharedPreferences = Settings.getSharedPreferences(this);
		fragmentManager = getSupportFragmentManager();
		studylist_position = getIntent().getIntExtra(
				KeyUtil.PracticeContentKey, 0);
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
		} else if (studylist_position == 1) {
			studylist_part1_content = getResources().getStringArray(R.array.studylist_part1_content2);
		}
	}

	private void addIndicator() {
		ViewUtil.addIndicator(studylist_part1_content.length, page_navigation,this);
	}

	private void addFragment() {
		PracticeFourChooseOneFragment fragment = new PracticeFourChooseOneFragment(studylist_part1_content[pageIndex], this,
				vedioPath + pageIndex + SDCardUtil.Delimiter,mSharedPreferences);
		fragmentManager.beginTransaction()
				.add(R.id.page_content, fragment)
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
		if (pageIndex == 1) {
			mActionBar.setTitle(getResources().getString(R.string.practice_spoken_englist_style2));
			PracticeReadAfterMeFragment mpramf = new PracticeReadAfterMeFragment(
					studylist_part1_content[pageIndex], this, vedioPath + pageIndex + SDCardUtil.Delimiter,mSharedPreferences);
			setFragment(mpramf);
		} else if (pageIndex == 2) {
			mActionBar.setTitle(getResources().getString(R.string.practice_spoken_englist_style4));
			PracticeWriteFragment mpramf = new PracticeWriteFragment(
					studylist_part1_content[pageIndex], this, vedioPath + pageIndex + SDCardUtil.Delimiter,mSharedPreferences);
			setFragment(mpramf);
		} else if (pageIndex == 3) {
			mActionBar.setTitle(getResources().getString(R.string.practice_spoken_englist_style3));
			PracticeReadAfterMeFragment mpramf = new PracticeReadAfterMeFragment(
					studylist_part1_content[pageIndex], this, vedioPath + pageIndex + SDCardUtil.Delimiter,mSharedPreferences);
			setFragment(mpramf);
		}else {
			mActionBar.setTitle(getResources().getString(R.string.practice_spoken_englist_style3));
			FinishFragment mpramf = new FinishFragment(this);
			setFragment(mpramf);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		pageIndex = 0;
		vedioPath = "";
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
