package com.messi.languagehelper.util;

import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.messi.languagehelper.R;

public class ViewUtil {

	public static ImageView getImageView(Context mContext){
		ImageView img = new ImageView(mContext);
		return img;
	}
	
	public static void getStudyProgress(Context mContext,ImageView img, int color, int margin){
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(0,LayoutParams.WRAP_CONTENT);
		mParams.rightMargin = ScreenUtil.dip2px(mContext, margin);
		mParams.weight = 1;
		img.setImageResource(mContext.getResources().getColor(color));
		img.setLayoutParams(mParams);
	}
	
	public static void addIndicator(int size, LinearLayout view, Context mContext){
		for(int i=0; i<size; i++){
			ImageView img = getImageView(mContext);
			getStudyProgress(mContext, img, R.color.green_light, 5);
			view.addView(img);
		}
	}
	
}
