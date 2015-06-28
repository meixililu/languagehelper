package com.messi.languagehelper.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.gc.materialdesign.widgets.Dialog;
import com.messi.languagehelper.R;

public class ShowView {
	
	public static void showIndexPageGuide(Context mContext,String guideKey){
		SharedPreferences sharedPrefs = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
		if(!sharedPrefs.getBoolean(guideKey, false)){
			String title = mContext.getResources().getString(R.string.textclickguidedialog_title);
			String content = mContext.getResources().getString(R.string.textclickguidedialog_content);
			Dialog dialog = new Dialog(mContext, title, content);
			dialog.addAcceptButton("确定");
			dialog.setCancelable(true);
			dialog.show();
			Editor editor = sharedPrefs.edit();
			editor.putBoolean(guideKey,true);
			editor.commit();
		}
	}

}
