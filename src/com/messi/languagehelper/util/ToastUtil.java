package com.messi.languagehelper.util;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.text.TextUtils;
import android.widget.Toast;

public class ToastUtil {
	
	public static void diaplayMesShort(Context context, int mes){
		try {
			Toast.makeText(context, mes, 0).show();
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
	}
	public static void diaplayMesShort(Context context, String mes){
		try {
			if(!TextUtils.isEmpty( mes)){
				Toast.makeText(context, mes, 0).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void diaplayMesLong(Context context, String mes){
		try {
			if(!TextUtils.isEmpty( mes)){
				Toast.makeText(context, mes, 1).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
