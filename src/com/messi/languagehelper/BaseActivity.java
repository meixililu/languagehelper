package com.messi.languagehelper;

import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.baidu.mobstat.StatService;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.messi.languagehelper.util.AudioTrackUtil;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.ScreenUtil;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;

public class BaseActivity extends ActionBarActivity {

	public Toolbar toolbar;
	private View mScrollable;
	public ProgressBarCircularIndeterminate mProgressbar;
	public SwipeRefreshLayout mSwipeRefreshLayout;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TransparentStatusbar();
	}
	
	protected void TransparentStatusbar(){
		if(VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}
	}
	
	@Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
        initProgressbar();
    }
	
	protected void getActionBarToolbar() {
        if (toolbar == null) {
        	toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                if(VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
                	toolbar.setPadding(0, ScreenUtil.dip2px(this, 6), 0, 0);
                }
            }
            String title = getIntent().getStringExtra(KeyUtil.ActionbarTitle);
            if(!TextUtils.isEmpty(title)){
            	getSupportActionBar().setTitle(title);
            }
        }
    }
	
	protected void toActivity(Class mClass,Bundle bundle){
		Intent intent = new Intent(this,mClass);
		if(bundle != null){
			intent.putExtra(KeyUtil.BundleKey, bundle);
		}
		startActivity(intent);
	}
	
	protected void initProgressbar(){
		if(mProgressbar == null){
			mProgressbar = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndetermininate);
		}
	}
	
	/**
	 * need init beford use
	 */
	protected void initSwipeRefresh(){
		if(mSwipeRefreshLayout == null){
			mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mswiperefreshlayout);
			mSwipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright, 
		            R.color.holo_green_light, 
		            R.color.holo_orange_light, 
		            R.color.holo_red_light);
			mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
				@Override
				public void onRefresh() {
					onSwipeRefreshLayoutRefresh();
				}
			});
		}
	}
	
	public void onSwipeRefreshLayoutFinish(){
		if(mSwipeRefreshLayout != null){
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}
	
	public void onSwipeRefreshLayoutRefresh(){
	}
	
	public void showProgressbar(){
		if(mProgressbar != null){
			mProgressbar.setVisibility(View.VISIBLE);
		}
	}
	
	public void hideProgressbar(){
		if(mProgressbar != null){
			mProgressbar.setVisibility(View.GONE);
		}
	}
	
	protected int getScreenHeight() {
        return findViewById(android.R.id.content).getHeight();
    }
	
	protected void setScrollable(View s){
		mScrollable = s;
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
		}
       return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			 AudioTrackUtil.adjustStreamVolume(BaseActivity.this,keyCode);
	         return true;
	    case KeyEvent.KEYCODE_VOLUME_DOWN:
	    	 AudioTrackUtil.adjustStreamVolume(BaseActivity.this,keyCode);
		     return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	protected boolean toolbarIsShown() {
        return ViewHelper.getTranslationY(toolbar) == 0;
    }

	protected boolean toolbarIsHidden() {
        return ViewHelper.getTranslationY(toolbar) == -toolbar.getHeight();
    }
	
	protected void hideViews() {
		ObjectAnimator animY = ObjectAnimator.ofFloat(toolbar, "y", -toolbar.getHeight());
    	animY.setInterpolator(new AccelerateInterpolator(2));
    	animY.start();
    }

	protected void showViews() {
    	ObjectAnimator animY = ObjectAnimator.ofFloat(toolbar, "y", 0);
    	animY.setInterpolator(new DecelerateInterpolator(2));
    	animY.start();
    }
    
	protected void showToolbar() {
        moveToolbar(0);
    }

	protected void hideToolbar() {
		moveToolbar(-toolbar.getHeight());
    }
    
    private void moveToolbar(float toTranslationY) {
        ValueAnimator animator = ValueAnimator.ofFloat(ViewHelper.getTranslationY(toolbar), toTranslationY).setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float translationY = (Float) animation.getAnimatedValue();
                ViewHelper.setTranslationY(toolbar, translationY);
                ViewHelper.setTranslationY((View) mScrollable, translationY);
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) ((View) mScrollable).getLayoutParams();
                lp.height = (int) -translationY + getScreenHeight() - lp.topMargin;
                ((View) mScrollable).requestLayout();
            }
        });
        animator.start();
    }
}
