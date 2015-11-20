package com.messi.languagehelper.http;

import cn.contentx.MD5;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.messi.languagehelper.util.Settings;


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
	
	public static String getBaiduTranslateSign(long salt){
		String str = Settings.baidu_appid + Settings.q + salt + Settings.baidu_secretkey;
		return MD5.encode(str);
	}

}
