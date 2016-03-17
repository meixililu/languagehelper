package com.messi.languagehelper;

import com.avos.avoscloud.AVAnalytics;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.ShareUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class MoreActivity extends BaseActivity implements OnClickListener {

	private FrameLayout setting_layout,costom_share_layout,comments_layout;
	private FrameLayout help_layout,about_layout,invite_layout,qrcode_layout;
	private ImageView unread_dot_setting;
	private SharedPreferences mSharedPreferences;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_activity);
		init();
	}

	private void init() {
		getSupportActionBar().setTitle(getResources().getString(R.string.title_more));
		mSharedPreferences = Settings.getSharedPreferences(this);
		setting_layout = (FrameLayout) findViewById(R.id.setting_layout);
		costom_share_layout = (FrameLayout) findViewById(R.id.costom_share_layout);
		comments_layout = (FrameLayout) findViewById(R.id.comments_layout);
		help_layout = (FrameLayout) findViewById(R.id.help_layout);
		about_layout = (FrameLayout) findViewById(R.id.about_layout);
		invite_layout = (FrameLayout) findViewById(R.id.invite_layout);
		qrcode_layout = (FrameLayout) findViewById(R.id.qrcode_layout);
		unread_dot_setting = (ImageView) findViewById(R.id.unread_dot_setting);
		
		if(!mSharedPreferences.getBoolean(KeyUtil.IsShowSettingNewAdd, false)){
			unread_dot_setting.setVisibility(View.VISIBLE);
		}else{
			unread_dot_setting.setVisibility(View.GONE);
		}
		
		setting_layout.setOnClickListener(this);
		costom_share_layout.setOnClickListener(this);
		comments_layout.setOnClickListener(this);
		help_layout.setOnClickListener(this);
		about_layout.setOnClickListener(this);
		invite_layout.setOnClickListener(this);
		qrcode_layout.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setting_layout:
			unread_dot_setting.setVisibility(View.GONE);
			Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.IsShowSettingNewAdd, true);
			toActivity(SettingActivity.class, null);
			AVAnalytics.onEvent(MoreActivity.this, "more_pg_tosettingpg_btn");
			break;
		case R.id.costom_share_layout:
			toActivity(ImgShareActivity.class, null);
			break;
		case R.id.comments_layout:
			try{
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id=com.messi.languagehelper"));
				MoreActivity.this.startActivity(intent);
				AVAnalytics.onEvent(MoreActivity.this, "more_pg_tocommendpg_btn");
			}catch(Exception e){
				e.printStackTrace();
			}
			break;
		case R.id.help_layout:
			toActivity(HelpActivity.class, null);
			AVAnalytics.onEvent(MoreActivity.this, "more_pg_tohelppg_btn");
			break;
		case R.id.about_layout:
			toActivity(AboutActivity.class, null);
			AVAnalytics.onEvent(MoreActivity.this, "more_pg_toaboutpg_btn");
			break;
		case R.id.invite_layout:
			ShareUtil.shareLink(MoreActivity.this,MoreActivity.this.getResources().getString(R.string.invite_friends_prompt));
			AVAnalytics.onEvent(this, "more_pg_invite_btn", "邀请小伙伴", 1);
			break;
		case R.id.qrcode_layout:
			toActivity(QRCodeShareActivity.class, null);
			AVAnalytics.onEvent(MoreActivity.this, "more_pg_qrcode_btn");
			break;
		default:
			break;
		}
	}
}
