package com.messi.languagehelper.util;

public class StringUtils {
	
	public static void setSpeakerByLan(String lan) {
		if(lan.equals("en")){
			Settings.role = XFUtil.SpeakerEn;
		}else{
			Settings.role = XFUtil.SpeakerZh;
		}
	}
	
	public static void setSpeaker(String content) {
		Settings.role = XFUtil.SpeakerEn;	
		char[] ch = content.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (isChinese(c)) {
				Settings.role = XFUtil.SpeakerZh;	
				break;
			}
		}
	}
	
	public static boolean isEnglish(String content) {
		boolean isEnglish = true;
		char[] ch = content.toCharArray();
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (isChinese(c)) {
				isEnglish = false;
				break;
			}
		}
		return isEnglish;
	}

	// 根据Unicode编码完美的判断中文汉字和符号
	private static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
			return true;
		}
		return false;
	}

}
