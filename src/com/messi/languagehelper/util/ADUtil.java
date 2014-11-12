package com.messi.languagehelper.util;

import android.app.Activity;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.iflytek.adserving.IFLYBannerAdView;
import com.iflytek.adserving.IFLYFullScreenAdView;
import com.iflytek.adserving.IFLYInterstitialAdView;
import com.iflytek.adserving.IFLYSplashAdView;
import com.iflytek.adserving.request.IFLYAdListener;
import com.iflytek.adserving.request.IFLYAdRequest;
import com.iflytek.adserving.request.IFLYAdSize;

public class ADUtil {
	
	public static final String KaiPingADId = "E170E50B2CBFE09CFE53F6D0A446560C";
	public static final String BannerADId = "A16A4713FB525DECF20126886F957534";
	public static final String ChaPingADId = "484C6E8F51357AFF26AEDB2441AB1847";
	public static final String QuanPingADId = "72C0E6B1042EA9F06A5A9B76235626CF";
	
	// initQuanPingAD  initChaPingAD    initBannerAD    initKaiPingAD
	
	/**
	 * 添加开屏广告
	 * @param mActivity
	 * @param view
	 */
	public static void initKaiPingAD(Activity mActivity,LinearLayout view){
		final IFLYSplashAdView splashAd = new IFLYSplashAdView (mActivity); 
		//设置广告ID 
		splashAd.setAdUnitId(KaiPingADId); 
		//设置activity 
		splashAd.setActivity(mActivity); 
		//设置需要请求的广告大小 
		splashAd.setAdSize(IFLYAdSize.SPLASH); 
		
		view.addView(splashAd);
		//创建请求对象 
		IFLYAdRequest request = new IFLYAdRequest(mActivity); 
		//显示广告 
		splashAd.showAd(); 
		//请求广告 
		splashAd.loadAd(request); 
		//打印log 
		splashAd.setDebugMode(true);
		splashAd.setAdListener(new IFLYAdListener() {
			@Override
			public void onReceiveAd() {
				splashAd.showAd(); 
			}
			@Override
			public void onPresentScreen() {
			}
			@Override
			public void onFailedToReceiveAd(String arg0) {
			}
		});
	}
	
	/**
	 * 添加banner广告条
	 * @param mActivity
	 * @param view
	 */
	public static void initBannerAD(Activity mActivity,LinearLayout view){
		//创建IFLYBannerAdView对象 
		final IFLYBannerAdView bannerAd = new IFLYBannerAdView(mActivity); 
		//设置广告ID 
		bannerAd.setAdUnitId(BannerADId); 
		//设置activity 
		bannerAd.setActivity(mActivity); 
		//设置需要请求的广告大小 
		bannerAd.setAdSize(IFLYAdSize.BANNER); 
		//创建请求对象 
		IFLYAdRequest request = new IFLYAdRequest(mActivity); 
		//请求广告 
		bannerAd.loadAd(request); 
		//将广告位添加至layout中 
//		view.addView(bannerAd); 
		view.addView(bannerAd,new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT)); 
		//打印log 
		bannerAd.setDebugMode(true); 
		//添加监听器 
		bannerAd.setAdListener(new IFLYAdListener() {
			@Override
			public void onReceiveAd() {
				bannerAd.showAd();
			}
			@Override
			public void onPresentScreen() {
			}
			@Override
			public void onFailedToReceiveAd(String arg0) {
			}
		});
	}
	
	/**
	 * 添加插屏广告
	 * @param mActivity
	 * @param view
	 */
	public static void initChaPingAD(Activity mActivity,LinearLayout view){
		//创建IFLYBannerAdView对象 
		final IFLYInterstitialAdView interstitialAd = new IFLYInterstitialAdView (mActivity);
		//设置广告ID 
		interstitialAd.setAdUnitId(ChaPingADId); 
		//设置activity 
		interstitialAd.setActivity(mActivity); 
		//设置需要请求的广告大小 
		interstitialAd.setAdSize(IFLYAdSize.INTERSTITIAL); 
		//创建请求对象 
		IFLYAdRequest request = new IFLYAdRequest(mActivity); 
		//请求广告 
		interstitialAd.loadAd(request); 
		//将广告位添加至layout中 
		view.addView(interstitialAd); 
		//打印log 
		interstitialAd.setDebugMode(true); 
		//添加监听器 
		interstitialAd.setAdListener(new IFLYAdListener() {
			@Override
			public void onReceiveAd() {
				interstitialAd.showAd();
			}
			@Override
			public void onPresentScreen() {
			}
			@Override
			public void onFailedToReceiveAd(String arg0) {
			}
		});
	}
	
	/**
	 * 添加全屏广告
	 * @param mActivity
	 * @param view
	 */
	public static void initQuanPingAD(Activity mActivity,LinearLayout view){
		//创建IFLYBannerAdView对象 
		final IFLYFullScreenAdView fullScreenAd = new IFLYFullScreenAdView (mActivity);
		//设置广告ID 
		fullScreenAd.setAdUnitId(QuanPingADId); 
		//设置activity 
		fullScreenAd.setActivity(mActivity); 
		//设置需要请求的广告大小 
		fullScreenAd.setAdSize(IFLYAdSize.FULLSCREEN); 
		//创建请求对象 
		IFLYAdRequest request = new IFLYAdRequest(mActivity); 
		//请求广告 
		fullScreenAd.loadAd(request); 
		//将广告位添加至layout中 
		view.addView(fullScreenAd); 
		//打印log 
		fullScreenAd.setDebugMode(true); 
		//添加监听器 
		fullScreenAd.setAdListener(new IFLYAdListener() {
			@Override
			public void onReceiveAd() {
				fullScreenAd.showAd();
			}
			@Override
			public void onPresentScreen() {
			}
			@Override
			public void onFailedToReceiveAd(String arg0) {
			}
		});
	}

}
