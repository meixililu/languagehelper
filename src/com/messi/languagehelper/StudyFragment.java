package com.messi.languagehelper;

import java.util.List;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
	private FrameLayout study_daily_sentence,study_spoken_english,study_dailog,study_test,study_to_all_user;
	private TextView dailysentence_txt;
	private ImageView unread_dot;
	private LinearLayout study_ad_view;
	public static StudyFragment mMainFragment;
	private SharedPreferences mSharedPreferences;
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
		mSharedPreferences = getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);
		study_daily_sentence = (FrameLayout)view.findViewById(R.id.study_daily_sentence);
		study_spoken_english = (FrameLayout)view.findViewById(R.id.study_spoken_english);
		study_dailog = (FrameLayout)view.findViewById(R.id.study_dailog);
		study_test = (FrameLayout)view.findViewById(R.id.study_test);
		study_to_all_user = (FrameLayout)view.findViewById(R.id.study_to_all_user);
		study_ad_view = (LinearLayout)view.findViewById(R.id.study_ad_view);
		dailysentence_txt = (TextView)view.findViewById(R.id.dailysentence_txt);
		unread_dot = (ImageView)view.findViewById(R.id.unread_dot);
		
		study_daily_sentence.setOnClickListener(this);
		study_spoken_english.setOnClickListener(this);
		study_dailog.setOnClickListener(this);
		study_test.setOnClickListener(this);
		study_to_all_user.setOnClickListener(this);
		dailysentence_txt.setOnClickListener(this);
		if(showNewFunction()){
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
	}
	
	private boolean showNewFunction(){
		int IsCanShowAD_Study = mSharedPreferences.getInt(KeyUtil.IsCanShowAD_Study, 0);
        if(IsCanShowAD_Study > 1){
        	return true;
        }else{
        	IsCanShowAD_Study++;
        	Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.IsCanShowAD_Study,IsCanShowAD_Study);
        	return false;
        }
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
		case R.id.study_daily_sentence:
			toDailySentenceActivity();
			break;
		case R.id.study_spoken_english:
			toStudyListActivity();
			StatService.onEvent(getActivity(), "19_studylist_part2", "口语练习第2部分", 1);
			break;
		case R.id.study_dailog:
			toGetContentActivity();
			StatService.onEvent(getActivity(), "19_studylist_part3", "口语练习第3部分", 1);
			break;
		case R.id.study_test:
			toGetContentActivity();
			StatService.onEvent(getActivity(), "19_studylist_part4", "你不是一个人在战斗", 1);
			break;
		case R.id.study_to_all_user:
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
	
	private void toStudyListActivity(){
		Intent intent = new Intent(getActivity(),StudyCategoryActivity.class);
		startActivity(intent);
	}
	private void toGetContentActivity(){
		Intent intent = new Intent(getActivity(),GetContentActivity.class);
		startActivity(intent);
	}
	
}
