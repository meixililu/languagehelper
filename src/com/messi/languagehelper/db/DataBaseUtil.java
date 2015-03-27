package com.messi.languagehelper.db;

import java.util.List;

import android.content.Context;

import com.messi.languagehelper.BaseApplication;
import com.messi.languagehelper.MainFragment;
import com.messi.languagehelper.dao.DaoSession;
import com.messi.languagehelper.dao.Dictionary;
import com.messi.languagehelper.dao.DictionaryDao;
import com.messi.languagehelper.dao.EveryDaySentence;
import com.messi.languagehelper.dao.EveryDaySentenceDao;
import com.messi.languagehelper.dao.record;
import com.messi.languagehelper.dao.recordDao;
import com.messi.languagehelper.dao.recordDao.Properties;
import com.messi.languagehelper.util.LogUtil;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;

public class DataBaseUtil {

	private static DataBaseUtil instance;  
    private static Context appContext;  
    private DaoSession mDaoSession;  
    private recordDao recordDao; 
    private DictionaryDao mDictionaryDao;
    private EveryDaySentenceDao mEveryDaySentenceDao;

	public DataBaseUtil() {
	}
	
	public static DataBaseUtil getInstance() {  
        if (instance == null) {  
            instance = new DataBaseUtil();  
            if (appContext == null){  
                appContext = BaseApplication.mInstance;  
            }  
            instance.mDaoSession = BaseApplication.getDaoSession(appContext);  
            instance.recordDao = instance.mDaoSession.getRecordDao();  
            instance.mDictionaryDao = instance.mDaoSession.getDictionaryDao();  
            instance.mEveryDaySentenceDao = instance.mDaoSession.getEveryDaySentenceDao();  
        }  
        return instance;  
    }  
	
	public long insert(Dictionary bean){
		bean.setIscollected("0");
		bean.setVisit_times(0);
		bean.setSpeak_speed(MainFragment.speed);
		bean.setQuestionVoiceId(System.currentTimeMillis() + "");
		return mDictionaryDao.insert(bean);
	}

	public long insert(record bean) {
		bean.setIscollected("0");
		bean.setVisit_times(0);
		bean.setSpeak_speed(MainFragment.speed);
		bean.setQuestionVoiceId(System.currentTimeMillis() + "");
		bean.setResultVoiceId(System.currentTimeMillis()-5 + "");
		return recordDao.insert(bean);
	}
	
	public void update(record bean){
		recordDao.update(bean);
	}
	
	public void update(Dictionary bean){
		mDictionaryDao.update(bean);
	}

	public List<record> getDataListRecord(int offset, int maxResult) {
		QueryBuilder<record> qb = recordDao.queryBuilder();
		qb.orderDesc(Properties.Id);
		qb.limit(maxResult);
		return qb.list();
	}
	
	public List<Dictionary> getDataListDictionary(int offset, int maxResult) {
		QueryBuilder<Dictionary> qb = mDictionaryDao.queryBuilder();
		qb.orderDesc(DictionaryDao.Properties.Id);
		qb.limit(maxResult);
		return qb.list();
	}
	
	public List<record> getDataListCollected(int offset, int maxResult) {
		QueryBuilder<record> qb = recordDao.queryBuilder();
		qb.where(Properties.Iscollected.eq("1"));
		qb.orderDesc(Properties.Id);
		qb.limit(maxResult);
		return qb.list();
	}

	public void dele(record bean) {
		recordDao.delete(bean);
	}
	
	public void dele(Dictionary bean) {
		mDictionaryDao.delete(bean);
	}
	
	public void clearExceptFavorite() {
		QueryBuilder<record> qb = recordDao.queryBuilder();
		DeleteQuery<record> bd = qb.where(Properties.Iscollected.eq("0")).buildDelete();
		bd.executeDeleteWithoutDetachingEntities();
	}
	
	public void clearAll() {
		recordDao.deleteAll();
	}
	
	public void clearAllDictionary() {
		mDictionaryDao.deleteAll();
	}

	public long getRecordCount() {
		return recordDao.count();
	}
	
	public long getDictionaryCount(){
		return mDictionaryDao.count();
	}
	
	/**Daily Sentence CURD**/
	public long insert(EveryDaySentence bean){
		return mEveryDaySentenceDao.insert(bean);
	}
	
	public boolean isExist(long cid){
		QueryBuilder<EveryDaySentence> qb = mEveryDaySentenceDao.queryBuilder();
		qb.where(EveryDaySentenceDao.Properties.Cid.eq(cid));
		int size = qb.list().size();
		LogUtil.DefalutLog("isExist---size:"+size);
		return size > 0;
	}
	
	public List<EveryDaySentence> getDailySentenceList(int limit){
		QueryBuilder<EveryDaySentence> qb = mEveryDaySentenceDao.queryBuilder();
		qb.orderDesc(EveryDaySentenceDao.Properties.Id);
		qb.limit(limit);
		return qb.list();
	}

	/**Daily Sentence CURD**/
}
