package com.messi.languagehelper.http;

import java.net.URLEncoder;

import cn.contentx.MD5;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.StringUtils;


public class LanguagehelperHttpClient {

	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.get(url, params, responseHandler);
	}
	
	public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.post(url, params, responseHandler);
	}
	
	public static void postBaidu(AsyncHttpResponseHandler responseHandler) {
		RequestParams mRequestParams = new RequestParams();
		long salt = System.currentTimeMillis();
		mRequestParams.put("appid", Settings.baidu_appid);
		mRequestParams.put("salt", salt);
		mRequestParams.put("q", Settings.q);
		mRequestParams.put("from", Settings.from);
		mRequestParams.put("to", Settings.to);
		mRequestParams.put("sign", getBaiduTranslateSign(salt));
		client.post(Settings.baiduTranslateUrl, mRequestParams, responseHandler);
	}
	
	public static void postIcibaWeb(AsyncHttpResponseHandler responseHandler){
		try {
			client.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.80 Safari/537.36");
			String url = "http://www.iciba.com/" + URLEncoder.encode(Settings.q, "utf-8");
			client.get(url, new RequestParams(), responseHandler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void postIciba(AsyncHttpResponseHandler responseHandler){
		try {
			client.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.80 Safari/537.36");
			RequestParams mRequestParams = new RequestParams();
			mRequestParams.put("q", Settings.q);
			mRequestParams.put("type", "auto");
			client.post(Settings.IcibaTranslateUrl, mRequestParams, responseHandler);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	public static void postBingYing(AsyncHttpResponseHandler responseHandler){
//		try {
//			client.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.80 Safari/537.36");
////			String url = Settings.bingTranslateUrl + "appId=" + "TZP5DtAfYF55t1bvTVOfJPZNIRR1RXvQJ1ekn0gu_RHQ*" +
////					"&texts=" + URLEncoder.encode(Settings.q, "utf-8") +"&from=" + "%22zh-CHS%22" + "&to=" + "%22en%22" + "&options=" + "%7B%7D" +
////					"&oncomplete=" + "onComplete_36" + "&onerror=" + "onError_36" + "&_=" + "1449978756560";
//			String url = Settings.bingTranslateUrl + "appId=" + "TZP5DtAfYF55t1bvTVOfJPZNIRR1RXvQJ1ekn0gu_RHQ*" +
//					"&texts=" + "%5B%22"+Settings.q+"%22%5D" +"&from=" + "%22zh-CHS%22" + "&to=" + "%22en%22" + "&options=" + "%7B%7D" +
//					"&oncomplete=" + "onComplete_3" + "&onerror=" + "onError_3" + "&_=" + String.valueOf(System.currentTimeMillis());
//			LogUtil.DefalutLog("url--"+url);
////			url = "http://api.microsofttranslator.com/v2/ajax.svc/TranslateArray2?appId=TZP5DtAfYF55t1bvTVOfJPZNIRR1RXvQJ1ekn0gu_RHQ*&texts=n你好&from=%22zh-CHS%22&to=%22en%22&options=%7B%7D&oncomplete=onComplete_3&onerror=onError_3&_=1449975354994";
////		url = "http://api.microsofttranslator.com/v2/ajax.svc/TranslateArray2?appId=TZP5DtAfYF55t1bvTVOfJPZNIRR1RXvQJ1ekn0gu_RHQ*&texts=%5B%22%E4%BD%A0%E5%8F%AB%E4%BB%80%E4%B9%88%E5%90%8D%E5%AD%97%22%5D&from=%22zh-CHS%22&to=%22en%22&options=%7B%7D&oncomplete=onComplete_3&onerror=onError_3&_=1449975354994";
//			client.get(url, new RequestParams(), responseHandler);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public static String getBaiduTranslateSign(long salt){
		String str = Settings.baidu_appid + Settings.q + salt + Settings.baidu_secretkey;
		return MD5.encode(str);
	}
	
	public static void setTranslateLan(){
		if(StringUtils.isChinese(Settings.q)){
			Settings.from = "zh";
			Settings.to = "en";
		}else{
			Settings.from = "en";
			Settings.to = "zh";
		}
	}

}
