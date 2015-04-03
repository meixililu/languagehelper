package com.messi.languagehelper;

import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.gc.materialdesign.views.ProgressBarCircularIndetermininate;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.messi.languagehelper.adapter.DailySentenceListAdapter;
import com.messi.languagehelper.dao.EveryDaySentence;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.listener.HidingScrollListener;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.views.DividerItemDecoration;

public class DailySentenceActivity extends BaseActivity implements OnClickListener {

	private RecyclerView recent_used_lv;
	private LayoutInflater mInflater;
	private DailySentenceListAdapter mAdapter;
	private List<EveryDaySentence> beans;
	private ProgressBarCircularIndetermininate progressbar;

	// 识别对象
//	private SpeechRecognizer recognizer;
	// 缓存，保存当前的引擎参数到下一次启动应用程序使用.
//	private SharedPreferences mSharedPreferences;
	//合成对象.
//	private SpeechSynthesizer mSpeechSynthesizer;

	private Bundle bundle;
	private int maxNumber = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.daily_sentence_activity);
		init();
	}
	
	private void init() {
		getSupportActionBar().setTitle(getResources().getString(R.string.dailysentence));
		mInflater = LayoutInflater.from(this);
		recent_used_lv = (RecyclerView) findViewById(R.id.collected_listview);
		progressbar = (ProgressBarCircularIndetermininate) findViewById(R.id.lottery_result_hall_progressbar_m);
//		mSharedPreferences = this.getSharedPreferences(this.getPackageName(), Activity.MODE_PRIVATE);
//		mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(this, null);
//		recognizer = SpeechRecognizer.createRecognizer(this, null);
		
		recent_used_lv.setHasFixedSize(true);
		LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
	    mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		recent_used_lv.setLayoutManager(mLayoutManager);
		recent_used_lv.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha)));
		beans = DataBaseUtil.getInstance().getDailySentenceList(Settings.offset);
		mAdapter = new DailySentenceListAdapter(this, mInflater, beans);
		recent_used_lv.setAdapter(mAdapter);
		recent_used_lv.setOnScrollListener(new HidingScrollListener() {
			@Override
			public void onShow() {
				showViews();
			}
			@Override
			public void onHide() {
				hideViews();
			}
		});
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.DefalutLog("CollectedFragment-onDestroy");
	}

	@Override
	public void onClick(View v) {
		
	}
}
