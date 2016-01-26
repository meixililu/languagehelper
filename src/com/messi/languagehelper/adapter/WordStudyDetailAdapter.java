package com.messi.languagehelper.adapter;

import java.util.List;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.messi.languagehelper.R;
import com.messi.languagehelper.WordStudyDetailActivity;
import com.messi.languagehelper.dao.WordDetailListItem;
import com.messi.languagehelper.db.DataBaseUtil;
import com.messi.languagehelper.task.MyThread;
import com.messi.languagehelper.util.AudioTrackUtil;
import com.messi.languagehelper.util.DownLoadUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.SDCardUtil;
import com.messi.languagehelper.util.ToastUtil;
import com.messi.languagehelper.util.XFUtil;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class WordStudyDetailAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private WordStudyDetailActivity context;
	private List<WordDetailListItem> avObjects;
	private ListView category_lv;
	private MediaPlayer mPlayer;
	private String audioPath;
	private String fullName;
	private boolean isPlayNext;
	private int autoPlayIndex;
	private int loopTime;
	
	private SpeechSynthesizer mSpeechSynthesizer;
	private SharedPreferences mSharedPreferences;
	private Thread mThread;
	private MyThread mMyThread;
	
	public WordStudyDetailAdapter(WordStudyDetailActivity mContext,SharedPreferences mSharedPreferences,
			SpeechSynthesizer mSpeechSynthesizer, ListView category_lv,List<WordDetailListItem> avObjects, String audioPath, MediaPlayer mPlayer) {
		context = mContext;
		this.mInflater = LayoutInflater.from(mContext);
		this.avObjects = avObjects;
		this.category_lv = category_lv;
		this.mPlayer = mPlayer;
		this.audioPath = audioPath;
		this.mSharedPreferences = mSharedPreferences;
		this.mSpeechSynthesizer = mSpeechSynthesizer;
		mMyThread = new MyThread(mHandler);
	}

	public int getCount() {
		return avObjects.size();
	}

	public WordDetailListItem getItem(int position) {
		return avObjects.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.word_study_detail_item, null);
			holder = new ViewHolder();
			holder.cover = (View) convertView.findViewById(R.id.layout_cover);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.des = (TextView) convertView.findViewById(R.id.des);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final WordDetailListItem mAVObject = avObjects.get(position);
		holder.name.setText( mAVObject.getName() );
		if(!TextUtils.isEmpty(mAVObject.getBackup1())){
			holder.des.setText( mAVObject.getSymbol() +"\n" + mAVObject.getDesc());
		}else{
			holder.des.setText("");
		}
		holder.cover.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				isPlayNext = false;
				playItem(mAVObject);
			}
		});
		return convertView;
	}

	static class ViewHolder {
		View cover;
		TextView name;
		TextView des;
	}

	private void playItem(WordDetailListItem mAVObject){
		clearPlaySign();
		mAVObject.setBackup1("play");
		notifyDataSetChanged();
		if(TextUtils.isEmpty(mAVObject.getSound()) ||  mAVObject.getSound().equals("http://app1.showapi.com/en_word")){
			playWithSpeechSynthesizer(mAVObject);
		}else{
			String mp3Name = mAVObject.getSound().substring(mAVObject.getSound().lastIndexOf("/")+1);
			fullName = SDCardUtil.getDownloadPath(audioPath) + mp3Name;
			if(!SDCardUtil.isFileExist(fullName)){
				DownLoadUtil.downloadFile(context, mAVObject.getSound(), audioPath, mp3Name, mHandler);
			}else{
				playMp3();
			}
		}
	}
	
	private void playWithSpeechSynthesizer(WordDetailListItem mAVObject){
		String filepath = SDCardUtil.getDownloadPath(audioPath) + mAVObject.getItem_id() + ".pcm";
		if(!AudioTrackUtil.isFileExists(filepath)){
			mSpeechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH, filepath);
			XFUtil.showSpeechSynthesizer(context,mSharedPreferences,mSpeechSynthesizer,mAVObject.getName(),
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
				}
				@Override
				public void onCompleted(SpeechError arg0) {
					replay();
				}
				@Override
				public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
				}
				@Override
				public void onEvent(int arg0, int arg1, int arg2,Bundle arg3) {
				}
			});
		}else{
			mMyThread.setDataUri(filepath);
			mThread = AudioTrackUtil.startMyThread(mMyThread);
		}
	}
	
	public void onPlayBtnClick(int index){
		if(index < avObjects.size()){
			autoPlayIndex = index;
			isPlayNext = true;
			WordDetailListItem mAVObject = avObjects.get(index);
			playItem(mAVObject);
			category_lv.setSelection(index);
		}else{
			isPlayNext = false;
			ToastUtil.diaplayMesShort(context, "播放完毕");
			context.stopPlay();
		}
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 1){
				playMp3();
			}else if (msg.what == MyThread.EVENT_PLAY_OVER) {
				replay();
			}
		}
	};
	
	public void playMp3(){
		try {
			if(mPlayer.isPlaying()){
				mPlayer.stop();
			}
			mPlayer.reset();
			Uri uri = Uri.parse(fullName);
			mPlayer.setDataSource(context, uri);
			mPlayer.prepare();
			mPlayer.start();
			mPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					replay();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void replay(){
		if(isPlayNext){
			loopTime ++;
			if(loopTime > 2){
				autoPlayIndex++;
				loopTime = 0;
			}
			onPlayBtnClick(autoPlayIndex);
		}
	}
	
	private void clearPlaySign(){
		for(WordDetailListItem mAVObject : avObjects){
			mAVObject.setBackup1("");
		}
	}
	
	public void setIsPlayNext(boolean isPlay){
		isPlayNext = isPlay;
	}
	
	public boolean isPlaying(){
		if(mPlayer.isPlaying()){
			mPlayer.stop();
			return false;
		}else{
			return true;
		}
	}

}
