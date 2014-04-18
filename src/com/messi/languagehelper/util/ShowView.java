package com.messi.languagehelper.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.messi.languagehelper.dialog.TextClickGuideDialog;

public class ShowView {
	
	public static void showIndexPageGuide(Context mContext,String guideKey){
		SharedPreferences sharedPrefs = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
		if(!sharedPrefs.getBoolean(guideKey, false)){
			TextClickGuideDialog mGuideDialog = new TextClickGuideDialog(mContext);
			mGuideDialog.setCancelable(true);
			mGuideDialog.show();
			Editor editor = sharedPrefs.edit();
			editor.putBoolean(guideKey,true);
			editor.commit();
		}
	}

}
