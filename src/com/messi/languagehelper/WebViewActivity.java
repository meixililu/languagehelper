package com.messi.languagehelper;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.gc.materialdesign.views.ProgressBarDetermininate;
import com.messi.languagehelper.observablescrollview.ObservableScrollViewCallbacks;
import com.messi.languagehelper.observablescrollview.ObservableWebView;
import com.messi.languagehelper.observablescrollview.ScrollState;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.ShareUtil;


public class WebViewActivity extends BaseActivity implements ObservableScrollViewCallbacks{
	
	private ProgressBarDetermininate progressdeterminate;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private ObservableWebView mWebView;
	private TextView tap_to_reload;
    private String Url;
    private String title;
    private boolean isReedPullDownRefresh;
    private float mActionBarHeight;
    private long lastClick;

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
		isReedPullDownRefresh = getIntent().getBooleanExtra(KeyUtil.IsReedPullDownRefresh, true);
		if(TextUtils.isEmpty(title)){
			title = getResources().getString(R.string.app_name);
		}
		getSupportActionBar().setTitle(title);
		
		final TypedArray styledAttributes = getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize });
		mActionBarHeight = styledAttributes.getDimension(0, 0);
    	styledAttributes.recycle(); 
    	
    	LogUtil.DefalutLog("mActionBarHeight:"+mActionBarHeight);
	}
	
	private void initViews(){
		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
		progressdeterminate = (ProgressBarDetermininate) findViewById(R.id.progressdeterminate);
		mWebView = (ObservableWebView) findViewById(R.id.refreshable_webview);
		tap_to_reload = (TextView) findViewById(R.id.tap_to_reload);
		setScrollable(mSwipeRefreshLayout);
		mWebView.requestFocus();//如果不设置，则在点击网页文本输入框时，不能弹出软键盘及不响应其他的一些事件。
		mWebView.getSettings().setJavaScriptEnabled(true);//如果访问的页面中有Javascript，则webview必须设置支持Javascript。
		mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		mWebView.requestFocus();
		mWebView.setScrollViewCallbacks(this);
		
		tap_to_reload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mWebView.loadUrl(Url);
				tap_to_reload.setVisibility(View.GONE);
				lastClick = System.currentTimeMillis();
			}
		});
		//当前页面加载
		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				LogUtil.DefalutLog("WebViewClient:onPageStarted");
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				view.loadUrl("");
				if(System.currentTimeMillis() - lastClick < 500){
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							tap_to_reload.setVisibility(View.VISIBLE);
						}
					}, 600);
				}else{
					tap_to_reload.setVisibility(View.VISIBLE);
				}
				LogUtil.DefalutLog("WebViewClient:onReceivedError---"+errorCode);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				mSwipeRefreshLayout.setRefreshing(false);
				LogUtil.DefalutLog("WebViewClient:onPageFinished");
			}

			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				mWebView.loadUrl(url);
				return true;
			}

		});
		
		mWebView.setWebChromeClient(new WebChromeClient() {
	        @Override
	        public void onProgressChanged(WebView view, int newProgress) {
	            if (newProgress == 100) {
	            	progressdeterminate.setVisibility(View.GONE);
	            	mSwipeRefreshLayout.setRefreshing(false);
	            	LogUtil.DefalutLog("WebViewClient:newProgress == 100");
	            } else {
	                if (progressdeterminate.getVisibility() == View.GONE)
	                	progressdeterminate.setVisibility(View.VISIBLE);
	                progressdeterminate.setProgress(newProgress);
	            }
	            super.onProgressChanged(view, newProgress);
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
		if(!isReedPullDownRefresh){
			mSwipeRefreshLayout.setEnabled(false);
		}
		mWebView.loadUrl(Url);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,0,0,this.getResources().getString(R.string.menu_share)).setIcon(R.drawable.ic_share_white_36dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:  
			if(Url.equals(Settings.CaiLingUrl)){
				ShareUtil.shareLink(WebViewActivity.this,WebViewActivity.this.getResources().getString(R.string.cailing_ad_prompt));
				StatService.onEvent(this, "1.9_menu_to_share_link", "分享彩铃链接", 1);
			}else if(Url.equals(Settings.YueduUrl)){
				ShareUtil.shareLink(WebViewActivity.this,WebViewActivity.this.getResources().getString(R.string.yuedu_ad_prompt));
				StatService.onEvent(this, "20_menu_to_share_yuedu_link", "分享阅读链接", 1);
			}else if(Url.equals(Settings.HotalUrl)){
				ShareUtil.shareLink(WebViewActivity.this,WebViewActivity.this.getResources().getString(R.string.hotel_ad_prompt));
				StatService.onEvent(this, "20_menu_to_share_hotel_link", "分享订酒店链接", 1);
			}else if(Url.equals(Settings.InstagramUrl)){
				ShareUtil.shareLink(WebViewActivity.this,WebViewActivity.this.getResources().getString(R.string.instagram_pinsta));
			}else if(Url.equals(Settings.MsnbcUrl)){
				ShareUtil.shareLink(WebViewActivity.this,WebViewActivity.this.getResources().getString(R.string.website_msnbc));
			}else if(Url.equals(Settings.BbcNewsUrl)){
				ShareUtil.shareLink(WebViewActivity.this,WebViewActivity.this.getResources().getString(R.string.website_bbc_news));
			}else if(Url.equals(Settings.BbcLearningenglishUrl)){
				ShareUtil.shareLink(WebViewActivity.this,WebViewActivity.this.getResources().getString(R.string.website_bbc_learning));
			}else if(Url.equals(Settings.EngadgetUrl)){
				ShareUtil.shareLink(WebViewActivity.this,WebViewActivity.this.getResources().getString(R.string.website_engadget));
			}else if(Url.equals(Settings.PhonedogUrl)){
				ShareUtil.shareLink(WebViewActivity.this,WebViewActivity.this.getResources().getString(R.string.website_phonedog));
			}
			break;
		}
       return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if((keyCode == KeyEvent.KEYCODE_BACK ) && mWebView.canGoBack()){
			mWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
//		if(mWebView != null){
//			mWebView.destroy();
//		}
	}

	@Override
	public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
		
	}

	@Override
	public void onDownMotionEvent() {
		
	}

	@Override
	public void onUpOrCancelMotionEvent(ScrollState scrollState) {
		if (scrollState == ScrollState.UP) {
            if (toolbarIsShown()) {
            	if(mWebView.getScrollY() != 0){
            		hideToolbar();
            	}
            }
            progressdeterminate.setVisibility(View.GONE);
            LogUtil.DefalutLog("onUpOrCancelMotionEvent-getScrollY:"+mWebView.getScrollY());
            LogUtil.DefalutLog("onUpOrCancelMotionEvent:ScrollState.UP");
        } else if (scrollState == ScrollState.DOWN) {
        	if(mWebView.getContentHeight()*mWebView.getScale()-(mWebView.getHeight()+mWebView.getScrollY()) != 0){
        		if (toolbarIsHidden()) {
        			showToolbar();
        		}
        	}
            LogUtil.DefalutLog("onUpOrCancelMotionEvent:ScrollState.DOWN");
        }
	}
	
}
