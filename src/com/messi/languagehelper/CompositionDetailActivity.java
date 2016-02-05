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
import com.messi.languagehelper.util.XFUtil;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
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
		content.setText(mAVObject.getString(AVOUtil.Composition.content));
		fab.setOnClickListener(this);
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == MyThread.EVENT_PLAY_OVER){
				
			}
		}
	};
	
	private void playContent(){
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
	public void onClick(View v) {
		if(v.getId() == R.id.play_btn){
			playContent();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AudioTrackUtil.stopPlayOnline(mSpeechSynthesizer);
		AudioTrackUtil.stopPlayPcm(mThread);
	}
	
}
