package com.messi.languagehelper.util;

import org.apache.http.Header;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.text.TextUtils;

import com.lerdian.wall.MainActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.messi.languagehelper.LoadingActivity;
import com.messi.languagehelper.R;
import com.messi.languagehelper.http.LanguagehelperHttpClient;

public class AppDownloadUtil {

	private Context mContext;
	private int record = -2;
	private String url;
	private String ContentTitle;
	private String Ticker;
	private String appCode;
	
	public AppDownloadUtil(Context mContext,String url,String appName,String appCode){
		this.mContext = mContext;
		this.url = url;
		this.ContentTitle = appName + "下载通知";
		this.Ticker = appName + "开始下载";
		this.appCode = appCode + ".apk";
	}
	
	public void DownloadFile(){
		if(!TextUtils.isEmpty(url)){
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
					DownLoadUtil.saveFile(mContext,SDCardUtil.apkPath,appCode,binaryData);
					String path = SDCardUtil.getDownloadPath(SDCardUtil.apkPath);
					String filePath = path + appCode;
					PendingIntent pend = PendingIntent.getActivity(mContext, 0, getInstallApkIntent(filePath), 
							PendingIntent.FLAG_UPDATE_CURRENT);
					mBuilder.setContentIntent (pend);
		            mNotifyManager.notify(0, mBuilder.build());
		            installApk(mContext,filePath);
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
	
}
