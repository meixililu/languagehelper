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
	private FrameLayout instagram_layout, invest_layout, game_layout,baidu_layout;
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
		instagram_layout = (FrameLayout)view.findViewById(R.id.instagram_layout);
		cailing_layout = (FrameLayout)view.findViewById(R.id.cailing_layout);
		baidu_layout = (FrameLayout)view.findViewById(R.id.baidu_layout);
		game_layout = (FrameLayout)view.findViewById(R.id.game_layout);
		yuedu_layout = (FrameLayout)view.findViewById(R.id.yuedu_layout);
		hotal_layout = (FrameLayout)view.findViewById(R.id.hotal_layout);
		app_layout = (FrameLayout)view.findViewById(R.id.app_layout);
		invest_layout = (FrameLayout)view.findViewById(R.id.invest_layout);
		ad_layout = (RelativeLayout)view.findViewById(R.id.ad_layout);
		instagram_layout.setOnClickListener(this);
		cailing_layout.setOnClickListener(this);
		yuedu_layout.setOnClickListener(this);
		hotal_layout.setOnClickListener(this);
		app_layout.setOnClickListener(this);
		invest_layout.setOnClickListener(this);
		baidu_layout.setOnClickListener(this);
		game_layout.setOnClickListener(this);
		ad_layout.setOnClickListener(this);
		mInterstitialAd = ADUtil.initChaPingAD(getActivity(), ad_layout);
		mHandler = new Handler();
		showAD();
	}
	
	private void showAD(){
		if(showNewFunction()){
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
		}
		@Override
		public void onAdFailed(AdError arg0) {
			LogUtil.DefalutLog("LeisureFragment---onAdFailed:"+arg0.getErrorCode()+"---"+arg0.getErrorDescription());
		}
	};
	
	private boolean showNewFunction(){
		if(ADUtil.IsShowAdImmediately){
			return true;
		}else{
			int IsCanShowAD_Leisure = mSharedPreferences.getInt(KeyUtil.IsCanShowAD_Leisure, 0);
			if(IsCanShowAD_Leisure > 0){
				return true;
			}else{
				IsCanShowAD_Leisure++;
				Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.IsCanShowAD_Leisure,IsCanShowAD_Leisure);
				return false;
			}
		}
	}

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
		}else if(v.getId() == R.id.instagram_layout){
			toEnglishWebsiteRecommendActivity();
		}else if(v.getId() == R.id.invest_layout){
			toInvestorListActivity();
		}else if(v.getId() == R.id.game_layout){
			toGameCenterActivity();
		}else if(v.getId() == R.id.baidu_layout){
			toBaiduActivity();
		}else if(v.getId() == R.id.ad_layout){
			StatService.onEvent(getActivity(), "ad_kapian", "点击卡片广告", 1);
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
	
	private void toEnglishWebsiteRecommendActivity(){
		Intent intent = new Intent(getActivity(),EnglishWebsiteRecommendActivity.class);
		getActivity().startActivity(intent);
		StatService.onEvent(getActivity(), "leisure_to_english_website", "休闲页去英文网站页面", 1);
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
		Intent intent = new Intent(getActivity(),RecommendActivity.class);
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
