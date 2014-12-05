package com.messi.languagehelper;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.baidu.mobstat.StatService;
import com.messi.languagehelper.observablescrollview.ObservableScrollViewCallbacks;
import com.messi.languagehelper.observablescrollview.ObservableWebView;
import com.messi.languagehelper.observablescrollview.ScrollState;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.Settings;
import com.nineoldandroids.animation.ObjectAnimator;


public class WebViewActivity extends BaseActivity implements ObservableScrollViewCallbacks{
	
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private ObservableWebView mWebView;
    private String Url;
    private String title;
    private boolean isReedPullDownRefresh;
    private float mActionBarHeight;
    private int mQuickReturnHeight;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_view);
		initData();
		initViews();
	}
	
	private void initData(){
		toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
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
		mWebView = (ObservableWebView) findViewById(R.id.refreshable_webview);
		mWebView.requestFocus();//如果不设置，则在点击网页文本输入框时，不能弹出软键盘及不响应其他的一些事件。
		mWebView.getSettings().setJavaScriptEnabled(true);//如果访问的页面中有Javascript，则webview必须设置支持Javascript。
		mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		mWebView.requestFocus();
		mWebView.setScrollViewCallbacks(this);
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
		
		toolbar.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						mQuickReturnHeight = toolbar.getHeight();
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
		
//		mWebView.getViewTreeObserver().addOnScrollChangedListener(new OnScrollChangedListener() {
//			@Override
//			public void onScrollChanged() {
//				float y = mWebView.getScrollY();
//				if (y >= mActionBarHeight && getSupportActionBar().isShowing()) {
//					getSupportActionBar().hide();
//				} else if ( y==0 && !getSupportActionBar().isShowing()) {
//					getSupportActionBar().show();
//				}
//			}
//		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,0,0,this.getResources().getString(R.string.menu_share)).setIcon(R.drawable.icon_share).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:  
			if(Url.equals(Settings.CaiLingUrl)){
				shareLink(WebViewActivity.this.getResources().getString(R.string.cailing_ad_prompt));
				StatService.onEvent(this, "1.9_menu_to_share_link", "分享彩铃链接", 1);
			}else if(Url.equals(Settings.YueduUrl)){
				shareLink(WebViewActivity.this.getResources().getString(R.string.yuedu_ad_prompt));
				StatService.onEvent(this, "20_menu_to_share_yuedu_link", "分享阅读链接", 1);
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
	
	private void shareLink(String dstString){
		Intent intent = new Intent(Intent.ACTION_SEND);    
		intent.setType("text/plain"); // 纯文本     
		intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.share));    
		intent.putExtra(Intent.EXTRA_TEXT, dstString);    
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
		startActivity(Intent.createChooser(intent, getResources().getString(R.string.share)));    
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
		ActionBar mActionBar = getSupportActionBar();
        if (scrollState == ScrollState.UP) {
            if (mActionBar.isShowing()) {
//            	toolbar.s
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!mActionBar.isShowing()) {
                
            }
        }
	}
	
	private void hideActionbar(){
		ObjectAnimator itemAnimator1 = ObjectAnimator.ofFloat(toolbar, "y", toolbar.getBottom(), mQuickReturnHeight);
		itemAnimator1.setDuration(250).start();
		getSupportActionBar().hide();
		
	}
	
}
