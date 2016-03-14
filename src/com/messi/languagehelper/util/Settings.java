package com.messi.languagehelper.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources.NotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.ClipboardManager;
import android.view.View;

import com.baidu.mobstat.StatService;
import com.messi.languagehelper.ImgShareActivity;
import com.messi.languagehelper.R;
import com.messi.languagehelper.dao.record;
import com.messi.languagehelper.dialog.PopDialog;
import com.messi.languagehelper.dialog.PopDialog.PopViewItemOnclickListener;

public class Settings {

	/**baidu translate api**/
	public static String baiduTranslateUrl = "http://api.fanyi.baidu.com/api/trans/vip/translate";
	
	/**baidu dictionary api**/
	public static String baiduDictionaryUrl = "http://openapi.baidu.com/public/2.0/translate/dict/simple";
	
	public static String baiduWebTranslateUrl = "http://fanyi.baidu.com/v2transapi";

	public static String IcibaTranslateUrl = "http://fy.iciba.com/api.php";
	
	public static String BaiduOCRUrl = "http://apis.baidu.com/idl_baidu/baiduocrpay/idlocrpaid";
	
	/**juhe dictionary api**/
	public static String JuheYoudaoApiUrl = "http://japi.juhe.cn/youdao/dictionary.from?key=a8edec6297be9b017a106aec955974f6&word=";
	
	/**showapi dictionary api**/
	public static String ShowApiDictionaryUrl = "http://route.showapi.com/32-9";
	
	/**showapi word list api**/
	public static String ShowApiWordListUrl = "http://route.showapi.com/8-11";
	
	/**showapi word detail list api**/
	public static String ShowApiWordDetailListUrl = "http://route.showapi.com/8-10";
	
	/**jinshan daily sentence api**/
	public static String DailySentenceUrl = "http://open.iciba.com/dsapi/";
	
	/**showapi budejie api**/
	public static String BudejieUrl = "http://route.showapi.com/255-1";
	
	/**showapi joke picture api**/
	public static String JokePictureUrl = "http://route.showapi.com/341-2";
	
	/**showapi joke text api**/
	public static String JokeTextUrl = "http://route.showapi.com/341-1";
	
	public static final String CaiLingUrl = "http://api.openspeech.cn/kyls/NTBhYTEyMTM=";
	
	public static final String YueduUrl = "http://api.openspeech.cn/cmread/NTBhYTEyMTM=";
	
	public static final String HotalUrl = "http://api.openspeech.cn/trip/NTBhYTEyMTM=";
	
	public static final String GameUrl = "http://h5.huosu.com/zhongyinghuyi/";
	
	/**invest list**/
	public static final String InvestListUrl = "http://lilu168.sinaapp.com/list.html";
	
	/**广阅通**/
	public static final String GuangyuetongUrl = "http://p.contx.cn/v1/access?id=f9136944-bc17-4cb1-9b14-ece9de91b39d&uid=#uid#&ud=#ud#";

	public static final String Email = "meixililulu@163.com";
	
	public static final String showapi_appid = "11619";	
	public static final String showapi_secret = "f27574671ec14eb4a97faacb2eee3ef2";	
	
	public static final String baidu_appid = "20151111000005006";	
	public static final String baidu_secretkey = "91mGcsmdvX9HAaE8tXoI";	
	public static final String client_id = "vCV6TTGRTI5QrckdYSKHQIhq";	
	public static String from = "auto";	
	public static String to = "auto";	
	public static String q = "";	
	public static String role = "vimary";	
	public static final int offset = 100;
	
	/**is today already do something
	 * @param mSharedPreferences
	 * @return
	 */
	public static boolean isTodayShow(SharedPreferences mSharedPreferences){
		String today = TimeUtil.getTimeDate(System.currentTimeMillis());
		LogUtil.DefalutLog("---isTodayShow---today:"+today);
		String lastTime = mSharedPreferences.getString(KeyUtil.IsLoadingShowToday, "");
		if(today.equals(lastTime)){
			return true;
		}else{
			saveSharedPreferences(mSharedPreferences,KeyUtil.IsLoadingShowToday,today);
			return false;
		}
	}
	
	/**time interval
	 * @param mSharedPreferences
	 * @param interval
	 * @return
	 */
	public static boolean isEnoughTime(SharedPreferences mSharedPreferences, long interval){
		long now = System.currentTimeMillis();
		long lastTime = mSharedPreferences.getLong(KeyUtil.IsEnoughIntervalTime, 0);
		saveSharedPreferences(mSharedPreferences,KeyUtil.IsEnoughIntervalTime,now);
		if(now - lastTime > interval){
			return true;
		}else{
			return false;
		}
	}
	
	/**获取配置文件类
	 * @param context
	 * @return
	 */
	public static SharedPreferences getSharedPreferences(Context context){
		return context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
	}
	
	/**
	 * 保存配置信息
	 * 
	 * @param context
	 * @param value
	 * @param key
	 */
	public static void saveSharedPreferences(SharedPreferences sharedPrefs, String key, String value) {
		LogUtil.DefalutLog("key-value:" + key + "-" + value);
		Editor editor = sharedPrefs.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	/**
	 * 保存配置信息
	 * 
	 * @param context
	 * @param value
	 * @param key
	 */
	public static void saveSharedPreferences(SharedPreferences sharedPrefs, String key, boolean value) {
		LogUtil.DefalutLog("key-value:" + key + "-" + value);
		Editor editor = sharedPrefs.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	/**
	 * 保存配置信息
	 * 
	 * @param context
	 * @param value
	 * @param key
	 */
	public static void saveSharedPreferences(SharedPreferences sharedPrefs, String key, long value) {
		LogUtil.DefalutLog("key-value:" + key + "-" + value);
		Editor editor = sharedPrefs.edit();
		editor.putLong(key, value);
		editor.commit();
	}
	
	/**
	 * 保存配置信息
	 * 
	 * @param context
	 * @param value
	 * @param key
	 */
	public static void saveSharedPreferences(SharedPreferences sharedPrefs, String key, int value) {
		LogUtil.DefalutLog("key-value:" + key + "-" + value);
		Editor editor = sharedPrefs.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static void contantUs(Context mContext){
		try {
			Intent emailIntent = new Intent(Intent.ACTION_SEND);
			emailIntent.setType("message/rfc822");
			emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[] { Email });
			mContext.startActivity(emailIntent);
		} catch (Exception e) {
			copy(mContext,Email);
			e.printStackTrace();
		}
	}
	
	/**
	 * 复制按钮
	 */
	public static void copy(Context mContext,String dstString){
		try {
			// 得到剪贴板管理器
			ClipboardManager cmb = (ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
			cmb.setText(dstString);
			ToastUtil.diaplayMesShort(mContext, mContext.getResources().getString(R.string.copy_success));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int getVersion(Context mContext) {
		try {
			PackageManager manager = mContext.getPackageManager();
			PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
			return info.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}
	
	public static String getIpAddress(Context mContext){
		//获取wifi服务  
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);  
        //判断wifi是否开启  
        if (!wifiManager.isWifiEnabled()) {  
        	wifiManager.setWifiEnabled(true);    
        }  
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();       
        int ipAddress = wifiInfo.getIpAddress();   
        return intToIp(ipAddress);   
	}
	
	public static String intToIp(int i) {       
      return (i & 0xFF ) + "." +       
      ((i >> 8 ) & 0xFF) + "." +       
      ((i >> 16 ) & 0xFF) + "." +       
      ( i >> 24 & 0xFF) ;  
   }  
	
	/**
	 * 分享
	 */
	public static void share(final Context context,final String dstString){
		String[] tempText = new String[2];
		tempText[0] = context.getResources().getString(R.string.share_dialog_text_1);
		tempText[1] = context.getResources().getString(R.string.share_dialog_text_2);
		PopDialog mPopDialog = new PopDialog(context,tempText);
		mPopDialog.setCanceledOnTouchOutside(true);
		mPopDialog.setListener(new PopViewItemOnclickListener() {
			@Override
			public void onSecondClick(View v) {
				toShareImageActivity(context,dstString);
				StatService.onEvent(context, "tab_translate_share_image_btn", "首页翻译页面列表图片分享按钮", 1);
			}
			@Override
			public void onFirstClick(View v) {
				toShareTextActivity(context,dstString);
				StatService.onEvent(context, "tab_translate_share_text_btn", "首页翻译页面列表文字分享按钮", 1);
			}
		});
		mPopDialog.show();
	}
	
	public static void toShareTextActivity(Context context,String dstString){
		Intent intent = new Intent(Intent.ACTION_SEND);    
		intent.setType("text/plain"); // 纯文本     
		intent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.share));    
		intent.putExtra(Intent.EXTRA_TEXT, dstString);    
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
		context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.share)));    
	}
	
	public static void toShareImageActivity(Context context,String dstString){
		Intent intent = new Intent(context, ImgShareActivity.class); 
		intent.putExtra(KeyUtil.ShareContentKey, dstString);
		context.startActivity(intent); 
	}
	
	
}
