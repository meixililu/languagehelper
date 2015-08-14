package com.messi.languagehelper;

import java.util.List;

import org.apache.http.Header;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.loopj.android.http.JsonHttpResponseHandler;
import com.messi.languagehelper.dao.EveryDaySentence;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.http.LanguagehelperHttpClient;
import com.messi.languagehelper.impl.FragmentProgressbarListener;
import com.messi.languagehelper.util.ADUtil;
import com.messi.languagehelper.util.DownLoadUtil;
import com.messi.languagehelper.util.JsonParser;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.NumberUtil;
import com.messi.languagehelper.util.SDCardUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.TimeUtil;
import com.squareup.picasso.Picasso;

public class StudyFragment extends Fragment implements OnClickListener{

	private View view;
	private FrameLayout study_daily_sentence,study_spoken_english,study_dailog,study_test,study_to_all_user;
	private TextView dailysentence_txt;
	private ImageView daily_sentence_item_img;
	private ImageView play_img;
	private LinearLayout study_ad_view;
	private FrameLayout instagram_layout;
	public static StudyFragment mMainFragment;
	private SharedPreferences mSharedPreferences;
	private EveryDaySentence mEveryDaySentence;
	private IFLYBannerAd mIFLYBannerAd;
	private FragmentProgressbarListener mProgressbarListener;
	private MediaPlayer mPlayer;
	private String fileFullName;
	private boolean isInitMedia;
	
	public static StudyFragment getInstance(){
		if(mMainFragment == null){
			mMainFragment = new StudyFragment();
		}
		return mMainFragment;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mProgressbarListener = (FragmentProgressbarListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement FragmentProgressbarListener");
        }
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
		mPlayer = new MediaPlayer();
		mSharedPreferences = getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);
		study_daily_sentence = (FrameLayout)view.findViewById(R.id.study_daily_sentence);
		study_spoken_english = (FrameLayout)view.findViewById(R.id.study_spoken_english);
		instagram_layout = (FrameLayout)view.findViewById(R.id.instagram_layout);
		study_dailog = (FrameLayout)view.findViewById(R.id.study_dailog);
		study_test = (FrameLayout)view.findViewById(R.id.study_test);
		study_to_all_user = (FrameLayout)view.findViewById(R.id.study_to_all_user);
		study_ad_view = (LinearLayout)view.findViewById(R.id.study_ad_view);
		dailysentence_txt = (TextView)view.findViewById(R.id.dailysentence_txt);
		daily_sentence_item_img = (ImageView)view.findViewById(R.id.daily_sentence_item_img);
		play_img = (ImageView)view.findViewById(R.id.play_img);
		
		study_daily_sentence.setOnClickListener(this);
		study_spoken_english.setOnClickListener(this);
		instagram_layout.setOnClickListener(this);
		study_dailog.setOnClickListener(this);
		study_test.setOnClickListener(this);
		study_to_all_user.setOnClickListener(this);
		dailysentence_txt.setOnClickListener(this);
		study_ad_view.setOnClickListener(this);
		play_img.setOnClickListener(this);
		addAd();
	}
	
	private void addAd(){
		if(ADUtil.isShowAd(getActivity())){
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
					study_ad_view.setVisibility(View.GONE);
				}
				@Override
				public void onAdClose() {
				}
				@Override
				public void onAdClick() {
					StatService.onEvent(getActivity(), "ad_banner", "点击banner广告", 1);
				}
			});
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
			Picasso.with(getActivity())
			.load(mEveryDaySentence.getPicture2())
			.tag(getActivity())
			.into(daily_sentence_item_img);
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
			StatService.onEvent(getActivity(), "tab_study_words", "学习-口语修炼", 1);
			break;
		case R.id.study_dailog:
			toStudyDialoListgActivity();
			StatService.onEvent(getActivity(), "tab_study_dialog", "学习-模拟对话", 1);
			break;
		case R.id.study_test:
			toEvaluationActivity();
			StatService.onEvent(getActivity(), "tab_study_test", "学习-口语评测", 1);
			break;
		case R.id.study_to_all_user:
			toGetfansActivity();
			StatService.onEvent(getActivity(), "tab_study_to_all_user", "致所有用户", 1);
			break;
		case R.id.instagram_layout:
			toEnglishWebsiteRecommendActivity();
			break;
		case R.id.play_img:
			if(mEveryDaySentence != null){
				int pos = mEveryDaySentence.getTts().lastIndexOf(SDCardUtil.Delimiter) + 1;
				String fileName = mEveryDaySentence.getTts().substring(pos, mEveryDaySentence.getTts().length());
				fileFullName = SDCardUtil.getDownloadPath(SDCardUtil.DailySentencePath) + fileName;
				LogUtil.DefalutLog("fileName:"+fileName+"---fileFullName:"+fileFullName);
				if(SDCardUtil.isFileExist(fileFullName)){
					playMp3(fileFullName);
					LogUtil.DefalutLog("FileExist");
				}else{
					LogUtil.DefalutLog("FileNotExist");
					loadding();
					DownLoadUtil.downloadFile(getActivity(), mEveryDaySentence.getTts(), SDCardUtil.DailySentencePath, fileName, mHandler);
				}
			}
			StatService.onEvent(getActivity(), "play_daily_sentence", "播放每日一句", 1);
			break;
		default:
			break;
		}
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 1){
				finishLoadding();
				playMp3(fileFullName);
			}
		}
	};
	
	private void playMp3(String uriPath){
		try {
			if(mEveryDaySentence != null && !isInitMedia){
				isInitMedia = true;
				play_img.setBackgroundResource(R.drawable.ic_pause_circle_outline_white_48dp);
				Uri uri = Uri.parse(uriPath);
				mPlayer.setDataSource(getActivity(), uri);
				mPlayer.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						play_img.setBackgroundResource(R.drawable.ic_play_circle_outline_white_48dp);
					}
				});
				mPlayer.prepare();
				mPlayer.start();
			}else{
				if(mPlayer.isPlaying()){
					play_img.setBackgroundResource(R.drawable.ic_play_circle_outline_white_48dp);
					mPlayer.pause();
				}else{
					play_img.setBackgroundResource(R.drawable.ic_pause_circle_outline_white_48dp);
					mPlayer.start();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	private void toEnglishWebsiteRecommendActivity(){
		Intent intent = new Intent(getActivity(),EnglishWebsiteRecommendActivity.class);
		getActivity().startActivity(intent);
		StatService.onEvent(getActivity(), "leisure_to_english_website", "休闲页去英文网站页面", 1);
	}
	
	private void toDailySentenceActivity(){
		Intent intent = new Intent(getActivity(),DailySentenceActivity.class);
		startActivity(intent);
		StatService.onEvent(getActivity(), "tab_study_todailysentence", "首页去每日一句页面按钮", 1);
	}
	
	private void toGetfansActivity(){
		Intent intent = new Intent(getActivity(),GetFansActivity.class);
		startActivity(intent);
		StatService.onEvent(getActivity(), "tab_study_todeveloping", "首页去正在开发页", 1);
	}
	
	private void toStudyListActivity(){
		Intent intent = new Intent(getActivity(),PracticeCategoryActivity.class);
		startActivity(intent);
		StatService.onEvent(getActivity(), "tab_study_tostudylist", "首页去口语练习页面", 1);
	}
	
	private void toStudyDialoListgActivity(){
		Intent intent = new Intent(getActivity(),StudyDialogCategoryActivity.class);
		startActivity(intent);
		StatService.onEvent(getActivity(), "tab_study_tostudylist", "首页去模拟对话页面", 1);
	}
	
	private void toEvaluationActivity(){
		Intent intent = new Intent(getActivity(),EvaluationCategoryActivity.class);
		startActivity(intent);
		StatService.onEvent(getActivity(), "tab_study_tostudylist", "首页去口语评测页面", 1);
	}
	
	private void toGetContentActivity(String title){
		Intent intent = new Intent(getActivity(),GetContentActivity.class);
		intent.putExtra(KeyUtil.ActionbarTitle, title);
		startActivity(intent);
		StatService.onEvent(getActivity(), "tab_study_toalluser", "首页致所有用户按钮", 1);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (mPlayer != null) {
			if(mPlayer.isPlaying()){
				play_img.setBackgroundResource(R.drawable.ic_play_circle_outline_white_48dp);
				mPlayer.pause();  
			}
		}   
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mPlayer != null) {   
			mPlayer.stop();  
			mPlayer.release();   
			mPlayer = null;   
        }   
	}
	
	/**
	 * 通过接口回调activity执行进度条显示控制
	 */
	private void loadding(){
		if(mProgressbarListener != null){
			mProgressbarListener.showProgressbar();
		}
	}
	
	/**
	 * 通过接口回调activity执行进度条显示控制
	 */
	private void finishLoadding(){
		if(mProgressbarListener != null){
			mProgressbarListener.hideProgressbar();
		}
	}
	
}
