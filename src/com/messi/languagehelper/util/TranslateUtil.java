package com.messi.languagehelper.util;

import java.io.IOException;

import com.avos.avoscloud.okhttp.Callback;
import com.avos.avoscloud.okhttp.FormEncodingBuilder;
import com.avos.avoscloud.okhttp.Request;
import com.avos.avoscloud.okhttp.RequestBody;
import com.avos.avoscloud.okhttp.Response;
import com.google.gson.Gson;
import com.messi.languagehelper.BaseApplication;
import com.messi.languagehelper.dao.Dictionary;
import com.messi.languagehelper.dao.DictionaryRootJuhe;
import com.messi.languagehelper.dao.Root;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.http.LanguagehelperHttpClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

public class TranslateUtil {
	
	public static int translateRetryTimes = 3;
	public static String juhStr = "";
	public static String showapiStr = "";
	public static String translate_api_juhe = "baidu_api,show_api,juhe_api";
	public static String translate_api_showapi = "baidu_api,juhe_api,show_api";

	// show api translate
	public static void Translate(Context mActivity, Handler mHandler) {
		SharedPreferences sp = Settings.getSharedPreferences(mActivity);
		String apiOrders = sp.getString(KeyUtil.TranslateApiOrder, translate_api_showapi);
		LogUtil.DefalutLog("apiOrders:"+apiOrders);
		String[] apiOrder = apiOrders.split(",");
		if(apiOrder.length != 3){
			apiOrder = translate_api_showapi.split(",");
		}
		translateRetryTimes--;
		LogUtil.DefalutLog("Translate---translateRetryTimes:" + translateRetryTimes);
		if(translateRetryTimes > -1){
			selectTranslateApi(mActivity, mHandler, apiOrder[translateRetryTimes]);
		}else{
			sendMessage(mHandler,2);
		}
		compareResultForBeterOne(sp);
	}
	
	public static void selectTranslateApi(Context mActivity, Handler mHandler, String method){
		LogUtil.DefalutLog("selectTranslateApi:"+method);
		if(method.equals("show_api")){
			Translate_Showapi(mActivity, mHandler);
		}else if(method.equals("juhe_api")){
			Translate_Juhe(mActivity, mHandler);
		}else{
			Translate_baidu(mActivity, mHandler);
		}
	}

	public static void Translate_Showapi(final Context mActivity, final Handler mHandler) {
		RequestBody formBody = new FormEncodingBuilder()
				.add("showapi_appid", Settings.showapi_appid)
				.add("showapi_sign", Settings.showapi_secret)
				.add("showapi_timestamp", String.valueOf(System.currentTimeMillis()))
				.add("showapi_res_gzip", "1")
				.add("q", Settings.q)
				.build();
		LanguagehelperHttpClient.post(Settings.ShowApiDictionaryUrl, formBody, new Callback() {
			@Override
			public void onFailure(Request arg0, IOException arg1) {
				Translate(mActivity,mHandler);
			}

			@Override
			public void onResponse(Response mResponse) throws IOException {
				try {
					if (mResponse.isSuccessful()) {
						String responseString = mResponse.body().string();
						LogUtil.DefalutLog("Result---showapi:" + responseString);
						if (!TextUtils.isEmpty(responseString)) {
							if (JsonParser.isJson(responseString)) {
								Root mRoot = new Gson().fromJson(responseString, Root.class);
								if (mRoot != null && mRoot.getShowapi_res_code() == 0
										&& mRoot.getShowapi_res_body() != null
										&& mRoot.getShowapi_res_body().getRet_code() != -1) {
									Dictionary mDictionaryBean = JsonParser.changeShowapiResultToDicBean(mRoot,Settings.q);
									BaseApplication.dataMap.put(KeyUtil.DataMapKey, mDictionaryBean);
									sendMessage(mHandler,1);
								}else{
									Translate(mActivity,mHandler);
								}
							}else{
								Translate(mActivity,mHandler);
							}
						}else{
							Translate(mActivity,mHandler);
						}
					}else{
						Translate(mActivity,mHandler);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Translate(mActivity,mHandler);
				}
			}
		});
	}

	public static void Translate_Juhe(final Context mActivity, final Handler mHandler) {
		LanguagehelperHttpClient.get(Settings.JuheYoudaoApiUrl + Settings.q, new Callback() {
			@Override
			public void onFailure(Request arg0, IOException arg1) {
				Translate(mActivity,mHandler);
			}

			@Override
			public void onResponse(Response mResponse) throws IOException {
				try {
					if (mResponse.isSuccessful()) {
						String responseString = mResponse.body().string();
						LogUtil.DefalutLog("Result---juhe:" + responseString);
						if (!TextUtils.isEmpty(responseString)) {
							if (JsonParser.isJson(responseString)) {
								if(responseString.contains("us-phonetic")){
									responseString = responseString.replace("us-phonetic", "us_phonetic");
								}
								if(responseString.contains("uk-phonetic")){
									responseString = responseString.replace("uk-phonetic", "uk_phonetic");
								}
								DictionaryRootJuhe mRoot = new Gson().fromJson(responseString, DictionaryRootJuhe.class);
								if (mRoot != null && mRoot.getError_code() == 0 && mRoot.getResult() != null) {
									Dictionary mDictionaryBean = JsonParser.changeJuheResultToDicBean(mRoot,Settings.q);
									BaseApplication.dataMap.put(KeyUtil.DataMapKey, mDictionaryBean);
									sendMessage(mHandler,1);
								}else{
									Translate(mActivity,mHandler);
								}
							}else{
								Translate(mActivity,mHandler);
							}
						}else{
							Translate(mActivity,mHandler);
						}
					}else{
						Translate(mActivity,mHandler);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Translate(mActivity,mHandler);
				}
			}
		});
	}

	/**
	 * if dictionary api fail baidu translate api
	 */
	public static void Translate_baidu(final Context mActivity, final Handler mHandler) {
		LanguagehelperHttpClient.postBaidu(new Callback() {
			@Override
			public void onFailure(Request arg0, IOException arg1) {
				Translate(mActivity,mHandler);
			}
			@Override
			public void onResponse(Response mResponse) throws IOException {
				try {
					if (mResponse.isSuccessful()) {
						String responseString = mResponse.body().string();
						if (!TextUtils.isEmpty(responseString)) {
							LogUtil.DefalutLog("Result---baidu tran:" + responseString);
							String dstString = JsonParser.getTranslateResult(responseString);
							if (dstString.contains("error_msg:")) {
								LogUtil.DefalutLog("Result---baidu tran---error_msg:" + dstString);
								Translate(mActivity,mHandler);
							} else {
								Dictionary mDictionaryBean = new Dictionary();
								mDictionaryBean.setType(KeyUtil.ResultTypeTranslate);
								mDictionaryBean.setWord_name(Settings.q);
								mDictionaryBean.setResult(dstString);
								DataBaseUtil.getInstance().insert(mDictionaryBean);
								BaseApplication.dataMap.put(KeyUtil.DataMapKey, mDictionaryBean);
								sendMessage(mHandler,1);
							}
						} else {
							Translate(mActivity,mHandler);
						}
					}else{
						Translate(mActivity,mHandler);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Translate(mActivity,mHandler);
				}
			}
		});
	}
	
	public static void sendMessage(Handler mHandler,int result_code){
		if (mHandler != null) {
			translateRetryTimes = 3;
			Message msg = Message.obtain(mHandler, result_code);
			mHandler.sendMessage(msg);
		}
	}
	
	public static void compareResultForBeterOne(final SharedPreferences sp){
		long lastTimeCompare = sp.getLong(KeyUtil.CompareTranslateApiTime, 0);
		if(System.currentTimeMillis() - lastTimeCompare > 1000*60*10){
			new Thread(new Runnable() {
				@Override
				public void run() {
					showapiStr = "init";
					juhStr = "init";
					RequestBody formBody = new FormEncodingBuilder()
							.add("showapi_appid", Settings.showapi_appid)
							.add("showapi_sign", Settings.showapi_secret)
							.add("showapi_timestamp", String.valueOf(System.currentTimeMillis()))
							.add("showapi_res_gzip", "1")
							.add("q", Settings.q)
							.build();
					LanguagehelperHttpClient.post(Settings.ShowApiDictionaryUrl, formBody, new Callback() {
						@Override
						public void onFailure(Request arg0, IOException arg1) {
							showapiStr = "error";
						}
						
						@Override
						public void onResponse(Response mResponse) throws IOException {
							try {
								if (mResponse.isSuccessful()) {
									String responseString = mResponse.body().string();
									LogUtil.DefalutLog("Result---showapi:" + responseString);
									if (!TextUtils.isEmpty(responseString)) {
										showapiStr = responseString;
										compareResult(sp);
									}else{
										showapiStr = "error";
									}
								}else{
									showapiStr = "error";
								}
							} catch (Exception e) {
								showapiStr = "error";
								e.printStackTrace();
							}
						}
					});
					
					LanguagehelperHttpClient.get(Settings.JuheYoudaoApiUrl + Settings.q, new Callback() {
						@Override
						public void onFailure(Request arg0, IOException arg1) {
							juhStr = "error";
						}
						@Override
						public void onResponse(Response mResponse) throws IOException {
							try {
								if (mResponse.isSuccessful()) {
									String responseString = mResponse.body().string();
									LogUtil.DefalutLog("Result---juhe:" + responseString);
									if (!TextUtils.isEmpty(responseString)) {
										juhStr = responseString;
										compareResult(sp);
									}else{
										juhStr = "error";
									}
								}else{
									juhStr = "error";
								}
							} catch (Exception e) {
								juhStr = "error";
								e.printStackTrace();
							}
						}
					});
				}
			}).start();
		}
	}
	
	public static void compareResult(SharedPreferences sp){
		if(!showapiStr.equals("init") && !juhStr.equals("init")){
			Settings.saveSharedPreferences(sp, KeyUtil.CompareTranslateApiTime, System.currentTimeMillis());
			if(showapiStr.length() > juhStr.length()){
				Settings.saveSharedPreferences(sp, KeyUtil.TranslateApiOrder, translate_api_showapi);
			}else{
				Settings.saveSharedPreferences(sp, KeyUtil.TranslateApiOrder, translate_api_juhe);
			}
		}
	}

}
