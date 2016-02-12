package com.messi.languagehelper.views;

import com.avos.avoscloud.okhttp.FormEncodingBuilder;
import com.avos.avoscloud.okhttp.RequestBody;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.gson.Gson;
import com.messi.languagehelper.DictionaryFragment;
import com.messi.languagehelper.dao.Dictionary;
import com.messi.languagehelper.dao.Root;
import com.messi.languagehelper.dialog.TranslateResultDialog;
import com.messi.languagehelper.http.LanguagehelperHttpClient;
import com.messi.languagehelper.http.UICallback;
import com.messi.languagehelper.util.JsonParser;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.Settings;

import android.app.Activity;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;

public class TouchableSpan extends ClickableSpan {// extend ClickableSpan

	private String word;
	private Activity context;
	private ProgressBarCircularIndeterminate mProgressbar;

	public TouchableSpan(Activity context, ProgressBarCircularIndeterminate mProgressbar, String string) {
		super();
		word = string;
		this.mProgressbar = mProgressbar;
		this.context = context;
	}

	public void onClick(View tv) {
		LogUtil.DefalutLog(word);
		RequestShowapiAsyncTask();
	}

	public void updateDrawState(TextPaint ds) {
		ds.setUnderlineText(false); // set to false to remove underline
	}
	
	private void RequestShowapiAsyncTask(){
		if(mProgressbar != null){
			mProgressbar.setVisibility(View.VISIBLE);
		}
		RequestBody formBody = new FormEncodingBuilder()
		.add("showapi_appid", Settings.showapi_appid)
		.add("showapi_sign", Settings.showapi_secret)
		.add("showapi_timestamp", String.valueOf(System.currentTimeMillis()))
		.add("showapi_res_gzip", "1")
		.add("q", word)
		.build();
		LanguagehelperHttpClient.post(Settings.ShowApiDictionaryUrl, formBody, new UICallback(context){
			@Override
			public void onResponsed(String responseString){
				LogUtil.DefalutLog("Result---showapi:"+responseString);
				try {
					if (!TextUtils.isEmpty(responseString)) {
						if(JsonParser.isJson(responseString)){
							Root mRoot = new Gson().fromJson(responseString, Root.class);
							if(mRoot != null && mRoot.getShowapi_res_code() == 0 && mRoot.getShowapi_res_body() != null){
								Dictionary bean = JsonParser.changeShowapiResultToDicBean(mRoot,word);
								DictionaryFragment.isRefresh = true;
								TranslateResultDialog dialog = new TranslateResultDialog(context, bean);
								dialog.setCancelable(true);
								dialog.setCanceledOnTouchOutside(true);
								dialog.show();
							}else{
//								GetDictionaryFaultAsyncTask();
							}
						}else{
//							GetDictionaryFaultAsyncTask();
						}
					} else {
//						GetDictionaryFaultAsyncTask();
					}
				} catch (Exception e) {
//					GetDictionaryFaultAsyncTask();
					e.printStackTrace();
				}
			}
			@Override
			public void onFailured() {
			}
			@Override
			public void onFinished() {
				if(mProgressbar != null){
					mProgressbar.setVisibility(View.GONE);
				}
			}
		});
	}
	
}
