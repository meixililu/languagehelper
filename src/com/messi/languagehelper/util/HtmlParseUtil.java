package com.messi.languagehelper.util;

import java.net.URLEncoder;


import android.text.TextUtils;

import com.messi.languagehelper.http.LanguagehelperHttpClient;

public class HtmlParseUtil {
	
	public static String JinShanResult = "";

	public static void parseJinShanHtml(final String html) {
		try {
//			Document doc = Jsoup.parse(html);
//			Elements items = doc.select("ul.base-list");//zh to en
////			Elements items = doc.select("ul.base-list > li > p");//en to zh
//			if (items.size() > 0) {
//				Element item = items.get(0);
//				JinShanResult = item.text();
//				LogUtil.DefalutLog("Html----" + JinShanResult);
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void parseHtml() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
//					LanguagehelperHttpClient.setTranslateLan();
//					String question = URLEncoder.encode(Settings.q, "utf-8");
//					String url = Settings.baiduWebTranslateUrl + Settings.from + "/" + Settings.to + "/" + question;
//					Document doc = Jsoup.connect(url).get();
//					Elements items = doc.select("p.ordinary-output target-output");
//					if (items.size() > 0) {
//						Element item = items.get(0);
//						LogUtil.DefalutLog("Html----" + item.text());
//					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
}
