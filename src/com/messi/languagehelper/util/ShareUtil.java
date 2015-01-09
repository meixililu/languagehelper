package com.messi.languagehelper.util;

import android.content.Context;
import android.content.Intent;

import com.messi.languagehelper.R;

public class ShareUtil {
	
	public static void shareLink(Context mContext, String dstString){
		Intent intent = new Intent(Intent.ACTION_SEND);    
		intent.setType("text/plain"); // 纯文本     
		intent.putExtra(Intent.EXTRA_SUBJECT, mContext.getResources().getString(R.string.share));    
		intent.putExtra(Intent.EXTRA_TEXT, dstString);    
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    
		mContext.startActivity(Intent.createChooser(intent, mContext.getResources().getString(R.string.share)));    
	}

}
