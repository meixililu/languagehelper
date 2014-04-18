package com.messi.languagehelper;

import java.util.List;

import org.apache.http.Header;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.iflytek.cloud.speech.RecognizerListener;
import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechRecognizer;
import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.messi.languagehelper.CollectedFragment.WaitTask;
import com.messi.languagehelper.adapter.CollectedListItemAdapter;
import com.messi.languagehelper.bean.DialogBean;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.http.LanguagehelperHttpClient;
import com.messi.languagehelper.http.RequestParams;
import com.messi.languagehelper.http.TextHttpResponseHandler;
import com.messi.languagehelper.util.BaiduStatistics;
import com.messi.languagehelper.util.JsonParser;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.SharedPreferencesUtil;
import com.messi.languagehelper.util.ToastUtil;
import com.messi.languagehelper.util.XFUtil;
import com.messi.languagehelper.wxapi.WXEntryActivity;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

public class MainFragment extends Fragment implements OnClickListener, IWXAPIEventHandler {

	private EditText input_et;
	private FrameLayout submit_btn;
	private LinearLayout baidu_translate;
	private FrameLayout clear_btn_layout;
	private Button voice_btn;
	private LinearLayout speak_round_layout;
	private RadioButton cb_speak_language_ch,cb_speak_language_en;
	private ListView recent_used_lv;
	/**record**/
	private LinearLayout record_layout;
	private ImageView record_anim_img;
	private DialogBean currentDialogBean;
	
	private LayoutInflater mInflater;
	private CollectedListItemAdapter mAdapter;
	private List<DialogBean> beans;
	private String dstString = "";
	private IWXAPI api;
	private Animation fade_in,fade_out;

	// 识别对象
	private SpeechRecognizer recognizer;
	// 缓存，保存当前的引擎参数到下一次启动应用程序使用.
	private SharedPreferences mSharedPreferences;
	//合成对象.
	private SpeechSynthesizer mSpeechSynthesizer;

	public static int speed;
	private boolean isSpeakYueyu;
	public static boolean isSpeakYueyuNeedUpdate;
	private DataBaseUtil mDataBaseUtil;
	private Bundle bundle;
	public static boolean isRespondWX;
	public static boolean isRefresh;
	private View view;
	private static MainFragment mMainFragment;
	
	public static MainFragment getInstance(Bundle bundle){
		if(mMainFragment == null){
			mMainFragment = new MainFragment();
			mMainFragment.bundle = bundle;
		}
		return mMainFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.activity_main, null);
		init();
		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isRespondWX = false;
		LogUtil.DefalutLog("MainFragment-onDestroy");
	}

	private void init() {
		mInflater = LayoutInflater.from(getActivity());
		mSharedPreferences = getActivity().getSharedPreferences(getActivity().getPackageName(), Activity.MODE_PRIVATE);
		recent_used_lv = (ListView) view.findViewById(R.id.recent_used_lv);
		input_et = (EditText) view.findViewById(R.id.input_et);
		submit_btn = (FrameLayout) view.findViewById(R.id.submit_btn_layout);
		cb_speak_language_ch = (RadioButton) view.findViewById(R.id.cb_speak_language_ch);
		cb_speak_language_en = (RadioButton) view.findViewById(R.id.cb_speak_language_en);
		speak_round_layout = (LinearLayout) view.findViewById(R.id.speak_round_layout);
		clear_btn_layout = (FrameLayout) view.findViewById(R.id.clear_btn_layout);
		record_layout = (LinearLayout) view.findViewById(R.id.record_layout);
		record_anim_img = (ImageView) view.findViewById(R.id.record_anim_img);
		fade_in = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
		fade_out = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
		voice_btn = (Button) view.findViewById(R.id.voice_btn);
		
		boolean IsHasShowBaiduMessage = mSharedPreferences.getBoolean(SharedPreferencesUtil.IsHasShowBaiduMessage, false);
		if(!IsHasShowBaiduMessage){
			View listviewFooter = mInflater.inflate(R.layout.listview_item_recent_used_footer, null);
			baidu_translate = (LinearLayout) listviewFooter.findViewById(R.id.baidu_translate);
			recent_used_lv.addFooterView(listviewFooter);
			SharedPreferencesUtil.saveBoolean(mSharedPreferences, SharedPreferencesUtil.IsHasShowBaiduMessage, true);
			baidu_translate.setOnClickListener(this);
		}
		
		mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(getActivity());
		recognizer = SpeechRecognizer.createRecognizer(getActivity());
		
		submit_btn.setOnClickListener(this);
		cb_speak_language_ch.setOnClickListener(this);
		cb_speak_language_en.setOnClickListener(this);
		speak_round_layout.setOnClickListener(this);
		clear_btn_layout.setOnClickListener(this);
		mDataBaseUtil = new DataBaseUtil(getActivity());
		beans = mDataBaseUtil.getDataList(0, Settings.offset);
		mAdapter = new CollectedListItemAdapter(getActivity(), mInflater, beans, 
				mSpeechSynthesizer, mSharedPreferences, mDataBaseUtil, bundle, "MainFragment");
		recent_used_lv.setAdapter(mAdapter);
		
		getAccent();
		String selectedLanguage = getSpeakLanguage();
		if(selectedLanguage.equals(XFUtil.VoiceEngineCH)){
			cb_speak_language_ch.setChecked(true);
			cb_speak_language_en.setChecked(false);
		}else{
			cb_speak_language_ch.setChecked(false);
			cb_speak_language_en.setChecked(true);
			ToastUtil.diaplayMesShort(getActivity(), "请说英文");
		}
		speed = mSharedPreferences.getInt(getString(R.string.preference_key_tts_speed), 50);
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.submit_btn_layout) {
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
			setSpeakLanguage(XFUtil.VoiceEngineCH);
			if(isSpeakYueyu){
				ToastUtil.diaplayMesShort(getActivity(), "请说粤语");
			}else{
				ToastUtil.diaplayMesShort(getActivity(), "请说普通话");
			}
			StatService.onEvent(getActivity(), "1.6_putonghuabtn", "普通话按钮", 1);
		}else if (v.getId() == R.id.cb_speak_language_en) {
			cb_speak_language_ch.setChecked(false);
			setSpeakLanguage(XFUtil.VoiceEngineEN);
			ToastUtil.diaplayMesShort(getActivity(), "请说英语");
			StatService.onEvent(getActivity(), "1.6_yingyubtn", "英语按钮", 1);
		}
	}
	
	@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
        	if(isRefresh){
        		isRefresh = false;
        		new WaitTask().execute();
        	}
        }
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(isSpeakYueyuNeedUpdate){
        	getAccent();
        	if(cb_speak_language_ch.isChecked()){
        		setSpeakLanguage(XFUtil.VoiceEngineCH);
        	}
        	isSpeakYueyuNeedUpdate = false;
        }
	}
	
	private void getAccent(){
		isSpeakYueyu = mSharedPreferences.getBoolean(SharedPreferencesUtil.SpeakPutonghuaORYueyu, false);
    	if(isSpeakYueyu){
			cb_speak_language_ch.setText("粤语");
		}else{
			cb_speak_language_ch.setText("普通话");
		}
	}
	
	class WaitTask extends AsyncTask<Void, Void, Void>{
		@Override
		protected void onPreExecute() {
			WXEntryActivity.mWXEntryActivity.setSupportProgressBarIndeterminateVisibility(true);
			WXEntryActivity.mWXEntryActivity.setSupportProgressBarVisibility(true);
		}
		@Override
		protected Void doInBackground(Void... params) {
			try {
				beans = mDataBaseUtil.getDataList(0, Settings.offset);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			WXEntryActivity.mWXEntryActivity.setSupportProgressBarIndeterminateVisibility(false);
			WXEntryActivity.mWXEntryActivity.setSupportProgressBarVisibility(false);
			mAdapter.notifyDataChange(beans,0);
		}
	}
	
	/**get defalut speaker 
	 * @return
	 */
	private String getSpeakLanguage(){
		return mSharedPreferences.getString(this.getString(R.string.preference_key_recognizer), XFUtil.VoiceEngineCH);
	}
	
	/**set speaker
	 * @param language
	 */
	private void setSpeakLanguage(String language){
		Editor mEditor = mSharedPreferences.edit();
		if(language.equals(XFUtil.VoiceEngineCH)){
			if(isSpeakYueyu){
				mEditor.putString(this.getString(R.string.preference_key_recognizer), XFUtil.VoiceEngineCH);
				mEditor.putString(this.getString(R.string.preference_key_accent), "cantonese");
			}else{
				mEditor.putString(this.getString(R.string.preference_key_recognizer),XFUtil.VoiceEngineCH);
				mEditor.putString(this.getString(R.string.preference_key_accent),"mandarin");
			}
		}else if(language.equals(XFUtil.VoiceEngineEN)){
			mEditor.putString(this.getString(R.string.preference_key_recognizer),XFUtil.VoiceEngineEN);
			mEditor.putString(this.getString(R.string.preference_key_accent), "");
		}
		mEditor.commit();
	}
	
	/**
	 * send translate request
	 */
	private void RequestAsyncTask(){
		WXEntryActivity.mWXEntryActivity.setSupportProgressBarIndeterminateVisibility(true);
		WXEntryActivity.mWXEntryActivity.setSupportProgressBarVisibility(true);
		submit_btn.setEnabled(false);
		RequestParams mRequestParams = new RequestParams();
		mRequestParams.put("client_id", Settings.client_id);
		mRequestParams.put("q", Settings.q);
		mRequestParams.put("from", Settings.from);
		mRequestParams.put("to", Settings.to);
		LanguagehelperHttpClient.post(mRequestParams, new TextHttpResponseHandler() {
			@Override
			public void onFinish() {
				super.onFinish();
				WXEntryActivity.mWXEntryActivity.setSupportProgressBarIndeterminateVisibility(false);
				WXEntryActivity.mWXEntryActivity.setSupportProgressBarVisibility(false);
				submit_btn.setEnabled(true);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,String responseString, Throwable throwable) {
				showToast("网络连接错误("+statusCode+")");
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				if (!TextUtils.isEmpty(responseString)) {
					LogUtil.DefalutLog(responseString);
					dstString = JsonParser.getTranslateResult(responseString);
					if (dstString.contains("error_msg:")) {
						showToast(dstString);
					} else {
						currentDialogBean = new DialogBean(dstString, Settings.q);
						long newRowId = mDataBaseUtil.insert(currentDialogBean);
						beans.add(0,currentDialogBean);
						mAdapter.notifyDataSetChanged();
						recent_used_lv.setSelection(0);
						LogUtil.DefalutLog("mDataBaseUtil:"+currentDialogBean.toString());
					}
				} else {
					showToast("网络连接错误，请稍后再试！");
				}
			}
		});
		
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
			voice_btn.setText("完毕");
			speak_round_layout.setBackgroundResource(R.drawable.round_light_blue_bgl);
			XFUtil.showSpeechRecognizer(getActivity(),mSharedPreferences,recognizer,recognizerListener);
		}else{
			finishRecord();
			recognizer.stopListening();
			WXEntryActivity.mWXEntryActivity.setSupportProgressBarIndeterminateVisibility(true);
			WXEntryActivity.mWXEntryActivity.setSupportProgressBarVisibility(true);
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
			WXEntryActivity.mWXEntryActivity.setSupportProgressBarIndeterminateVisibility(false);
			WXEntryActivity.mWXEntryActivity.setSupportProgressBarVisibility(false);
		}

		@Override
		public void onEndOfSpeech() {
			LogUtil.DefalutLog("onEndOfSpeech");
			WXEntryActivity.mWXEntryActivity.setSupportProgressBarIndeterminateVisibility(true);
			WXEntryActivity.mWXEntryActivity.setSupportProgressBarVisibility(true);
			finishRecord();
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, String msg) {
			LogUtil.DefalutLog("onEvent");
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
	 * submit request task
	 */
	private void submit(){
		Settings.q = input_et.getText().toString().trim();
		if (!TextUtils.isEmpty(Settings.q)) {
			RequestAsyncTask();
			StatService.onEvent(getActivity(), BaiduStatistics.TranslateBtn, "翻译按钮", 1);
		} else {
			showToast("请输入需要翻译的内容！");
			WXEntryActivity.mWXEntryActivity.setSupportProgressBarIndeterminateVisibility(false);
			WXEntryActivity.mWXEntryActivity.setSupportProgressBarVisibility(false);
		}
	}
	
	@Override
	public void onReq(BaseReq req) {
		switch (req.getType()) {
		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
			isRespondWX = true;	
			LogUtil.DefalutLog("respond wx");
			break;
		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
			isRespondWX = false;	
			LogUtil.DefalutLog("show message wx");
			break;
		}
	}

	@Override
	public void onResp(BaseResp resp) {
		int result = 0;
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			result = R.string.errcode_success;
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = R.string.errcode_cancel;
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = R.string.errcode_deny;
			break;
		default:
			result = R.string.errcode_unknown;
			break;
		}
		ToastUtil.diaplayMesShort(getActivity(), result);
		LogUtil.DefalutLog("onResp");
	}
	
}
