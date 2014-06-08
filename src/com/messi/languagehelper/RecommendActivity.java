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

public class RecommendActivity extends BaseActivity implements OnClickListener {

	private FrameLayout recommend_yyzs,recommend_zrzs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recommend_activity);
		init();
	}
	
	private void init() {
        mActionBar.setTitle(this.getResources().getString(R.string.title_apps));
        
        recommend_yyzs = (FrameLayout) findViewById(R.id.recommend_yyzs);
        recommend_zrzs = (FrameLayout) findViewById(R.id.recommend_zrzs);
        recommend_yyzs.setOnClickListener(this);
        recommend_zrzs.setOnClickListener(this);
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
		case R.id.recommend_zrzs:
			try {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id=com.messi.languagehelper_ja"));
				startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
			StatService.onEvent(RecommendActivity.this, "1.8_zrzsbtn", "点击中日助手按钮", 1);
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
