package com.messi.languagehelper.adapter;

import java.util.List;

import com.avos.avoscloud.AVAnalytics;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.messi.languagehelper.BaseApplication;
import com.messi.languagehelper.ImgShareActivity;
import com.messi.languagehelper.MainFragment;
import com.messi.languagehelper.PracticeActivity;
import com.messi.languagehelper.R;
import com.messi.languagehelper.dao.record;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.dialog.PopDialog;
import com.messi.languagehelper.dialog.PopDialog.PopViewItemOnclickListener;
import com.messi.languagehelper.task.MyThread;
import com.messi.languagehelper.util.AudioTrackUtil;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.SDCardUtil;
import com.messi.languagehelper.util.Settings;
import com.messi.languagehelper.util.ToastUtil;
import com.messi.languagehelper.util.XFUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CollectedListItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<record> beans;
	private Context context;
	private SpeechSynthesizer mSpeechSynthesizer;
	private AudioTrack audioTrack;
	private SharedPreferences mSharedPreferences;
	private Bundle bundle;
	private Thread mThread;
	private MyThread mMyThread;
	private Handler mHandler;
	private AnimationDrawable currentAnimationDrawable;

	public CollectedListItemAdapter(Context mContext,LayoutInflater mInflater,List<record> mBeans,
			SpeechSynthesizer mSpeechSynthesizer,SharedPreferences mSharedPreferences, 
			Bundle bundle) {
		LogUtil.DefalutLog("public CollectedListItemAdapter");
		this.context = mContext;
		beans = mBeans;
		this.mInflater = mInflater;
		this.mSharedPreferences = mSharedPreferences;
		this.mSpeechSynthesizer = mSpeechSynthesizer;
		this.bundle = bundle;
		mHandler = new Handler() {
			public void handleMessage(Message message) {
				if (message.what == MyThread.EVENT_PLAY_OVER) {
					mThread = null;
					if (currentAnimationDrawable != null) {
						currentAnimationDrawable.setOneShot(true);
						currentAnimationDrawable.stop();
						currentAnimationDrawable.selectDrawable(0);
					}
					resetStatus();
				}
			}
		};
		mMyThread = new MyThread(mHandler);
	}

	public int getCount() {
		return beans.size();
	}

	public Object getItem(int position) {
		return beans.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LogUtil.DefalutLog("CollectedListItemAdapter---getView");
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listview_item_recent_used, null);
			holder = new ViewHolder();
			holder.record_question_cover = (FrameLayout) convertView.findViewById(R.id.record_question_cover);
			holder.record_answer_cover = (FrameLayout) convertView.findViewById(R.id.record_answer_cover);
			holder.record_to_practice = (FrameLayout) convertView.findViewById(R.id.record_to_practice);
			holder.record_question = (TextView) convertView.findViewById(R.id.record_question);
			holder.record_answer = (TextView) convertView.findViewById(R.id.record_answer);
			holder.unread_dot_answer = (ImageView) convertView.findViewById(R.id.unread_dot_answer);
			holder.unread_dot_question = (ImageView) convertView.findViewById(R.id.unread_dot_question);
			holder.voice_play = (ImageButton) convertView.findViewById(R.id.voice_play);
			holder.collected_cb = (CheckBox) convertView.findViewById(R.id.collected_cb);
			holder.voice_play_layout = (FrameLayout) convertView.findViewById(R.id.voice_play_layout);
			holder.delete_btn = (FrameLayout) convertView.findViewById(R.id.delete_btn);
			holder.copy_btn = (FrameLayout) convertView.findViewById(R.id.copy_btn);
			holder.collected_btn = (FrameLayout) convertView.findViewById(R.id.collected_btn);
			holder.weixi_btn = (FrameLayout) convertView.findViewById(R.id.weixi_btn);
			holder.play_content_btn_progressbar = (ProgressBar) convertView.findViewById(R.id.play_content_btn_progressbar);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final record mBean = beans.get(position);
		AnimationDrawable animationDrawable = (AnimationDrawable) holder.voice_play.getBackground();
		MyOnClickListener mMyOnClickListener = new MyOnClickListener(mBean,animationDrawable,holder.voice_play,
				holder.play_content_btn_progressbar,true);
		MyOnClickListener mQuestionOnClickListener = new MyOnClickListener(mBean,animationDrawable,holder.voice_play,
				holder.play_content_btn_progressbar,false);
		if(mBean.getIscollected().equals("0")){
			holder.collected_cb.setChecked(false);
		}else{
			holder.collected_cb.setChecked(true);
		}
		if(position == 0){
			if(!mSharedPreferences.getBoolean(KeyUtil.IsShowAnswerUnread, false)){
				holder.unread_dot_answer.setVisibility(View.VISIBLE);
			}else{
				holder.unread_dot_answer.setVisibility(View.GONE);
			}
			if(!mSharedPreferences.getBoolean(KeyUtil.IsShowQuestionUnread, false)){
				holder.unread_dot_question.setVisibility(View.VISIBLE);
			}else{
				holder.unread_dot_question.setVisibility(View.GONE);
			}
		}else{
			holder.unread_dot_answer.setVisibility(View.GONE);
			holder.unread_dot_question.setVisibility(View.GONE);
		}
		holder.record_question.setText(mBean.getChinese());
		holder.record_answer.setText(mBean.getEnglish());
		
		holder.record_question_cover.setOnClickListener(mQuestionOnClickListener);
		holder.record_answer_cover.setOnClickListener(mMyOnClickListener);
		holder.voice_play_layout.setOnClickListener(mMyOnClickListener);
		
		holder.record_answer_cover.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				copy(mBean.getEnglish());
				return true;
			}
		});
		holder.record_question_cover.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				copy(mBean.getChinese());
				return true;
			}
		});
		
		holder.delete_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DataBaseUtil.getInstance().dele(mBean);
				beans.remove(mBean);
				notifyDataSetChanged();
				showToast(context.getResources().getString(R.string.dele_success));
				MainFragment.isRefresh = true;
				AVAnalytics.onEvent(context, "tab1_delete_btn");
			}
		});
		holder.copy_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				copy(mBean.getEnglish());
				AVAnalytics.onEvent(context, "tab1_copy_btn");
			}
		});
		holder.weixi_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendToWechat(mBean);
			}
		});
		holder.collected_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				updateCollectedStatus(mBean);
				AVAnalytics.onEvent(context, "tab1_collected_btn");
			}
		});
		holder.record_to_practice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,PracticeActivity.class);
				BaseApplication.dataMap.put(KeyUtil.DialogBeanKey, mBean);
				context.startActivity(intent);
				AVAnalytics.onEvent(context, "tab1_to_practice_btn");
			}
		});
		return convertView;
	}
	
	static class ViewHolder {
		TextView record_question;
		TextView record_answer;
		FrameLayout record_answer_cover;
		FrameLayout record_to_practice;
		FrameLayout record_question_cover;
		FrameLayout delete_btn;
		FrameLayout copy_btn;
		FrameLayout collected_btn;
		FrameLayout weixi_btn;
		ImageButton voice_play;
		ImageView unread_dot_answer;
		ImageView unread_dot_question;
		CheckBox collected_cb;
		FrameLayout voice_play_layout;
		ProgressBar play_content_btn_progressbar;
	}
	
	public void notifyDataChange(List<record> mBeans,int maxNumber){
		if(maxNumber == 0){
			beans = mBeans;
		}else{
			beans.addAll(mBeans);
		}
		notifyDataSetChanged();
	}
	
	/**
	 * 复制按钮
	 */
	private void copy(String dstString){
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(dstString);
		showToast(context.getResources().getString(R.string.copy_success));
	}
	
	/**
	 * 分享
	 */
	private void sendToWechat(final record mBean){
		String[] tempText = new String[2];
		tempText[0] = context.getResources().getString(R.string.share_dialog_text_1);
		tempText[1] = context.getResources().getString(R.string.share_dialog_text_2);
		PopDialog mPopDialog = new PopDialog(context,tempText);
		mPopDialog.setCanceledOnTouchOutside(true);
		mPopDialog.setListener(new PopViewItemOnclickListener() {
			@Override
			public void onSecondClick(View v) {
				toShareImageActivity(mBean);
				AVAnalytics.onEvent(context, "tab1_share_for_image_btn");
			}
			@Override
			public void onFirstClick(View v) {
				toShareTextActivity(mBean.getEnglish());
				AVAnalytics.onEvent(context, "tab1_share_for_text_btn");
			}
		});
		mPopDialog.show();
	}
	
	private void toShareTextActivity(String dstString){
		Intent intent = new Intent(Intent.ACTION_SEND);    
		intent.setType("text/plain"); // 纯文本     
		intent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.share));    
		intent.putExtra(Intent.EXTRA_TEXT, dstString);    
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
		context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.share)));    
	}
	
	private void toShareImageActivity(record mBean){
		Intent intent = new Intent(context, ImgShareActivity.class); 
		intent.putExtra(KeyUtil.ShareContentKey, mBean.getEnglish()+"\n"+mBean.getChinese());
		context.startActivity(intent); 
	}
	
	private void updateCollectedStatus(record mBean){
		if(mBean.getIscollected().equals("0")){
			mBean.setIscollected("1");
			showToast(context.getResources().getString(R.string.favorite_success));
		}else{
			mBean.setIscollected("0");
			showToast(context.getResources().getString(R.string.favorite_cancle));
		}  
		notifyDataSetChanged();
		DataBaseUtil.getInstance().update(mBean);
	}
	
	/**
	 * 调用发短信界面
	 */
	private void sendMessage(String dstString){
		if(!TextUtils.isEmpty(dstString)){
			Uri smsToUri = Uri.parse("smsto:");  
			Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);  
			intent.putExtra("sms_body", dstString);  
			context.startActivity(intent);  
		}else{
			showToast("无法发短信，没有翻译结果！");
		}
	}

	public class MyOnClickListener implements OnClickListener {
		
		private record mBean;
		private ImageButton voice_play;
		private AnimationDrawable animationDrawable;
		private ProgressBar play_content_btn_progressbar;
		private boolean isPlayResult;
		
		private MyOnClickListener(record bean,AnimationDrawable mAnimationDrawable,ImageButton voice_play,
				ProgressBar progressbar, boolean isPlayResult){
			this.mBean = bean;
			this.voice_play = voice_play;
			this.animationDrawable = mAnimationDrawable;
			this.play_content_btn_progressbar = progressbar;
			this.isPlayResult = isPlayResult;
		}
		@Override
		public void onClick(final View v) {
			try {
				boolean isPlay = true;
				stopPlay();
				if(TextUtils.isEmpty(mBean.getBackup2())){
					isPlay = true;
				}else if(isPlayResult && (mBean.getBackup2().equals(XFUtil.PlayResultOnline) || mBean.getBackup2().equals(XFUtil.PlayResultOffline))){
					isPlay = false;
				}else if(!isPlayResult && (mBean.getBackup2().equals(XFUtil.PlayQueryOnline) || mBean.getBackup2().equals(XFUtil.PlayQueryOffline))){
					isPlay = false;
				}
				resetStatus();
				if(isPlay){
//					ShowView.showIndexPageGuide(context, KeyUtil.IsHasShowClickText);
					String path = SDCardUtil.getDownloadPath(SDCardUtil.sdPath);
					if(TextUtils.isEmpty(mBean.getResultVoiceId()) || TextUtils.isEmpty(mBean.getQuestionVoiceId())){
						mBean.setQuestionVoiceId(System.currentTimeMillis() + "");
						mBean.setResultVoiceId(System.currentTimeMillis()-5 + "");
					}
					String filepath = "";
					String speakContent = "";
					if(isPlayResult){
						Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.IsShowAnswerUnread, true);
						filepath = path + mBean.getResultVoiceId() + ".pcm";
						mBean.setResultAudioPath(filepath);
						speakContent = mBean.getEnglish();
					}else{
						Settings.saveSharedPreferences(mSharedPreferences, KeyUtil.IsShowQuestionUnread, true);
						filepath = path + mBean.getQuestionVoiceId() + ".pcm";
						mBean.setQuestionAudioPath(filepath);
						speakContent = mBean.getChinese();
					}
					notifyDataSetChanged();
					if(mBean.getSpeak_speed() != MainFragment.speed){
						String filep1 = path + mBean.getResultVoiceId() + ".pcm";
						String filep2 = path + mBean.getQuestionVoiceId() + ".pcm";
						AudioTrackUtil.deleteFile(filep1);
						AudioTrackUtil.deleteFile(filep2);
						mBean.setSpeak_speed(MainFragment.speed);
					}
					mSpeechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, filepath);
					if(!AudioTrackUtil.isFileExists(filepath)){
						if(isPlayResult){
							mBean.setBackup2(XFUtil.PlayResultOnline);
						}else{
							mBean.setBackup2(XFUtil.PlayQueryOnline);
						}
						play_content_btn_progressbar.setVisibility(View.VISIBLE);
						voice_play.setVisibility(View.GONE);
						XFUtil.showSpeechSynthesizer(context,mSharedPreferences,mSpeechSynthesizer,speakContent,
								new SynthesizerListener() {
							@Override
							public void onSpeakResumed() {
							}
							@Override
							public void onSpeakProgress(int arg0, int arg1, int arg2) {
							}
							@Override
							public void onSpeakPaused() {
							}
							@Override
							public void onSpeakBegin() {
								play_content_btn_progressbar.setVisibility(View.GONE);
								voice_play.setVisibility(View.VISIBLE);
								if(!animationDrawable.isRunning()){
									animationDrawable.setOneShot(false);
									animationDrawable.start();  
								}
							}
							@Override
							public void onCompleted(SpeechError arg0) {
								LogUtil.DefalutLog("---onCompleted");
								if(arg0 != null){
									ToastUtil.diaplayMesShort(context, arg0.getErrorDescription());
								}
								mBean.setBackup2("");
								DataBaseUtil.getInstance().update(mBean);
								animationDrawable.setOneShot(true);
								animationDrawable.stop(); 
								animationDrawable.selectDrawable(0);
							}
							@Override
							public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
							}
							@Override
							public void onEvent(int arg0, int arg1, int arg2,Bundle arg3) {
							}
						});
					}else{
						if(isPlayResult){
							mBean.setBackup2(XFUtil.PlayResultOffline);
						}else{
							mBean.setBackup2(XFUtil.PlayQueryOffline);
						}
						if (!animationDrawable.isRunning()) {
							animationDrawable.setOneShot(false);
							animationDrawable.start();
						}
						currentAnimationDrawable = animationDrawable;
						mMyThread.setDataUri(filepath);
						mThread = AudioTrackUtil.startMyThread(mMyThread);
						LogUtil.DefalutLog("mThread--start:"+mThread.getId());
					}
					if(v.getId() == R.id.record_question_cover){
						AVAnalytics.onEvent(context, "tab1_play_question", "首页翻译页面列表播放内容", 1);
					}else if(v.getId() == R.id.record_answer_cover){
						AVAnalytics.onEvent(context, "tab1_play_result", "首页翻译页面列表播放结果", 1);
					}else if(v.getId() == R.id.voice_play_layout){
						AVAnalytics.onEvent(context, "tab1_play_voice_btn", "首页翻译页面列表播放按钮", 1);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void stopPlay(){
		AudioTrackUtil.stopPlayOnline(mSpeechSynthesizer);
		AudioTrackUtil.stopPlayPcm(mThread);
	}
	
	public void resetStatus(){
		for(record mBean : beans){
			mBean.setBackup2("");
		}
	}
	
	/**toast message
	 * @param toastString
	 */
	private void showToast(String toastString) {
		if(!TextUtils.isEmpty(toastString)){
			Toast.makeText(context, toastString, 0).show();
		}
	}
}
