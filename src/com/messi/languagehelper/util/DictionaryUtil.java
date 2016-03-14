package com.messi.languagehelper.util;

import java.util.List;

import android.text.TextUtils;

import com.messi.languagehelper.dao.Dictionary;
import com.messi.languagehelper.dao.Means;
import com.messi.languagehelper.dao.Parts;

public class DictionaryUtil {

	//baidu 词典 api  for display
	public static String getListToString(Dictionary mBean) throws Exception {
		StringBuilder sb = new StringBuilder();
		boolean isEnglishResult = mBean.getTo().equals("en");
		List<Parts> partsList = mBean.getPartList();
		if (mBean.getFrom().equals("en")) {
			if(!TextUtils.isEmpty(mBean.getPh_am())){
				sb.append("美 /" + mBean.getPh_am() + "/      英 /" + mBean.getPh_en() + "/");
				sb.append("\n");
			}
		} else {
			if(!TextUtils.isEmpty(mBean.getPh_zh())){
				sb.append("拼 /" + mBean.getPh_zh() + "/");
				sb.append("\n");
			}
		}
		for (int i=0; i<partsList.size(); i++) {
			Parts mParts = partsList.get(i);
			if(i>0){
				sb.append("\n");
			}
			sb.append(mParts.getPart());
			if (!TextUtils.isEmpty(mParts.getPart())) {
				sb.append(" ");
			}
			sb.append(getMeans(mParts.getMeanList(), isEnglishResult));
		}
		LogUtil.DefalutLog("getListToString:"+sb.toString());
		return sb.toString();
	}

	public static String getMeans(List<Means> mMeans, boolean isEnglishResult) throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<mMeans.size(); i++) {
			Means mbean = mMeans.get(i);
			sb.append(mbean.getMean());
			if (isEnglishResult) {
				if(i < mMeans.size()-1){
					sb.append("\n");
				}
			} else {
				sb.append(" ");
			}
		}
		return sb.toString();
	}
	
	public static String getShareContent(Dictionary mBean) throws Exception{
		if(mBean.getType().equals(KeyUtil.ResultTypeDictionary)){
			return mBean.getWord_name() + "\n" + mBean.getResult();
		}else if(mBean.getType().equals(KeyUtil.ResultTypeShowapi)){
			return mBean.getWord_name() + "\n" + mBean.getResult();
		}else{
			return mBean.getResult();
		}
	}
	
	public static void getResultSetData(Dictionary mBean) throws Exception {
		StringBuilder sb = new StringBuilder();
		List<Parts> partsList = mBean.getPartList();
		for (Parts mParts : partsList) {
			sb.append( getMeansForPlay(mParts.getMeanList()) );
		}
		mBean.setBackup1(sb.toString());
	}
	
	public static String getMeansForPlay(List<Means> mMeans) throws Exception {
		StringBuilder sb = new StringBuilder();
		for (Means mbean : mMeans) {
			sb.append(mbean.getMean());
			sb.append(",");
		}
		return sb.toString();
	}
	
}
