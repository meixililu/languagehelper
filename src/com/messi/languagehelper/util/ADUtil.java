package com.messi.languagehelper.util;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.LinearLayout;

import com.iflytek.voiceads.AdKeys;
import com.iflytek.voiceads.IFLYAdSize;
import com.iflytek.voiceads.IFLYBannerAd;
import com.iflytek.voiceads.IFLYFullScreenAd;
import com.iflytek.voiceads.IFLYInterstitialAd;
import com.jdwx.sdk.ApiManager;


public class ADUtil {
	
	public static final String KaiPingADId = "E170E50B2CBFE09CFE53F6D0A446560C";
	public static final String BannerADId = "A16A4713FB525DECF20126886F957534";
	public static final String ChaPingADId = "484C6E8F51357AFF26AEDB2441AB1847";
	public static final String QuanPingADId = "72C0E6B1042EA9F06A5A9B76235626CF";
	public static final String ListADId = "8FCA7E5106A3DB7DBC97B3B357E8A57F";
	public static final String XiuxianBanner = "8067D0538A5CC32E32550CCC816A23D2";
	public static final String XiuxianYSNRLAd = "A6505AA06C919195709A1194CB632879";
	public static final String MRYJYSNRLAd = "ED72385915DAC4C681891487523EBE87";
	
	public static final boolean IsShowAdImmediately = false;
	public static final int adCount = 3;
	public static final int adInterval = 5000;

	public static void initIFLYAD(Context context){
		ApiManager.getInstance().registerApp(context, "wxeb69070155cec01c", true);
	}
	
	// initQuanPingAD  initChaPingAD    initBannerAD    initKaiPingAD
	
	/**
	 * 添加banner广告条
	 * @param mActivity
	 * @param view
	 */
	public static IFLYBannerAd initBannerAD(Activity mActivity,LinearLayout view,String adId){
		//创建IFLYBannerAdView对象 
		final IFLYBannerAd bannerAd = IFLYBannerAd.createBannerAd(mActivity,adId); 
		bannerAd.setAdSize(IFLYAdSize.BANNER); 
		bannerAd.setParameter(AdKeys.DOWNLOAD_ALERT, "true");
		try {
			view.removeAllViews();
			view.addView(bannerAd);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return bannerAd;
	}
	
	/**
	 * 添加banner广告条
	 * @param mActivity
	 * @param view
	 */
	public static IFLYBannerAd initBannerAD(Activity mActivity,LinearLayout view){
		//创建IFLYBannerAdView对象 
		final IFLYBannerAd bannerAd = IFLYBannerAd.createBannerAd(mActivity,BannerADId); 
		bannerAd.setAdSize(IFLYAdSize.BANNER); 
		bannerAd.setParameter(AdKeys.DOWNLOAD_ALERT, "true");
		try {
			view.removeAllViews();
			view.addView(bannerAd);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return bannerAd;
	}
	
	/**
	 * 添加插屏广告
	 * @param mActivity
	 * @param view
	 */
	public static IFLYInterstitialAd initChaPingAD(Activity mActivity){
		//创建IFLYBannerAdView对象 
		final IFLYInterstitialAd interstitialAd = IFLYInterstitialAd.createInterstitialAd(mActivity,ChaPingADId);
		//设置需要请求的广告大小 
		interstitialAd.setAdSize(IFLYAdSize.INTERSTITIAL);
		interstitialAd.setParameter(AdKeys.DOWNLOAD_ALERT, "true");
		return interstitialAd;
	}
	
	/**
	 * 添加全屏广告
	 * @param mActivity
	 * @param view
	 */
	public static IFLYFullScreenAd initQuanPingAD(Activity mActivity){
		//创建IFLYBannerAdView对象 
		final IFLYFullScreenAd fullScreenAd = IFLYFullScreenAd.createFullScreenAd(mActivity,QuanPingADId);
		fullScreenAd.setAdSize(IFLYAdSize.FULLSCREEN);
		fullScreenAd.setParameter(AdKeys.SHOW_TIME_FULLSCREEN, "3000");
		fullScreenAd.setParameter(AdKeys.DOWNLOAD_ALERT, "true");
		return fullScreenAd;
	}
	
	public static boolean isShowAd(Context mContext){
		if(IsShowAdImmediately){
			return true;
		}else{
			SharedPreferences mSharedPreferences = Settings.getSharedPreferences(mContext);
			int times = mSharedPreferences.getInt(KeyUtil.IsCanShowAD_Loading, 0);
			return times > 1;
		}
	}
	
}
