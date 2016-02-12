package com.messi.languagehelper;

import com.avos.avoscloud.AVObject;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.messi.languagehelper.task.MyThread;
import com.messi.languagehelper.util.AVOUtil;
import com.messi.languagehelper.util.AudioTrackUtil;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.SDCardUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.TextHandlerUtil;
import com.messi.languagehelper.util.XFUtil;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class CompositionDetailActivity extends BaseActivity implements OnClickListener{

	private TextView title,content;
	private AVObject mAVObject;
	private FloatingActionButton fab;
	
	private SpeechSynthesizer mSpeechSynthesizer;
	private SharedPreferences mSharedPreferences;
	private Thread mThread;
	private MyThread mMyThread;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.composition_detail_activity);
		initData();
		initViews();
	}
	
	private void initData(){
		mAVObject = (AVObject) BaseApplication.dataMap.get(KeyUtil.DataMapKey);
		BaseApplication.dataMap.clear();
		if(mAVObject == null){
			finish();
		}
	}
	
	private void initViews(){
		getSupportActionBar().setTitle(mAVObject.getString(AVOUtil.Composition.type_name));
		title = (TextView) findViewById(R.id.title);
		content = (TextView) findViewById(R.id.content);
		fab = (FloatingActionButton) findViewById(R.id.play_btn);
		mSharedPreferences = this.getSharedPreferences(this.getPackageName(), Activity.MODE_PRIVATE);
		mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(this, null);
		mMyThread = new MyThread(mHandler);
		
		title.setText(mAVObject.getString(AVOUtil.Composition.title));
		TextHandlerUtil.handlerText(this, mProgressbar, content, mAVObject.getString(AVOUtil.Composition.content));
		fab.setOnClickListener(this);
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == MyThread.EVENT_PLAY_OVER){
				fab.setImageResource(R.drawable.ic_play_arrow_white_48dp);
			}
		}
	};
	
	private void playContent(){
		fab.setImageResource(R.drawable.ic_stop_white_48dp);
		String filepath = SDCardUtil.getDownloadPath(SDCardUtil.CompositionPath) + 
				String.valueOf(mAVObject.getNumber(AVOUtil.Composition.item_id)) + ".pcm";
		if(!AudioTrackUtil.isFileExists(filepath)){
			mSpeechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, filepath);
			XFUtil.showSpeechSynthesizer(this,mSharedPreferences,mSpeechSynthesizer,
					mAVObject.getString(AVOUtil.Composition.content),XFUtil.SpeakerEn,
					new SynthesizerListener() {
				@Override
				public void onSpeakResumed() {
				}
				@Override
				public void onSpeakProgress(int arg0, int arg1, int arg2) {
				}
				@Override
				public void onSpeakPaused() {
				}
				@Override
				public void onSpeakBegin() {
				}
				@Override
				public void onCompleted(SpeechError arg0) {
					fab.setImageResource(R.drawable.ic_play_arrow_white_48dp);
				}
				@Override
				public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
				}
				@Override
				public void onEvent(int arg0, int arg1, int arg2,Bundle arg3) {
				}
			});
		}else{
			mMyThread.setDataUri(filepath);
			mThread = AudioTrackUtil.startMyThread(mMyThread);
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.composition, menu);
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.action_share:
			copyOrshare(0);
			break;
		case R.id.action_copy:
			copyOrshare(1);
			break;
		}
       return true;
	}
	
	private void copyOrshare(int i){
		StringBuilder sb = new StringBuilder();
		sb.append(mAVObject.getString(AVOUtil.Composition.title));
		sb.append("\n");
		sb.append(mAVObject.getString(AVOUtil.Composition.content));
		if(i == 0){
			Settings.share(this, sb.toString());
		}else{
			Settings.copy(this, sb.toString());
		}
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.play_btn){
			if(!isPlaying()){
				playContent();
			}else{
				fab.setImageResource(R.drawable.ic_play_arrow_white_48dp);
			}
		}
	}
	
	private boolean isPlaying(){
		boolean isPlaying = false;
		if(mSpeechSynthesizer.isSpeaking()){
			mSpeechSynthesizer.stopSpeaking();
			isPlaying = true;
		}
		if(mMyThread.isPlaying){
			AudioTrackUtil.stopPlayPcm(mThread);
			isPlaying = true;
		}
		return isPlaying;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AudioTrackUtil.stopPlayOnline(mSpeechSynthesizer);
		AudioTrackUtil.stopPlayPcm(mThread);
	}
	
}
