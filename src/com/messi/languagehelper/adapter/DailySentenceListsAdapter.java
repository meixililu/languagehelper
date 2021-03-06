package com.messi.languagehelper.adapter;

import java.io.IOException;
import java.util.List;

import com.avos.avoscloud.okhttp.Callback;
import com.avos.avoscloud.okhttp.Request;
import com.avos.avoscloud.okhttp.Response;
import com.bumptech.glide.Glide;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.messi.languagehelper.R;
import com.messi.languagehelper.ViewImageActivity;
import com.messi.languagehelper.dao.EveryDaySentence;
import com.messi.languagehelper.http.LanguagehelperHttpClient;
import com.messi.languagehelper.util.DownLoadUtil;
import com.messi.languagehelper.util.KeyUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.SDCardUtil;
import com.messi.languagehelper.util.TextHandlerUtil;
import com.messi.languagehelper.views.ProportionalImageView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class DailySentenceListsAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<EveryDaySentence> beans;
	private Activity context;
	private MediaPlayer mPlayer;
	public ProgressBarCircularIndeterminate mProgressbar;

	public DailySentenceListsAdapter(Activity mContext,LayoutInflater mInflater,List<EveryDaySentence> mBeans, MediaPlayer mPlayer,
			ProgressBarCircularIndeterminate mProgressbar) {
		context = mContext;
		beans = mBeans;
		this.mInflater = mInflater;
		this.mPlayer = mPlayer;
		this.mProgressbar = mProgressbar;
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
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.daily_sentence_list_item, null);
			holder.daily_sentence_list_item_cover = (FrameLayout) convertView.findViewById(R.id.daily_sentence_list_item_cover);
			holder.daily_sentence_list_item_img = (ProportionalImageView) convertView.findViewById(R.id.daily_sentence_list_item_img);
			holder.play_img = (ImageView) convertView.findViewById(R.id.play_img);
			holder.english_txt = (TextView) convertView.findViewById(R.id.english_txt);
			holder.chinese_txt = (TextView) convertView.findViewById(R.id.chinese_txt);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final EveryDaySentence mBean = beans.get(position);
		holder.chinese_txt.setText(mBean.getNote());
		TextHandlerUtil.handlerText(context, mProgressbar, holder.english_txt, mBean.getContent());
		
		Glide.with(context)
		.load(mBean.getPicture2())
		.into(holder.daily_sentence_list_item_img);
		
		holder.daily_sentence_list_item_cover.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toViewImgActivity(mBean.getFenxiang_img());
			}
		});
		if(mBean.isPlaying()){
			holder.play_img.setBackgroundResource(R.drawable.ic_stop_circle_outline_white_48dp);
		}else{
			holder.play_img.setBackgroundResource(R.drawable.ic_play_circle_outline_white_48dp);
		}
		holder.play_img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!mProgressbar.isShown()){
					if(mPlayer.isPlaying() && mBean.isPlaying()){
						mPlayer.stop();
						resetData();
					}else if(mPlayer.isPlaying()){
						mPlayer.stop();
						resetData();
						prepareData(mBean);
					}else{
						prepareData(mBean);
					}
				}
				
			}
		});
		return convertView;
	}
	
	private void prepareData(EveryDaySentence mBean){
		int pos = mBean.getTts().lastIndexOf(SDCardUtil.Delimiter) + 1;
		String fileName = mBean.getTts().substring(pos, mBean.getTts().length());
		String fileFullName = SDCardUtil.getDownloadPath(SDCardUtil.DailySentencePath) + fileName;
		LogUtil.DefalutLog("fileName:"+fileName+"---fileFullName:"+fileFullName);
		mBean.setPlaying(true);
		notifyDataSetChanged();
		if(SDCardUtil.isFileExist(fileFullName)){
			playMp3(fileFullName);
			LogUtil.DefalutLog("FileExist");
		}else{
			LogUtil.DefalutLog("FileNotExist");
			downloadFile(context, mBean.getTts(), fileName);
		}
	}
	
	private void resetData(){
		for(EveryDaySentence mBean : beans){
			if(mBean.isPlaying()){
				mBean.setPlaying(false);
			}
		}
		notifyDataSetChanged();
	}
	
	private void playMp3(String uriPath){
		try {
			mPlayer.reset();
			Uri uri = Uri.parse(uriPath);
			mPlayer.setDataSource(context, uri);
			mPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					resetData();
				}
			});
			mPlayer.prepare();
			mPlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void downloadFile(final Context mContext,String url, final String suffix){
		LogUtil.DefalutLog("---url:"+url);
		if(mProgressbar != null){
			mProgressbar.setVisibility(View.VISIBLE);
		}
		LanguagehelperHttpClient.get(url,new Callback() {
			@Override
			public void onResponse(Response arg0) throws IOException {
				DownLoadUtil.saveFile(context, SDCardUtil.DailySentencePath, suffix, arg0.body().bytes());
				String fileFullName = SDCardUtil.getDownloadPath(SDCardUtil.DailySentencePath) + suffix;
				onFinish();
				playMp3(fileFullName);
			}
			@Override
			public void onFailure(Request arg0, IOException arg1) {
				onFinish();
			}
		});
	}
	
	private void onFinish(){
		context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(mProgressbar != null){
					mProgressbar.setVisibility(View.GONE);
				}
			}
		});
	}
	
	private void toViewImgActivity(String imgurl){
		Intent intent = new Intent(context, ViewImageActivity.class);
		intent.putExtra(KeyUtil.DailySentenceBigImgUrl, imgurl);
		context.startActivity(intent);
	}
	
	static class ViewHolder {
		TextView english_txt;
		TextView chinese_txt;
		ProportionalImageView daily_sentence_list_item_img;
		ImageView play_img;
		FrameLayout daily_sentence_list_item_cover;
	}
	
}
