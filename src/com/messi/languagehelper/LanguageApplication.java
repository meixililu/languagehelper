package com.messi.languagehelper;

import java.util.HashMap;

import android.app.Application;

public class LanguageApplication extends Application {
	
	/** 可以用于所有activity页面之间传送数据的静态map **/
	public static HashMap<String, Object> dataMap = new HashMap<String, Object>();

}
