package com.messi.languagehelper;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.Dialog;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.messi.languagehelper.bean.UserSpeakBean;
import com.messi.languagehelper.dao.record;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.task.PublicTask;
import com.messi.languagehelper.task.PublicTask.PublicTaskListener;
import com.messi.languagehelper.util.AVOUtil;
import com.messi.languagehelper.util.AudioTrackUtil;
import com.messi.languagehelper.util.JsonParser;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.SDCardUtil;
import com.messi.languagehelper.util.ScoreUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.ToastUtil;
import com.messi.languagehelper.util.XFUtil;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;

public class EvaluationDetailActivity extends BaseActivity implements OnClickListener {

	private FrameLayout record_question_cover,evaluation_en_cover;
	private ImageButton voice_play_answer;
	private TextView evaluation_zh_tv,evaluation_en_tv,practice_prompt,record_animation_text;
	private ImageView record_anim_img;
	private LinearLayout record_layout,record_animation_layout;
	private ButtonRectangle voice_btn;
	
	private MyOnClickListener mEvaluationOnClickListener;
	private SpeechSynthesizer mSpeechSynthesizer;
	private SpeechRecognizer recognizer;
	private SharedPreferences mSharedPreferences;
	private ArrayList<UserSpeakBean> mUserSpeakBeanList;
	private boolean isNewIn = true;
	private boolean isFollow;
	private StringBuilder sbResult = new StringBuilder();
	private AVObject avObject;
	private String ECCode,ECLCode;
	private String content,EDCode;
	private String[] studyContent;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.evaluation_detail_activity);
		initData();
		initView();
		new QueryTask().execute();
	}
	
	private void initData(){
		getSupportActionBar().setTitle(getResources().getString(R.string.spokenEnglishTest));
		ECCode = getIntent().getStringExtra(AVOUtil.EvaluationDetail.ECLCode);
		ECLCode = getIntent().getStringExtra(AVOUtil.EvaluationDetail.ECCode);
		mUserSpeakBeanList = new ArrayList<UserSpeakBean>();
	}

	private void initView() {
        mSharedPreferences = Settings.getSharedPreferences(this);
        mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(this,null);
        recognizer = SpeechRecognizer.createRecognizer(this,null);
		evaluation_en_cover = (FrameLayout) findViewById(R.id.record_answer_cover);
		record_question_cover = (FrameLayout) findViewById(R.id.record_question_cover);
		practice_prompt = (TextView) findViewById(R.id.practice_prompt);
		evaluation_en_tv = (TextView) findViewById(R.id.record_answer);
		evaluation_zh_tv = (TextView) findViewById(R.id.record_question);
		voice_play_answer = (ImageButton) findViewById(R.id.voice_play_answer);
		voice_btn = (ButtonRectangle) findViewById(R.id.voice_btn);
		record_anim_img = (ImageView) findViewById(R.id.record_anim_img);
		record_layout = (LinearLayout) findViewById(R.id.record_layout);
		record_animation_layout = (LinearLayout) findViewById(R.id.record_animation_layout);
		record_animation_text = (TextView) findViewById(R.id.record_animation_text);
		
		voice_btn.setOnClickListener(this);
		practice_prompt.setText(this.getResources().getString(R.string.practice_prompt_english));
		
	}
	
	private class QueryTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressbar();
//			error_txt.setVisibility(View.GONE);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.EvaluationDetail.EvaluationDetail);
			query.whereEqualTo(AVOUtil.EvaluationDetail.ECCode, ECCode);
			query.whereEqualTo(AVOUtil.EvaluationDetail.ECLCode, ECLCode);
			try {
				List<AVObject> avObjects  = query.find();
				if(avObjects != null && avObjects.size() > 0){
					avObject = avObjects.get(0);
				}
			} catch (AVException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			hideProgressbar();
			if(avObject != null){
				content = avObject.getString(AVOUtil.EvaluationDetail.EDContent);
				EDCode = avObject.getString(AVOUtil.EvaluationDetail.EDCode);
				LogUtil.DefalutLog("EDContent:"+content);
				if(!TextUtils.isEmpty(content)){
					studyContent = content.split("#");
					setData();
				}else{
//					error_txt.setVisibility(View.VISIBLE);
				}
			}else{
//				error_txt.setVisibility(View.VISIBLE);
			}
		}
	}
	
	private void setData(){
		if(studyContent != null){
			if(studyContent.length > 1){
				evaluation_en_tv.setText(studyContent[0]);
				evaluation_zh_tv.setText(studyContent[1]);
				evaluation_en_cover.setOnClickListener(mEvaluationOnClickListener);
				mEvaluationOnClickListener = new MyOnClickListener(studyContent[0],voice_play_answer);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) { 
		case R.id.voice_btn:
			showIatDialog();
			StatService.onEvent(EvaluationDetailActivity.this, "practice_page_speak_btn", "口语练习页说话按钮", 1);
			break;
		}
	}
	
	/**
	 * 显示转写对话框.
	 */
	public void showIatDialog() {
		if(!recognizer.isListening()){
			if(isNewIn){
				isNewIn = false;
				isFollow = true;
				practice_prompt.setVisibility(View.GONE);
				mEvaluationOnClickListener.onClick(voice_play_answer);
				record_animation_layout.setVisibility(View.VISIBLE);
				record_animation_text.setText("Listen");
			}else{
				record_layout.setVisibility(View.VISIBLE);
				voice_btn.setText(this.getResources().getString(R.string.finish));
				XFUtil.showSpeechRecognizer(this,mSharedPreferences,recognizer,recognizerListener);
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
		recognizer.stopListening();
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
	
	private void animationReward(int score){
		String word = "Nice";
		if(score > 90){
			word = "Perfect";
		}else if(score > 70){
			word = "Great";
		}else if(score > 59){
			word = "Not bad";
		}else {
			word = "Try harder";
		}
		record_animation_layout.setVisibility(View.VISIBLE);
		record_animation_text.setText(word);
		ObjectAnimator mObjectAnimator = ObjectAnimator.ofFloat(record_animation_layout, "alpha", 1, 0f);
		mObjectAnimator.addListener(mAnimatorListenerReward);
		mObjectAnimator.setStartDelay(300);
		mObjectAnimator.setDuration(1500).start();
	}
	
	private AnimatorListener mAnimatorListenerReward = new AnimatorListener() {
		@Override
		public void onAnimationStart(Animator animation) {
		}
		@Override
		public void onAnimationRepeat(Animator animation) {
		}
		@Override
		public void onAnimationEnd(Animator animation) {
			record_animation_layout.setVisibility(View.GONE);
		}
		@Override
		public void onAnimationCancel(Animator animation) {
		}
	};
	
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
	
	RecognizerListener recognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			LogUtil.DefalutLog("onBeginOfSpeech");
		}

		@Override
		public void onError(SpeechError err) {
			LogUtil.DefalutLog("onError:"+err.getErrorDescription());
			finishRecord();
			hideProgressbar();
			ToastUtil.diaplayMesShort(EvaluationDetailActivity.this, err.getErrorDescription());
		}

		@Override
		public void onEndOfSpeech() {
			LogUtil.DefalutLog("onEndOfSpeech");
			finishRecord();
			showProgressbar();
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			LogUtil.DefalutLog("onResult---getResultString:"+results.getResultString());
			String text = JsonParser.parseIatResult(results.getResultString());
			sbResult.append(text);
			if(isLast) {
				LogUtil.DefalutLog("isLast-------onResult:"+sbResult.toString());
				hideProgressbar();
				finishRecord();
				UserSpeakBean bean = ScoreUtil.score(EvaluationDetailActivity.this, sbResult.toString(), evaluation_en_tv.getText().toString());
				mUserSpeakBeanList.add(0,bean);
				animationReward(bean.getScoreInt());
				sbResult.setLength(0);
			}
		}

		@Override
		public void onVolumeChanged(int volume) {
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

		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
		}
	};
	
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
				XFUtil.showSpeechSynthesizer(EvaluationDetailActivity.this,mSharedPreferences,mSpeechSynthesizer,ev_content,
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
			if(v.getId() == R.id.record_question_cover){
				StatService.onEvent(EvaluationDetailActivity.this, "practice_page_play_content", "口语练习页播放内容", 1);
			}else if(v.getId() == R.id.record_answer_cover){
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
		Dialog dialog = new Dialog(this, "温馨提示", "科大讯飞语音评测技术是业界唯一通过国家语委鉴定并达到实用水平的成果，自动评分效果已经达到甚至超过人工专家的打分水平，并且在国家普通话等级考试、英语四六级考试中的成功运用，请放心使用！");
		dialog.addAcceptButton("确定");
		dialog.show();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mSpeechSynthesizer != null){
			mSpeechSynthesizer.destroy();
			mSpeechSynthesizer = null;
		}
		if(recognizer != null){
			recognizer.destroy();
			recognizer = null;
		}
	}
}
