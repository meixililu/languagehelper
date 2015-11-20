package com.messi.languagehelper.task;

import android.media.AudioFormat;
import android.os.Handler;
import android.os.Message;

import com.messi.languagehelper.util.AudioTrackUtil;
import com.messi.languagehelper.util.MyAudioTrack;

public class MyThread implements Runnable {

	public static final int EVENT_PLAY_OVER = 0x100;
	byte[] data;
	Handler mHandler;
	public boolean isPlaying;
	private Object object = new Object();

	public MyThread(Handler handler) {
		this.mHandler = handler;
	}
	
	public MyThread(byte[] data) {
		this.data = data;
	}

	public MyThread(byte[] data, Handler handler) {
		this.data = data;
		mHandler = handler;
	}

	public void run() {
		synchronized (object) {
			if (data == null || data.length == 0) {
				return;
			}
			isPlaying = true;
			// MyAudioTrack: 锟斤拷AudioTrack锟斤拷锟叫简单凤拷装锟斤拷锟斤拷
			MyAudioTrack myAudioTrack = new MyAudioTrack(8000,
					AudioFormat.CHANNEL_OUT_STEREO,
					AudioFormat.ENCODING_PCM_16BIT);
			myAudioTrack.init();
			int playSize = myAudioTrack.getPrimePlaySize();
			int index = 0;
			int offset = 0;
			while (true) {
				try {
					Thread.sleep(0);
					offset = index * playSize;
					if (offset >= data.length) {
						break;
					}
					myAudioTrack.playAudioTrack(data, offset, playSize);
				} catch (Exception e) {
					e.printStackTrace();
					break;
				}
				index++;
			}
			myAudioTrack.release();
			if(mHandler != null){
				Message msg = Message.obtain(mHandler, EVENT_PLAY_OVER);
				mHandler.sendMessage(msg);
			}
			isPlaying = false;
		}
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}
	
	public void setDataUri(String path){
		byte[] data = AudioTrackUtil.getBytes(path);
		setData(data);
	}

}