package com.messi.languagehelper.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.iflytek.cloud.speech.SynthesizerListener;
import com.messi.languagehelper.CollectedFragment;
import com.messi.languagehelper.ImgShareActivity;
import com.messi.languagehelper.MainFragment;
import com.messi.languagehelper.PracticeActivity;
import com.messi.languagehelper.R;
import com.messi.languagehelper.bean.DialogBean;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.dialog.PopDialog;
import com.messi.languagehelper.dialog.PopDialog.PopViewItemOnclickListener;
import com.messi.languagehelper.task.PublicTask;
import com.messi.languagehelper.task.PublicTask.PublicTaskListener;
import com.messi.languagehelper.util.AudioTrackUtil;
import com.messi.languagehelper.util.BaiduStatistics;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.SDCardUtil;
import com.messi.languagehelper.util.SharedPreferencesUtil;
import com.messi.languagehelper.util.ShowView;
import com.messi.languagehelper.util.ToastUtil;
import com.messi.languagehelper.util.XFUtil;

public class CollectedListItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<DialogBean> beans;
	private Context context;
	private DataBaseUtil mDataBaseUtil;
	private SpeechSynthesizer mSpeechSynthesizer;
	private SharedPreferences mSharedPreferences;
	private Bundle bundle;
	private String from;

	public CollectedListItemAdapter(Context mContext,LayoutInflater mInflater,List<DialogBean> mBeans,
			SpeechSynthesizer mSpeechSynthesizer,SharedPreferences mSharedPreferences,DataBaseUtil mDataBaseUtil,
			Bundle bundle, String from) {
		LogUtil.DefalutLog("public CollectedListItemAdapter");
		context = mContext;
		beans = mBeans;
		this.mInflater = mInflater;
		this.mSharedPreferences = mSharedPreferences;
		this.mSpeechSynthesizer = mSpeechSynthesizer;
		this.mDataBaseUtil = mDataBaseUtil;
		this.bundle = bundle;
		this.from = from;
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
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listview_item_recent_used, null);
			holder = new ViewHolder();
			holder.record_question_cover = (FrameLayout) convertView.findViewById(R.id.record_question_cover);
			holder.record_answer_cover = (FrameLayout) convertView.findViewById(R.id.record_answer_cover);
			holder.record_to_practice = (FrameLayout) convertView.findViewById(R.id.record_to_practice);
			holder.record_question = (TextView) convertView.findViewById(R.id.record_question);
			holder.record_answer = (TextView) convertView.findViewById(R.id.record_answer);
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
		final DialogBean mBean = beans.get(position);
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
		holder.record_question.setText(mBean.getQuestion());
		holder.record_answer.setText(mBean.getAnswer());
		
		holder.record_question_cover.setOnClickListener(mQuestionOnClickListener);
		holder.record_answer_cover.setOnClickListener(mMyOnClickListener);
		holder.voice_play_layout.setOnClickListener(mMyOnClickListener);
		
		holder.delete_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDataBaseUtil.dele(mBean.getId());
				beans.remove(mBean);
				notifyDataSetChanged();
				showToast(context.getResources().getString(R.string.dele_success));
				MainFragment.isRefresh = true;
				StatService.onEvent(context, "1.6_deletebtn", "删除按钮", 1);
			}
		});
		holder.copy_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				copy(mBean.getAnswer());
				StatService.onEvent(context, "1.6_copybtn", "复制按钮", 1);
			}
		});
		holder.weixi_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendToWechat(mBean.getAnswer());
				StatService.onEvent(context, "1.6_sharebtn", "分享按钮", 1);
			}
		});
		holder.collected_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				updateCollectedStatus(mBean);
				notifyDataSetChanged();
				StatService.onEvent(context, "1.6_collectedbtn", "收藏按钮", 1);
			}
		});
		holder.record_to_practice.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,PracticeActivity.class);
				context.startActivity(intent);
				StatService.onEvent(context, "1.8_to_practice", "去口语练页面", 1);
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
		CheckBox collected_cb;
		FrameLayout voice_play_layout;
		ProgressBar play_content_btn_progressbar;
	}
	
	public void notifyDataChange(List<DialogBean> mBeans,int maxNumber){
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
		StatService.onEvent(context, BaiduStatistics.CopyBtn, "复制按钮", 1);
	}
	
	/**
	 * 分享
	 */
	private void sendToWechat(final String dstString){
		String[] tempText = new String[2];
		tempText[0] = context.getResources().getString(R.string.share_dialog_text_1);
		tempText[1] = context.getResources().getString(R.string.share_dialog_text_2);
		PopDialog mPopDialog = new PopDialog(context,tempText);
		mPopDialog.setCanceledOnTouchOutside(true);
		mPopDialog.setListener(new PopViewItemOnclickListener() {
			@Override
			public void onSecondClick(View v) {
				toShareImageActivity(dstString);
			}
			@Override
			public void onFirstClick(View v) {
				toShareTextActivity(dstString);
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
	
	private void toShareImageActivity(String dstString){
		Intent intent = new Intent(context, ImgShareActivity.class); 
		intent.putExtra(KeyUtil.ShareContentKey, dstString);
		context.startActivity(intent); 
	}
	
	private void updateCollectedStatus(DialogBean mBean){
		if(mBean.getIscollected().equals("0")){
			mBean.setIscollected("1");
			showToast(context.getResources().getString(R.string.favorite_success));
		}else{
			mBean.setIscollected("0");
			showToast(context.getResources().getString(R.string.favorite_cancle));
		}  
		if(from.equals("CollectedFragment")){
			beans.remove(mBean);
			notifyDataSetChanged();
			MainFragment.isRefresh = true;
		}else{
			CollectedFragment.isRefresh = true;
		}
		mDataBaseUtil.update(mBean);
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

	private class MyOnClickListener implements OnClickListener {
		
		private DialogBean mBean;
		private ImageButton voice_play;
		private AnimationDrawable animationDrawable;
		private ProgressBar play_content_btn_progressbar;
		private boolean isPlayResult;
		
		private MyOnClickListener(DialogBean bean,AnimationDrawable mAnimationDrawable,ImageButton voice_play,
				ProgressBar progressbar, boolean isPlayResult){
			this.mBean = bean;
			this.voice_play = voice_play;
			this.animationDrawable = mAnimationDrawable;
			this.play_content_btn_progressbar = progressbar;
			this.isPlayResult = isPlayResult;
		}
		@Override
		public void onClick(final View v) {
			ShowView.showIndexPageGuide(context, SharedPreferencesUtil.IsHasShowClickText);
			String path = SDCardUtil.getDownloadPath();
			if(TextUtils.isEmpty(mBean.getResultVoiceId()) || TextUtils.isEmpty(mBean.getQuestionVoiceId())){
				mBean.setQuestionVoiceId(System.currentTimeMillis() + "");
				mBean.setResultVoiceId(System.currentTimeMillis()-5 + "");
			}
			String filepath = "";
			String speakContent = "";
			if(isPlayResult){
				filepath = path + mBean.getResultVoiceId() + ".pcm";
				mBean.setResultAudioPath(filepath);
				speakContent = mBean.getAnswer();
			}else{
				filepath = path + mBean.getQuestionVoiceId() + ".pcm";
				mBean.setQuestionAudioPath(filepath);
				speakContent = mBean.getQuestion();
			}
			if(mBean.getSpeak_speed() != MainFragment.speed){
				String filep1 = path + mBean.getResultVoiceId() + ".pcm";
				String filep2 = path + mBean.getQuestionVoiceId() + ".pcm";
				AudioTrackUtil.deleteFile(filep1);
				AudioTrackUtil.deleteFile(filep2);
				mBean.setSpeak_speed(MainFragment.speed);
			}
			mSpeechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, filepath);
			if(!AudioTrackUtil.isFileExists(filepath)){
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
						mDataBaseUtil.update(mBean);
						animationDrawable.setOneShot(true);
						animationDrawable.stop(); 
						animationDrawable.selectDrawable(0);
					}
					@Override
					public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
					}
				});
			}else{
				playLocalPcm(filepath,animationDrawable);
			}
			if(v.getId() == R.id.record_question_cover){
				StatService.onEvent(context, "1.7_play_content", "点击翻译内容", 1);
			}else if(v.getId() == R.id.record_answer_cover){
				StatService.onEvent(context, "1.7_play_result", "点击翻译结果", 1);
			}else if(v.getId() == R.id.voice_play_layout){
				StatService.onEvent(context, "1.6_playvoicebtn", "播放按钮", 1);
			}
		}
	}
	
	private void playLocalPcm(final String path,final AnimationDrawable animationDrawable){
		PublicTask mPublicTask = new PublicTask(context);
		mPublicTask.setmPublicTaskListener(new PublicTaskListener() {
			@Override
			public void onPreExecute() {
				if(!animationDrawable.isRunning()){
					animationDrawable.setOneShot(false);
					animationDrawable.start();  
				}
			}
			@Override
			public Object doInBackground() {
				AudioTrackUtil.createAudioTrack(path);
				return null;
			}
			@Override
			public void onFinish(Object resutl) {
				animationDrawable.setOneShot(true);
				animationDrawable.stop(); 
				animationDrawable.selectDrawable(0);
			}
		});
		mPublicTask.execute();
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
