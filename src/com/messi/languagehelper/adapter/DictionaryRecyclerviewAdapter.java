package com.messi.languagehelper.adapter;

import java.util.List;

import com.avos.avoscloud.AVAnalytics;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.messi.languagehelper.DictionaryFragment;
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
import com.messi.languagehelper.util.DictionaryUtil;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.SDCardUtil;
import com.messi.languagehelper.util.XFUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class DictionaryRecyclerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private static final int TYPE_HEADER = 2;
    private static final int TYPE_ITEM = 1;
	private LayoutInflater mInflater;
	private List<Dictionary> beans;
	private Context context;
	private SpeechSynthesizer mSpeechSynthesizer;
	private SharedPreferences mSharedPreferences;
	private Bundle bundle;
	private boolean isShowHeader;

	public DictionaryRecyclerviewAdapter(Context mContext,LayoutInflater mInflater, List<Dictionary> mBeans,SpeechSynthesizer mSpeechSynthesizer,
			SharedPreferences mSharedPreferences, Bundle bundle, boolean isShowHeader) {
		context = mContext;
		beans = mBeans;
		this.mInflater = mInflater;
		this.mSharedPreferences = mSharedPreferences;
		this.mSpeechSynthesizer = mSpeechSynthesizer;
		this.bundle = bundle;
		this.isShowHeader = isShowHeader;
	}
	
	public static class ItemViewHolder extends RecyclerView.ViewHolder {
		
		public TextView record_question;
		public TextView record_answer;
		public FrameLayout record_answer_cover;
		public FrameLayout record_to_practice;
		public FrameLayout record_question_cover;
		public FrameLayout delete_btn;
		public FrameLayout copy_btn;
		public FrameLayout collected_btn;
		public FrameLayout weixi_btn;
		public ImageButton voice_play;
		public CheckBox collected_cb;
		public FrameLayout voice_play_layout;
		public ProgressBar play_content_btn_progressbar;
		
        public ItemViewHolder(View convertView) {
            super(convertView);
            record_question_cover = (FrameLayout) convertView.findViewById(R.id.record_question_cover);
			record_answer_cover = (FrameLayout) convertView.findViewById(R.id.record_answer_cover);
			record_to_practice = (FrameLayout) convertView.findViewById(R.id.record_to_practice);
			record_question = (TextView) convertView.findViewById(R.id.record_question);
			record_answer = (TextView) convertView.findViewById(R.id.record_answer);
			voice_play = (ImageButton) convertView.findViewById(R.id.voice_play);
			collected_cb = (CheckBox) convertView.findViewById(R.id.collected_cb);
			voice_play_layout = (FrameLayout) convertView.findViewById(R.id.voice_play_layout);
			delete_btn = (FrameLayout) convertView.findViewById(R.id.delete_btn);
			copy_btn = (FrameLayout) convertView.findViewById(R.id.copy_btn);
			collected_btn = (FrameLayout) convertView.findViewById(R.id.collected_btn);
			weixi_btn = (FrameLayout) convertView.findViewById(R.id.weixi_btn);
			play_content_btn_progressbar = (ProgressBar) convertView.findViewById(R.id.play_content_btn_progressbar);
        }
    }
	
	public static class RecyclerHeaderViewHolder extends RecyclerView.ViewHolder {
	    public RecyclerHeaderViewHolder(View itemView) {
	        super(itemView);
	    }
	}

	@Override
	public int getItemCount() {
		if(isShowHeader){
			return getBasicItemCount() + 1;
		}else{
			return getBasicItemCount();
		}
	}
	
	@Override
	public int getItemViewType(int position) {
		if(isShowHeader){
			if (isPositionHeader(position)) {
				return TYPE_HEADER;
			}
		}
        return TYPE_ITEM;
	}
	
	private boolean isPositionHeader(int position) {
		if(isShowHeader){
			return position == 0;
		}else{
			return false;
		}
    }
	
	public int getBasicItemCount() {
        return beans == null ? 0 : beans.size();
    }

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup arg0, int viewType) {
		if (viewType == TYPE_ITEM) {
			View v = mInflater.inflate(R.layout.listview_item_dictionary, arg0, false);
			return new ItemViewHolder(v);
		} else {
			View v = mInflater.inflate(R.layout.recycler_header, arg0, false);
			return new RecyclerHeaderViewHolder(v);
		}
	}
	
	public void addEntity(int i, Dictionary entity) {
		beans.add(i, entity);
        notifyItemInserted(i);
    }
 
    public void deleteEntity(int i) {
    	beans.remove(i);
        notifyItemRemoved(i);
    }
    
    @Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
		if (!isPositionHeader(position)) {
			try {
				LogUtil.DefalutLog("DictionaryListViewAdapter---getView");
				final ItemViewHolder holder = (ItemViewHolder) viewHolder;
				int itemposition = position;
				if(isShowHeader){
					itemposition = position-1;
				}
				final Dictionary mBean = beans.get(position);
				AnimationDrawable animationDrawable = (AnimationDrawable) holder.voice_play.getBackground();
				MyOnClickListener mResultClickListener = new MyOnClickListener(mBean,animationDrawable, holder.voice_play,holder.play_content_btn_progressbar, true);
				MyOnClickListener mQuestionOnClickListener = new MyOnClickListener(mBean, animationDrawable, holder.voice_play,holder.play_content_btn_progressbar, false);
				if (mBean.getIscollected().equals("0")) {
					holder.collected_cb.setChecked(false);
				} else {
					holder.collected_cb.setChecked(true);
				}
				holder.record_answer.setText(mBean.getWord_name());
				holder.record_question.setText(mBean.getResult());
				holder.voice_play_layout.setOnClickListener(mResultClickListener);
				holder.record_question_cover.setOnClickListener(mResultClickListener);
				holder.record_answer_cover.setOnClickListener(mQuestionOnClickListener);
				
				holder.delete_btn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						DataBaseUtil.getInstance().dele(mBean);
						beans.remove(mBean);
						notifyDataSetChanged();
						showToast(context.getResources().getString(R.string.dele_success));
						AVAnalytics.onEvent(context, "favor_dic_delete");
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
						AVAnalytics.onEvent(context, "favor_dic_copy");
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
						AVAnalytics.onEvent(context, "favor_dic_collected");
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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
				AVAnalytics.onEvent(context, "favor_dic_share_for_img");
			}

			@Override
			public void onFirstClick(View v) {
				try {
					toShareTextActivity( DictionaryUtil.getShareContent(mBean) );
				} catch (Exception e) {
					e.printStackTrace();
				}
				AVAnalytics.onEvent(context, "favor_dic_share_for_text");
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
		int clickItemPosition = beans.indexOf(mBean);
		beans.remove(mBean);
		if(isShowHeader){
			clickItemPosition = clickItemPosition+1;
		}
		notifyItemRemoved(clickItemPosition);
		DictionaryFragment.isRefresh = true;
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
//				ShowView.showIndexPageGuide(context, KeyUtil.IsHasShowClickText);
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
					 AVAnalytics.onEvent(context, "favor_dic_play_question");
				 }else if(v.getId() == R.id.record_answer_cover){
					 AVAnalytics.onEvent(context, "favor_dic_play_result");
				 }else if(v.getId() == R.id.voice_play_layout){
					 AVAnalytics.onEvent(context, "favor_dic_play_voice");
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
