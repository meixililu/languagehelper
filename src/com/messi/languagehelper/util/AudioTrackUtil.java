package com.messi.languagehelper.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.view.KeyEvent;

public class AudioTrackUtil {

	public static void createAudioTrack(String path) {
		Test(path);
//		try {
//			byte[] audioData = getBytes(path);
//			if (audioData != null) {
//				AudioTrack audioTrack = new AudioTrack(
//						AudioManager.STREAM_MUSIC, 
//						8000,
//						AudioFormat.CHANNEL_OUT_STEREO,
//						AudioFormat.ENCODING_PCM_16BIT, 
//						audioData.length,
//						AudioTrack.MODE_STATIC);
//				audioTrack.write(audioData, 0, audioData.length);
//				audioTrack.setPositionNotificationPeriod(audioData.length -2);
//				audioTrack.setNotificationMarkerPosition(audioData.length/2);
//				int position = audioTrack.getNotificationMarkerPosition();
//				LogUtil.DefalutLog("---getNotificationMarkerPosition:"+position);
//				audioTrack.setPlaybackPositionUpdateListener(new OnPlaybackPositionUpdateListener() {
//							@Override
//							public void onPeriodicNotification(AudioTrack arg0) {
//								LogUtil.DefalutLog("---onPeriodicNotification");
//							}
//
//							@Override
//							public void onMarkerReached(AudioTrack arg0) {
//								LogUtil.DefalutLog("---onMarkerReached");
//							}
//						});
//				audioTrack.play();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	public static void Test(String path){
	    try {
	    	byte[] audioData = getBytes(path);
	        int minBufferSize = AudioTrack.getMinBufferSize(8000, 
	        		AudioFormat.CHANNEL_OUT_STEREO,
	        		AudioFormat.ENCODING_PCM_16BIT); 
	        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_STEREO,
					AudioFormat.ENCODING_PCM_16BIT, 
					minBufferSize*2,
					AudioTrack.MODE_STREAM);
	        audioTrack.play();
	        audioTrack.write(audioData, 0, audioData.length);
	        audioTrack.stop();
	        audioTrack.release();
	    } catch (Exception e) {
	       e.printStackTrace();
	    } 
	}

	public static boolean isFileExists(String filePath) {
		File file = new File(filePath);
		return file.exists();
	}

	public static void deleteFile(String filePath) {
		try {
			File file = new File(filePath);
			if (file.exists())
				file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得指定文件的byte数组
	 */
	public static byte[] getBytes(String filePath) {
		byte[] buffer = null;
		try {
			File file = new File(filePath);
			if (file.exists()) {
				FileInputStream fis = new FileInputStream(file);
				ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
				byte[] b = new byte[1024];
				int n;
				while ((n = fis.read(b)) != -1) {
					bos.write(b, 0, n);
				}
				fis.close();
				bos.close();
				buffer = bos.toByteArray();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	/**
	 * 根据byte数组，生成文件
	 */
	public static void getFile(byte[] bfile, String filePath, String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if (!dir.exists() && dir.isDirectory()) {// 判断文件目录是否存在
				dir.mkdirs();
			}
			file = new File(filePath + "\\" + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(bfile);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	public static void adjustStreamVolume(Context mContext, int action){
		AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE); 
		if(action == KeyEvent.KEYCODE_VOLUME_UP){
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
		}else{
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
		}
	}

}
