package com.messi.languagehelper.util;

import android.app.Activity;
import android.widget.LinearLayout;

import com.iflytek.adserving.IFLYBannerAdView;
import com.iflytek.adserving.IFLYFullScreenAdView;
import com.iflytek.adserving.IFLYInterstitialAdView;
import com.iflytek.adserving.IFLYSplashAdView;
import com.iflytek.adserving.request.IFLYAdSize;

public class ADUtil {
	
	public static final String KaiPingADId = "E170E50B2CBFE09CFE53F6D0A446560C";
	public static final String BannerADId = "A16A4713FB525DECF20126886F957534";
	public static final String ChaPingADId = "484C6E8F51357AFF26AEDB2441AB1847";
	public static final String QuanPingADId = "72C0E6B1042EA9F06A5A9B76235626CF";
	
	// initQuanPingAD  initChaPingAD    initBannerAD    initKaiPingAD
	
	/**
	 * 添加banner广告条
	 * @param mActivity
	 * @param view
	 */
	public static void initBannerAD(Activity mActivity,LinearLayout view){
		//创建IFLYBannerAdView对象 
		final IFLYBannerAdView bannerAd = new IFLYBannerAdView(mActivity,BannerADId); 
		//设置需要请求的广告大小 
		bannerAd.setAdSize(IFLYAdSize.BANNER); 
		view.addView(bannerAd); 
		//添加监听器 
	}
	
	/**
	 * 添加插屏广告
	 * @param mActivity
	 * @param view
	 */
	public static IFLYInterstitialAdView initChaPingAD(Activity mActivity,LinearLayout view){
		//创建IFLYBannerAdView对象 
		final IFLYInterstitialAdView interstitialAd = new IFLYInterstitialAdView (mActivity,ChaPingADId);
		//设置需要请求的广告大小 
		interstitialAd.setAdSize(IFLYAdSize.INTERSTITIAL); 
		view.addView(interstitialAd);
		return interstitialAd;
	}
	
	/**
	 * 添加全屏广告
	 * @param mActivity
	 * @param view
	 */
	public static IFLYFullScreenAdView initQuanPingAD(Activity mActivity, LinearLayout view){
		//创建IFLYBannerAdView对象 
		final IFLYFullScreenAdView fullScreenAd = new IFLYFullScreenAdView (mActivity,QuanPingADId);
		fullScreenAd.setAdSize(IFLYAdSize.FULLSCREEN); 
		view.addView(fullScreenAd); 
		return fullScreenAd;
	}

}
