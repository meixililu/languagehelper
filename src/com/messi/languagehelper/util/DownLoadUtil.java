package com.messi.languagehelper.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import com.avos.avoscloud.okhttp.Response;
import com.messi.languagehelper.StudyFragment;
import com.messi.languagehelper.dao.SymbolListDao;
import com.messi.languagehelper.http.LanguagehelperHttpClient;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

public class DownLoadUtil {
	
	public static void downloadFile(final Context mContext,final String url, final String path, final String fileName, final Handler mHandler){
		new Thread(new Runnable() {
			@Override
			public void run() {
				Message msg = new Message();
				try {
					LogUtil.DefalutLog("---url:"+url);
					Response mResponse = LanguagehelperHttpClient.get(url);
					if(mResponse != null){
						if(mResponse.isSuccessful()){
							saveFile(mContext, path, fileName, mResponse.body().bytes());
							if(mHandler != null){
								msg.what = 1;
								mHandler.sendMessage(msg);
							}
						}else if(mResponse.code() == 404){
							if(mHandler != null){
								msg.what = 3;
								mHandler.sendMessage(msg);
							}
						}else{
							if(mHandler != null){
								msg.what = 2;
								mHandler.sendMessage(msg);
							}
						}
					}else{
						if(mHandler != null){
							msg.what = 2;
							mHandler.sendMessage(msg);
						}
					}
				} catch (Exception e) {
					if(mHandler != null){
						msg.what = 2;
						mHandler.sendMessage(msg);
					}
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public static void saveFile(Context mContext, String path, String suffix, byte[] binaryData){
		try {
			FileOutputStream mFileOutputStream = getFile(mContext,path,suffix);
			if(mFileOutputStream != null){
				mFileOutputStream.write(binaryData);
				mFileOutputStream.flush();
				mFileOutputStream.close();
			}
			LogUtil.DefalutLog("---saveFile");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public static FileOutputStream getFile(Context mContext,String dir, String suffix) throws IOException{
		String path = SDCardUtil.getDownloadPath(dir);
		if(!TextUtils.isEmpty(path)){
			File sdDir = new File(path);
			if(!sdDir.exists()){
				sdDir.mkdirs();
			}
			String filePath = path + suffix;
			File mFile = new File(filePath);
			mFile.createNewFile();
			LogUtil.DefalutLog("---getFile---saveSDDirmPath:"+filePath);
			return new FileOutputStream(mFile);
		}else{
			return null;
		}
	}
	
	/**删除内部存储中之前下载的apk
	 * @param mContext
	 */
	public static void deleteOldUpdateFile(Context mContext){
		try {
			File file = mContext.getFilesDir();
			File[] files = file.listFiles();  
			for (int i = 0; i < files.length; i++) {  
				LogUtil.DefalutLog("----------logoutFiles:"+files[i].getName());
				if (files[i].isFile()) {  
					String name = files[i].getName();
					if(name.startsWith("zcp_stand_")){
						SDCardUtil.deleteFile(files[i].getAbsolutePath());  
					}
				}  
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void downloadSymbolMp3(final Context mContext,final List<SymbolListDao> mSymbolListDao,final Handler mHandler){
		new Thread(new Runnable() {
			@Override
			public void run() {
				for(SymbolListDao avObject : mSymbolListDao){
					String audioPath = SDCardUtil.SymbolPath + avObject.getSDCode() + SDCardUtil.Delimiter;
					String SDAudioMp3Url = avObject.getSDAudioMp3Url();
					String SDAudioMp3Name = SDAudioMp3Url.substring(SDAudioMp3Url.lastIndexOf("/")+1);
					String SDAudioMp3FullName = SDCardUtil.getDownloadPath(audioPath) + SDAudioMp3Name;
					if(!SDCardUtil.isFileExist(SDAudioMp3FullName)){
						DownLoadUtil.downloadFile(mContext, SDAudioMp3Url, audioPath, SDAudioMp3Name, null);
					}
					
					String SDTeacherMp3Url = avObject.getSDTeacherMp3Url();
					String SDTeacherMp3Name = SDTeacherMp3Url.substring(SDTeacherMp3Url.lastIndexOf("/")+1);
					String SDTeacherMp3FullName = SDCardUtil.getDownloadPath(audioPath) + SDTeacherMp3Name;
					if(!SDCardUtil.isFileExist(SDTeacherMp3FullName)){
						DownLoadUtil.downloadFile(mContext, SDTeacherMp3Url, audioPath, SDTeacherMp3Name, mHandler);
					}if(mHandler != null){
						Message msg = new Message();
						msg.what = 1;
						mHandler.sendMessage(msg);
					}
				}
			}
		}).start();
	}

}
