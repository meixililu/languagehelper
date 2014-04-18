package com.messi.languagehelper.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.messi.languagehelper.MainFragment;
import com.messi.languagehelper.bean.DialogBean;
import com.messi.languagehelper.util.LogUtil;

public class DataBaseUtil {

	private DBOpenHelper mDbHelper;

	public DataBaseUtil(Context mContext) {
		mDbHelper = new DBOpenHelper(mContext);
	}

	public long insert(DialogBean bean) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		bean.setIscollected("0");
		bean.setVisit_times(0);
		bean.setVisit_times(MainFragment.speed);
		bean.setQuestionVoiceId(System.currentTimeMillis() + "");
		ContentValues values = new ContentValues();
		values.put(LHContract.FeedEntry.COLUMN_NAME_ENGLISH, bean.getAnswer());
		values.put(LHContract.FeedEntry.COLUMN_NAME_CHINESE, bean.getQuestion());
		values.put(LHContract.FeedEntry.COLUMN_NAME_ISCOLLECTED, "0");//default uncollected
		values.put(LHContract.FeedEntry.COLUMN_NAME_VISIT_TIMES, 0);//default vist time 0
		values.put(LHContract.FeedEntry.COLUMN_NAME_SPEAK_SPEED, bean.getVisit_times());//default speak speed 50
		bean.setResultVoiceId(System.currentTimeMillis()-5 + "");
		values.put(LHContract.FeedEntry.COLUMN_NAME_QUESTIONVOICEID, bean.getQuestionVoiceId());
		values.put(LHContract.FeedEntry.COLUMN_NAME_RESULTVOICEID, bean.getResultVoiceId());
		long newRowId = db.insert(LHContract.FeedEntry.TABLE_NAME,
				LHContract.FeedEntry.COLUMN_NAME_NULLABLE, values);
		bean.setId(newRowId+"");
		return newRowId;
	}
	
	public void update(DialogBean bean){
		String nid = bean.getId();
		String QuestionAudioPath = bean.getQuestionAudioPath();
		String ResultAudioPath = bean.getResultAudioPath();
		try{
			SQLiteDatabase db = mDbHelper.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(LHContract.FeedEntry.COLUMN_NAME_QUESTIONVOICEID, bean.getQuestionVoiceId());
			values.put(LHContract.FeedEntry.COLUMN_NAME_RESULTVOICEID, bean.getResultVoiceId());
			values.put(LHContract.FeedEntry.COLUMN_NAME_QUESTIONAUDIOPATH, QuestionAudioPath);
			values.put(LHContract.FeedEntry.COLUMN_NAME_RESULTAUDIOPATH, ResultAudioPath);
			values.put(LHContract.FeedEntry.COLUMN_NAME_SPEAK_SPEED, bean.getSpeak_speed());
			values.put(LHContract.FeedEntry.COLUMN_NAME_ISCOLLECTED, bean.getIscollected());
			values.put(LHContract.FeedEntry.COLUMN_NAME_VISIT_TIMES, bean.getVisit_times());
			String[] args = {nid};
			db.update(LHContract.FeedEntry.TABLE_NAME, values, LHContract.FeedEntry.COLUMN_NAME_RECORD_ID + " = ?",args);
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			close();
		}
	}

	public List<DialogBean> getDataList(int offset, int maxResult) {
		List<DialogBean> noteBeans = new ArrayList<DialogBean>();
		Cursor cursor = null;
		try {
			SQLiteDatabase db = mDbHelper.getWritableDatabase();
			cursor = db.rawQuery(
					"select * from record order by recordid desc limit ?,? ",
					new String[] { String.valueOf(offset),
							String.valueOf(maxResult) });

			while (cursor.moveToNext()) {
				int recordid = cursor.getInt(cursor.getColumnIndex("recordid"));
				String english = cursor.getString(cursor.getColumnIndex("english"));
				String chinese = cursor.getString(cursor.getColumnIndex("chinese"));
				String questionVoiceId = cursor.getString(cursor.getColumnIndex("questionVoiceId"));
				String resultVoiceId = cursor.getString(cursor.getColumnIndex("resultVoiceId"));
				String questionAudioPath = cursor.getString(cursor.getColumnIndex("questionAudioPath"));
				String resultAudioPath = cursor.getString(cursor.getColumnIndex("resultAudioPath"));
				
				String iscollected = cursor.getString(cursor.getColumnIndex("iscollected"));
				int visit_times = cursor.getInt(cursor.getColumnIndex("visit_times"));
				int speak_speed = cursor.getInt(cursor.getColumnIndex("speak_speed"));
				
				DialogBean mBean = new DialogBean(recordid+"", english, chinese);
				mBean.setQuestionVoiceId(questionVoiceId);
				mBean.setResultVoiceId(resultVoiceId);
				mBean.setQuestionAudioPath(questionAudioPath);
				mBean.setResultAudioPath(resultAudioPath);
				mBean.setIscollected(iscollected);
				mBean.setVisit_times(visit_times);
				mBean.setSpeak_speed(speak_speed);
				
				LogUtil.DefalutLog("DataBaseUtil---getDataList:"+mBean.toString());
				noteBeans.add(mBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return noteBeans;
	}
	
	public List<DialogBean> getDataListCollected(int offset, int maxResult) {
		List<DialogBean> noteBeans = new ArrayList<DialogBean>();
		Cursor cursor = null;
		try {
			SQLiteDatabase db = mDbHelper.getWritableDatabase();
			cursor = db.rawQuery(
					"select * from record where iscollected = '1' order by recordid desc limit ?,? ",
					new String[] { String.valueOf(offset),
							String.valueOf(maxResult) });
			
			while (cursor.moveToNext()) {
				int recordid = cursor.getInt(cursor.getColumnIndex("recordid"));
				String english = cursor.getString(cursor.getColumnIndex("english"));
				String chinese = cursor.getString(cursor.getColumnIndex("chinese"));
				String questionVoiceId = cursor.getString(cursor.getColumnIndex("questionVoiceId"));
				String resultVoiceId = cursor.getString(cursor.getColumnIndex("resultVoiceId"));
				String questionAudioPath = cursor.getString(cursor.getColumnIndex("questionAudioPath"));
				String resultAudioPath = cursor.getString(cursor.getColumnIndex("resultAudioPath"));
				
				String iscollected = cursor.getString(cursor.getColumnIndex("iscollected"));
				int visit_times = cursor.getInt(cursor.getColumnIndex("visit_times"));
				int speak_speed = cursor.getInt(cursor.getColumnIndex("speak_speed"));
				
				DialogBean mBean = new DialogBean(recordid+"", english, chinese);
				mBean.setQuestionVoiceId(questionVoiceId);
				mBean.setResultVoiceId(resultVoiceId);
				mBean.setQuestionAudioPath(questionAudioPath);
				mBean.setResultAudioPath(resultAudioPath);
				mBean.setIscollected(iscollected);
				mBean.setVisit_times(visit_times);
				mBean.setSpeak_speed(speak_speed);
				
				LogUtil.DefalutLog("DataBaseUtil---getDataList:"+mBean.toString());
				noteBeans.add(mBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		
		return noteBeans;
	}

	public void dele(String id) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		db.execSQL("delete from record where recordid=?", new Object[] { id });
	}

	public long getCount() {
		Cursor cursor = null;
		long count = 0;
		try {
			SQLiteDatabase db = mDbHelper.getWritableDatabase();
			cursor = db.rawQuery("select count(*) from record", null);
			cursor.moveToFirst();
			count = cursor.getLong(0);
		} catch (Exception e) {
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return count;
	}
	
	public void close(){
		if(mDbHelper != null){
			mDbHelper.close();
		}
	}

}
