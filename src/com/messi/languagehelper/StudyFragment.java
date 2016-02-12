package com.messi.languagehelper;

import java.util.List;

import com.baidu.mobstat.StatService;
import com.bumptech.glide.Glide;
import com.messi.languagehelper.dao.EveryDaySentence;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.http.LanguagehelperHttpClient;
import com.messi.languagehelper.http.UICallback;
import com.messi.languagehelper.impl.FragmentProgressbarListener;
import com.messi.languagehelper.util.ADUtil;
import com.messi.languagehelper.util.AVOUtil;
import com.messi.languagehelper.util.DownLoadUtil;
import com.messi.languagehelper.util.GytUtil;
import com.messi.languagehelper.util.JsonParser;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.NumberUtil;
import com.messi.languagehelper.util.SDCardUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.TextHandlerUtil;
import com.messi.languagehelper.util.TimeUtil;
import com.messi.languagehelper.views.ProportionalImageView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
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
import android.widget.TextView;
import cn.contentx.ContExManager;

public class StudyFragment extends Fragment implements OnClickListener{

	private View view;
	private FrameLayout study_daily_sentence,study_spoken_english,study_composition,study_test;
	private FrameLayout symbol_study_cover;
	private FrameLayout word_study_cover;
	private FrameLayout en_examination_layout;
	private TextView dailysentence_txt;
	private ProportionalImageView daily_sentence_item_img;
	private ImageView play_img;
	private FrameLayout instagram_layout,news_layout;
	public static StudyFragment mMainFragment;
	private SharedPreferences mSharedPreferences;
	private EveryDaySentence mEveryDaySentence;
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
		news_layout = (FrameLayout)view.findViewById(R.id.news_layout);
		en_examination_layout = (FrameLayout)view.findViewById(R.id.en_examination_layout);
		study_composition = (FrameLayout)view.findViewById(R.id.study_composition);
		study_test = (FrameLayout)view.findViewById(R.id.study_test);
		symbol_study_cover = (FrameLayout)view.findViewById(R.id.symbol_study_cover);
		word_study_cover = (FrameLayout)view.findViewById(R.id.word_study_cover);
		dailysentence_txt = (TextView)view.findViewById(R.id.dailysentence_txt);
		daily_sentence_item_img = (ProportionalImageView)view.findViewById(R.id.daily_sentence_item_img);
		play_img = (ImageView)view.findViewById(R.id.play_img);
		
		study_daily_sentence.setOnClickListener(this);
		study_spoken_english.setOnClickListener(this);
		instagram_layout.setOnClickListener(this);
		study_composition.setOnClickListener(this);
		study_test.setOnClickListener(this);
		symbol_study_cover.setOnClickListener(this);
		word_study_cover.setOnClickListener(this);
		news_layout.setOnClickListener(this);
		en_examination_layout.setOnClickListener(this);
		play_img.setOnClickListener(this);
		addAd();
	}
	
	private void addAd(){
		if(ADUtil.isShowAd(getActivity())){
			
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
		LanguagehelperHttpClient.get(Settings.DailySentenceUrl, new UICallback(getActivity()){
			public void onResponsed(String responseString) {
				if(JsonParser.isJson(responseString)){
					mEveryDaySentence = JsonParser.parseEveryDaySentence(responseString);
					setSentence();
				}
			}
		});
	}
	
	private void setSentence(){
		LogUtil.DefalutLog("StudyFragment-setSentence()");
		if(mEveryDaySentence != null){
			TextHandlerUtil.handlerText(getActivity(), null, dailysentence_txt, mEveryDaySentence.getContent());
			Glide.with(getActivity())
			.load(mEveryDaySentence.getPicture2())
			.into(daily_sentence_item_img);
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.symbol_study_cover:
			toSymbolListActivity();
			break;
		case R.id.word_study_cover:
			toWordStudyListActivity();
			break;
		case R.id.study_daily_sentence:
			toDailySentenceActivity();
			break;
		case R.id.study_spoken_english:
			toStudyListActivity();
			StatService.onEvent(getActivity(), "tab_study_words", "学习-口语修炼", 1);
			break;
		case R.id.study_composition:
			toStudyCompositionActivity();
			StatService.onEvent(getActivity(), "tab_study_dialog", "学习-模拟对话", 1);
			break;
		case R.id.study_test:
			toEvaluationActivity();
			StatService.onEvent(getActivity(), "tab_study_test", "学习-口语评测", 1);
			break;
		case R.id.instagram_layout:
			toEnglishWebsiteRecommendActivity();
			break;
		case R.id.news_layout:
			ContExManager.initWithAPPId(getActivity(),"61181d6c-9093-4735-93d1-9b07d50e5ab2", "w1461Eub");
			GytUtil.showHtml(getActivity(), getActivity().getResources().getString(R.string.reading));
			break;
		case R.id.en_examination_layout:
			ContExManager.initWithAPPId(getActivity(),"c18b33e973d147159ee52a8debac9b4c", "w1461Eub");
			GytUtil.showHtml(getActivity(), getActivity().getResources().getString(R.string.examination));
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
					DownLoadUtil.downloadFile(getContext(), mEveryDaySentence.getTts(), SDCardUtil.DailySentencePath, fileName, mHandler);
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
			}else if(msg.what == 2){
				finishLoadding();
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
	
	private void toSymbolListActivity(){
		Intent intent = new Intent(getActivity(),SymbolListActivity.class);
		startActivity(intent);
		StatService.onEvent(getActivity(), "tab_symbol_study", "去音标学习页面", 1);
	}
	
	private void toWordStudyListActivity(){
		Intent intent = new Intent(getActivity(),WordStudyListActivity.class);
		startActivity(intent);
		StatService.onEvent(getActivity(), "tab_symbol_study", "去音标学习页面", 1);
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
	
	private void toStudyCompositionActivity(){
		Intent intent = new Intent(getActivity(),CompositionActivity.class);
		startActivity(intent);
		StatService.onEvent(getActivity(), "tab_study_tostudylist", "首页去模拟对话页面", 1);
	}
	
	private void toEvaluationActivity(){
		Intent intent = new Intent(getActivity(),EvaluationTypeActivity.class);
		startActivity(intent);
		StatService.onEvent(getActivity(), "tab_study_tostudylist", "首页去口语评测页面", 1);
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
	public void loadding(){
		if(mProgressbarListener != null){
			mProgressbarListener.showProgressbar();
		}
	}
	
	/**
	 * 通过接口回调activity执行进度条显示控制
	 */
	public void finishLoadding(){
		if(mProgressbarListener != null){
			mProgressbarListener.hideProgressbar();
		}
	}
	
}
