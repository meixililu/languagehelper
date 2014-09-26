package com.messi.languagehelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.messi.languagehelper.impl.PracticeProgress;
import com.messi.languagehelper.util.AudioTrackUtil;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.NumberUtil;
import com.messi.languagehelper.util.SDCardUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.XFUtil;

public class PracticeFourChooseOneFragment extends BaseFragment implements OnClickListener{

	private View view;
	private String content;
	private TextView questionTv;
	private Button check_btn;
	private PracticeProgress mPracticeProgress;
	private CheckBox select_answer1,select_answer2,select_answer3,select_answer4;
	private int resultPosition;
	private int userSelect;
	private String[] cn,en;
	private boolean isGoNext;
	private String videoPath;
	private SpeechSynthesizer mSpeechSynthesizer;
	private SharedPreferences mSharedPreferences;
	
	public PracticeFourChooseOneFragment(String content, PracticeProgress mPracticeProgress, String videoPath, 
			SharedPreferences mSharedPreferences){
		this.content = content;
		this.mPracticeProgress = mPracticeProgress;
		resultPosition = NumberUtil.getRandomNumber(4);
		getContent();
		this.videoPath = SDCardUtil.getDownloadPath(videoPath);
		mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(getActivity());
		this.mSharedPreferences = mSharedPreferences;
	}
	
	private void getContent(){
		String temp[] = content.split("#");
		cn = temp[0].split(",");
		en = temp[1].split(",");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.practice_one_fragment, container, false);
		initViews();
		return view;
	}
	
	private void initViews(){
		questionTv = (TextView)view.findViewById(R.id.questiontv);
		select_answer1 = (CheckBox)view.findViewById(R.id.select_answer1);
		select_answer2 = (CheckBox)view.findViewById(R.id.select_answer2);
		select_answer3 = (CheckBox)view.findViewById(R.id.select_answer3);
		select_answer4 = (CheckBox)view.findViewById(R.id.select_answer4);
		check_btn = (Button)view.findViewById(R.id.check_btn);
		setContent();
		
		select_answer1.setOnClickListener(this);
		select_answer2.setOnClickListener(this);
		select_answer3.setOnClickListener(this);
		select_answer4.setOnClickListener(this);
		check_btn.setOnClickListener(this);
	}
	
	private void setContent(){
		questionTv.setText("选择  " + "\"" + cn[resultPosition] +"\"");
		select_answer1.setText(en[0]);
		select_answer2.setText(en[1]);
		select_answer3.setText(en[2]);
		select_answer4.setText(en[3]);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.check_btn:
			submit();
			break;
		case R.id.select_answer1:
			clearChecked();
			select_answer1.setChecked(true);
			userSelect = 0;
			playVideo(userSelect);
			break;
		case R.id.select_answer2:
			clearChecked();
			select_answer2.setChecked(true);
			userSelect = 1;
			playVideo(userSelect);
			break;
		case R.id.select_answer3:
			clearChecked();
			select_answer3.setChecked(true);
			userSelect = 2;
			playVideo(userSelect);
			break;
		case R.id.select_answer4:
			clearChecked();
			select_answer4.setChecked(true);
			userSelect = 3;
			playVideo(userSelect);
			break;
		}
	}
	
	private void submit(){
		if(isGoNext){
			if(resultPosition == userSelect){
				toNextPage();
				isGoNext = false;
			}else{
				checkResult();
			}
		}else{
			checkResult();
		}
	}
	
	private void checkResult(){
		if(resultPosition == userSelect){
			isGoNext = true;
			check_btn.setText("正确，下一个");
		}else{
			tryAgain();
			if(userSelect == 0){
				select_answer1.setText(en[0] + "\n" + cn[0]);
			}else if(userSelect == 1){
				select_answer2.setText(en[1] + "\n" + cn[1]);
			}else if(userSelect == 2){
				select_answer3.setText(en[2] + "\n" + cn[2]);
			}else if(userSelect == 3){
				select_answer4.setText(en[3] + "\n" + cn[3]);
			}
		}
	}
	
	private void tryAgain(){
		isGoNext = false;
		check_btn.setText("错了，再试试");
		check_btn.setEnabled(false);
	}
	
	private void toNextPage(){
		if(mPracticeProgress != null){
			mPracticeProgress.toNextPage();
		}
	}
	
	private void clearChecked(){
		select_answer1.setChecked(false);
		select_answer2.setChecked(false);
		select_answer3.setChecked(false);
		select_answer4.setChecked(false);
		check_btn.setEnabled(true);
		check_btn.setText("检查");
	}
	
	private void playVideo(int position){
		String filepath = videoPath + position + ".pcm";
		XFUtil.playVideoInBackground(getActivity(), mSpeechSynthesizer, mSharedPreferences, filepath, en[position]);
	}
	
	private void toCailingActivity(){
		Intent intent = new Intent(getActivity(),WebViewActivity.class);
		intent.putExtra(KeyUtil.URL, Settings.CaiLingUrl);
		intent.putExtra(KeyUtil.ActionbarTitle, getActivity().getResources().getString(R.string.title_cailing));
		getActivity().startActivity(intent);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mSpeechSynthesizer = null;
		LogUtil.DefalutLog("PracticeOneFragment---onDestroy");
	}
}
