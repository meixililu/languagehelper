package com.messi.languagehelper;

import java.util.ArrayList;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.baidu.mobstat.StatService;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.messi.languagehelper.adapter.PracticePageListItemAdapter;
import com.messi.languagehelper.bean.DialogBean;
import com.messi.languagehelper.bean.UserSpeakBean;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.impl.PracticeProgressListener;
import com.messi.languagehelper.task.PublicTask;
import com.messi.languagehelper.task.PublicTask.PublicTaskListener;
import com.messi.languagehelper.util.AudioTrackUtil;
import com.messi.languagehelper.util.JsonParser;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.NumberUtil;
import com.messi.languagehelper.util.SDCardUtil;
import com.messi.languagehelper.util.ScoreUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.StringUtils;
import com.messi.languagehelper.util.ToastUtil;
import com.messi.languagehelper.util.XFUtil;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;

public class PracticeReadAfterMeFragment extends BaseFragment implements OnClickListener {

	private FrameLayout record_answer_cover,repeat_time_minus_cover,repeat_time_plus_cover;
	private ImageButton voice_play_answer;
	private TextView record_question,record_answer,practice_prompt,record_animation_text;
	private TextView repeat_time,repeat_time_minus,repeat_time_plus;
	private ListView recent_used_lv;
	private ImageView record_anim_img;
	private Button voice_btn;
	private LinearLayout record_layout,record_animation_layout;
	
	private MyOnClickListener mAnswerOnClickListener;
	private DataBaseUtil mDataBaseUtil;
	private SpeechSynthesizer mSpeechSynthesizer;
	private SpeechRecognizer recognizer;
	private SharedPreferences mSharedPreferences;
	private ArrayList<UserSpeakBean> mUserSpeakBeanList;
	private PracticePageListItemAdapter adapter;
	private boolean isNewIn = true;
	private boolean isFollow;
	
	private String[] cn,en;
	private String content;
	private String videoPath;
	private int resultPosition;
	private int repeatTimes;
	private int times;
	private PracticeProgressListener mPracticeProgress;
	
	public PracticeReadAfterMeFragment(String content, PracticeProgressListener mPracticeProgress, String videoPath, 
			SharedPreferences mSharedPreferences,SpeechSynthesizer mSpeechSynthesizer){
		this.content = content;
		this.mPracticeProgress = mPracticeProgress;
		resultPosition = NumberUtil.getRandomNumber(4);
		getContent();
		this.videoPath = SDCardUtil.getDownloadPath(videoPath);
		this.mSharedPreferences = mSharedPreferences;
		this.mSpeechSynthesizer = mSpeechSynthesizer;
	}
	
	private void getContent(){
		String temp[] = content.split("#");
		cn = temp[0].split(",");
		en = temp[1].split(",");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.practice_read_after_me_fragmenty, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initData();
		initView();
	}
	
	private void initData(){
		mUserSpeakBeanList = new ArrayList<UserSpeakBean>();
		adapter = new PracticePageListItemAdapter(getActivity(), mUserSpeakBeanList);
	}

	private void initView() {
        mSharedPreferences = Settings.getSharedPreferences(getActivity());
        recognizer = SpeechRecognizer.createRecognizer(getActivity(), null);
        mDataBaseUtil = new DataBaseUtil(getActivity());
        repeatTimes = mSharedPreferences.getInt(KeyUtil.ReadRepeatTime, 2);
        
		record_answer_cover = (FrameLayout) getView().findViewById(R.id.record_answer_cover);
		repeat_time_minus_cover = (FrameLayout) getView().findViewById(R.id.repeat_time_minus_cover);
		repeat_time_plus_cover = (FrameLayout) getView().findViewById(R.id.repeat_time_plus_cover);
		repeat_time = (TextView) getView().findViewById(R.id.repeat_time);
		repeat_time_minus = (TextView) getView().findViewById(R.id.repeat_time_minus);
		repeat_time_plus = (TextView) getView().findViewById(R.id.repeat_time_plus);
		
		practice_prompt = (TextView) getView().findViewById(R.id.practice_prompt);
		record_answer = (TextView) getView().findViewById(R.id.record_answer);
		record_question = (TextView) getView().findViewById(R.id.record_question);
		voice_play_answer = (ImageButton) getView().findViewById(R.id.voice_play_answer);
		voice_btn = (Button) getView().findViewById(R.id.check_btn);
		record_anim_img = (ImageView) getView().findViewById(R.id.record_anim_img);
		record_layout = (LinearLayout) getView().findViewById(R.id.record_layout);
		record_animation_layout = (LinearLayout) getView().findViewById(R.id.record_animation_layout);
		record_animation_text = (TextView) getView().findViewById(R.id.record_animation_text);
		recent_used_lv = (ListView) getView().findViewById(R.id.recent_used_lv);
		recent_used_lv.setAdapter(adapter);
		
		record_answer.setText(en[resultPosition]);
		record_question.setText(cn[resultPosition]);
		repeat_time.setText("跟读 " + repeatTimes + " 次");
		
		initSpeakLanguage();
		mAnswerOnClickListener = new MyOnClickListener(en[resultPosition],voice_play_answer,true);
		record_answer_cover.setOnClickListener(mAnswerOnClickListener);
		voice_btn.setOnClickListener(this);
		repeat_time_minus_cover.setOnClickListener(this);
		repeat_time_plus_cover.setOnClickListener(this);
		
	}
	
	private void initSpeakLanguage(){
		XFUtil.setSpeakLanguage(getActivity(),mSharedPreferences,XFUtil.VoiceEngineEN);
		practice_prompt.setText(getActivity().getResources().getString(R.string.practice_prompt_english));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) { 
		case R.id.check_btn:
			showIatDialog();
			StatService.onEvent(getActivity(), "1.8_practice_speak_btn", "口语练习-说话按钮", 1);
			break;
		case R.id.repeat_time_minus_cover:
			if(repeatTimes > 1){
				repeatTimes--;
				repeat_time.setText("跟读 " + repeatTimes + " 次");
			}
			Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.ReadRepeatTime, repeatTimes);
			break;
		case R.id.repeat_time_plus_cover:
			repeatTimes++;
			repeat_time.setText("跟读 " + repeatTimes + " 次");
			Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.ReadRepeatTime, repeatTimes);
			break;
		default:
			break;
		}
	}
	
	/**
	 * 显示转写对话框.
	 */
	public void showIatDialog() {
		if(!recognizer.isListening()){
			if(times >= repeatTimes){
				if(mPracticeProgress != null){
					mPracticeProgress.toNextPage();
				}
			}else{
				if(isNewIn){
					isNewIn = false;
					isFollow = true;
					practice_prompt.setVisibility(View.GONE);
					mAnswerOnClickListener.onClick(voice_play_answer);
					record_animation_layout.setVisibility(View.VISIBLE);
					record_animation_text.setText("Listen");
				}else{
					record_layout.setVisibility(View.VISIBLE);
					voice_btn.setText(getActivity().getResources().getString(R.string.finish));
					XFUtil.showSpeechRecognizer(getActivity(),mSharedPreferences,recognizer,recognizerListener);
				}
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
		voice_btn.setText("开始");
	}
	
	private void onfinishPlay(){
		if(isFollow){
			isFollow = false;
			record_animation_layout.setVisibility(View.VISIBLE);
			record_animation_text.setText(getActivity().getResources().getString(R.string.your_turn));
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
	
	private void isEnough(){
		if(times >= repeatTimes){
			voice_btn.setText("继续，下一关");
		}
	}
	
	RecognizerListener recognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			LogUtil.DefalutLog("onBeginOfSpeech");
			times++;
		}

		@Override
		public void onError(SpeechError err) {
			LogUtil.DefalutLog("onError:"+err.getErrorDescription());
			finishRecord();
			hideProgressbar();
			isEnough();
			ToastUtil.diaplayMesShort(getActivity(), err.getErrorDescription());
		}

		@Override
		public void onEndOfSpeech() {
			LogUtil.DefalutLog("onEndOfSpeech");
			finishRecord();
			showProgressbar();
			isEnough();
		}


		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			LogUtil.DefalutLog("onResult");
			String text = JsonParser.parseIatResult(results.getResultString()).toLowerCase();
			if(isLast) {
				hideProgressbar();
				finishRecord();
				UserSpeakBean bean = ScoreUtil.score(getActivity(), text, record_answer.getText().toString());
				mUserSpeakBeanList.add(0,bean);
				adapter.notifyDataSetChanged();
				animationReward(bean.getScoreInt());
				isEnough();
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
			// TODO Auto-generated method stub
			
		}

	};
	
	public class MyOnClickListener implements OnClickListener {
		
		private String content;
		private ImageButton voice_play;
		private AnimationDrawable animationDrawable;
		private boolean isPlayResult;
		
		public void setPlayResult(boolean isPlayResult) {
			this.isPlayResult = isPlayResult;
		}

		private MyOnClickListener(String content,ImageButton voice_play,boolean isPlayResult){
			this.content = content;
			this.voice_play = voice_play;
			this.animationDrawable = (AnimationDrawable) voice_play.getBackground();
			this.isPlayResult = isPlayResult;
		}
		
		@Override
		public void onClick(final View v) {
			resetVoicePlayButton();
			String filepath = videoPath + resultPosition + ".pcm";
			if(!AudioTrackUtil.isFileExists(filepath)){
				showProgressbar();
				mSpeechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, filepath);
				XFUtil.showSpeechSynthesizer(getActivity(),mSharedPreferences,mSpeechSynthesizer,content,
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
							ToastUtil.diaplayMesShort(getActivity(), arg0.getErrorDescription());
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
						// TODO Auto-generated method stub
						
					}
				});
			}else{
				playLocalPcm(filepath,animationDrawable);
			}
			StatService.onEvent(getActivity(), "1.8_practice_play_result", "口语练习-点击翻译结果", 1);
		}
	}
	
	private void playLocalPcm(final String path,final AnimationDrawable animationDrawable){
		PublicTask mPublicTask = new PublicTask(getActivity());
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
	
	private void resetVoicePlayButton(){
		resetVoiceAnimation(voice_play_answer);
	}
	
	private void resetVoiceAnimation(View voice_play){
		AnimationDrawable animationDrawable = (AnimationDrawable) voice_play.getBackground();
		animationDrawable.setOneShot(true);
		animationDrawable.stop(); 
		animationDrawable.selectDrawable(0);
	}
	
	private void showProgressbar(){
		if(mPracticeProgress != null){
			mPracticeProgress.onLoading();
		}
	}
	
	private void hideProgressbar(){
		if(mPracticeProgress != null){
			mPracticeProgress.onCompleteLoading();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mSpeechSynthesizer != null){
			mSpeechSynthesizer.destroy();
		}
		if(recognizer != null){
			recognizer.cancel();
		}
	}
	
}
