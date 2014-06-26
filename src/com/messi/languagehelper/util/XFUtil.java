package com.messi.languagehelper.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.iflytek.cloud.speech.RecognizerListener;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechRecognizer;
import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.iflytek.cloud.speech.SpeechUser;
import com.iflytek.cloud.speech.SynthesizerListener;
import com.messi.languagehelper.MainFragment;
import com.messi.languagehelper.R;

public class XFUtil {
	
	public static final String VoiceEngineCH = "zh_cn";
	public static final String VoiceEngineEN = "en_us";
	public static final String VoiceEngineHK = "cantonese";
	
//	中文：
//	domain=iat,language=zh_cn,accent=mandarin
//	英文:
//	domain=iat，language=en_us
//	粤语：
//	domain=iat,language=zh_cn,accent=cantonese
//	这个需要你自己设置，合成使用tts_audio_path设置，识别转写使用asr_audio_path
	
	/**语音转写**/
	public static void showSpeechRecognizer(Context mContext,SharedPreferences mSharedPreferences,
			SpeechRecognizer iatRecognizer,RecognizerListener recognizerListener) {
		if (null == iatRecognizer) {
			iatRecognizer = SpeechRecognizer.createRecognizer(mContext);
		}
		//获取引擎参数
		String domain = mSharedPreferences.getString(
				mContext.getString(R.string.preference_key_iat_engine),
				mContext.getString(R.string.preference_default_iat_engine));
		String language = mSharedPreferences.getString(
				mContext.getString(R.string.preference_key_recognizer),
				mContext.getString(R.string.preference_default_recognizer));
		String accent = mSharedPreferences.getString(
				mContext.getString(R.string.preference_key_accent),
				mContext.getString(R.string.preference_default_accent));
		//清空Grammar_ID，防止识别后进行听写时Grammar_ID的干扰
		iatRecognizer.setParameter(SpeechConstant.CLOUD_GRAMMAR, null);
		iatRecognizer.setParameter(SpeechConstant.DOMAIN, domain);
		iatRecognizer.setParameter(SpeechConstant.LANGUAGE, language);
		iatRecognizer.setParameter(SpeechConstant.ACCENT, accent);
		//设置采样率参数，支持8K和16K 
		String rate = mSharedPreferences.getString(
				mContext.getString(R.string.preference_key_iat_rate),
				mContext.getString(R.string.preference_default_iat_rate));
		LogUtil.DefalutLog("language:"+language+"---"+"accent:"+accent);
		if(rate.equals("rate8k")){
			iatRecognizer.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
		}else {
			iatRecognizer.setParameter(SpeechConstant.SAMPLE_RATE, "16000");
		}
		iatRecognizer.startListening(recognizerListener);
	}
	
	/**合成语音**/
	public static void showSpeechSynthesizer(Context mContext,SharedPreferences mSharedPreferences,
			SpeechSynthesizer mSpeechSynthesizer, String source, SynthesizerListener mSynthesizerListener) {
		if (null == mSpeechSynthesizer) {
			mSpeechSynthesizer = SpeechSynthesizer.createSynthesizer(mContext);
		}
		StringUtils.isChOrEn(source);
		mSpeechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, Settings.role);
		mSpeechSynthesizer.setParameter(SpeechConstant.SPEED, String.valueOf(MainFragment.speed));
		mSpeechSynthesizer.setParameter(SpeechConstant.VOLUME, "100");
		mSpeechSynthesizer.startSpeaking(source, mSynthesizerListener);
	}
	
	/**set speaker
	 * @param language
	 */
	public static void setSpeakLanguage(Context mContext,SharedPreferences mSharedPreferences,String language){
		Editor mEditor = mSharedPreferences.edit();
		if(language.equals(VoiceEngineCH)){
			mEditor.putString(mContext.getString(R.string.preference_key_recognizer),XFUtil.VoiceEngineCH);
			mEditor.putString(mContext.getString(R.string.preference_key_accent),"mandarin");
		}else if(language.equals(VoiceEngineHK)){
			mEditor.putString(mContext.getString(R.string.preference_key_recognizer), XFUtil.VoiceEngineCH);
			mEditor.putString(mContext.getString(R.string.preference_key_accent), "cantonese");
		}else if(language.equals(VoiceEngineEN)){
			mEditor.putString(mContext.getString(R.string.preference_key_recognizer),XFUtil.VoiceEngineEN);
			mEditor.putString(mContext.getString(R.string.preference_key_accent), "");
		}
		mEditor.commit();
	}
}
