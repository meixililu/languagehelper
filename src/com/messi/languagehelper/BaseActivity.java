package com.messi.languagehelper;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.baidu.mobstat.StatService;
import com.messi.languagehelper.util.AudioTrackUtil;
import com.messi.languagehelper.util.ScreenUtil;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;

public class BaseActivity extends ActionBarActivity {

	public Toolbar toolbar;
	private View mScrollable;
	
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
    }
	
	protected Toolbar getActionBarToolbar() {
        if (toolbar == null) {
        	toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            if(VERSION.SDK_INT >= VERSION_CODES.KITKAT) {
            	toolbar.setPadding(0, ScreenUtil.dip2px(this, 5), 0, 0);
            }
        }
        return toolbar;
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
