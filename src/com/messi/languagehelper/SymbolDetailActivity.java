package com.messi.languagehelper;

import java.io.IOException;
import java.util.List;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.baidu.mobstat.StatService;
import com.messi.languagehelper.util.AVOUtil;
import com.messi.languagehelper.util.DownLoadUtil;
import com.messi.languagehelper.util.LogUtil;
import com.messi.languagehelper.util.SDCardUtil;

public class SymbolDetailActivity extends BaseActivity implements OnClickListener {

	private FrameLayout symbol_cover,teacher_cover;
	private TextView symbol_en,symbol_des,symbol_info,error_txt;
	private LinearLayout content;
	private ImageView play_img;
	private ImageButton symbol_play_img,teacher_play_img;
	private MediaPlayer mPlayer;
	private String SLCode,audioPath;
	private AVObject avObject;
	private String SDAudioMp3FullName,SDTeacherMp3FullName;
	private String SDAudioMp3Name,SDTeacherMp3Name;
	private String SDAudioMp3Url,SDTeacherMp3Url;
	private String currentFileFullName;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.symbol_study_activity);
		initData();
		initViews();
		new QueryTask().execute();
	}
	
	private void initData(){
		SLCode = getIntent().getStringExtra(AVOUtil.SymbolList.SLCode);
		audioPath = SDCardUtil.SymbolPath + SLCode + SDCardUtil.Delimiter;
		mPlayer = new MediaPlayer();
	}

	private void initViews() {
		content = (LinearLayout) findViewById(R.id.content);
		symbol_cover = (FrameLayout) findViewById(R.id.symbol_cover);
		teacher_cover = (FrameLayout) findViewById(R.id.teacher_cover);
		symbol_en = (TextView) findViewById(R.id.symbol_en);
		symbol_des = (TextView) findViewById(R.id.symbol_des);
		symbol_info = (TextView) findViewById(R.id.symbol_info);
		error_txt = (TextView) findViewById(R.id.error_txt);
		play_img = (ImageView) findViewById(R.id.play_img);
		symbol_play_img = (ImageButton) findViewById(R.id.symbol_play_img);
		teacher_play_img = (ImageButton) findViewById(R.id.teacher_play_img);
		error_txt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new QueryTask().execute();
			}
		});
		
		symbol_cover.setOnClickListener(this);
		teacher_cover.setOnClickListener(this);
	}
	
	private class QueryTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressbar();
			content.setVisibility(View.VISIBLE);
			error_txt.setVisibility(View.GONE);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			AVQuery<AVObject> query = new AVQuery<AVObject>(AVOUtil.SymbolDetail.SymbolDetail);
			query.whereEqualTo(AVOUtil.SymbolDetail.SLCode, SLCode);
			query.whereEqualTo(AVOUtil.SymbolDetail.SDIsValid, "1");
			try {
				List<AVObject> avObjects  = query.find();
				if(avObjects != null && avObjects.size() > 0){
					avObject = avObjects.get(0);
				}
			} catch (AVException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			hideProgressbar();
			if(avObject != null){
				String SDName = avObject.getString(AVOUtil.SymbolDetail.SDName);
				if(!TextUtils.isEmpty(SDName)){
					setData();
				}else{
					error_txt.setVisibility(View.VISIBLE);
					content.setVisibility(View.GONE);
				}
			}else{
				content.setVisibility(View.GONE);
				error_txt.setVisibility(View.VISIBLE);
			}
		}
	}
	
	private void setData(){
		try {
			symbol_en.setText(avObject.getString(AVOUtil.SymbolDetail.SDName));
			symbol_des.setText(avObject.getString(AVOUtil.SymbolDetail.SDDes));
			symbol_info.setText(avObject.getString(AVOUtil.SymbolDetail.SDInfo));
			
			AVFile avFile = avObject.getAVFile(AVOUtil.SymbolDetail.SDAudioMp3);
			SDAudioMp3Url = avFile.getUrl();
			SDAudioMp3Name = SDAudioMp3Url.substring(SDAudioMp3Url.lastIndexOf("/")+1);
			SDAudioMp3FullName = SDCardUtil.getDownloadPath(audioPath) + SDAudioMp3Name;
			
			if(!SDCardUtil.isFileExist(SDAudioMp3FullName)){
				DownLoadUtil.downloadFile(this, SDAudioMp3Url, audioPath, SDAudioMp3Name, null);
			}
			
			AVFile avFileTeacher = avObject.getAVFile(AVOUtil.SymbolDetail.SDTeacherMp3);
			SDTeacherMp3Url = avFileTeacher.getUrl();
			SDTeacherMp3Name = SDTeacherMp3Url.substring(SDTeacherMp3Url.lastIndexOf("/")+1);
			SDTeacherMp3FullName = SDCardUtil.getDownloadPath(audioPath) + SDTeacherMp3Name;
			
			if(!SDCardUtil.isFileExist(SDTeacherMp3FullName)){
				DownLoadUtil.downloadFile(this, SDTeacherMp3Url, audioPath, SDTeacherMp3Name, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 1){
				hideProgressbar();
				initUri(currentFileFullName);
			}
		}
	};
	
	private void playMp3(final String uriPath){
		try {
			if(mPlayer.isPlaying()){
				play_img.setBackgroundResource(R.drawable.ic_play_circle_outline_grey600_36dp);
				if(!uriPath.equals(currentFileFullName)){
					mPlayer.stop();
					initUri(uriPath);
				}else{
					mPlayer.pause();
				}
				currentFileFullName = uriPath;
			}else{
				if(TextUtils.isEmpty(currentFileFullName) || !uriPath.equals(currentFileFullName)){
					initUri(uriPath);
				}else{
					if(uriPath.equals(SDTeacherMp3FullName)){
						play_img.setBackgroundResource(R.drawable.ic_pause_circle_outline_grey600_36dp);
					}
					mPlayer.start();
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	private void initUri(final String uriPath){
		try {
			if(uriPath.equals(SDTeacherMp3FullName)){
				play_img.setBackgroundResource(R.drawable.ic_pause_circle_outline_grey600_36dp);
			}
			mPlayer.reset();
			Uri uri = Uri.parse(uriPath);
			mPlayer.setDataSource(this, uri);
			mPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					if(uriPath.equals(SDTeacherMp3FullName)){
						play_img.setBackgroundResource(R.drawable.ic_play_circle_outline_grey600_36dp);
					}
				}
			});
			mPlayer.prepare();
			mPlayer.start();
			if(uriPath.equals(SDTeacherMp3FullName)){
				play_img.setBackgroundResource(R.drawable.ic_pause_circle_outline_grey600_36dp);
			}
			mPlayer.start();
			currentFileFullName = uriPath;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) { 
		case R.id.symbol_cover:
			if(SDCardUtil.isFileExist(SDTeacherMp3FullName)){
				playMp3(SDAudioMp3FullName);
			}else{
				currentFileFullName = SDAudioMp3FullName;
				DownLoadUtil.downloadFile(this, SDAudioMp3Url, audioPath, SDAudioMp3Name, mHandler);
			}
			StatService.onEvent(SymbolDetailActivity.this, "symbol_mp3", "音标详情音标播放", 1);
			break;
		case R.id.teacher_cover:
			if(SDCardUtil.isFileExist(SDTeacherMp3FullName)){
				playMp3(SDTeacherMp3FullName);
			}else{
				currentFileFullName = SDTeacherMp3FullName;
				DownLoadUtil.downloadFile(this, SDTeacherMp3Url, audioPath, SDTeacherMp3Name, mHandler);
			}
			StatService.onEvent(SymbolDetailActivity.this, "symbol_teacher_mp3", "音标详情音标外教播放", 1);
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mHandler != null){
			mHandler = null;
		}
		if (mPlayer != null) {   
			mPlayer.stop();  
			mPlayer.release();   
			mPlayer = null;   
        }  
	}
	
}
