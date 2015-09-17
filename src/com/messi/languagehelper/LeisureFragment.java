package com.messi.languagehelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import cn.contentx.ContExManager;

import com.baidu.mobstat.StatService;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.IFLYAdListener;
import com.iflytek.voiceads.IFLYInterstitialAd;
import com.messi.languagehelper.util.ADUtil;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.Settings;

public class LeisureFragment extends BaseFragment implements OnClickListener {

	public static final long IntervalTime = 1000 * 20;
	
	private View view;
	private FrameLayout cailing_layout,app_layout,yuedu_layout,hotal_layout;
	private FrameLayout invest_layout, game_layout,baidu_layout;
	private FrameLayout news_layout;
	private RelativeLayout ad_layout;
	private IFLYInterstitialAd mInterstitialAd;
	public static LeisureFragment mMainFragment;
	private SharedPreferences mSharedPreferences;
	private Handler mHandler;
	
	public static LeisureFragment getInstance(){
		if(mMainFragment == null){
			mMainFragment = new LeisureFragment();
		}
		return mMainFragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.leisure_fragment, null);
		initViews();
		return view;
	}
	
	private void initViews(){
		mSharedPreferences = getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);
		cailing_layout = (FrameLayout)view.findViewById(R.id.cailing_layout);
		baidu_layout = (FrameLayout)view.findViewById(R.id.baidu_layout);
		game_layout = (FrameLayout)view.findViewById(R.id.game_layout);
		yuedu_layout = (FrameLayout)view.findViewById(R.id.yuedu_layout);
		hotal_layout = (FrameLayout)view.findViewById(R.id.hotal_layout);
		app_layout = (FrameLayout)view.findViewById(R.id.app_layout);
		invest_layout = (FrameLayout)view.findViewById(R.id.invest_layout);
		news_layout = (FrameLayout)view.findViewById(R.id.news_layout);
		ad_layout = (RelativeLayout)view.findViewById(R.id.ad_layout);
		
		cailing_layout.setOnClickListener(this);
		yuedu_layout.setOnClickListener(this);
		hotal_layout.setOnClickListener(this);
		app_layout.setOnClickListener(this);
		invest_layout.setOnClickListener(this);
		baidu_layout.setOnClickListener(this);
		game_layout.setOnClickListener(this);
		ad_layout.setOnClickListener(this);
		news_layout.setOnClickListener(this);
		mInterstitialAd = ADUtil.initChaPingAD(getActivity(), ad_layout);
		mHandler = new Handler();
		showAD();
	}
	
	private void showAD(){
		if(ADUtil.isShowAd(getActivity())){
			mInterstitialAd.loadAd(mIFLYAdListener);
		}else{
			ad_layout.setVisibility(View.GONE);
		}
	}
	
	private Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			showAD();
		}
	};
	
	private IFLYAdListener mIFLYAdListener = new IFLYAdListener() {
		@Override
		public void onAdReceive() {
			LogUtil.DefalutLog("LeisureFragment---InterstitialAd---onAdReceive");
			if(mInterstitialAd != null){
				mInterstitialAd.showAd();
				mHandler.postDelayed(mRunnable,IntervalTime);
			}
		}
		@Override
		public void onAdClose() {
			ad_layout.removeAllViews();
		}
		@Override
		public void onAdClick() {
			StatService.onEvent(getActivity(), "ad_kapian", "点击卡片广告", 1);
		}
		@Override
		public void onAdFailed(AdError arg0) {
			LogUtil.DefalutLog("LeisureFragment---onAdFailed:"+arg0.getErrorCode()+"---"+arg0.getErrorDescription());
		}
	};
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.cailing_layout){
			toCailingActivity();
		}else if(v.getId() == R.id.yuedu_layout){
			toYueduActivity();
		}else if(v.getId() == R.id.hotal_layout){
			toHotelActivity();
		}else if(v.getId() == R.id.app_layout){
			toAppActivity();
		}else if(v.getId() == R.id.invest_layout){
			toInvestorListActivity();
		}else if(v.getId() == R.id.game_layout){
			toGameCenterActivity();
		}else if(v.getId() == R.id.baidu_layout){
			toBaiduActivity();
		}else if(v.getId() == R.id.news_layout){
			ContExManager.initWithAPPId(getActivity(),"f9136944-bc17-4cb1-9b14-ece9de91b39d", "w1461Eub");
			ContExManager.show(getActivity());//直接显示，使用默认值
		}
	}
	
	private void toBaiduActivity(){
		Intent intent = new Intent(getActivity(),com.lerdian.search.SearchResult.class);
		startActivity(intent);
	}
	
	private void toInvestorListActivity(){
		Intent intent = new Intent(getActivity(),WebViewActivity.class);
		intent.putExtra(KeyUtil.URL, Settings.InvestListUrl);
		intent.putExtra(KeyUtil.ActionbarTitle, getActivity().getResources().getString(R.string.invest_activity_title));
		getActivity().startActivity(intent);
		StatService.onEvent(getActivity(), "leisure_to_invest_list", "休闲页去投资人列表", 1);
	}
	
	private void toGameCenterActivity(){
		Intent intent = new Intent(getActivity(),WebViewActivity.class);
		intent.putExtra(KeyUtil.URL, Settings.GameUrl);
		intent.putExtra(KeyUtil.ActionbarTitle, getActivity().getResources().getString(R.string.leisuer_game));
		getActivity().startActivity(intent);
		StatService.onEvent(getActivity(), "leisure_to_game_page", "休闲页去小游戏页面", 1);
	}
	
	private void toCailingActivity(){
		Intent intent = new Intent(getActivity(),WebViewActivity.class);
		intent.putExtra(KeyUtil.URL, Settings.CaiLingUrl);
		intent.putExtra(KeyUtil.ActionbarTitle, getActivity().getResources().getString(R.string.title_cailing));
		getActivity().startActivity(intent);
		StatService.onEvent(getActivity(), "leisure_to_cailing_page", "休闲页去彩铃页面", 1);
	}
	
	private void toYueduActivity(){
		Intent intent = new Intent(getActivity(),WebViewActivity.class);
		intent.putExtra(KeyUtil.URL, Settings.YueduUrl);
		intent.putExtra(KeyUtil.ActionbarTitle, getActivity().getResources().getString(R.string.title_reading));
		intent.putExtra(KeyUtil.IsReedPullDownRefresh, false);
		getActivity().startActivity(intent);
		StatService.onEvent(getActivity(), "leisure_to_yuedu_page", "休闲页去阅读页面", 1);
	}
	
	private void toHotelActivity(){
		Intent intent = new Intent(getActivity(),WebViewActivity.class);
		intent.putExtra(KeyUtil.URL, Settings.HotalUrl);
		intent.putExtra(KeyUtil.ActionbarTitle, getActivity().getResources().getString(R.string.title_hotel));
		getActivity().startActivity(intent);
		StatService.onEvent(getActivity(), "leisure_to_hotel_page", "休闲页去订酒店页面", 1);
	}
	
	private void toAppActivity(){
		Intent intent = new Intent(getActivity(),AppRecommendListActivity.class);
		getActivity().startActivity(intent);
		StatService.onEvent(getActivity(),"leisure_to_authors_software", "休闲页去作者其他应用页", 1);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(ad_layout != null){
			ad_layout.removeAllViews();
		}
		if(mHandler != null){
			mHandler.removeCallbacks(mRunnable);
		}
		if(mInterstitialAd != null){
			mInterstitialAd = null;
		}
	}
}
