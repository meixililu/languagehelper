package com.messi.languagehelper;

import java.util.List;

import org.apache.http.Header;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;

import com.baidu.mobstat.StatService;
import com.gc.materialdesign.views.ButtonRectangle;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.messi.languagehelper.adapter.DictionaryListViewAdapter;
import com.messi.languagehelper.dao.Dictionary;
import com.messi.languagehelper.dao.record;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.http.LanguagehelperHttpClient;
import com.messi.languagehelper.http.RequestParams;
import com.messi.languagehelper.http.TextHttpResponseHandler;
import com.messi.languagehelper.impl.FragmentProgressbarListener;
import com.messi.languagehelper.util.BaiduStatistics;
import com.messi.languagehelper.util.JsonParser;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.ToastUtil;
import com.messi.languagehelper.util.ViewUtil;
import com.messi.languagehelper.util.XFUtil;

public class DictionaryFragment extends Fragment implements OnClickListener {

	private EditText input_et;
	private ButtonRectangle submit_btn;
	private FrameLayout clear_btn_layout;
	private Button voice_btn;
	private LinearLayout speak_round_layout;
	private RadioButton cb_speak_language_ch,cb_speak_language_en;
	private ListView recent_used_lv;
	/**record**/
	private LinearLayout record_layout;
	private ImageView record_anim_img;
	
	private LayoutInflater mInflater;
	private DictionaryListViewAdapter mAdapter;
	private List<Dictionary> beans;
	private Animation fade_in,fade_out;
	
	private Dictionary mDictionaryBean;

	// 识别对象
	private SpeechRecognizer recognizer;
	// 缓存，保存当前的引擎参数到下一次启动应用程序使用.
	private SharedPreferences mSharedPreferences;
	//合成对象.
	private SpeechSynthesizer mSpeechSynthesizer;

	public static int speed;
	private boolean isSpeakYueyu;
	public static boolean isSpeakYueyuNeedUpdate;
	private Bundle bundle;
	public static boolean isRefresh;
	private View view;
	public static DictionaryFragment mMainFragment;
	private FragmentProgressbarListener mProgressbarListener;
	
	public static DictionaryFragment getInstance(Bundle bundle){
		if(mMainFragment == null){
			mMainFragment = new DictionaryFragment();
			mMainFragment.bundle = bundle;
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.DefalutLog("MainFragment-onCreate");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LogUtil.DefalutLog("MainFragment-onCreateView");
		view = inflater.inflate(R.layout.fragment_dictionary, null);
		init();
		return view;
	}

	private void init() {
		mInflater = LayoutInflater.from(getActivity());
		mSharedPreferences = getActivity().getSharedPreferences(getActivity().getPackageName(), Activity.MODE_PRIVATE);
		
		mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(getActivity(), null);
		recognizer = SpeechRecognizer.createRecognizer(getActivity(), null);
		beans = DataBaseUtil.getInstance().getDataListDictionary(0, Settings.offset);
		mAdapter = new DictionaryListViewAdapter(getActivity(), mInflater, beans, 
				mSpeechSynthesizer, mSharedPreferences, bundle);
		
		recent_used_lv = (ListView) view.findViewById(R.id.recent_used_lv);
		input_et = (EditText) view.findViewById(R.id.input_et);
		submit_btn = (ButtonRectangle) view.findViewById(R.id.submit_btn);
		cb_speak_language_ch = (RadioButton) view.findViewById(R.id.cb_speak_language_ch);
		cb_speak_language_en = (RadioButton) view.findViewById(R.id.cb_speak_language_en);
		speak_round_layout = (LinearLayout) view.findViewById(R.id.speak_round_layout);
		clear_btn_layout = (FrameLayout) view.findViewById(R.id.clear_btn_layout);
		record_layout = (LinearLayout) view.findViewById(R.id.record_layout);
		record_anim_img = (ImageView) view.findViewById(R.id.record_anim_img);
		fade_in = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
		fade_out = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
		voice_btn = (Button) view.findViewById(R.id.voice_btn);
		
		initLanguage();
		submit_btn.setOnClickListener(this);
		cb_speak_language_ch.setOnClickListener(this);
		cb_speak_language_en.setOnClickListener(this);
		speak_round_layout.setOnClickListener(this);
		clear_btn_layout.setOnClickListener(this);
		
		recent_used_lv.addFooterView( ViewUtil.getListFooterView(getActivity()) );
		recent_used_lv.setAdapter(mAdapter);
		getAccent();
		speed = mSharedPreferences.getInt(getString(R.string.preference_key_tts_speed), 50);
	}
	
	private void initLanguage(){
		if(mSharedPreferences != null){
			String selectedLanguage = getSpeakLanguage();
			if(selectedLanguage.equals(XFUtil.VoiceEngineCH)){
				XFUtil.setSpeakLanguage(getActivity(),mSharedPreferences,XFUtil.VoiceEngineCH);
				cb_speak_language_ch.setChecked(true);
				cb_speak_language_en.setChecked(false);
			}else{
				XFUtil.setSpeakLanguage(getActivity(),mSharedPreferences,XFUtil.VoiceEngineEN);
				cb_speak_language_ch.setChecked(false);
				cb_speak_language_en.setChecked(true);
			}
		}
	}
	
	private void resetLanguage(){
		if(mSharedPreferences != null && cb_speak_language_ch != null){
			if(cb_speak_language_ch.isChecked()){
				XFUtil.setSpeakLanguage(getActivity(),mSharedPreferences,XFUtil.VoiceEngineCH);
			}else{
				XFUtil.setSpeakLanguage(getActivity(),mSharedPreferences,XFUtil.VoiceEngineEN);
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.submit_btn) {
			hideIME();
			submit();
			StatService.onEvent(getActivity(), "1.6_fanyibtn", "翻译按钮", 1);
		}else if (v.getId() == R.id.speak_round_layout) {
			showIatDialog();
			StatService.onEvent(getActivity(), "1.6_shuohuabtn", "说话按钮", 1);
		}else if (v.getId() == R.id.clear_btn_layout) {
			input_et.setText("");
			StatService.onEvent(getActivity(), "1.6_clearbtn", "清空按钮", 1);
		}else if (v.getId() == R.id.baidu_translate) {
			try {
				Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("http://fanyi.baidu.com"));
				startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}else if (v.getId() == R.id.cb_speak_language_ch) {
			cb_speak_language_en.setChecked(false);
			XFUtil.setSpeakLanguage(getActivity(),mSharedPreferences,XFUtil.VoiceEngineCH);
			if(isSpeakYueyu){
				ToastUtil.diaplayMesShort(getActivity(), getActivity().getResources().getString(R.string.speak_chinese));
			}else{
				ToastUtil.diaplayMesShort(getActivity(), getActivity().getResources().getString(R.string.speak_chinese));
			}
			StatService.onEvent(getActivity(), "1.6_putonghuabtn", "普通话按钮", 1);
		}else if (v.getId() == R.id.cb_speak_language_en) {
			cb_speak_language_ch.setChecked(false);
			XFUtil.setSpeakLanguage(getActivity(),mSharedPreferences,XFUtil.VoiceEngineEN);
			ToastUtil.diaplayMesShort(getActivity(), getActivity().getResources().getString(R.string.speak_english));
			StatService.onEvent(getActivity(), "1.6_yingyubtn", "英语按钮", 1);
		}
	}
	
	@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtil.DefalutLog("MainFragment-setUserVisibleHint");
        if (isVisibleToUser) {
        	if(isRefresh){
        		isRefresh = false;
        		new WaitTask().execute();
        	}
        	resetLanguage();
        }
	}
	
	@Override
	public void onResume() {
		super.onResume();
		LogUtil.DefalutLog("MainFragment-onResume");
		if(isSpeakYueyuNeedUpdate){
        	getAccent();
        	if(cb_speak_language_ch.isChecked()){
        		XFUtil.setSpeakLanguage(getActivity(),mSharedPreferences,XFUtil.VoiceEngineCH);
        	}
        	isSpeakYueyuNeedUpdate = false;
        }
		setUserVisibleHint(true);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		LogUtil.DefalutLog("MainFragment-onPause");
		if(mSharedPreferences != null && cb_speak_language_ch != null){
			if(cb_speak_language_ch.isChecked()){
				Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.UserSelectLanguage, XFUtil.VoiceEngineCH);
			}else{
				Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.UserSelectLanguage, XFUtil.VoiceEngineEN);
			}
		}
	}
	
	private void getAccent(){
		isSpeakYueyu = mSharedPreferences.getBoolean(KeyUtil.SpeakPutonghuaORYueyu, false);
    	if(isSpeakYueyu){
			cb_speak_language_ch.setText("粤语");
		}else{
			cb_speak_language_ch.setText(getActivity().getResources().getString(R.string.chinese));
		}
	}
	
	class WaitTask extends AsyncTask<Void, Void, Void>{
		@Override
		protected void onPreExecute() {
			loadding();
		}
		@Override
		protected Void doInBackground(Void... params) {
			try {
				beans = DataBaseUtil.getInstance().getDataListDictionary(0, Settings.offset);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			finishLoadding();
			mAdapter.notifyDataChange(beans,0);
		}
	}
	
	/**get defalut speaker 
	 * @return
	 */
	private String getSpeakLanguage(){
		return mSharedPreferences.getString(KeyUtil.UserSelectLanguage, XFUtil.VoiceEngineCH);
	}
	
	/**
	 * send translate request
	 */
	private void RequestAsyncTask(){
		loadding();
		submit_btn.setEnabled(false);
		RequestParams mRequestParams = new RequestParams();
		mRequestParams.put("client_id", Settings.client_id);
		mRequestParams.put("q", Settings.q);
		mRequestParams.put("from", Settings.from);
		mRequestParams.put("to", Settings.to);
		LanguagehelperHttpClient.get(Settings.baiduDictionaryUrl, mRequestParams, new TextHttpResponseHandler() {
			@Override
			public void onFinish() {
				super.onFinish();
				finishLoadding();
				submit_btn.setEnabled(true);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,String responseString, Throwable throwable) {
				showToast("Error("+statusCode+")");
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				if (!TextUtils.isEmpty(responseString)) {
					LogUtil.DefalutLog(responseString);
					mDictionaryBean = JsonParser.parseDictionaryJson(responseString);
					if(mDictionaryBean != null){
						setData();
					}else{
						GetDictionaryFaultAsyncTask();
					}
					if(mSharedPreferences.getBoolean(KeyUtil.AutoPlayResult, false)){
						new AutoPlayWaitTask().execute();
					}
				} else {
					showToast(getActivity().getResources().getString(R.string.network_error));
				}
			}
		});
	}
	
	private void GetDictionaryFaultAsyncTask(){
		loadding();
		submit_btn.setEnabled(false);
		Settings.q = input_et.getText().toString().trim();
		RequestParams mRequestParams = new RequestParams();
		mRequestParams.put("client_id", Settings.client_id);
		mRequestParams.put("q", Settings.q);
		mRequestParams.put("from", Settings.from);
		mRequestParams.put("to", Settings.to);
		LanguagehelperHttpClient.get(Settings.baiduTranslateUrl, mRequestParams, new TextHttpResponseHandler() {
			@Override
			public void onFinish() {
				super.onFinish();
				finishLoadding();
				submit_btn.setEnabled(true);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,String responseString, Throwable throwable) {
				showToast("Error("+statusCode+")");
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				if (!TextUtils.isEmpty(responseString)) {
					LogUtil.DefalutLog(responseString);
					String dstString = JsonParser.getTranslateResult(responseString);
					if (dstString.contains("error_msg:")) {
						showToast(dstString);
					} else {
						mDictionaryBean = new Dictionary();
						mDictionaryBean.setType(KeyUtil.ResultTypeTranslate);
						mDictionaryBean.setWord_name(Settings.q);
						mDictionaryBean.setResult(dstString);
						DataBaseUtil.getInstance().insert(mDictionaryBean);
						setData();
					}
				} else {
					showToast(getActivity().getResources().getString(R.string.network_error));
				}
			}
		});
	}
	
	private void setData(){
		beans.add(0,mDictionaryBean);
		mAdapter.notifyDataSetChanged();
		recent_used_lv.setSelection(0);
	}
	
	class AutoPlayWaitTask extends AsyncTask<Void, Void, Void>{
		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			autoPlay();
		}
	}
	
	
	private void autoPlay(){
		View mView = recent_used_lv.getChildAt(0);
		FrameLayout record_answer_cover = (FrameLayout) mView.findViewById(R.id.record_answer_cover); 
		record_answer_cover.callOnClick();
	}

	/**toast message
	 * @param toastString
	 */
	private void showToast(String toastString) {
		ToastUtil.diaplayMesShort(getActivity(), toastString);
	}

	/**
	 * 显示转写对话框.
	 */
	public void showIatDialog() {
		if(!recognizer.isListening()){
			record_layout.setVisibility(View.VISIBLE);
			input_et.setText("");
			voice_btn.setBackgroundColor(getActivity().getResources().getColor(R.color.none));
			voice_btn.setText(getActivity().getResources().getString(R.string.finish));
			speak_round_layout.setBackgroundResource(R.drawable.round_light_blue_bgl);
			XFUtil.showSpeechRecognizer(getActivity(),mSharedPreferences,recognizer,recognizerListener);
		}else{
			finishRecord();
			recognizer.stopListening();
			loadding();
		}
	}
	
	/**
	 * finish record
	 */
	private void finishRecord(){
		record_layout.setVisibility(View.GONE);
		record_anim_img.setBackgroundResource(R.drawable.speak_voice_1);
		voice_btn.setText("");
		voice_btn.setBackgroundResource(R.drawable.ic_voice_padded_normal);
		speak_round_layout.setBackgroundResource(R.drawable.round_gray_bgl);
	}
	
	RecognizerListener recognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {
			LogUtil.DefalutLog("onBeginOfSpeech");
		}

		@Override
		public void onError(SpeechError err) {
			LogUtil.DefalutLog("onError:"+err.getErrorDescription());
			finishRecord();
			ToastUtil.diaplayMesShort(getActivity(), err.getErrorDescription());
			finishLoadding();
		}

		@Override
		public void onEndOfSpeech() {
			LogUtil.DefalutLog("onEndOfSpeech");
			loadding();
			finishRecord();
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			LogUtil.DefalutLog("onResult");
			String text = JsonParser.parseIatResult(results.getResultString());
			input_et.append(text);
			input_et.setSelection(input_et.length());
			if(isLast) {
				finishRecord();
				submit();
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
	
	/**
	 * 点击翻译之后隐藏输入法
	 */
	private void hideIME(){
		final InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);       
		imm.hideSoftInputFromWindow(submit_btn.getWindowToken(), 0); 
	}
	
	/**
	 * 点击编辑之后显示输入法
	 */
	private void showIME(){
		final InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);       
		imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS); 
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
	
	/**
	 * submit request task
	 */
	private void submit(){
		Settings.q = input_et.getText().toString().trim();
		Settings.q =  Settings.q.replaceAll("[\\p{P}]", "");
		if (!TextUtils.isEmpty(Settings.q)) {
			RequestAsyncTask();
			StatService.onEvent(getActivity(), BaiduStatistics.TranslateBtn, "翻译按钮", 1);
		} else {
			showToast(getActivity().getResources().getString(R.string.input_et_hint));
			finishLoadding();
		}
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
		LogUtil.DefalutLog("MainFragment-onDestroy");
	}
}
