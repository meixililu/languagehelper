package com.messi.languagehelper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.baidu.mobstat.StatService;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.ToastUtil;


public class WebViewActivity extends Activity{
//	private static final int finish = 0x001;
	public static final String URL = "url";
	
	private ProgressBar mProgressBar;

	private WebView mWebView;
	private double time = 56*1000;
	
	private String url;
	
	private Thread over;
	private boolean isThread;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_view);
		url = getIntent().getStringExtra(URL);
		mProgressBar = (ProgressBar) findViewById(R.id.web_progress);
		mWebView = (WebView) findViewById(R.id.web_view);
		mWebView.requestFocus();//如果不设置，则在点击网页文本输入框时，不能弹出软键盘及不响应其他的一些事件。
		mWebView.getSettings().setJavaScriptEnabled(true);//如果访问的页面中有Javascript，则webview必须设置支持Javascript。

		overtime();//初始化线程
		//当前页面加载
		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				mProgressBar.setVisibility(View.VISIBLE);
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				mProgressBar.setVisibility(View.GONE);
				super.onPageFinished(view, url);
			}

			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(URLUtil.isNetworkUrl(url)){
					mWebView.loadUrl(url);
				}else{
					ToastUtil.diaplayMesLong(getApplicationContext(), "链接无效");
					finish();
				}
				return true;
			}

		});
		
		mWebView.setWebChromeClient(new WebChromeClient(){

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				mProgressBar.setProgress(newProgress);
				if(newProgress < 100){
					isThread = true;
				}else {
					isThread = false;
				}
				super.onProgressChanged(view, newProgress);
				
			}
			
		});
		
		//判断URL 是否为有效连接
		if(!TextUtils.isEmpty(url) || !url.equals("null")){
			if(URLUtil.isNetworkUrl(url)){
				mWebView.loadUrl(url);
			}else{
				ToastUtil.diaplayMesLong(getApplicationContext(), "链接无效");
				finish();
			}
		}else{
			ToastUtil.diaplayMesLong(getApplicationContext(), "链接为空");
			finish();
		}
		
	}
	
	private void overtime(){
		over = new Thread() {
			public void run() {
				while (isThread) {
					try {
						LogUtil.CustomLog("TAG", "执行线程");
						if (time > 0){
							time -= 200;
						}else{
							Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
							
							startActivity(intent);
							finish();
						}
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		over.start();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if((keyCode == KeyEvent.KEYCODE_BACK ) && mWebView.canGoBack()){
			mWebView.goBack();
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();
		StatService.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		StatService.onPause(this);
	}
	
}
