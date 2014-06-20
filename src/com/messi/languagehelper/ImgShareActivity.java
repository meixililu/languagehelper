package com.messi.languagehelper;

import java.io.File;
import java.io.IOException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.baidu.mobstat.StatService;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.SDCardUtil;
import com.messi.languagehelper.util.ToastUtil;

public class ImgShareActivity extends BaseActivity implements OnClickListener {

	private EditText share_content;
	private FrameLayout share_btn_cover;
	private LinearLayout parent_layout;
	private TextView share_foot;
	private String shareContent;
	protected SoundPool mSoundPoll;
	private int mSoundId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.share_layout);
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init() {
		Intent intent = getIntent();
		shareContent = intent.getStringExtra(KeyUtil.ShareContentKey);
		mActionBar.setTitle(getResources().getString(R.string.title_share_preview));
        parent_layout = (LinearLayout)findViewById(R.id.parent_layout);
        share_content = (EditText) findViewById(R.id.share_content);
        share_foot = (TextView) findViewById(R.id.share_foot);
        share_btn_cover = (FrameLayout) findViewById(R.id.share_btn_cover);
        mSoundPoll = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        mSoundId = mSoundPoll.load(this, R.raw.camera, 1);
        
        if(!TextUtils.isEmpty(shareContent)){
        	share_content.setText(shareContent);
        }
        share_btn_cover.setOnClickListener(this);
        share_foot.setOnClickListener(this);
	}
	
	private void shareWithImg() throws IOException{
		share_content.setFocusable(false);
		parent_layout.setDrawingCacheEnabled(true);
		parent_layout.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		parent_layout.layout(0, 0, parent_layout.getMeasuredWidth(), parent_layout.getMeasuredHeight());
		parent_layout.buildDrawingCache();
		Bitmap bitmap = parent_layout.getDrawingCache();
		if(bitmap !=  null){
			String imgPath = SDCardUtil.saveBitmap(this, bitmap);
			File file = new File(imgPath);    
			if (file != null && file.exists() && file.isFile()) {    
				Uri uri = Uri.fromFile(file);    
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("image/png");    
				intent.putExtra(Intent.EXTRA_STREAM, uri); 
				intent.putExtra(Intent.EXTRA_SUBJECT, this.getResources().getString(R.string.share));  
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
				this.startActivity(Intent.createChooser(intent, this.getResources().getString(R.string.share))); 
			}    
		}else{
			LogUtil.DefalutLog("bitmap == null");
		}
		share_content.setFocusable(true);
		parent_layout.requestLayout();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,0,0,this.getResources().getString(R.string.menu_share))
		.setIcon(R.drawable.icon_share)
		.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:  
			share();
			StatService.onEvent(this, "1.8_menu_to_share_activity", "去自定义分享页面", 1);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.share_btn_cover:
			share();
			break;
		case R.id.share_foot:
			share_foot.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}
	
	private void playMusic() {
		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = actualVolume / maxVolume;
		mSoundPoll.play(mSoundId, volume, volume, 1, 0, 1f);
	}
	
	private void share(){
		try {
			playMusic();
			shareContent = share_content.getText().toString();
			if(!TextUtils.isEmpty(shareContent)){
				shareWithImg();
			}else{
				ToastUtil.diaplayMesShort(ImgShareActivity.this, R.string.share_text_hint);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
