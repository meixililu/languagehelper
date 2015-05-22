package com.messi.languagehelper.util;


import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.iflytek.voiceads.AdKeys;
import com.iflytek.voiceads.IFLYAdSize;
import com.iflytek.voiceads.IFLYBannerAd;
import com.iflytek.voiceads.IFLYFullScreenAd;
import com.iflytek.voiceads.IFLYInterstitialAd;


public class ADUtil {
	
	public static final String KaiPingADId = "E170E50B2CBFE09CFE53F6D0A446560C";
	public static final String BannerADId = "A16A4713FB525DECF20126886F957534";
	public static final String ChaPingADId = "484C6E8F51357AFF26AEDB2441AB1847";
	public static final String QuanPingADId = "72C0E6B1042EA9F06A5A9B76235626CF";
	
	public static final boolean IsShowAdImmediately = true;

	// initQuanPingAD  initChaPingAD    initBannerAD    initKaiPingAD
	
	/**
	 * 添加banner广告条
	 * @param mActivity
	 * @param view
	 */
	public static IFLYBannerAd initBannerAD(Activity mActivity,LinearLayout view){
		//创建IFLYBannerAdView对象 
		final IFLYBannerAd bannerAd = IFLYBannerAd.createBannerAd(mActivity,BannerADId); 
		bannerAd.setAdSize(IFLYAdSize.BANNER); 
		try {
			view.removeAllViews();
			view.addView(bannerAd);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		//添加监听器 
		return bannerAd;
	}
	
	/**
	 * 添加插屏广告
	 * @param mActivity
	 * @param view
	 */
	public static IFLYInterstitialAd initChaPingAD(Activity mActivity,ViewGroup view){
		//创建IFLYBannerAdView对象 
		final IFLYInterstitialAd interstitialAd = IFLYInterstitialAd.createInterstitialAd(mActivity,ChaPingADId);
		//设置需要请求的广告大小 
		interstitialAd.setAdSize(IFLYAdSize.INTERSTITIAL);
		try {
			view.removeAllViews();
			view.addView(interstitialAd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return interstitialAd;
	}
	
	/**
	 * 添加全屏广告
	 * @param mActivity
	 * @param view
	 */
	public static IFLYFullScreenAd initQuanPingAD(Activity mActivity, LinearLayout view){
		//创建IFLYBannerAdView对象 
		final IFLYFullScreenAd fullScreenAd = IFLYFullScreenAd.createFullScreenAd(mActivity,QuanPingADId);
		fullScreenAd.setAdSize(IFLYAdSize.FULLSCREEN);
		fullScreenAd.setParameter(AdKeys.SHOW_TIME_FULLSCREEN, "3000");
		try {
			view.removeAllViews();
			view.addView(fullScreenAd);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return fullScreenAd;
	}

}
