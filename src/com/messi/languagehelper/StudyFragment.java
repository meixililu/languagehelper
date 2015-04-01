package com.messi.languagehelper;

import java.util.List;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.IFLYAdListener;
import com.iflytek.voiceads.IFLYBannerAd;
import com.messi.languagehelper.dao.EveryDaySentence;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.http.JsonHttpResponseHandler;
import com.messi.languagehelper.http.LanguagehelperHttpClient;
import com.messi.languagehelper.util.ADUtil;
import com.messi.languagehelper.util.JsonParser;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.NumberUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.TimeUtil;

public class StudyFragment extends Fragment implements OnClickListener{

	private View view;
	private FrameLayout study_part1,study_part2,study_part3,study_part4;
	private TextView dailysentence_txt;
	private ImageView unread_dot;
	private LinearLayout study_ad_view;
	public static StudyFragment mMainFragment;
	
	public static final String PartOne = "part_one";
	public static final String PartTwo = "part_two";
	public static final String PartThree = "part_three";
	
	private EveryDaySentence mEveryDaySentence;
	private IFLYBannerAd mIFLYBannerAd;
	
	public static StudyFragment getInstance(){
		if(mMainFragment == null){
			mMainFragment = new StudyFragment();
		}
		return mMainFragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.study_fragment, null);
		initViews();
		getDailySentence();
		isLoadDailySentence();
		return view;
	}
	
	private void initViews(){
		study_part1 = (FrameLayout)view.findViewById(R.id.study_part1);
		study_part2 = (FrameLayout)view.findViewById(R.id.study_part2);
		study_part3 = (FrameLayout)view.findViewById(R.id.study_part3);
		study_part4 = (FrameLayout)view.findViewById(R.id.study_part4);
		study_ad_view = (LinearLayout)view.findViewById(R.id.study_ad_view);
		dailysentence_txt = (TextView)view.findViewById(R.id.dailysentence_txt);
		unread_dot = (ImageView)view.findViewById(R.id.unread_dot);
		study_part1.setOnClickListener(this);
		study_part2.setOnClickListener(this);
		study_part3.setOnClickListener(this);
		study_part4.setOnClickListener(this);
		dailysentence_txt.setOnClickListener(this);
		mIFLYBannerAd = ADUtil.initBannerAD(getActivity(), study_ad_view);
		mIFLYBannerAd.loadAd(new IFLYAdListener() {
			@Override
			public void onAdReceive() {
				if(mIFLYBannerAd != null){
					mIFLYBannerAd.showAd();
				}
			}
			@Override
			public void onAdFailed(AdError arg0) {
			}
			@Override
			public void onAdClose() {
			}
			@Override
			public void onAdClick() {
			}
		});
	}
	
	private void getDailySentence(){
		List<EveryDaySentence> mList = DataBaseUtil.getInstance().getDailySentenceList(1);
 		if(mList != null){
			if(mList.size() > 0){
				mEveryDaySentence = mList.get(0);
			}
		}
		setSentence();
		LogUtil.DefalutLog("StudyFragment-getDailySentence()");
	}
	
	private void isLoadDailySentence(){
		String todayStr = TimeUtil.getTimeDateLong(System.currentTimeMillis());
		long cid = NumberUtil.StringToLong(todayStr);
		boolean isExist = DataBaseUtil.getInstance().isExist(cid);
		if(!isExist){
			requestDailysentence();
		}
		LogUtil.DefalutLog("StudyFragment-isLoadDailySentence()");
	}
	
	private void requestDailysentence(){
		LogUtil.DefalutLog("StudyFragment-requestDailysentence()");
		LanguagehelperHttpClient.get(Settings.DailySentenceUrl, null, new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
				super.onSuccess(statusCode, headers, response);
				mEveryDaySentence = JsonParser.parseEveryDaySentence(response);
				setSentence();
				DataBaseUtil.getInstance().insert(mEveryDaySentence);
				unread_dot.setVisibility(View.VISIBLE);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				super.onFailure(statusCode, headers, responseString, throwable);
			}

			@Override
			public void onFinish() {
				super.onFinish();
			}
		});
	}
	
	private void setSentence(){
		LogUtil.DefalutLog("StudyFragment-setSentence()");
		if(mEveryDaySentence != null){
			dailysentence_txt.setText(mEveryDaySentence.getContent());
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.dailysentence_txt:
			toDailySentenceActivity();
			break;
		case R.id.study_part1:
			toDailySentenceActivity();
			break;
		case R.id.study_part2:
			toGetContentActivity();
			StatService.onEvent(getActivity(), "19_studylist_part2", "口语练习第2部分", 1);
			break;
		case R.id.study_part3:
			toGetContentActivity();
			StatService.onEvent(getActivity(), "19_studylist_part3", "口语练习第3部分", 1);
			break;
		case R.id.study_part4:
			toGetfansActivity();
			StatService.onEvent(getActivity(), "19_studylist_part4", "你不是一个人在战斗", 1);
			break;
		default:
			break;
		}
	}
	
	private void toDailySentenceActivity(){
		Intent intent = new Intent(getActivity(),DailySentenceActivity.class);
		startActivity(intent);
		unread_dot.setVisibility(View.GONE);
		StatService.onEvent(getActivity(), "19_studylist_part1", "口语练习第1部分", 1);
	}
	
	private void toGetfansActivity(){
		Intent intent = new Intent(getActivity(),GetFansActivity.class);
		startActivity(intent);
	}
	private void toStudyListActivity(String level){
		Intent intent = new Intent(getActivity(),StudyListActivity.class);
		intent.putExtra(KeyUtil.LevelKey, level);
		startActivity(intent);
	}
	private void toGetContentActivity(){
		Intent intent = new Intent(getActivity(),GetContentActivity.class);
		startActivity(intent);
	}
	
}
