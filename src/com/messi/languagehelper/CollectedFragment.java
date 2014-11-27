package com.messi.languagehelper;

import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import com.baidu.mobstat.StatService;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.messi.languagehelper.adapter.CollectedListItemAdapter;
import com.messi.languagehelper.bean.DialogBean;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.impl.FragmentProgressbarListener;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.wxapi.WXEntryActivity;

public class CollectedFragment extends Fragment implements OnClickListener {

	private ListView recent_used_lv;
	private View view;
	private LayoutInflater mInflater;
	private CollectedListItemAdapter mAdapter;
	private List<DialogBean> beans;

	// 识别对象
	private SpeechRecognizer recognizer;
	// 缓存，保存当前的引擎参数到下一次启动应用程序使用.
	private SharedPreferences mSharedPreferences;
	//合成对象.
	private SpeechSynthesizer mSpeechSynthesizer;

	private DataBaseUtil mDataBaseUtil;
	private Bundle bundle;
	private int maxNumber = 0;
	
	public static boolean isRespondWX;
	public static boolean isRefresh;
	public static CollectedFragment mMainFragment;
	private FragmentProgressbarListener mProgressbarListener;
	
	public static CollectedFragment getInstance(Bundle bundle){
		if(mMainFragment == null){
			mMainFragment = new CollectedFragment();
			mMainFragment.bundle = bundle;
		}
		return mMainFragment;
	}
	
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.collected_main, null);
		init();
		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isRespondWX = false;
		LogUtil.DefalutLog("CollectedFragment-onDestroy");
	}

	private void init() {
		mInflater = LayoutInflater.from(getActivity());
		mSharedPreferences = getActivity().getSharedPreferences(getActivity().getPackageName(), Activity.MODE_PRIVATE);
		recent_used_lv = (ListView) view.findViewById(R.id.collected_listview);
		mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(getActivity(), null);
		recognizer = SpeechRecognizer.createRecognizer(getActivity(), null);
		mDataBaseUtil = new DataBaseUtil(getActivity());
		beans = mDataBaseUtil.getDataListCollected(0, Settings.offset);
		mAdapter = new CollectedListItemAdapter(getActivity(), mInflater, beans, 
				mSpeechSynthesizer, mSharedPreferences, mDataBaseUtil, bundle, "CollectedFragment");
		recent_used_lv.setAdapter(mAdapter);
		
//		recent_used_lv.setOnRefreshListener(new OnRefreshListener<ListView>() {
//			@Override
//			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
//				Mode mCurrentMode = refreshView.getCurrentMode();
//				switch (mCurrentMode) {
//				case PULL_FROM_START:
//					maxNumber = 0;
//					break;
//				case PULL_FROM_END:
//					maxNumber += Settings.offset;
//					break;
//				}
//				new WaitTask().execute();				
//			}
//		});
	}
	
	@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
        	if(isRefresh){
        		isRefresh = false;
        		maxNumber = 0;
        		new WaitTask().execute();
        	}
        	StatService.onEvent(getActivity(), "1.6_viewcollectedpage", "浏览收藏页面", 1);
        }
	}
	
	class WaitTask extends AsyncTask<Void, Void, Void>{
		@Override
		protected void onPreExecute() {
			loadding();
		}
		@Override
		protected Void doInBackground(Void... params) {
			try {
				beans = mDataBaseUtil.getDataListCollected(maxNumber, maxNumber+Settings.offset);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
//			recent_used_lv.onRefreshComplete();
			finishLoadding();
			mAdapter.notifyDataChange(beans,maxNumber);
		}
	}
	
	/**
	 * 通过接口回调activity执行进度条显示控制
	 */
	private void loadding(){
		if(mProgressbarListener != null){
			mProgressbarListener.showProgressbar();
		}
	}
	
	/**
	 * 通过接口回调activity执行进度条显示控制
	 */
	private void finishLoadding(){
		if(mProgressbarListener != null){
			mProgressbarListener.hideProgressbar();
		}
	}
	
	@Override
	public void onClick(View v) {
		
	}
}
