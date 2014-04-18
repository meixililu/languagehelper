package com.messi.languagehelper.http;

import com.messi.languagehelper.util.Settings;

public class LanguagehelperHttpClient {

	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void get(RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.get(Settings.baiduUrl, params, responseHandler);
	}

	public static void post(RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.post(Settings.baiduUrl, params, responseHandler);
	}

}
