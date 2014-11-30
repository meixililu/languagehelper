package com.messi.languagehelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.baidu.mobstat.StatService;
import com.iflytek.adserving.IFLYInterstitialAdView;
import com.iflytek.adserving.request.IFLYAdListener;
import com.messi.languagehelper.util.ADUtil;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.Settings;

public class LeisureFragment extends BaseFragment implements OnClickListener {

	private View view;
	private FrameLayout cailing_layout,app_layout,yuedu_layout;
	private LinearLayout ad_layout;
	private IFLYInterstitialAdView mInterstitialAd;
	public static LeisureFragment mMainFragment;
	
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
		cailing_layout = (FrameLayout)view.findViewById(R.id.cailing_layout);
		yuedu_layout = (FrameLayout)view.findViewById(R.id.yuedu_layout);
		app_layout = (FrameLayout)view.findViewById(R.id.app_layout);
		ad_layout = (LinearLayout)view.findViewById(R.id.ad_layout);
		cailing_layout.setOnClickListener(this);
		yuedu_layout.setOnClickListener(this);
		app_layout.setOnClickListener(this);
		mInterstitialAd = ADUtil.initChaPingAD(getActivity(), ad_layout);
		mInterstitialAd.loadAd(new IFLYAdListener() {
			@Override
			public void onAdReceive() {
				if(mInterstitialAd != null){
					mInterstitialAd.showAd();
				}
			}
			@Override
			public void onAdFailed(int arg0, String arg1) {
				
			}
			@Override
			public void onAdClose() {
				
			}
			@Override
			public void onAdClick() {
				
			}
		});
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.cailing_layout){
			toCailingActivity();
		}else if(v.getId() == R.id.yuedu_layout){
			toYueduActivity();
		}else if(v.getId() == R.id.app_layout){
			toAppActivity();
		}
	}
	
	private void toCailingActivity(){
		Intent intent = new Intent(getActivity(),WebViewActivity.class);
		intent.putExtra(KeyUtil.URL, Settings.CaiLingUrl);
		intent.putExtra(KeyUtil.ActionbarTitle, getActivity().getResources().getString(R.string.title_cailing));
		getActivity().startActivity(intent);
		StatService.onEvent(getActivity(), "19_to_cailing_page", "去彩铃页面", 1);
	}
	
	private void toYueduActivity(){
		Intent intent = new Intent(getActivity(),WebViewActivity.class);
		intent.putExtra(KeyUtil.URL, Settings.YueduUrl);
		intent.putExtra(KeyUtil.ActionbarTitle, getActivity().getResources().getString(R.string.title_reading));
		intent.putExtra(KeyUtil.IsReedPullDownRefresh, false);
		getActivity().startActivity(intent);
		StatService.onEvent(getActivity(), "20_to_yuedu_page", "去阅读页面", 1);
	}
	
	private void toAppActivity(){
		Intent intent = new Intent(getActivity(),RecommendActivity.class);
		getActivity().startActivity(intent);
		StatService.onEvent(getActivity(),"19_authors_software", "作者其他应用", 1);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mInterstitialAd != null){
			mInterstitialAd.destroy();
			mInterstitialAd = null;
		}
	}
}
