package com.messi.languagehelper.util;


import com.messi.languagehelper.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


public class ShortCut {
	
	private static final String isAddShortCut = "isAddShortCut"; 

	/**检测是否已经创建过快捷方式
	 * @param mContext
	 * @param setting
	 * @return
	 */
	public static boolean hasShortcut(Context mContext,SharedPreferences setting) {
		return setting.getBoolean(isAddShortCut, false);
	}

	/**创建快捷方式
	 * @param mContext
	 * @param setting
	 */
	public static void addShortcut(Activity mContext,SharedPreferences setting) {
		if(!ShortCut.hasShortcut(mContext,setting)){
			Intent localIntent1 = new Intent("android.intent.action.MAIN");
			String str1 = mContext.getClass().getName();
			localIntent1.setClassName(mContext, str1);
			localIntent1.addCategory("android.intent.category.LAUNCHER");
			// 这里添加2个flag 可以 消除 在按home 键时，再点快捷方式重启程序的bug
			localIntent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			localIntent1.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			Intent localIntent2 = new Intent();
			localIntent2.putExtra("android.intent.extra.shortcut.INTENT",
					localIntent1);
			String str2 = mContext.getString(R.string.app_name);
			localIntent2.putExtra("android.intent.extra.shortcut.NAME", str2);
			Intent.ShortcutIconResource localShortcutIconResource = Intent.ShortcutIconResource
					.fromContext(mContext.getApplicationContext(), R.drawable.ic_launcher);
			localIntent2.putExtra("android.intent.extra.shortcut.ICON_RESOURCE",
					localShortcutIconResource);
			localIntent2.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
			localIntent2.putExtra("duplicate", false); // 不允许重复创建
			mContext.sendBroadcast(localIntent2);
			
			//快捷方式已经创建修改配置文件
		    SharedPreferences.Editor localEditor = setting.edit();
		    localEditor.putBoolean(isAddShortCut, true);
		    localEditor.commit();
		}
	}
}
