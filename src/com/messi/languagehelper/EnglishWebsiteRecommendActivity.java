package com.messi.languagehelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import com.baidu.mobstat.StatService;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.Settings;

public class EnglishWebsiteRecommendActivity extends BaseActivity implements OnClickListener {

	private FrameLayout english_website_layout1,english_website_layout2,english_website_layout3;
	private FrameLayout english_website_layout4,english_website_layout5,english_website_layout6;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.english_website_recommend_activity);
		initViews();
	}
	
	private void initViews(){
		getSupportActionBar().setTitle(getResources().getString(R.string.title_website));
		english_website_layout1 = (FrameLayout)findViewById(R.id.english_website_layout1);
		english_website_layout2 = (FrameLayout)findViewById(R.id.english_website_layout2);
		english_website_layout3 = (FrameLayout)findViewById(R.id.english_website_layout3);
		english_website_layout4 = (FrameLayout)findViewById(R.id.english_website_layout4);
		english_website_layout5 = (FrameLayout)findViewById(R.id.english_website_layout5);
		english_website_layout6 = (FrameLayout)findViewById(R.id.english_website_layout6);
		english_website_layout1.setOnClickListener(this);
		english_website_layout2.setOnClickListener(this);
		english_website_layout3.setOnClickListener(this);
		english_website_layout4.setOnClickListener(this);
		english_website_layout5.setOnClickListener(this);
		english_website_layout6.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.english_website_layout1){
			toActivity(Settings.InstagramUrl, R.string.instagram_pinsta);
			StatService.onEvent(this, "website_instagram", "浏览Instagram", 1);
			
		}else if(v.getId() == R.id.english_website_layout2){
			toActivity(Settings.MsnbcUrl, R.string.website_msnbc);
			StatService.onEvent(this, "website_msnbc", "浏览msnbc", 1);
			
		}else if(v.getId() == R.id.english_website_layout3){
			toActivity(Settings.BbcNewsUrl, R.string.website_bbc_news);
			StatService.onEvent(this, "website_bbcnews", "浏览bbcnews", 1);
			
		}else if(v.getId() == R.id.english_website_layout4){
			toActivity(Settings.BbcLearningenglishUrl, R.string.website_bbc_learning);
			StatService.onEvent(this, "website_bbc_learning", "浏览bbc_learning", 1);
			
		}else if(v.getId() == R.id.english_website_layout5){
			toActivity(Settings.EngadgetUrl, R.string.website_engadget);
			StatService.onEvent(this, "website_engadget", "浏览engadget", 1);
			
		}else if(v.getId() == R.id.english_website_layout6){
			toActivity(Settings.PhonedogUrl, R.string.website_phonedog);
			StatService.onEvent(this, "website_phonedog", "浏览phonedog", 1);
			
		}
	}
	
	private void toActivity(String url, int title){
		Intent intent = new Intent(this,WebViewActivity.class);
		intent.putExtra(KeyUtil.URL, url);
		intent.putExtra(KeyUtil.ActionbarTitle, this.getResources().getString(title));
		this.startActivity(intent);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
