package com.messi.languagehelper.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
	
	public static String getDate(long time){
		SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date mDate = new Date(time);
		return mSimpleDateFormat.format(mDate);
	}

}
