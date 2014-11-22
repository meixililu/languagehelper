package com.messi.languagehelper;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iflytek.cloud.SpeechSynthesizer;
import com.messi.languagehelper.impl.PracticeProgressListener;
import com.messi.languagehelper.util.SDCardUtil;
import com.messi.languagehelper.util.ViewUtil;
import com.messi.languagehelper.util.XFUtil;

public class PracticeEveryFragment extends BaseFragment implements OnClickListener{

	private View view;
	private String content;
	private TextView questionTv;
	private Button check_btn;
	private PracticeProgressListener mPracticeProgress;
	private int userSelect;
	private String[] cn,en;
	private boolean isGoNext;
	private String videoPath;
	private ViewPager viewpager;
	private LinearLayout viewpager_dot_layout;
	private SpeechSynthesizer mSpeechSynthesizer;
	private SharedPreferences mSharedPreferences;
	
	public PracticeEveryFragment(String content, PracticeProgressListener mPracticeProgress, String videoPath, 
			SharedPreferences mSharedPreferences,SpeechSynthesizer mSpeechSynthesizer){
		this.content = content;
		this.mPracticeProgress = mPracticeProgress;
		getContent();
		this.videoPath = SDCardUtil.getDownloadPath(videoPath);
		this.mSpeechSynthesizer = mSpeechSynthesizer;
		this.mSharedPreferences = mSharedPreferences;
	}
	
	private void getContent(){
		String temp[] = content.split("#");
		cn = temp[0].split(",");
		en = temp[1].split(",");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.practice_every_fragment, container, false);
		initViews();
		return view;
	}
	
	private void initViews(){
		questionTv = (TextView)view.findViewById(R.id.questiontv);
		viewpager = (ViewPager)view.findViewById(R.id.viewpager);
		viewpager_dot_layout = (LinearLayout)view.findViewById(R.id.viewpager_dot_layout);
		check_btn = (Button)view.findViewById(R.id.check_btn);
		ViewUtil.addDot(getActivity(), en.length, viewpager_dot_layout);
		
		check_btn.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.check_btn:
			submit();
			break;
		}
	}
	
	private void submit(){
		if(isGoNext){
			
		}else{
			checkResult();
		}
	}
	
	private void checkResult(){
		
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
		check_btn.setEnabled(true);
		check_btn.setText("检查");
	}
	
	private void playVideo(int position){
		String filepath = videoPath + position + ".pcm";
		XFUtil.playVideoInBackground(getActivity(), mSpeechSynthesizer, mSharedPreferences, filepath, en[position]);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mSpeechSynthesizer != null){
			mSpeechSynthesizer.destroy();
		}
	}
}
