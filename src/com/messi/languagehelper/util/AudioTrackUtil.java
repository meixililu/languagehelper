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
import android.os.Handler;
import android.view.KeyEvent;

import com.iflytek.cloud.SpeechSynthesizer;
import com.messi.languagehelper.task.MyThread;

public class AudioTrackUtil {
	
	public static void stopPlayOnline(SpeechSynthesizer mSpeechSynthesizer){
		if(mSpeechSynthesizer != null){
			mSpeechSynthesizer.stopSpeaking();
		}
	}
	
	public static void stopPlayPcm(Thread mThread){
		if(mThread != null){
			LogUtil.DefalutLog("mThread---stop:"+mThread.getId());
			mThread.interrupt();
    		mThread = null;
		}
	}
	
	public static Thread playPcmTask(String path,Handler mHandler){
		byte[] data = getBytes(path);
		Thread mThread = new Thread(new MyThread(data, mHandler));
		mThread.start();
		return mThread;
	}
	
	public static MyThread getMyThread(String path){
		byte[] data = getBytes(path);
		MyThread mMyThread = new MyThread(data);
		return mMyThread;
	}
	
	public static Thread startMyThread(MyThread mMyThread){
		Thread mThread = new Thread(mMyThread);
		mThread.start();
		return mThread;
	}
	
	public static AudioTrack createAudioTrack(String path) {
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
	        return audioTrack;
	    } catch (Exception e) {
	       e.printStackTrace();
	       return null;
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
