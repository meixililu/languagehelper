package com.messi.languagehelper.util;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.messi.languagehelper.R;

public class ViewUtil {

	public static TextView getImageView(Context mContext, int color, int margin){
		TextView img = new TextView(mContext);
		img.setBackgroundColor(mContext.getResources().getColor(color));
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(0,ScreenUtil.dip2px(mContext, 12));
		mParams.rightMargin = ScreenUtil.dip2px(mContext, 10);
		mParams.weight = 1;
		img.setLayoutParams(mParams);
		return img;
	}
	
	public static void addIndicator(int size, LinearLayout view, Context mContext){
		try {
			for(int i=0; i<size; i++){
				TextView img = getImageView(mContext,R.color.green_light, 5);
				view.addView(img);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setPracticeIndicator(Context mContext, LinearLayout parent,int position){
		try {
			int size = parent.getChildCount();
			if(position < size){
				View view = parent.getChildAt(position);
				view.setBackgroundColor(mContext.getResources().getColor(R.color.green));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
