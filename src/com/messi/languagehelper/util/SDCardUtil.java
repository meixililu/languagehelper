package com.messi.languagehelper.util;

import java.io.File;

import android.os.Environment;

public class SDCardUtil {

	/**sd卡保存文件夹名称**/
	public static final String sdPath = "/zyhy/audio/";
	
	/**sdcard路径
	 * @return
	 */
	public static String getDownloadPath() {
		File SDdir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals( android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			SDdir = Environment.getExternalStorageDirectory();
		}
		if (SDdir != null) {
			String path = SDdir.getPath() + sdPath;
			isFileExists(path);
			return path;
		} else {
			return null;
		}
	}
	
	public static void isFileExists(String path){
		File sdDir = new File(path);
		if(!sdDir.exists()){
			sdDir.mkdirs();
		}
	}
}
