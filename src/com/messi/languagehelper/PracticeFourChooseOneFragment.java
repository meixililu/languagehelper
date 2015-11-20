package com.messi.languagehelper;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.gc.materialdesign.views.ButtonRectangle;
import com.iflytek.cloud.SpeechSynthesizer;
import com.messi.languagehelper.impl.PracticeProgressListener;
import com.messi.languagehelper.util.NumberUtil;
import com.messi.languagehelper.util.SDCardUtil;
import com.messi.languagehelper.util.SpannableStringUtil;
import com.messi.languagehelper.util.XFUtil;

public class PracticeFourChooseOneFragment extends BaseFragment implements OnClickListener{

	private View view;
	private String content;
	private TextView questionTv;
	private ButtonRectangle check_btn;
	private PracticeProgressListener mPracticeProgress;
	private CheckBox select_answer1,select_answer2,select_answer3,select_answer4;
	private int resultPosition;
	private int userSelect;
	private int index;
	private int[] orderList;
	private String[] yb,cn,en;
	private boolean isCheck;
	private String videoPath;
	private SpeechSynthesizer mSpeechSynthesizer;
	private SharedPreferences mSharedPreferences;
	
	public PracticeFourChooseOneFragment(String content, PracticeProgressListener mPracticeProgress, String videoPath, 
			SharedPreferences mSharedPreferences,SpeechSynthesizer mSpeechSynthesizer){
		this.content = content;
		this.mPracticeProgress = mPracticeProgress;
		getContent();
		this.videoPath = SDCardUtil.getDownloadPath(videoPath);
		this.mSpeechSynthesizer = mSpeechSynthesizer;
		this.mSharedPreferences = mSharedPreferences;
		orderList = NumberUtil.getNumberOrderWithoutRepeat(4, 0, 4, false);
	}
	
	private void getContent(){
		String temp[] = content.split("#");
		if(temp.length > 3){
			yb = temp[0].split(",");
			cn = temp[1].split(",");
			en = temp[2].split(",");
		}else{
			cn = temp[0].split(",");
			en = temp[1].split(",");
		}
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
		check_btn = (ButtonRectangle)view.findViewById(R.id.check_btn);
		setIndex();
		setContent();
		check_btn.setEnabled(false);
		select_answer1.setOnClickListener(this);
		select_answer2.setOnClickListener(this);
		select_answer3.setOnClickListener(this);
		select_answer4.setOnClickListener(this);
		check_btn.setOnClickListener(this);
	}
	
	private void setIndex(){
		resultPosition = orderList[index];
	}
	
	private void setContent(){
		questionTv.setText("选择  " + "\"" + cn[resultPosition] +"\"");
		if(yb != null){
			select_answer1.setText(en[0] +"\n" );
			select_answer1.append( SpannableStringUtil.setTextSize(getActivity(), yb[0], R.dimen.big) );
			select_answer2.setText(en[1] +"\n");
			select_answer2.append( SpannableStringUtil.setTextSize(getActivity(), yb[1], R.dimen.big) );
			select_answer3.setText(en[2] +"\n");
			select_answer3.append( SpannableStringUtil.setTextSize(getActivity(), yb[2], R.dimen.big) );
			select_answer4.setText(en[3] +"\n");
			select_answer4.append( SpannableStringUtil.setTextSize(getActivity(), yb[3], R.dimen.big) );
		}else{
			select_answer1.setText(en[0]);
			select_answer2.setText(en[1]);
			select_answer3.setText(en[2]);
			select_answer4.setText(en[3]);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.check_btn:
			submit();
			break;
		case R.id.select_answer1:
			clearChecked();
			isCheck = true;
			select_answer1.setChecked(true);
			userSelect = 0;
			playVideo(userSelect);
			break;
		case R.id.select_answer2:
			clearChecked();
			isCheck = true;
			select_answer2.setChecked(true);
			userSelect = 1;
			playVideo(userSelect);
			break;
		case R.id.select_answer3:
			clearChecked();
			isCheck = true;
			select_answer3.setChecked(true);
			userSelect = 2;
			playVideo(userSelect);
			break;
		case R.id.select_answer4:
			clearChecked();
			isCheck = true;
			select_answer4.setChecked(true);
			userSelect = 3;
			playVideo(userSelect);
			break;
		}
	}
	
	private void submit(){
		if(isCheck){
			checkResult();
		}else{
			if(index < orderList.length-1){
				clearChecked();
				index++;
				setIndex();
				setContent();
				check_btn.setEnabled(false);
			}else{
				toNextPage();
			}
		}
	}
	
	private void checkResult(){
		if(resultPosition == userSelect){
			isCheck = false;
			if(index < orderList.length){
				check_btn.setText("正确，下一个");
			}else{
				check_btn.setText("正确，下一关");
			}
		}else{
			tryAgain();
			if(userSelect == 0){
				select_answer1.append("\n");
				select_answer1.append( SpannableStringUtil.setTextSize(getActivity(), cn[0], R.dimen.big) );
			}else if(userSelect == 1){
				select_answer2.append("\n");
				select_answer2.append( SpannableStringUtil.setTextSize(getActivity(), cn[1], R.dimen.big) );
			}else if(userSelect == 2){
				select_answer3.append("\n");
				select_answer3.append( SpannableStringUtil.setTextSize(getActivity(), cn[2], R.dimen.big) );
			}else if(userSelect == 3){
				select_answer4.append("\n");
				select_answer4.append( SpannableStringUtil.setTextSize(getActivity(), cn[3], R.dimen.big) );
			}
		}
	}
	
	private void tryAgain(){
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
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mSpeechSynthesizer != null){
			mSpeechSynthesizer.destroy();
		}
	}
}
