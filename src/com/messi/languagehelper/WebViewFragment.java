package com.messi.languagehelper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.baidu.mobstat.StatService;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshWebView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.messi.languagehelper.CollectedFragment.WaitTask;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.Settings;


public class WebViewFragment extends SherlockFragment{
	
	private ProgressBar mProgressBar;
	private PullToRefreshWebView mWebView;
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
        			mWebView.getRefreshableView().loadUrl(Settings.CaiLingUrl);
        		}
        	}
        }
	}
	
	private void initViews(){
		mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar_m);
		mWebView = (PullToRefreshWebView) view.findViewById(R.id.refreshable_webview);
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
