package com.messi.languagehelper;

import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.iflytek.cloud.SpeechSynthesizer;
import com.messi.languagehelper.adapter.ResultListHeaderAdapter;
import com.messi.languagehelper.dao.record;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.views.DividerItemDecoration;

public class CollectedTranslateFragment extends Fragment implements OnClickListener {

	private RecyclerView recent_used_lv;
	private LayoutInflater mInflater;
	private ResultListHeaderAdapter mAdapter;
	private List<record> beans;
	private ProgressBarCircularIndeterminate progressbar;
	private View view;
	// 缓存，保存当前的引擎参数到下一次启动应用程序使用.
	private SharedPreferences mSharedPreferences;
	//合成对象.
	private SpeechSynthesizer mSpeechSynthesizer;

	private Bundle bundle;
	private int maxNumber = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.collected_translate_fragment, null);
		init();
		return view;
	}
	
	private void init() {
		mInflater = LayoutInflater.from(getActivity());
		mSharedPreferences = getActivity().getSharedPreferences(getActivity().getPackageName(), Activity.MODE_PRIVATE);
		recent_used_lv = (RecyclerView) view.findViewById(R.id.collected_listview);
		progressbar = (ProgressBarCircularIndeterminate) view.findViewById(R.id.lottery_result_hall_progressbar_m);
		mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(getActivity(), null);
		
		recent_used_lv.setHasFixedSize(true);
		LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
	    mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		recent_used_lv.setLayoutManager(mLayoutManager);
		recent_used_lv.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha)));
//		recent_used_lv.setOnScrollListener(new HidingScrollListener() {
//			@Override
//			public void onShow() {
////				showViews();
//			}
//			@Override
//			public void onHide() {
////				hideViews();
//			}
//		});
		
		beans = DataBaseUtil.getInstance().getDataListCollected(0, Settings.offset);
		mAdapter = new ResultListHeaderAdapter(getActivity(), mInflater, beans, 
				mSpeechSynthesizer, mSharedPreferences, bundle, false);
		recent_used_lv.setAdapter(mAdapter);
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		
	}
}
