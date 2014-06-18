package com.messi.languagehelper.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

public class SDCardUtil {

	/**sd卡保存文件夹名称**/
	public static final String sdPath = "/zyhy/audio/";
	public static final String ImgPath = "/DCIM/zyhy/";
	
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
			String path = SDdir.getPath();
//			isFileExists(path);
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
	
	public static String isDirExits(Context mContext,String path) throws IOException{
		String sdcard = getDownloadPath();
		if(!TextUtils.isEmpty(path)){
			File sdDir = new File(sdcard + path);
			if(!sdDir.exists()){
				sdDir.mkdirs();
			}
			return sdcard + path;
		}else{
			return "";
		}
	}
	
	public static String saveBitmap(Context mContext, Bitmap bitmap) throws IOException {
		String sdcardDir = isDirExits(mContext, ImgPath);
		String filePath = "";
		if(!TextUtils.isEmpty(sdcardDir)){
			filePath = sdcardDir + "image_" + System.currentTimeMillis() + ".png";
			File file = new File(filePath);
			file.createNewFile();
			FileOutputStream out;
			try {
				out = new FileOutputStream(file);
				if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
					out.flush();
					out.close();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			ToastUtil.diaplayMesShort(mContext, "请插入SD卡");
		}
		return filePath;
	}
}
