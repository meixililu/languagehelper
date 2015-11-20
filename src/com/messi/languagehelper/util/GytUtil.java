package com.messi.languagehelper.util;

import com.messi.languagehelper.R;

import android.app.Activity;
import cn.contentx.ContExManager;

public class GytUtil {

	public static void showHtml(Activity mActivity, String title){
		 ContExManager.WebViewBuilder webViewBuilder = new ContExManager.WebViewBuilder()
		 .setShowHead(true)
		 .setHeight(45F)
         .setTitle(title)//默认是h5页面的标题，如果这里设置了标题，则显示为用户自己设置的标题
         .setTitleSize(16F)//标题文字的大小，默认14sp
         .setBkgColor("#2196F3")//头部导航条的背景颜色，默认黑色
		 .setBackupIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
		 webViewBuilder.show(mActivity);
	}
	
}
