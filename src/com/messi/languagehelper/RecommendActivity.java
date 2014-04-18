package com.messi.languagehelper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.baidu.mobstat.StatService;
import com.messi.languagehelper.util.ToastUtil;
import com.messi.languagehelper.wxapi.WXEntryActivity;

public class RecommendActivity extends SherlockFragmentActivity implements OnClickListener {

	private FrameLayout recommend_yyzs,recommend_zyzs;
	public ActionBar mActionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recommend_activity);
		init();
	}
	
	private void init() {
		mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(getResources().getDrawable(R.color.load_blue));
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setTitle("推荐应用");
        
        recommend_yyzs = (FrameLayout) findViewById(R.id.recommend_yyzs);
        recommend_zyzs = (FrameLayout) findViewById(R.id.recommend_zyzs);
        recommend_yyzs.setOnClickListener(this);
        recommend_zyzs.setOnClickListener(this);
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.recommend_zyzs:
			try {
				ToastUtil.diaplayMesShort(RecommendActivity.this, "预计5月初发布");
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id=com.messi.chtoenhelper"));
				startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
			StatService.onEvent(RecommendActivity.this, "1.6_zyzsbtn", "点击中英助手按钮", 1);
			break;
		case R.id.recommend_yyzs:
			try {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id=com.messi.cantonese.study"));
				startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
			StatService.onEvent(RecommendActivity.this, "1.6_yyzsbtn", "点击粤语助手按钮", 1);
			break;
		default:
			break;
		}
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
