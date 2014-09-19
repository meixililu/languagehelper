package com.messi.languagehelper.util;

import android.content.Context;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class ViewUtil {

	public static ImageView getImageView(Context mContext){
		ImageView img = new ImageView(mContext);
		ViewGroup.LayoutParams mParams = new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		img.setLayoutParams(mParams);
		return img;
	}
	
	public static ImageView getStudyProgress(Context mContext,ImageView img, int color){
		img.setImageResource(mContext.getResources().getColor(color));;
		return img;
	}
	
}
