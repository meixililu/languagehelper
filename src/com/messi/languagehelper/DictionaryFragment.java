package com.messi.languagehelper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.avos.avoscloud.okhttp.FormEncodingBuilder;
import com.avos.avoscloud.okhttp.RequestBody;
import com.baidu.mobstat.StatService;
import com.gc.materialdesign.views.ButtonRectangle;
import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.messi.languagehelper.adapter.DictionaryListViewAdapter;
import com.messi.languagehelper.dao.BaiduOcrRoot;
import com.messi.languagehelper.dao.Dictionary;
import com.messi.languagehelper.dao.Root;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.dialog.PopDialog;
import com.messi.languagehelper.dialog.PopDialog.PopViewItemOnclickListener;
import com.messi.languagehelper.http.LanguagehelperHttpClient;
import com.messi.languagehelper.http.UICallback;
import com.messi.languagehelper.impl.FragmentProgressbarListener;
import com.messi.languagehelper.task.MyThread;
import com.messi.languagehelper.util.CameraUtil;
import com.messi.languagehelper.util.JsonParser;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.ToastUtil;
import com.messi.languagehelper.util.TranslateUtil;
import com.messi.languagehelper.util.ViewUtil;
import com.messi.languagehelper.util.XFUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
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

public class DictionaryFragment extends Fragment implements OnClickListener {

	private EditText input_et;
	private ButtonRectangle submit_btn;
	private FrameLayout photo_tran_btn;
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
	
	public static Dictionary mDictionaryBean;

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
	private boolean AutoClearInputAfterFinish;
	
	private String mCurrentPhotoPath;
	
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
		photo_tran_btn = (FrameLayout) view.findViewById(R.id.photo_tran_btn);
		cb_speak_language_ch = (RadioButton) view.findViewById(R.id.cb_speak_language_ch);
		cb_speak_language_en = (RadioButton) view.findViewById(R.id.cb_speak_language_en);
		speak_round_layout = (LinearLayout) view.findViewById(R.id.speak_round_layout);
		clear_btn_layout = (FrameLayout) view.findViewById(R.id.clear_btn_layout);
		record_layout = (LinearLayout) view.findViewById(R.id.record_layout);
		record_anim_img = (ImageView) view.findViewById(R.id.record_anim_img);
		fade_in = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
		fade_out = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
		voice_btn = (Button) view.findViewById(R.id.voice_btn);
		
		AutoClearInputAfterFinish = mSharedPreferences.getBoolean(KeyUtil.AutoClearInputAfterFinish, true);
		initLanguage();
		photo_tran_btn.setOnClickListener(this);
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
			AutoClearInputAfterFinish = mSharedPreferences.getBoolean(KeyUtil.AutoClearInputAfterFinish, true);
		}
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.submit_btn) {
			hideIME();
			submit();
			StatService.onEvent(getActivity(), "1.6_fanyibtn", "翻译按钮", 1);
		}else if (v.getId() == R.id.photo_tran_btn) {
			photoSelectDialog();
//			StatService.onEvent(getActivity(), "ask_baidu", "首页问百度", 1);
		}else if (v.getId() == R.id.speak_round_layout) {
			showIatDialog();
			StatService.onEvent(getActivity(), "1.6_shuohuabtn", "说话按钮", 1);
		}else if (v.getId() == R.id.clear_btn_layout) {
			input_et.setText("");
			StatService.onEvent(getActivity(), "1.6_clearbtn", "清空按钮", 1);
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
	
	private void photoSelectDialog(){
		String[] titles = {getResources().getString(R.string.take_photo),getResources().getString(R.string.photo_album)};
		PopDialog mPhonoSelectDialog = new PopDialog(getContext(),titles);
		mPhonoSelectDialog.setListener(new PopViewItemOnclickListener() {
			@Override
			public void onSecondClick(View v) {
				getImageFromAlbum();
			}
			@Override
			public void onFirstClick(View v) {
				getImageFromCamera();
			}
		});
		mPhonoSelectDialog.show();
	}
	
	public void getImageFromAlbum() {
		Intent intent = new Intent(Intent.ACTION_PICK, 
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, CameraUtil.REQUEST_CODE_PICK_IMAGE);
    }
	
	public void getImageFromCamera() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
				File photoFile = null;
		        try {
		            photoFile = CameraUtil.createImageFile();
		            mCurrentPhotoPath = photoFile.getAbsolutePath();
		        } catch (IOException ex) {
		            ex.printStackTrace();
		        }
		        if (photoFile != null) {
		            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photoFile));
		            startActivityForResult(takePictureIntent, CameraUtil.REQUEST_CODE_CAPTURE_CAMEIA);
		        }
			} else {
				ToastUtil.diaplayMesShort(getContext(), "请确认已经插入SD卡");
			}
		}
	}
	
	public void doCropPhoto(Uri uri) {
		File photoTemp = null;
        try {
        	photoTemp = new File(CameraUtil.createTempFile());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection",  false); 
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoTemp));
		startActivityForResult(intent, CameraUtil.PHOTO_PICKED_WITH_DATA);
	}
	
	 @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CameraUtil.REQUEST_CODE_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
			if(data != null){
				Uri uri = data.getData();
				if(uri != null){
					doCropPhoto(uri);
				}
			}
		} else if (requestCode == CameraUtil.REQUEST_CODE_CAPTURE_CAMEIA && resultCode == Activity.RESULT_OK) {
			File f = new File(mCurrentPhotoPath);
    	    Uri contentUri = Uri.fromFile(f);
    	    doCropPhoto(contentUri);
		}else if (requestCode == CameraUtil.PHOTO_PICKED_WITH_DATA && resultCode == Activity.RESULT_OK) {
			sendBaiduOCR();
		}
	}
	 
	 public void sendBaiduOCR(){
			try {
				loadding();
				LanguagehelperHttpClient.postBaiduOCR(CameraUtil.createTempFile(), new UICallback(getActivity()){
					@Override
					public void onResponsed(String responseString){
						finishLoadding();
						if (!TextUtils.isEmpty(responseString)) {
							if(JsonParser.isJson(responseString)){
								BaiduOcrRoot mBaiduOcrRoot = JSON.parseObject(responseString, BaiduOcrRoot.class);
								if(mBaiduOcrRoot.getErrNum().equals("0")){
									input_et.setText("");
									input_et.setText(CameraUtil.getOcrResult(mBaiduOcrRoot));
								}else{
									ToastUtil.diaplayMesShort(getContext(), mBaiduOcrRoot.getErrMsg());
								}
							}
						} 
					}
					@Override
					public void onFailured() {
						finishLoadding();
						showToast(getActivity().getResources().getString(R.string.network_error));
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
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
	 * showapi dictionary api
	 */
	private void RequestTranslateApiTask(){
		loadding();
		submit_btn.setEnabled(false);
		TranslateUtil.Translate(getActivity(), mHandler);
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			onFinishRequest();
			if(msg.what == 1){
				setData();
			}else{
				showToast(getActivity().getResources().getString(R.string.network_error));
			}
		}
	};
	
	private void setData(){
		if(AutoClearInputAfterFinish){
			input_et.setText("");
		}
		mDictionaryBean = (Dictionary) BaseApplication.dataMap.get(KeyUtil.DataMapKey);
		BaseApplication.dataMap.clear();
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
	
	private void onFinishRequest(){
		finishLoadding();
		submit_btn.setEnabled(true);
	}
	
	private void autoPlay(){
		View mView = recent_used_lv.getChildAt(0);
		FrameLayout record_answer_cover = (FrameLayout) mView.findViewById(R.id.record_question_cover); 
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
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
			
		}

		@Override
		public void onVolumeChanged(int volume, byte[] arg1) {
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
		if (!TextUtils.isEmpty(Settings.q)) {
			String last = Settings.q.substring(Settings.q.length()-1);
			if(",.?!;:'，。？！‘；：".contains(last)){
				Settings.q = Settings.q.substring(0,Settings.q.length()-1);
			}
			RequestTranslateApiTask();
			StatService.onEvent(getActivity(), "tab_dic_submit_btn", "首页词典页面翻译提交按钮", 1);
		} else {
			showToast(getActivity().getResources().getString(R.string.input_et_hint));
			finishLoadding();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mSpeechSynthesizer != null){
			mSpeechSynthesizer.stopSpeaking();
			mSpeechSynthesizer = null;
		}
		if(recognizer != null){
			recognizer.stopListening();
			recognizer = null;
		}
		if(mAdapter != null){
			mAdapter.stopPlay();
		}
		LogUtil.DefalutLog("MainFragment-onDestroy");
	}
}
