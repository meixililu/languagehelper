package com.messi.languagehelper;

import java.util.List;

import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.views.ButtonRectangle;
import com.gc.materialdesign.widgets.Dialog;
import com.iflytek.cloud.EvaluatorListener;
import com.iflytek.cloud.EvaluatorResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvaluator;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.messi.languagehelper.result.Result;
import com.messi.languagehelper.task.PublicTask;
import com.messi.languagehelper.task.PublicTask.PublicTaskListener;
import com.messi.languagehelper.util.AVOUtil;
import com.messi.languagehelper.util.AudioTrackUtil;
import com.messi.languagehelper.util.LogUtil;
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
	private ImageButton voice_play_answer,show_zh_img;
	private TextView evaluation_zh_tv,evaluation_en_tv,practice_prompt,record_animation_text;
	private TextView practice_prompt_detail,practice_prompt_show;
	private ScrollView practice_prompt_scrollview;
	private ImageView record_anim_img;
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
		ECCode = getIntent().getStringExtra(AVOUtil.EvaluationDetail.ECCode);
		ECLCode = getIntent().getStringExtra(AVOUtil.EvaluationDetail.ECLCode);
		mSharedPreferences = Settings.getSharedPreferences(this);
		mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(this,null);
		mSpeechEvaluator = SpeechEvaluator.createEvaluator(this, null);
	}

	private void initView() {
		evaluation_en_cover = (FrameLayout) findViewById(R.id.record_answer_cover);
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
		record_layout = (LinearLayout) findViewById(R.id.record_layout);
		record_animation_layout = (LinearLayout) findViewById(R.id.record_animation_layout);
		record_animation_text = (TextView) findViewById(R.id.record_animation_text);
		
		voice_btn.setOnClickListener(this);
		show_zh_img.setOnClickListener(this);
		practice_prompt_show.setOnClickListener(this);
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
				mEvaluationOnClickListener = new MyOnClickListener(studyContent[0],voice_play_answer);
				evaluation_en_cover.setOnClickListener(mEvaluationOnClickListener);
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
		case R.id.show_zh_img:
			if( TextUtils.isEmpty(evaluation_zh_tv.getText().toString()) ){
				evaluation_zh_tv.setText(studyContent[1]);
			}else{
				evaluation_zh_tv.setText("");
			}
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
		mSpeechEvaluator.setParameter(SpeechConstant.ISE_AUDIO_PATH,path + EDCode + ".pcm");
		isEvaluating = true;
		mSpeechEvaluator.startEvaluating(studyContent[0], null, mEvaluatorListener);
	}

	
	/**
	 * 显示转写对话框.
	 */
	public void showIatDialog() {
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
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
		}
	};
	
	private void parseEvaluationResult(){
		if (!TextUtils.isEmpty(mLastResult)) {
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
				practice_prompt.setText("总分："+ result.total_score);
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
