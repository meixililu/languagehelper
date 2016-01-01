package com.messi.languagehelper;

import java.util.List;

import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.views.CheckBox;
import com.gc.materialdesign.widgets.Dialog;
import com.iflytek.cloud.EvaluatorListener;
import com.iflytek.cloud.EvaluatorResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvaluator;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.messi.languagehelper.result.Result;
import com.messi.languagehelper.task.MyThread;
import com.messi.languagehelper.task.PublicTask;
import com.messi.languagehelper.task.PublicTask.PublicTaskListener;
import com.messi.languagehelper.util.AVOUtil;
import com.messi.languagehelper.util.AudioTrackUtil;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.NumberUtil;
import com.messi.languagehelper.util.SDCardUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.ToastUtil;
import com.messi.languagehelper.util.XFUtil;
import com.messi.languagehelper.util.XmlResultParser;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;

public class EvaluationDetailActivity extends BaseActivity implements OnClickListener {

	private FrameLayout evaluation_en_cover;
	private FrameLayout auto_play_cover;
	private CheckBox auto_play_cb;
	private ImageButton voice_play_answer,show_zh_img;
	private TextView evaluation_zh_tv,evaluation_en_tv,practice_prompt,record_animation_text;
	private TextView practice_prompt_detail,practice_prompt_show;
	private ScrollView practice_prompt_scrollview;
	private ImageView record_anim_img;
	private ButtonFloat buttonFloat,previous_btn,next_btn;
	private LinearLayout record_layout,record_animation_layout;
	private ButtonRectangle voice_btn;
	
	private MyOnClickListener mEvaluationOnClickListener;
	private SpeechSynthesizer mSpeechSynthesizer;
	private SharedPreferences mSharedPreferences;
	private boolean isNewIn = true;
	private boolean isFollow;
	private StringBuilder sbResult = new StringBuilder();
	private AVObject avObject;
	private String ECCode,ECLCode;
	private String content,EDCode;
	private String[] studyContent;
	private String mLastResult;
	private SpeechEvaluator mSpeechEvaluator;
	private boolean isEvaluating;
	private String userPcmPath;
	private MyThread mMyThread;
	private Thread mThread;
	
	private List<AVObject> avObjects;
	private int positin;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.evaluation_detail_activity);
		initData();
		initView();
	}
	
	private void initData(){
		getSupportActionBar().setTitle(getResources().getString(R.string.spokenEnglishTest));
		positin = getIntent().getIntExtra(KeyUtil.PositionKey,0);
		avObjects = (List<AVObject>) BaseApplication.dataMap.get(KeyUtil.DataMapKey);
		BaseApplication.dataMap.clear();
		
		mSharedPreferences = Settings.getSharedPreferences(this);
		mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(this,null);
		mSpeechEvaluator = SpeechEvaluator.createEvaluator(this, null);
	}
	
	private void setDatas(){
		avObject = avObjects.get(positin);
		content = avObject.getString(AVOUtil.EvaluationDetail.EDContent);
		EDCode = avObject.getString(AVOUtil.EvaluationDetail.EDCode);
		if(!TextUtils.isEmpty(content)){
			studyContent = content.split("#");
			if(studyContent != null){
				if(studyContent.length > 1){
					evaluation_en_tv.setText(studyContent[0]);
					mEvaluationOnClickListener = new MyOnClickListener(studyContent[0],voice_play_answer);
					evaluation_en_cover.setOnClickListener(mEvaluationOnClickListener);
				}
			}
			changeZh();
		}
	}

	private void initView() {
		evaluation_en_cover = (FrameLayout) findViewById(R.id.record_answer_cover);
		auto_play_cover = (FrameLayout) findViewById(R.id.auto_play_cover);
		auto_play_cb = (CheckBox) findViewById(R.id.auto_play_cb);
		practice_prompt = (TextView) findViewById(R.id.practice_prompt);
		practice_prompt_scrollview = (ScrollView) findViewById(R.id.practice_prompt_scrollview);
		practice_prompt_detail = (TextView) findViewById(R.id.practice_prompt_detail);
		practice_prompt_show = (TextView) findViewById(R.id.practice_prompt_show);
		evaluation_en_tv = (TextView) findViewById(R.id.record_answer);
		evaluation_zh_tv = (TextView) findViewById(R.id.record_question);
		voice_play_answer = (ImageButton) findViewById(R.id.voice_play_answer);
		show_zh_img = (ImageButton) findViewById(R.id.show_zh_img);
		voice_btn = (ButtonRectangle) findViewById(R.id.voice_btn);
		record_anim_img = (ImageView) findViewById(R.id.record_anim_img);
		buttonFloat = (ButtonFloat) findViewById(R.id.buttonFloat);
		previous_btn = (ButtonFloat) findViewById(R.id.previous_btn);
		next_btn = (ButtonFloat) findViewById(R.id.next_btn);
		record_layout = (LinearLayout) findViewById(R.id.record_layout);
		record_animation_layout = (LinearLayout) findViewById(R.id.record_animation_layout);
		record_animation_text = (TextView) findViewById(R.id.record_animation_text);
		
		buttonFloat.setEnabled(false);
		previous_btn.setEnabled(true);
		next_btn.setEnabled(true);
		auto_play_cover.setOnClickListener(this);
		previous_btn.setOnClickListener(this);
		next_btn.setOnClickListener(this);
		voice_btn.setOnClickListener(this);
		show_zh_img.setOnClickListener(this);
		buttonFloat.setOnClickListener(this);
		practice_prompt_show.setOnClickListener(this);
		practice_prompt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		practice_prompt.setText(this.getResources().getString(R.string.practice_prompt_english));
		setDatas();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) { 
		case R.id.voice_btn:
			showIatDialog();
			StatService.onEvent(EvaluationDetailActivity.this, "practice_page_speak_btn", "口语练习页说话按钮", 1);
			break;
		case R.id.buttonFloat:
			playUserPcm();
			break;
		case R.id.auto_play_cover:
			auto_play_cb.setChecked(!auto_play_cb.isCheck());
			break;
		case R.id.previous_btn:
			if(positin > 0){
				positin--;
				setDatas();
			}else{
				ToastUtil.diaplayMesShort(EvaluationDetailActivity.this, "已经是第一个了");
			}
			break;
		case R.id.next_btn:
			if(positin < avObjects.size()-1){
				positin++;
				setDatas();
			}else{
				ToastUtil.diaplayMesShort(EvaluationDetailActivity.this, "已经是最后一个了");
			}
			break;
		case R.id.show_zh_img:
			showZH();
			break;
		case R.id.practice_prompt_show:
			if(!practice_prompt_scrollview.isShown()){
				practice_prompt_scrollview.setVisibility(View.VISIBLE);
				practice_prompt_show.setText(EvaluationDetailActivity.this.getResources().getString(R.string.detail_total));
			}else{
				practice_prompt_scrollview.setVisibility(View.GONE);
				practice_prompt_show.setText(EvaluationDetailActivity.this.getResources().getString(R.string.detail));
			}
			break;
		}
	}
	
	private void showZH(){
		if( TextUtils.isEmpty(evaluation_zh_tv.getText().toString()) ){
			evaluation_zh_tv.setText(studyContent[1]);
		}else{
			evaluation_zh_tv.setText("");
		}
	}
	
	private void changeZh(){
		if( !TextUtils.isEmpty(evaluation_zh_tv.getText().toString()) ){
			evaluation_zh_tv.setText(studyContent[1]);
		}
	}
	
	private void startEvaluation() {
		mSpeechEvaluator.setParameter(SpeechConstant.LANGUAGE, "en_us");//zh_cn
		mSpeechEvaluator.setParameter(SpeechConstant.ISE_CATEGORY, "read_sentence");
		mSpeechEvaluator.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
		mSpeechEvaluator.setParameter(SpeechConstant.VAD_BOS, "5000");
		mSpeechEvaluator.setParameter(SpeechConstant.VAD_EOS, "1800");
		mSpeechEvaluator.setParameter(SpeechConstant.KEY_SPEECH_TIMEOUT, "-1");
		mSpeechEvaluator.setParameter(SpeechConstant.RESULT_LEVEL, "complete");
		// 设置音频保存路径，保存音频格式仅为pcm，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		String path = SDCardUtil.getDownloadPath(SDCardUtil.EvaluationUserPath);
		userPcmPath = path + EDCode + ".pcm";
		mSpeechEvaluator.setParameter(SpeechConstant.ISE_AUDIO_PATH, userPcmPath);
		isEvaluating = true;
		mSpeechEvaluator.startEvaluating(studyContent[0], null, mEvaluatorListener);
	}
	
	private void playUserPcm(){
		if(!TextUtils.isEmpty(userPcmPath)){
			if(mMyThread != null){
				if(mMyThread.isPlaying){
					AudioTrackUtil.stopPlayPcm(mThread);
				}else{
					mThread = AudioTrackUtil.startMyThread(mMyThread);
				}
			}else{
				mMyThread = AudioTrackUtil.getMyThread(userPcmPath);
				mThread = AudioTrackUtil.startMyThread(mMyThread);
			}
		}
	}
	
	/**
	 * 显示转写对话框.
	 */
	public void showIatDialog() {
		buttonFloat.setEnabled(false);
		if(!isEvaluating){
			if(isNewIn){
				isNewIn = false;
				isFollow = true;
				showListen();
				mEvaluationOnClickListener.onClick(voice_play_answer);
			}else{
				record_layout.setVisibility(View.VISIBLE);
				voice_btn.setText(this.getResources().getString(R.string.finish));
				startEvaluation();
			}
		}else{
			showProgressbar();
			finishRecord();
		}
	}
	
	/**
	 * finish record
	 */
	private void finishRecord(){
		isEvaluating = false;
		isNewIn = true;
		mSpeechEvaluator.stopEvaluating();
		record_layout.setVisibility(View.GONE);
		record_anim_img.setBackgroundResource(R.drawable.speak_voice_1);
		voice_btn.setText("Start");
	}
	
	private void onfinishPlay(){
		if(isFollow){
			isFollow = false;
			record_animation_layout.setVisibility(View.VISIBLE);
			record_animation_text.setText(this.getResources().getString(R.string.your_turn));
			animation();
		}
	}
	
	private void showListen(){
		record_animation_layout.setVisibility(View.VISIBLE);
		record_animation_text.setText("Listen");
		ObjectAnimator mObjectAnimator = ObjectAnimator.ofFloat(record_animation_layout, "scaleX", 1f, 1f);
		mObjectAnimator.setDuration(800).start();
		ObjectAnimator mObjectAnimator1 = ObjectAnimator.ofFloat(record_animation_layout, "scaleY", 1f, 1f);
		mObjectAnimator1.setDuration(800).start();
		ObjectAnimator mObjectAnimator2 = ObjectAnimator.ofFloat(record_animation_layout, "alpha", 1, 1);
		mObjectAnimator2.start();
	}
	
	private void animation(){
		ObjectAnimator mObjectAnimator = ObjectAnimator.ofFloat(record_animation_layout, "scaleX", 1f, 1.3f);
		mObjectAnimator.addListener(mAnimatorListener);
		mObjectAnimator.setDuration(800).start();
		ObjectAnimator mObjectAnimator1 = ObjectAnimator.ofFloat(record_animation_layout, "scaleY", 1f, 1.3f);
		mObjectAnimator1.setDuration(800).start();
		ObjectAnimator mObjectAnimator2 = ObjectAnimator.ofFloat(record_animation_layout, "alpha", 1, 0);
		mObjectAnimator2.setDuration(800).start();
	}
	
	private AnimatorListener mAnimatorListener = new AnimatorListener() {
		@Override
		public void onAnimationStart(Animator animation) {
		}
		@Override
		public void onAnimationRepeat(Animator animation) {
		}
		@Override
		public void onAnimationEnd(Animator animation) {
			record_animation_layout.setVisibility(View.GONE);
			showIatDialog();
		}
		@Override
		public void onAnimationCancel(Animator animation) {
		}
	};
	
	private void playNext(){
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if(auto_play_cb.isCheck()){
					if(positin < avObjects.size()-1){
						showNext();
					}else{
						ToastUtil.diaplayMesShort(EvaluationDetailActivity.this, "很好，本节已完成！");
					}
				}
			}
		}, 800);
	}
	
	private void showNext(){
		record_animation_layout.setVisibility(View.VISIBLE);
		record_animation_text.setText("Next");
		ObjectAnimator mObjectAnimator = ObjectAnimator.ofFloat(record_animation_layout, "scaleX", 1f, 1f);
		mObjectAnimator.addListener(mShowNextAnimator);
		mObjectAnimator.setDuration(800).start();
		ObjectAnimator mObjectAnimator1 = ObjectAnimator.ofFloat(record_animation_layout, "scaleY", 1f, 1f);
		mObjectAnimator1.setDuration(800).start();
		ObjectAnimator mObjectAnimator2 = ObjectAnimator.ofFloat(record_animation_layout, "alpha", 1, 0);
		mObjectAnimator2.setDuration(800).start();
	}
	
	private AnimatorListener mShowNextAnimator = new AnimatorListener() {
		@Override
		public void onAnimationStart(Animator animation) {
		}
		@Override
		public void onAnimationRepeat(Animator animation) {
		}
		@Override
		public void onAnimationEnd(Animator animation) {
			onClick(next_btn);
			onClick(voice_btn);
		}
		@Override
		public void onAnimationCancel(Animator animation) {
		}
	};
	
	// 评测监听接口
	private EvaluatorListener mEvaluatorListener = new EvaluatorListener() {
		
		@Override
		public void onResult(EvaluatorResult result, boolean isLast) {
			if (isLast) {
				StringBuilder builder = new StringBuilder();
				builder.append(result.getResultString());
				mLastResult = builder.toString();
				hideProgressbar();
				finishRecord();
				parseEvaluationResult();
				playNext();
				LogUtil.DefalutLog("isLast-------mLastResult:"+mLastResult);
			}
		}

		@Override
		public void onError(SpeechError error) {
			LogUtil.DefalutLog("onError:"+error.getErrorDescription());
			finishRecord();
			hideProgressbar();
			ToastUtil.diaplayMesShort(EvaluationDetailActivity.this, error.getErrorDescription());
		}

		@Override
		public void onBeginOfSpeech() {
			LogUtil.DefalutLog("evaluator begin");
		}

		@Override
		public void onEndOfSpeech() {
			LogUtil.DefalutLog("evaluator end");
			finishRecord();
			showProgressbar();
		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
			
		}

		@Override
		public void onVolumeChanged(int volume, byte[] arg1) {
			if(volume < 4){
				record_anim_img.setBackgroundResource(R.drawable.speak_voice_1);
			}else if(volume < 8){
				record_anim_img.setBackgroundResource(R.drawable.speak_voice_2);
			}else if(volume < 12){
				record_anim_img.setBackgroundResource(R.drawable.speak_voice_3);
			}else if(volume < 16){
				record_anim_img.setBackgroundResource(R.drawable.speak_voice_4);
			}else if(volume < 20){
				record_anim_img.setBackgroundResource(R.drawable.speak_voice_5);
			}else if(volume < 24){
				record_anim_img.setBackgroundResource(R.drawable.speak_voice_6);
			}else if(volume < 31){
				record_anim_img.setBackgroundResource(R.drawable.speak_voice_7);
			}
		}
	};
	
	private void parseEvaluationResult(){
		if (!TextUtils.isEmpty(mLastResult)) {
			buttonFloat.setEnabled(true);
			mMyThread = AudioTrackUtil.getMyThread(userPcmPath);
			XmlResultParser resultParser = new XmlResultParser();
			Result result = resultParser.parse(mLastResult);
			practice_prompt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28);
			if (null != result) {
				if(result.total_score > 4.2){
					practice_prompt.setTextColor(getResources().getColor(R.color.green_500));
				}else if(result.total_score > 2.5){
					practice_prompt.setTextColor(getResources().getColor(R.color.yellow_400));
				}else {
					practice_prompt.setTextColor(getResources().getColor(R.color.red));
				}
				practice_prompt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 80);
				practice_prompt.setText( String.valueOf(NumberUtil.hundred_mark(result.total_score)) );
				practice_prompt_detail.setText(result.toString());
			} else {
				practice_prompt.setText("结析结果为空");
			}
		}
	}
	
	public class MyOnClickListener implements OnClickListener {
		
		private String ev_content;
		private ImageButton voice_play;
		private AnimationDrawable animationDrawable;
		
		private MyOnClickListener(String evcon,ImageButton voice_play){
			this.ev_content = evcon;
			this.voice_play = voice_play;
			this.animationDrawable = (AnimationDrawable) voice_play.getBackground();
		}
		
		@Override
		public void onClick(final View v) {
			resetVoicePlayButton();
			String path = SDCardUtil.getDownloadPath(SDCardUtil.EvaluationPath);
			String filepath = path + EDCode + ".pcm";
			mSpeechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, filepath);
			if(!AudioTrackUtil.isFileExists(filepath)){
				showProgressbar();
				XFUtil.showSpeechSynthesizer(EvaluationDetailActivity.this,mSharedPreferences,
						mSpeechSynthesizer,ev_content,XFUtil.SpeakerEn,
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
						hideProgressbar();
						voice_play.setVisibility(View.VISIBLE);
						if(!animationDrawable.isRunning()){
							animationDrawable.setOneShot(false);
							animationDrawable.start();  
						}
					}
					@Override
					public void onCompleted(SpeechError arg0) {
						LogUtil.DefalutLog("---onCompleted");
						if(arg0 != null){
							ToastUtil.diaplayMesShort(EvaluationDetailActivity.this, arg0.getErrorDescription());
						}
						animationDrawable.setOneShot(true);
						animationDrawable.stop(); 
						animationDrawable.selectDrawable(0);
						hideProgressbar();
						onfinishPlay();
					}
					@Override
					public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
					}
					@Override
					public void onEvent(int arg0, int arg1, int arg2,Bundle arg3) {
						
					}
				});
			}else{
				playLocalPcm(filepath,animationDrawable);
			}
			if(v.getId() == R.id.record_answer_cover){
				StatService.onEvent(EvaluationDetailActivity.this, "practice_page_play_result", "口语练习页播放结果", 1);
			}
		}
	}
	
	private void playLocalPcm(final String path,final AnimationDrawable animationDrawable){
		PublicTask mPublicTask = new PublicTask(EvaluationDetailActivity.this);
		mPublicTask.setmPublicTaskListener(new PublicTaskListener() {
			@Override
			public void onPreExecute() {
				if(!animationDrawable.isRunning()){
					animationDrawable.setOneShot(false);
					animationDrawable.start();  
				}
			}
			@Override
			public Object doInBackground() {
				AudioTrackUtil.createAudioTrack(path);
				return null;
			}
			@Override
			public void onFinish(Object resutl) {
				animationDrawable.setOneShot(true);
				animationDrawable.stop(); 
				animationDrawable.selectDrawable(0);
				onfinishPlay();
			}
		});
		mPublicTask.execute();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.evaluation_detail_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_info:  
			showXunFeiDialog();
			StatService.onEvent(this, "show_xunfi_evaluation_dialog", "显示讯飞口语评测说明", 1);
			break;
		}
       return super.onOptionsItemSelected(item);
	}
	
	private void resetVoicePlayButton(){
		resetVoiceAnimation(voice_play_answer);
	}
	
	private void resetVoiceAnimation(View voice_play){
		AnimationDrawable animationDrawable = (AnimationDrawable) voice_play.getBackground();
		animationDrawable.setOneShot(true);
		animationDrawable.stop(); 
		animationDrawable.selectDrawable(0);
	}
	
	private void showXunFeiDialog(){
		Dialog dialog = new Dialog(this, "关于评测", "科大讯飞语音评测技术是业界唯一通过国家语委鉴定并达到实用水平的成果，自动评分效果已经达到甚至超过人工专家的打分水平，并且在国家普通话等级考试、英语四六级考试中的成功运用，请放心使用！");
		dialog.addAcceptButton("确定");
		dialog.show();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.DefalutLog("EvaluationDetailActivity onDestroy");
		if(mSpeechSynthesizer != null){
			mSpeechSynthesizer.stopSpeaking();
			mSpeechSynthesizer.destroy();
		}
		if(mSpeechEvaluator != null){
			mSpeechEvaluator.stopEvaluating();
			mSpeechEvaluator.cancel(true);
		}
	}
}
