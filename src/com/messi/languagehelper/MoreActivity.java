package com.messi.languagehelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.baidu.mobstat.StatService;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.ShareUtil;

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
			StatService.onEvent(MoreActivity.this, "menu_page_settingbtn","去应用设置按钮", 1);
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
				StatService.onEvent(MoreActivity.this, "menu_page_commend","去吐槽评价按钮", 1);
			}catch(Exception e){
				e.printStackTrace();
			}
			break;
		case R.id.help_layout:
			toActivity(HelpActivity.class, null);
			StatService.onEvent(MoreActivity.this, "menu_page_help","去使用帮助按钮", 1);
			break;
		case R.id.about_layout:
			toActivity(AboutActivity.class, null);
			StatService.onEvent(MoreActivity.this, "menu_page_aboutus","去关于我们按钮", 1);
			break;
		case R.id.invite_layout:
			ShareUtil.shareLink(MoreActivity.this,MoreActivity.this.getResources().getString(R.string.invite_friends_prompt));
			StatService.onEvent(this, "setting_page_invite_friends", "邀请小伙伴", 1);
			break;
		case R.id.qrcode_layout:
			toActivity(QRCodeShareActivity.class, null);
			StatService.onEvent(MoreActivity.this, "setting_page_qrcode", "二维码", 1);
			break;
		default:
			break;
		}
	}
}
