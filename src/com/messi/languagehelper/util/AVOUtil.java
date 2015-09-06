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
	
	public static class StudyDialogCategory {
		
		public final static String StudyDialogCategory = "StudyDialogCategory";
		
		public final static String SDCode = "SDCode";
		
		public final static String SDName = "SDName";
		
		public final static String SDType = "SDType";
		
		public final static String SDIsValid = "SDIsValid";
		
		public final static String SDSign = "SDSign";
		
		public final static String SDImgUrl = "SDImgUrl";
		
		public final static String SDOrder = "SDOrder";
	}
	
	public static class StudyDialogListCategory {
		
		public final static String StudyDialogListCategory = "StudyDialogListCategory";
		
		public final static String SDLCode = "SDLCode";
		
		public final static String SDLName = "SDLName";
		
		public final static String SDCode = "SDCode";
		
		public final static String SDLType = "SDLType";
		
		public final static String SDLIsValid = "SDLIsValid";
		
		public final static String SDLSign = "SDLSign";
		
		public final static String SDLImgUrl = "SDLImgUrl";
		
		public final static String SDLOrder = "SDLOrder";
	}
	
	public static class StudyDialogDetail {
		
		public final static String StudyDialogDetail = "StudyDialogDetail";
		
		public final static String SDDCode = "SDDCode";
		
		public final static String SDDContent = "SDDContent";
		
		public final static String SDCode = "SDCode";
		
		public final static String SDLCode = "SDLCode";
		
		public final static String SDDType = "SDDType";
		
		public final static String SDDIsValid = "SDDIsValid";
		
	}
	
	public static class EvaluationCategory {
		
		public final static String EvaluationCategory = "EvaluationCategory";
		
		public final static String ECCode = "ECCode";
		
		public final static String ECName = "ECName";
		
		public final static String ECType = "ECType";
		
		public final static String ECIsValid = "ECIsValid";
		
		public final static String ECSign = "ECSign";
		
		public final static String ECImgUrl = "ECImgUrl";
		
		public final static String ECOrder = "ECOrder";
	}
	
	public static class EvaluationCategoryList {
		
		public final static String EvaluationCategoryList = "EvaluationCategoryList";
		
		public final static String ECLCode = "ECLCode";
		
		public final static String ECLName = "ECLName";
		
		public final static String ECCode = "ECCode";
		
		public final static String ECLType = "ECLType";
		
		public final static String ECLIsValid = "ECLIsValid";
		
		public final static String ECLSign = "ECLSign";
		
		public final static String ECLImgUrl = "ECLImgUrl";
		
		public final static String ECLOrder = "ECLOrder";
	}
	
	public static class EvaluationDetail {
		
		public final static String EvaluationDetail = "EvaluationDetail";
		
		public final static String EDCode = "EDCode";
		
		public final static String EDContent = "EDContent";
		
		public final static String ECCode = "ECCode";
		
		public final static String ECLCode = "ECLCode";
		
		public final static String EDType = "EDType";
		
		public final static String EDIsValid = "EDIsValid";
		
	}
	
	public static class EnglishWebsite {
		
		public final static String EnglishWebsite = "EnglishWebsite";
		
		public final static String Title = "Title";
		
		public final static String Describe = "Describe";
		
		public final static String ImgUrl = "ImgUrl";
		
		public final static String ShareMsg = "ShareMsg";
		
		public final static String Url = "Url";
		
		public final static String Order = "Order";
		
		public final static String IsValid = "IsValid";
		
	}
	
	public static class AppRecommendList {
		
		public final static String AppRecommendList = "AppRecommendList";
		
		public final static String AppTypeName = "AppTypeName";
		
		public final static String AppTypeCode = "AppTypeCode";
		
		public final static String AppTypeDescribe = "AppTypeDescribe";
		
		public final static String AppTypeIcon = "AppTypeIcon";
		
		public final static String Order = "Order";
		
		public final static String IsValid = "IsValid";
		
	}
	
	public static class AppRecommendDetail {
		
		public final static String AppRecommendDetail = "AppRecommendDetail";
		
		public final static String AppName = "AppName";
		
		public final static String AppCode = "AppCode";
		
		public final static String Apk = "Apk";
		
		public final static String DownloadType = "DownloadType";//apk  url
		
		public final static String AppTypeCode = "AppTypeCode";
		
		public final static String AppDescribe = "AppDescribe";
		
		public final static String AppSize = "AppSize";
		
		public final static String APPIcon = "APPIcon";
		
		public final static String APPUrl = "APPUrl";
		
		public final static String Order = "Order";
		
		public final static String IsValid = "IsValid";
		
		public final static String DownloadTimes = "DownloadTimes";
		
	}
	
	public static class UpdateInfo {
		
		public final static String UpdateInfo = "UpdateInfo";
		
		public final static String AppName = "AppName";
		
		public final static String AppCode = "AppCode";
		
		public final static String VersionCode = "VersionCode";
		
		public final static String Apk = "Apk";
		
		public final static String DownloadType = "DownloadType";//apk  url
		
		public final static String AppUpdateInfo = "AppUpdateInfo";
		
		public final static String AppSize = "AppSize";
		
		public final static String APPUrl = "APPUrl";
		
		public final static String IsValid = "IsValid";
		
		public final static String DownloadTimes = "DownloadTimes";
		
	}

}
