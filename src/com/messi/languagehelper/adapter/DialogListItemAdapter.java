package com.messi.languagehelper.adapter;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.messi.languagehelper.MainFragment;
import com.messi.languagehelper.R;
import com.messi.languagehelper.bean.DialogBean;
import com.messi.languagehelper.dao.record;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.task.PublicTask;
import com.messi.languagehelper.task.PublicTask.PublicTaskListener;
import com.messi.languagehelper.util.AudioTrackUtil;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.SDCardUtil;
import com.messi.languagehelper.util.ShowView;
import com.messi.languagehelper.util.ToastUtil;
import com.messi.languagehelper.util.XFUtil;

public class DialogListItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<DialogBean> beans;
	private Context context;
	private SpeechSynthesizer mSpeechSynthesizer;
	private SharedPreferences mSharedPreferences;

	public DialogListItemAdapter(Context mContext,LayoutInflater mInflater,List<DialogBean> mBeans,
			SpeechSynthesizer mSpeechSynthesizer,SharedPreferences mSharedPreferences ) {
		this.context = mContext;
		beans = mBeans;
		this.mInflater = mInflater;
		this.mSharedPreferences = mSharedPreferences;
		this.mSpeechSynthesizer = mSpeechSynthesizer;
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
			convertView = mInflater.inflate(R.layout.dialog_practice_item, null);
			holder = new ViewHolder();
			holder.dialog_a = (RelativeLayout) convertView.findViewById(R.id.dialog_a);
			holder.dialog_b = (RelativeLayout) convertView.findViewById(R.id.dialog_b);
			holder.dialog_a_content = (TextView) convertView.findViewById(R.id.dialog_a_content);
			holder.dialog_a_content_resutl = (TextView) convertView.findViewById(R.id.dialog_a_content_resutl);
			holder.dialog_b_content = (TextView) convertView.findViewById(R.id.dialog_a_content);
			holder.dialog_b_content_resutl = (TextView) convertView.findViewById(R.id.dialog_b_content_resutl);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final DialogBean mBean = beans.get(position);
//		MyOnClickListener mMyOnClickListener = new MyOnClickListener(mBean,animationDrawable,holder.voice_play,
//				holder.play_content_btn_progressbar,true);
//		MyOnClickListener mQuestionOnClickListener = new MyOnClickListener(mBean,animationDrawable,holder.voice_play,
//				holder.play_content_btn_progressbar,false);
		if(mBean.getRole().equals("A")){
			holder.dialog_a.setVisibility(View.VISIBLE);
			holder.dialog_b.setVisibility(View.GONE);
			holder.dialog_a_content.setText(mBean.getContent());
		}else{
			holder.dialog_a.setVisibility(View.GONE);
			holder.dialog_b.setVisibility(View.VISIBLE);
			holder.dialog_b_content.setText(mBean.getContent());
		}
		return convertView;
	}
	
	static class ViewHolder {
		RelativeLayout dialog_a;
		RelativeLayout dialog_b;
		TextView dialog_a_content;
		TextView dialog_a_content_resutl;
		TextView dialog_b_content;
		TextView dialog_b_content_resutl;
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
			ShowView.showIndexPageGuide(context, KeyUtil.IsHasShowClickText);
			String path = SDCardUtil.getDownloadPath(SDCardUtil.sdPath);
			if(TextUtils.isEmpty(mBean.getResultVoiceId()) || TextUtils.isEmpty(mBean.getQuestionVoiceId())){
				mBean.setQuestionVoiceId(System.currentTimeMillis() + "");
				mBean.setResultVoiceId(System.currentTimeMillis()-5 + "");
			}
			String filepath = "";
			String speakContent = "";
			if(isPlayResult){
				filepath = path + mBean.getResultVoiceId() + ".pcm";
				mBean.setResultAudioPath(filepath);
				speakContent = mBean.getEnglish();
			}else{
				filepath = path + mBean.getQuestionVoiceId() + ".pcm";
				mBean.setQuestionAudioPath(filepath);
				speakContent = mBean.getChinese();
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
				playLocalPcm(filepath,animationDrawable);
			}
			if(v.getId() == R.id.record_question_cover){
				StatService.onEvent(context, "tab_translate_play_content", "首页翻译页面列表播放内容", 1);
			}else if(v.getId() == R.id.record_answer_cover){
				StatService.onEvent(context, "tab_translate_play_result", "首页翻译页面列表播放结果", 1);
			}else if(v.getId() == R.id.voice_play_layout){
				StatService.onEvent(context, "tab_translate_playvoicebtn", "首页翻译页面列表播放按钮", 1);
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
