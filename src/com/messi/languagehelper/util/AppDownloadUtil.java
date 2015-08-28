package com.messi.languagehelper.util;

import java.io.File;

import org.apache.http.Header;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;

import com.avos.avoscloud.AVObject;
import com.lerdian.wall.MainActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.messi.languagehelper.R;
import com.messi.languagehelper.http.LanguagehelperHttpClient;

public class AppDownloadUtil {

	private Context mContext;
	private int record = -2;
	private String url;
	private String ContentTitle;
	private String Ticker;
	private String appFileName;
	private String appLocalFullName;
	private String AVObjectId;
	private String path;
	
	public AppDownloadUtil(Context mContext, String url, String appName, String AVObjectId, String path){
		this.mContext = mContext;
		this.url = url;
		this.ContentTitle = appName + "下载通知";
		this.Ticker = appName + "开始下载";
		this.appFileName = getFileName(url);
		this.AVObjectId = AVObjectId;
		this.path = path;
		this.appLocalFullName = getLocalFile(appFileName);
	}
	
	public void DownloadFile(){
		if(isFileExist()){
			installApk(mContext,appLocalFullName);
		}else if(!TextUtils.isEmpty(url)){
			final NotificationManager mNotifyManager  = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
			final Builder mBuilder = new NotificationCompat.Builder(mContext); 
			mBuilder.setContentTitle(ContentTitle).setContentText("开始下载").setSmallIcon(R.drawable.ic_get_app_white_36dp).setTicker(Ticker).setAutoCancel(true);
			Intent intent = new Intent (mContext, MainActivity.class);
			intent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent pend = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			mBuilder.setContentIntent (pend);
			mNotifyManager.notify(0, mBuilder.build());
			LanguagehelperHttpClient.get(url, new RequestParams(), new AsyncHttpResponseHandler(){
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
					LogUtil.DefalutLog("---DownloadFile success");
					DownLoadUtil.saveFile(mContext,SDCardUtil.apkPath,appFileName,binaryData);
					PendingIntent pend = PendingIntent.getActivity(mContext, 0, getInstallApkIntent(appLocalFullName), 
							PendingIntent.FLAG_UPDATE_CURRENT);
					mBuilder.setContentIntent (pend);
		            mNotifyManager.notify(0, mBuilder.build());
		            installApk(mContext,appLocalFullName);
		            updateDownloadTime();
				}
				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
					LogUtil.DefalutLog("---DownloadFile onFailure");
					mBuilder.setContentText("下载失败,请稍后重试").setProgress(0,0,false);
		            mNotifyManager.notify(0, mBuilder.build());
					error.printStackTrace();
				}
				@Override
				public void onProgress(int bytesWritten, int totalSize) {
					super.onProgress(bytesWritten, totalSize);
					if(bytesWritten <= totalSize){
						int percent = (int) (((float)bytesWritten / totalSize) * 100);
						if(percent != 100 && record != percent){
							record = percent;
							mBuilder.setProgress(100, percent, false);
							mBuilder.setContentText("更新进度"+percent+"%");
							mNotifyManager.notify(0, mBuilder.build());
						}else if(percent == 100){
							mBuilder.setContentText("下载完成").setProgress(0,0,false);
							mNotifyManager.notify(0, mBuilder.build());
						}
					}
				}
			});
		}
	}
	
	private void updateDownloadTime(){
		AVObject post = AVObject.createWithoutData(AVOUtil.AppRecommendDetail.AppRecommendDetail, AVObjectId);
		post.increment(AVOUtil.AppRecommendDetail.DownloadTimes);
		post.saveInBackground();
	} 
	
	/**安装apk**/
	public void installApk(Context mContext,String filePath){
		mContext.startActivity(getInstallApkIntent(filePath));
	}
	
	public Intent getInstallApkIntent(String filePath){
		LogUtil.DefalutLog("---filePath:"+filePath);
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
		return i;
	}
	
	public String getFileName(String url){
		String name = url.substring(url.lastIndexOf("/")+1);
		LogUtil.DefalutLog("FileName:"+name);
		return name;
	}
	
	public String getLocalFile(String fileName){
		String tPath = SDCardUtil.getDownloadPath(path);
		return tPath + fileName;
	}
	
	public boolean isFileExist(){
		String filePath = getLocalFile(appFileName);
		File file = new File(filePath);
		return file.exists();
	}
	
}
