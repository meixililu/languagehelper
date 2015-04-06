package com.messi.languagehelper.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.text.TextUtils;

import com.messi.languagehelper.dao.Dictionary;
import com.messi.languagehelper.dao.EveryDaySentence;
import com.messi.languagehelper.dao.Means;
import com.messi.languagehelper.dao.Parts;
import com.messi.languagehelper.dao.Tag;


public class JsonParser {

	//{"from":"en","to":"zh","trans_result":[{"src":"cold","dst":"\u51b7"}]}
	public static String getTranslateResult(String jsonString){
		try {
			JSONObject jObject = new JSONObject(jsonString);
			if(jObject.has("error_code")){
				return "Error:"+jObject.getString("error_msg");
			}else{
				if(jObject.has("trans_result")){
					JSONArray jArray = new JSONArray(jObject.getString("trans_result"));
					int len = jArray.length();
					if(len == 1){
						JSONObject jaObject = jArray.getJSONObject(0);
						if(jaObject.has("dst")){
							 String tempString = jaObject.getString("dst");
							 return UnicodeToStr.decodeUnicode(tempString);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	/**解析字典json
	 * @param jsonString
	 * @return
	 */
	public static Dictionary parseDictionaryJson(String jsonString){
		Dictionary bean = null;
		try {
			JSONObject jObject = new JSONObject(jsonString);
			if(jObject.has("errno")){
				String errno = jObject.getString("errno");
				if(errno.equals("0")){
					bean = new Dictionary();
					JSONObject dataObject = jObject.getJSONObject("data");
					if(dataObject != null){
						if(dataObject.has("symbols")){
							JSONArray symbols = dataObject.getJSONArray("symbols");
							if(symbols != null){
								for (int i = 0; i < symbols.length(); i++) {
									JSONObject symbol = symbols.getJSONObject(i);
									if(symbol.has("ph_zh")){
										bean.setPh_zh(symbol.getString("ph_zh"));
									}
									if(symbol.has("ph_en")){
										bean.setPh_en(symbol.getString("ph_en"));
									}
									if(symbol.has("ph_am")){
										bean.setPh_am(symbol.getString("ph_am"));
									}
									JSONArray parts = symbol.getJSONArray("parts");
									bean.setPartsList( getPartsJSONArrayToString(parts) );
					            }
							}
						}
						if(dataObject.has("word_name")){
							bean.setWord_name(dataObject.getString("word_name")); 
						}
					}
				}
			}
			if(jObject.has("to")){
				bean.setTo(jObject.getString("to")); 
			}
			if(jObject.has("from")){
				bean.setFrom(jObject.getString("from")); 
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return bean;
	}
	
	public static List<Parts> getPartsJSONArrayToString(JSONArray parts) throws JSONException{
		List<Parts> mPartsList = new ArrayList<Parts>();
		for (int i = 0; i < parts.length(); i++) {
			Parts mParts = new Parts();
			JSONObject part1 = parts.getJSONObject(i);
			JSONArray means = part1.getJSONArray("means");
			List<Means> meanList = getMeansJSONArrayToString(means);
			String part2 = part1.getString("part");
			mParts.setPart(part2);
			mParts.setParts(meanList);
			mPartsList.add(mParts);
        }
		return mPartsList;
	}
	
	public static List<Means> getMeansJSONArrayToString(JSONArray means) throws JSONException{
		List<Means> listMeans = new ArrayList<Means>();
		for (int i = 0; i < means.length(); i++) {
			Means mMeans = new Means();
			mMeans.setMean( means.getString(i) );
			listMeans.add(mMeans);
        }
		return listMeans;
	}
	
	/**
	 * 听写结果的Json格式解析
	 * @param json
	 * @return
	 */
	public static String parseIatResult(String json) {
		if(TextUtils.isEmpty(json))
			return "";
		
		StringBuffer ret = new StringBuffer();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);

			JSONArray words = joResult.getJSONArray("ws");
			for (int i = 0; i < words.length(); i++) {
				// 听写结果词，默认使用第一个结果
				JSONArray items = words.getJSONObject(i).getJSONArray("cw");
				JSONObject obj = items.getJSONObject(0);
				ret.append(obj.getString("w"));
//				如果需要多候选结果，解析数组其他字段
//				for(int j = 0; j < items.length(); j++)
//				{
//					JSONObject obj = items.getJSONObject(j);
//					ret.append(obj.getString("w"));
//				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return ret.toString();
	}
	
	/**
	 * 识别结果的Json格式解析
	 * @param json
	 * @return
	 */
	public static String parseGrammarResult(String json) {
		StringBuffer ret = new StringBuffer();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);

			JSONArray words = joResult.getJSONArray("ws");
			for (int i = 0; i < words.length(); i++) {
				JSONArray items = words.getJSONObject(i).getJSONArray("cw");
				for(int j = 0; j < items.length(); j++)
				{
					JSONObject obj = items.getJSONObject(j);
					if(obj.getString("w").contains("nomatch"))
					{
						ret.append("没有匹配结果.");
						return ret.toString();
					}
					ret.append("【结果】" + obj.getString("w"));
					ret.append("【置信度】" + obj.getInt("sc"));
					ret.append("\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ret.append("没有匹配结果.");
		} 
		return ret.toString();
	}
	
	/**
	 * 语义结果的Json格式解析
	 * @param json
	 * @return
	 */
	public static String parseUnderstandResult(String json) {
		StringBuffer ret = new StringBuffer();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);

			ret.append("【应答码】" + joResult.getString("rc") + "\n");
			ret.append("【转写结果】" + joResult.getString("text") + "\n");
			ret.append("【服务名称】" + joResult.getString("service") + "\n");
			ret.append("【操作名称】" + joResult.getString("operation") + "\n");
			ret.append("【完整结果】" + json);
		} catch (Exception e) {
			e.printStackTrace();
			ret.append("没有匹配结果.");
		} 
		return ret.toString();
	}
	
	public static EveryDaySentence parseEveryDaySentence(JSONObject jObject){
		EveryDaySentence bean = new EveryDaySentence();
		try {
			if(jObject.has("sid")){
				bean.setSid(jObject.getString("sid"));
			}
			if(jObject.has("tts")){
				bean.setTts(jObject.getString("tts"));
			}
			if(jObject.has("content")){
				bean.setContent(jObject.getString("content")); 
			}
			if(jObject.has("note")){
				bean.setNote(jObject.getString("note"));
			}
			if(jObject.has("love")){
				bean.setLove(jObject.getString("love"));
			}
			if(jObject.has("translation")){
				bean.setTranslation(jObject.getString("translation"));
			}
			if(jObject.has("picture")){
				bean.setPicture(jObject.getString("picture")); 
			}
			if(jObject.has("picture2")){
				bean.setPicture2(jObject.getString("picture2")); 
			}
			if(jObject.has("caption")){
				bean.setCaption(jObject.getString("caption")); 
			}
			if(jObject.has("dateline")){
				String dateline = jObject.getString("dateline");
				bean.setDateline(dateline); 
				String temp = dateline.replaceAll("-", "");
				long cid = NumberUtil.StringToLong(temp);
				bean.setCid(cid);
			}
			if(jObject.has("s_pv")){
				bean.setS_pv(jObject.getString("s_pv")); 
			}
			if(jObject.has("sp_pv")){
				bean.setSp_pv(jObject.getString("sp_pv")); 
			}
			if(jObject.has("fenxiang_img")){
				bean.setFenxiang_img(jObject.getString("fenxiang_img")); 
			}
			if(jObject.has("tags")){
				JSONArray tags = jObject.getJSONArray("tags");
				List<Tag> taglist = new ArrayList<Tag>();
				for(int i=0; i<tags.length(); i++){
					Tag mTag = new Tag();
					JSONObject tag = tags.getJSONObject(i);
					mTag.setName(tag.getString("name"));
					taglist.add(mTag);
				}
				bean.setTagList(taglist);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bean;
	}
}
