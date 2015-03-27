package com.messi.languagehelper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

	private View view;
	private FrameLayout cailing_layout,app_layout,yuedu_layout,hotal_layout;
	private FrameLayout instagram_layout;
	private RelativeLayout ad_layout;
	private IFLYInterstitialAd mInterstitialAd;
	public static LeisureFragment mMainFragment;
	private SharedPreferences mSharedPreferences;
	
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
		yuedu_layout = (FrameLayout)view.findViewById(R.id.yuedu_layout);
		hotal_layout = (FrameLayout)view.findViewById(R.id.hotal_layout);
		app_layout = (FrameLayout)view.findViewById(R.id.app_layout);
		ad_layout = (RelativeLayout)view.findViewById(R.id.ad_layout);
		instagram_layout.setOnClickListener(this);
		cailing_layout.setOnClickListener(this);
		yuedu_layout.setOnClickListener(this);
		hotal_layout.setOnClickListener(this);
		app_layout.setOnClickListener(this);
		if(showNewFunction()){
			mInterstitialAd = ADUtil.initChaPingAD(getActivity(), ad_layout);
			mInterstitialAd.loadAd(new IFLYAdListener() {
				@Override
				public void onAdReceive() {
					LogUtil.DefalutLog("LeisureFragment---InterstitialAd---onAdReceive");
					if(mInterstitialAd != null){
						mInterstitialAd.showAd();
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
			});
		}else{
			ad_layout.setVisibility(View.GONE);
		}
		
	}
	
	private boolean showNewFunction(){
		int IsCanShowAD_Leisure = mSharedPreferences.getInt(KeyUtil.IsCanShowAD_Leisure, 0);
        if(IsCanShowAD_Leisure > 1){
        	return true;
        }else{
        	IsCanShowAD_Leisure++;
        	Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.IsCanShowAD_Leisure,IsCanShowAD_Leisure);
        	return false;
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
			toInstagramActivity();
		}
	}
	
	private void toInstagramActivity(){
		Intent intent = new Intent(getActivity(),WebViewActivity.class);
		intent.putExtra(KeyUtil.URL, Settings.InstagramUrl);
		intent.putExtra(KeyUtil.ActionbarTitle, getActivity().getResources().getString(R.string.title_instagram));
		getActivity().startActivity(intent);
		StatService.onEvent(getActivity(), "19_to_cailing_page", "去彩铃页面", 1);
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
	
	private void toHotelActivity(){
		Intent intent = new Intent(getActivity(),WebViewActivity.class);
		intent.putExtra(KeyUtil.URL, Settings.HotalUrl);
		intent.putExtra(KeyUtil.ActionbarTitle, getActivity().getResources().getString(R.string.title_hotel));
		getActivity().startActivity(intent);
		StatService.onEvent(getActivity(), "20_to_hotel_page", "去订酒店页面", 1);
	}
	
	private void toAppActivity(){
		Intent intent = new Intent(getActivity(),RecommendActivity.class);
		getActivity().startActivity(intent);
		StatService.onEvent(getActivity(),"19_authors_software", "作者其他应用", 1);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		ad_layout.removeAllViews();
		if(mInterstitialAd != null){
			mInterstitialAd = null;
		}
	}
}
