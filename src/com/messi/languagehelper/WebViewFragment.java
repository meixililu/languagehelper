package com.messi.languagehelper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockFragment;
import com.messi.languagehelper.util.Settings;


public class WebViewFragment extends SherlockFragment{
	
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private WebView mWebView;
	private View view;
	private boolean isHasLoadPage;
    public static WebViewFragment mMainFragment;
    
    public static WebViewFragment getInstance(){
		if(mMainFragment == null){
			mMainFragment = new WebViewFragment();
		}
		return mMainFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.web_view, null);
		initViews();
		return view;
	}
	
	@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
        	if (!isHasLoadPage) {
        		isHasLoadPage = true;
        		if(mWebView != null){
        			mWebView.loadUrl(Settings.CaiLingUrl);
        		}
        	}
        }
	}
	
	private void initViews(){
		mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
		mWebView = (WebView) view.findViewById(R.id.refreshable_webview);
		mWebView.requestFocus();//如果不设置，则在点击网页文本输入框时，不能弹出软键盘及不响应其他的一些事件。
		mWebView.getSettings().setJavaScriptEnabled(true);//如果访问的页面中有Javascript，则webview必须设置支持Javascript。
		
		//当前页面加载
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				mSwipeRefreshLayout.setRefreshing(true);
				super.onPageStarted(view, url, favicon);
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				mSwipeRefreshLayout.setRefreshing(false);
				super.onPageFinished(view, url);
			}
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				mWebView.loadUrl(url);
				return true;
			}
		});
		mSwipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright, 
	            R.color.holo_green_light, 
	            R.color.holo_orange_light, 
	            R.color.holo_red_light);
		mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				mWebView.reload();
			}
		});
	}
	
//	shareLink(WebViewFragment.this.getResources().getString(R.string.cailing_ad_prompt));
	private void shareLink(String dstString){
		Intent intent = new Intent(Intent.ACTION_SEND);    
		intent.setType("text/plain"); // 纯文本     
		intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.share));    
		intent.putExtra(Intent.EXTRA_TEXT, dstString);    
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
		startActivity(Intent.createChooser(intent, getResources().getString(R.string.share)));    
	}
	
}
