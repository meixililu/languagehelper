package com.messi.languagehelper.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
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
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.messi.languagehelper.CollectedFragment;
import com.messi.languagehelper.ImgShareActivity;
import com.messi.languagehelper.MainFragment;
import com.messi.languagehelper.R;
import com.messi.languagehelper.dao.Dictionary;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.dialog.PopDialog;
import com.messi.languagehelper.dialog.PopDialog.PopViewItemOnclickListener;
import com.messi.languagehelper.task.PublicTask;
import com.messi.languagehelper.task.PublicTask.PublicTaskListener;
import com.messi.languagehelper.util.AudioTrackUtil;
import com.messi.languagehelper.util.BaiduStatistics;
import com.messi.languagehelper.util.DictionaryUtil;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.SDCardUtil;
import com.messi.languagehelper.util.ShowView;
import com.messi.languagehelper.util.ToastUtil;
import com.messi.languagehelper.util.XFUtil;

public class DictionaryListViewAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<Dictionary> beans;
	private Context context;
	private SpeechSynthesizer mSpeechSynthesizer;
	private SharedPreferences mSharedPreferences;
	private Bundle bundle;
	private String from;

	public DictionaryListViewAdapter(Context mContext,
			LayoutInflater mInflater, List<Dictionary> mBeans,
			SpeechSynthesizer mSpeechSynthesizer,
			SharedPreferences mSharedPreferences, Bundle bundle, String from) {
		LogUtil.DefalutLog("public CollectedListItemAdapter");
		context = mContext;
		beans = mBeans;
		this.mInflater = mInflater;
		this.mSharedPreferences = mSharedPreferences;
		this.mSpeechSynthesizer = mSpeechSynthesizer;
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
		LogUtil.DefalutLog("DictionaryListViewAdapter---getView");
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listview_item_dictionary, null);
			holder = new ViewHolder();
			holder.cover_result = (FrameLayout) convertView.findViewById(R.id.record_question_cover);
			holder.cover_question = (FrameLayout) convertView.findViewById(R.id.record_answer_cover);
			holder.txt_result = (TextView) convertView.findViewById(R.id.record_question);
			holder.txt_question = (TextView) convertView.findViewById(R.id.record_answer);
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
		try {
			final Dictionary mBean = beans.get(position);
			AnimationDrawable animationDrawable = (AnimationDrawable) holder.voice_play.getBackground();
			MyOnClickListener mResultClickListener = new MyOnClickListener(mBean,animationDrawable, holder.voice_play,holder.play_content_btn_progressbar, true);
			MyOnClickListener mQuestionOnClickListener = new MyOnClickListener(mBean, animationDrawable, holder.voice_play,holder.play_content_btn_progressbar, false);
			if (mBean.getIscollected().equals("0")) {
				holder.collected_cb.setChecked(false);
			} else {
				holder.collected_cb.setChecked(true);
			}
			holder.txt_question.setText(mBean.getWord_name());
			if(mBean.getType().equals(KeyUtil.ResultTypeDictionary)){
				holder.txt_result.setText( DictionaryUtil.getListToString(mBean) );
			}else{
				holder.txt_result.setText(mBean.getResult());
			}
			holder.voice_play_layout.setOnClickListener(mResultClickListener);
			holder.cover_result.setOnClickListener(mResultClickListener);
			holder.cover_question.setOnClickListener(mQuestionOnClickListener);
			
			holder.delete_btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					DataBaseUtil.getInstance().dele(mBean);
					beans.remove(mBean);
					notifyDataSetChanged();
					showToast(context.getResources().getString(R.string.dele_success));
					StatService.onEvent(context, "1.6_deletebtn", "删除按钮", 1);
				}
			});
			holder.copy_btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String dictionary;
					try {
						dictionary = DictionaryUtil.getShareContent(mBean);
						copy(dictionary);
					} catch (Exception e) {
						e.printStackTrace();
					}
					StatService.onEvent(context, "1.6_copybtn", "复制按钮", 1);
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
					notifyDataSetChanged();
					StatService.onEvent(context, "1.6_collectedbtn", "收藏按钮", 1);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	

	static class ViewHolder {
		TextView txt_result;
		TextView txt_question;
		FrameLayout cover_question;
		FrameLayout cover_result;
		FrameLayout delete_btn;
		FrameLayout copy_btn;
		FrameLayout collected_btn;
		FrameLayout weixi_btn;
		ImageButton voice_play;
		CheckBox collected_cb;
		FrameLayout voice_play_layout;
		ProgressBar play_content_btn_progressbar;
	}

	public void notifyDataChange(List<Dictionary> mBeans, int maxNumber) {
		if (maxNumber == 0) {
			beans = mBeans;
		} else {
			beans.addAll(mBeans);
		}
		notifyDataSetChanged();
	}

	/**
	 * 复制按钮
	 */
	private void copy(String dstString) {
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(dstString);
		showToast(context.getResources().getString(R.string.copy_success));
		StatService.onEvent(context, BaiduStatistics.CopyBtn, "复制按钮", 1);
	}

	/**
	 * 分享
	 */
	private void sendToWechat(final Dictionary mBean) {
		String[] tempText = new String[2];
		tempText[0] = context.getResources().getString(R.string.share_dialog_text_1);
		tempText[1] = context.getResources().getString(R.string.share_dialog_text_2);
		PopDialog mPopDialog = new PopDialog(context, tempText);
		mPopDialog.setCanceledOnTouchOutside(true);
		mPopDialog.setListener(new PopViewItemOnclickListener() {
			@Override
			public void onSecondClick(View v) {
				try {
					toShareImageActivity(mBean);
				} catch (Exception e) {
					e.printStackTrace();
				}
				StatService.onEvent(context, "1.8_to_share_image_btn", "去图片分享页面按钮", 1);
			}

			@Override
			public void onFirstClick(View v) {
				try {
					toShareTextActivity( DictionaryUtil.getShareContent(mBean) );
				} catch (Exception e) {
					e.printStackTrace();
				}
				StatService.onEvent(context, "1.8_to_share_text_btn",
						"去文字分享页面按钮", 1);
			}
		});
		mPopDialog.show();
	}

	private void toShareTextActivity(String dstString) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain"); // 纯文本
		intent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.share));
		intent.putExtra(Intent.EXTRA_TEXT, dstString);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.share)));
	}

	private void toShareImageActivity(Dictionary mBean) throws Exception {
		Intent intent = new Intent(context, ImgShareActivity.class);
		intent.putExtra(KeyUtil.ShareContentKey, DictionaryUtil.getShareContent(mBean));
		context.startActivity(intent);
	}

	private void updateCollectedStatus(Dictionary mBean) {
		if (mBean.getIscollected().equals("0")) {
			mBean.setIscollected("1");
			showToast(context.getResources().getString(R.string.favorite_success));
		} else {
			mBean.setIscollected("0");
			showToast(context.getResources().getString(R.string.favorite_cancle));
		}
		if (from.equals("CollectedFragment")) {
			beans.remove(mBean);
			notifyDataSetChanged();
			MainFragment.isRefresh = true;
		} else {
			CollectedFragment.isRefresh = true;
		}
		DataBaseUtil.getInstance().update(mBean);
	}

	public class MyOnClickListener implements OnClickListener {

		private Dictionary mBean;
		private ImageButton voice_play;
		private AnimationDrawable animationDrawable;
		private ProgressBar play_content_btn_progressbar;
		private boolean isPlayResult;

		private MyOnClickListener(Dictionary bean,
				AnimationDrawable mAnimationDrawable, ImageButton voice_play,
				ProgressBar progressbar, boolean isPlayResult) {
			this.mBean = bean;
			this.voice_play = voice_play;
			this.animationDrawable = mAnimationDrawable;
			this.play_content_btn_progressbar = progressbar;
			this.isPlayResult = isPlayResult;
		}

		@Override
		public void onClick(final View v) {
			try {
				ShowView.showIndexPageGuide(context, KeyUtil.IsHasShowClickText);
				String path = SDCardUtil.getDownloadPath(SDCardUtil.sdPath);
				 if(TextUtils.isEmpty(mBean.getResultVoiceId()) || TextUtils.isEmpty(mBean.getQuestionVoiceId())){
					 mBean.setQuestionVoiceId(System.currentTimeMillis() + "");
					 mBean.setResultVoiceId(System.currentTimeMillis()-5 + "");
				 }
				 String filepath = "";
				 String speakContent = "";
				 //isPlayResult  true is result;
				 if(isPlayResult){
					 filepath = path + mBean.getResultVoiceId() + ".pcm";
					 mBean.setResultAudioPath(filepath);
					 if(mBean.getType().equals(KeyUtil.ResultTypeDictionary)){
						 if(TextUtils.isEmpty(mBean.getBackup1())){
							 DictionaryUtil.getResultSetData(mBean);
						 }
						 speakContent = mBean.getBackup1();
					 }else{
						 speakContent = mBean.getResult();
					 }
				 }else{
					 filepath = path + mBean.getQuestionVoiceId() + ".pcm";
					 mBean.setQuestionAudioPath(filepath);
					 speakContent = mBean.getWord_name();
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
							 LogUtil.DefalutLog("SynthesizerListener---onCompleted");
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
							 LogUtil.DefalutLog("---arg0:"+arg0+"---arg1:"+arg1+"---arg2:"+arg2);
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
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void playLocalPcm(final String path,
			final AnimationDrawable animationDrawable) {
		PublicTask mPublicTask = new PublicTask(context);
		mPublicTask.setmPublicTaskListener(new PublicTaskListener() {
			@Override
			public void onPreExecute() {
				if (!animationDrawable.isRunning()) {
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

	/**
	 * toast message
	 * 
	 * @param toastString
	 */
	private void showToast(String toastString) {
		if (!TextUtils.isEmpty(toastString)) {
			Toast.makeText(context, toastString, 0).show();
		}
	}
}
