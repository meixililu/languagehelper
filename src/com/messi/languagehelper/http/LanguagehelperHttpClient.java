package com.messi.languagehelper.http;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import cn.contentx.MD5;

import com.avos.avoscloud.okhttp.Cache;
import com.avos.avoscloud.okhttp.Callback;
import com.avos.avoscloud.okhttp.FormEncodingBuilder;
import com.avos.avoscloud.okhttp.Interceptor;
import com.avos.avoscloud.okhttp.MediaType;
import com.avos.avoscloud.okhttp.MultipartBuilder;
import com.avos.avoscloud.okhttp.OkHttpClient;
import com.avos.avoscloud.okhttp.Request;
import com.avos.avoscloud.okhttp.RequestBody;
import com.avos.avoscloud.okhttp.Response;
import com.messi.languagehelper.inteface.ProgressListener;
import com.messi.languagehelper.util.CameraUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.StringUtils;

public class LanguagehelperHttpClient {
	
	public static final int HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 10 * 1024 * 1024;
	private static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpg");

	public static OkHttpClient client = new OkHttpClient();
	
	public static OkHttpClient initClient(Context mContext){
		if(client ==  null){
			client = new OkHttpClient();
		}
		client.setConnectTimeout(15, TimeUnit.SECONDS);
		client.setWriteTimeout(15, TimeUnit.SECONDS);
		client.setReadTimeout(30, TimeUnit.SECONDS);
		File baseDir = mContext.getCacheDir();
		if(baseDir != null){
			File cacheDir = new File(baseDir,"HttpResponseCache");
			client.setCache(new Cache(cacheDir, HTTP_RESPONSE_DISK_CACHE_MAX_SIZE));
		}
		return client;
	}

	public static Response get(String url) {
		Response mResponse = null;
		try {
			Request request = new Request.Builder()
			.url(url)
			.build();
			mResponse = client.newCall(request).execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mResponse;
	}
	
	public static void get(String url, Callback mCallback) {
		Request request = new Request.Builder()
			.url(url)
			.build();
		client.newCall(request).enqueue(mCallback);
	}

	public static void post(String url, RequestBody params, Callback mCallback) {
		Request request = new Request.Builder()
			.url(url)
			.post(params)
			.build();
		client.newCall(request).enqueue(mCallback);
	}

	public static void postBaidu(Callback mCallback) {
		long salt = System.currentTimeMillis();
		RequestBody formBody  = new FormEncodingBuilder()
			.add("appid", Settings.baidu_appid)
			.add("salt", String.valueOf(salt))
			.add("q", Settings.q)
			.add("from", Settings.from)
			.add("to", Settings.to)
			.add("sign", getBaiduTranslateSign(salt))
			.build();
		post(Settings.baiduTranslateUrl,  formBody , mCallback);
	}

	public static void postIciba(Callback mCallback) {
		try {
			RequestBody formBody = new FormEncodingBuilder()
				.add("q", Settings.q)
				.add("type", "auto")
				.build();
			Request request = new Request.Builder()
				.url(Settings.IcibaTranslateUrl)
				.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.80 Safari/537.36")
				.post(formBody)
				.build();
			client.newCall(request).enqueue(mCallback);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void postBaiduOCR(String path, Callback mCallback) {
		try {
			RequestBody mRequestBody = RequestBody.create( MEDIA_TYPE_JPG, CameraUtil.getFile(path) );
			RequestBody formBody = new MultipartBuilder()
				.type(MultipartBuilder.FORM)
				.addFormDataPart("fromdevice", "android")
				.addFormDataPart("clientip", "10.10.10.0")
				.addFormDataPart("detecttype", "LocateRecognize")
				.addFormDataPart("languagetype", "CHN_ENG")
				.addFormDataPart("imagetype", "2")
				.addFormDataPart("image", "picture.jpg", mRequestBody)
				.build();
			Request request = new Request.Builder()
				.url(Settings.BaiduOCRUrl)
				.header("apikey", "bb3e54f1ade6307919e47bd1eccc3dde")
				.post(formBody)
				.build();
			client.newCall(request).enqueue(mCallback);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static OkHttpClient addProgressResponseListener(final ProgressListener progressListener){
        //克隆
		OkHttpClient clone = client.clone();
		// 增加拦截器
		clone.networkInterceptors().add(new Interceptor() {
			@Override
			public Response intercept(Chain chain) throws IOException {
				// 拦截
				Response originalResponse = chain.proceed(chain.request());
				// 包装响应体并返回
				return originalResponse
						.newBuilder()
						.body(new ProgressResponseBody(originalResponse.body(), progressListener))
						.build();
			}
		});
        return clone;
    }

	public static String getBaiduTranslateSign(long salt) {
		String str = Settings.baidu_appid + Settings.q + salt + Settings.baidu_secretkey;
		return MD5.encode(str);
	}

//	public static void postIcibaWeb(AsyncHttpResponseHandler responseHandler) {
//		try {
//			client.addHeader(
//					"User-Agent",
//					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.80 Safari/537.36");
//			String url = "http://www.iciba.com/"
//					+ URLEncoder.encode(Settings.q, "utf-8");
//			client.get(url, new RequestParams(), responseHandler);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	public static void setTranslateLan() {
		if (StringUtils.isChinese(Settings.q)) {
			Settings.from = "zh";
			Settings.to = "en";
		} else {
			Settings.from = "en";
			Settings.to = "zh";
		}
	}
	
	// public static void postBingYing(AsyncHttpResponseHandler
	// responseHandler){
	// try {
	// client.addHeader("User-Agent",
	// "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.80 Safari/537.36");
	// // String url = Settings.bingTranslateUrl + "appId=" +
	// "TZP5DtAfYF55t1bvTVOfJPZNIRR1RXvQJ1ekn0gu_RHQ*" +
	// // "&texts=" + URLEncoder.encode(Settings.q, "utf-8") +"&from=" +
	// "%22zh-CHS%22" + "&to=" + "%22en%22" + "&options=" + "%7B%7D" +
	// // "&oncomplete=" + "onComplete_36" + "&onerror=" + "onError_36" + "&_="
	// + "1449978756560";
	// String url = Settings.bingTranslateUrl + "appId=" +
	// "TZP5DtAfYF55t1bvTVOfJPZNIRR1RXvQJ1ekn0gu_RHQ*" +
	// "&texts=" + "%5B%22"+Settings.q+"%22%5D" +"&from=" + "%22zh-CHS%22" +
	// "&to=" + "%22en%22" + "&options=" + "%7B%7D" +
	// "&oncomplete=" + "onComplete_3" + "&onerror=" + "onError_3" + "&_=" +
	// String.valueOf(System.currentTimeMillis());
	// LogUtil.DefalutLog("url--"+url);
	// // url =
	// "http://api.microsofttranslator.com/v2/ajax.svc/TranslateArray2?appId=TZP5DtAfYF55t1bvTVOfJPZNIRR1RXvQJ1ekn0gu_RHQ*&texts=n你好&from=%22zh-CHS%22&to=%22en%22&options=%7B%7D&oncomplete=onComplete_3&onerror=onError_3&_=1449975354994";
	// // url =
	// "http://api.microsofttranslator.com/v2/ajax.svc/TranslateArray2?appId=TZP5DtAfYF55t1bvTVOfJPZNIRR1RXvQJ1ekn0gu_RHQ*&texts=%5B%22%E4%BD%A0%E5%8F%AB%E4%BB%80%E4%B9%88%E5%90%8D%E5%AD%97%22%5D&from=%22zh-CHS%22&to=%22en%22&options=%7B%7D&oncomplete=onComplete_3&onerror=onError_3&_=1449975354994";
	// client.get(url, new RequestParams(), responseHandler);
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
}
