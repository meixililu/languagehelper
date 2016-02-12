package com.messi.languagehelper.util;

import android.content.Context;
import android.view.WindowManager;

public class ScreenUtil {
	
	public static int dip2px(Context context, float dipValue) {
		if(context != null){
			final float scale = context.getResources().getDisplayMetrics().density;
			return (int) (dipValue * scale + 0.5f);
		}else{
			return (int)dipValue;
		}
	}
	
	public static int px2dip(Context context, float pxValue){
		float scale = context.getResources().getDisplayMetrics().density;
		return (int)(pxValue / scale + 0.5f);
	}

	public static int px2sp(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().scaledDensity;
	    return (int) (pxValue / scale + 0.5f);
	}
	 
	public static int sp2px(Context context, float spValue) {
		final float scale = context.getResources().getDisplayMetrics().scaledDensity;
	    return (int) (spValue * scale + 0.5f);
	}
	
	public static int getScreenWith(Context context){
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return wm.getDefaultDisplay().getWidth();
	}
	
	public static int getCurrentViewWith(Context context, int margin){
		return getScreenWith(context) - dip2px(context,margin);
	}
}
