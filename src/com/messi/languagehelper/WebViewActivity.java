package com.messi.languagehelper;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.handmark.pulltorefresh.library.PullToRefreshWebView;
import com.messi.languagehelper.util.KeyUtil;


public class WebViewActivity extends BaseActivity{
	
	private ProgressBar mProgressBar;
	private PullToRefreshWebView mWebView;
    private String Url;
    private String title;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_view);
		initData();
		initViews();
	}
	
	private void initData(){
		Url = getIntent().getStringExtra(KeyUtil.URL);
		title = getIntent().getStringExtra(KeyUtil.ActionbarTitle);
		if(TextUtils.isEmpty(title)){
			title = getResources().getString(R.string.app_name);
		}
		mActionBar.setTitle(title);
	}
	
	private void initViews(){
		mProgressBar = (ProgressBar) findViewById(R.id.progressbar_m);
		mWebView = (PullToRefreshWebView) findViewById(R.id.refreshable_webview);
		mWebView.requestFocus();//如果不设置，则在点击网页文本输入框时，不能弹出软键盘及不响应其他的一些事件。
		mWebView.getRefreshableView().getSettings().setJavaScriptEnabled(true);//如果访问的页面中有Javascript，则webview必须设置支持Javascript。
		
		//当前页面加载
		mWebView.getRefreshableView().setWebViewClient(new WebViewClient() {

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
				mWebView.getRefreshableView().loadUrl(url);
				return true;
			}

		});
		mWebView.getRefreshableView().loadUrl(Url);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if((keyCode == KeyEvent.KEYCODE_BACK ) && mWebView.getRefreshableView().canGoBack()){
			mWebView.getRefreshableView().goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
