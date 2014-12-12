package com.messi.languagehelper.util;

import com.messi.languagehelper.R;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;

public class SpannableStringUtil {

	public static SpannableStringBuilder setTextSize(Context mContext, String content, int size){
		SpannableStringBuilder style = new SpannableStringBuilder(content);
		//改变 0 - dataEnd 的颜色 跟边的颜色相同
		style.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.text_grey)), 0, content.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		//改变 0 - dataEnd 的字体，字体为 35
		style.setSpan(new AbsoluteSizeSpan((int) mContext.getResources().getDimension(size)),  0, content.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		return style;
	}
	
}
