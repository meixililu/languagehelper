package com.messi.languagehelper.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.Header;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.messi.languagehelper.http.LanguagehelperHttpClient;

public class DownLoadUtil {
	
	public static void downloadFile(final Context mContext,String url, final String path, final String fileName, final Handler mHandler){
		LogUtil.DefalutLog("---url:"+url);
		String[] allowedContentTypes = new String[] {".*"};  
		LanguagehelperHttpClient.get(url, new RequestParams(), new BinaryHttpResponseHandler(allowedContentTypes){
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,Throwable arg3) {
				LogUtil.DefalutLog("---onFailure");
				arg3.printStackTrace();
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				LogUtil.DefalutLog("---onSuccess");
				saveFile(mContext, path, fileName, arg2);
				if(mHandler != null){
					Message msg = new Message();
					msg.what = 1;
					mHandler.sendMessage(msg);
				}
			}
		});
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

}
