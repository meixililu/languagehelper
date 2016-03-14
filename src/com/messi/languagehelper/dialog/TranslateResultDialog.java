package com.messi.languagehelper.dialog;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.messi.languagehelper.R;
import com.messi.languagehelper.dao.Dictionary;
import com.messi.languagehelper.util.SDCardUtil;
import com.messi.languagehelper.util.ScreenUtil;
import com.messi.languagehelper.util.XFUtil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

public class TranslateResultDialog extends Dialog {
	
	private Context context;
	private Dictionary bean;
	private SpeechSynthesizer mSpeechSynthesizer;
	private SharedPreferences mSharedPreferences;
	private FloatingActionButton play_btn;
	
	public TranslateResultDialog(Context context, int theme) {
	    super(context, theme);
	    this.context = context;
	}

	public TranslateResultDialog(Context context) {
	    super(context);
	    this.context = context;
	}

	/**
	 * 更改TextView的提示内容
	 * @param context
	 * @param theme
	 * @param tempText
	 */
	public TranslateResultDialog(Context context, Dictionary bean) {
		super(context, R.style.mydialog);
		this.context = context;
		this.bean = bean;
		mSharedPreferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
		mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(context, null);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.dialog_translate_result);
	    setFullScreen();
	    FrameLayout close_layout = (FrameLayout) findViewById(R.id.close_layout);
	    play_btn = (FloatingActionButton) findViewById(R.id.play_btn);
	    TextView result = (TextView) findViewById(R.id.result);
	    TextView title = (TextView) findViewById(R.id.title);
	    
		title.setText(bean.getWord_name());
	    result.setText(bean.getResult());
	    close_layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TranslateResultDialog.this.dismiss();
			}
		});
	    play_btn.setOnClickListener(new View.OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
	    		play();
	    	}
	    });
	}
	
	private void play(){
		if(mSpeechSynthesizer.isSpeaking()){
			mSpeechSynthesizer.stopSpeaking();
			play_btn.setImageResource(R.drawable.ic_play_arrow_white_48dp);
		}else{
			String path = SDCardUtil.getDownloadPath(SDCardUtil.sdPath);
			if(TextUtils.isEmpty(bean.getResultVoiceId()) || TextUtils.isEmpty(bean.getQuestionVoiceId())){
				bean.setQuestionVoiceId(System.currentTimeMillis() + "");
				bean.setResultVoiceId(System.currentTimeMillis()-5 + "");
			}
			String filepath = path + bean.getResultVoiceId() + ".pcm";
			bean.setResultAudioPath(filepath);
			mSpeechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, filepath);
			play_btn.setImageResource(R.drawable.ic_stop_white_48dp);
			XFUtil.showSpeechSynthesizer(context,mSharedPreferences,mSpeechSynthesizer,
					bean.getBackup1(),
					XFUtil.SpeakerEn,new SynthesizerListener() {
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
					play_btn.setImageResource(R.drawable.ic_play_arrow_white_48dp);
				}
				@Override
				public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
				}
				@Override
				public void onEvent(int arg0, int arg1, int arg2,Bundle arg3) {
				}
			});
		}
	}
	
	private void setFullScreen(){
		WindowManager windowManager = this.getWindow().getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		lp.width = display.getWidth() - ScreenUtil.dip2px(context, 40); //设置宽度
		this.getWindow().setAttributes(lp);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if(mSpeechSynthesizer.isSpeaking()){
			mSpeechSynthesizer.stopSpeaking();
			mSpeechSynthesizer = null;
		}
	}
	
}
