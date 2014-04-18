package com.messi.languagehelper.util;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesUtil {
	
	public static String IsHasShowBaiduMessage = "IsHasShowBaiduMessage";
	
	public static String IsHasShowClickText = "IsHasShowClickText";
	
	public static String SpeakPutonghuaORYueyu = "SpeakPutonghuaORYueyu";
	
	public static void saveString(SharedPreferences mSharedPreferences, String key, String value){
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putString(key, value);
		mEditor.commit();
	}
	
	public static void saveInt(SharedPreferences mSharedPreferences, String key, int value){
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putInt(key, value);
		mEditor.commit();
	}
	
	public static void saveBoolean(SharedPreferences mSharedPreferences, String key, boolean value){
		Editor mEditor = mSharedPreferences.edit();
		mEditor.putBoolean(key, value);
		mEditor.commit();
	}
}
