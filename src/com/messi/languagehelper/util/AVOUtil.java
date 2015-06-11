package com.messi.languagehelper.util;

import com.avos.avoscloud.AVObject;

public class AVOUtil {

	public static void addObject(){
		AVObject testObject = new AVObject(AVOUtil.PracticeCategory.PracticeCategory);
		testObject.put(AVOUtil.PracticeCategory.PCCode, "002");
		testObject.saveInBackground();
	}
	
	public static class PracticeCategory {
		
		public final static String PracticeCategory = "PracticeCategory";
		
		public final static String PCCode = "PCCode";
		
		public final static String PCName = "PCName";
		
		public final static String PCType = "PCType";
		
		public final static String PCIsValid = "PCIsValid";
		
		public final static String PCSign = "PCSign";
		
		public final static String PCIsNew = "PCIsNew";
		
		public final static String PCImgUrl = "PCImgUrl";
		
		public final static String PCOrder = "PCOrder";
		
	}
	
	public static class PracticeCategoryList {
		
		public final static String PracticeCategoryList = "PracticeCategoryList";
		
		public final static String PCLCode = "PCLCode";
		
		public final static String PCLName = "PCLName";
		
		public final static String PCCode = "PCCode";
		
		public final static String PCLType = "PCLType";
		
		public final static String PCLIsValid = "PCLIsValid";
		
		public final static String PCLSign = "PCLSign";
		
		public final static String PCLIsNew = "PCLIsNew";
		
		public final static String PCLImgUrl = "PCLImgUrl";
		
		public final static String PCLOrder = "PCLOrder";
		
	}
	
	public static class PracticeDetail {
		
		public final static String PracticeDetail = "PracticeDetail";
		
		public final static String PDCode = "PDCode";
		
		public final static String PDContent = "PDContent";
		
		public final static String PCCode = "PCCode";
		
		public final static String PCLCode = "PCLCode";
		
		public final static String PDType = "PDType";
		
		public final static String PDIsValid = "PDIsValid";
		
		
	}
	
}
