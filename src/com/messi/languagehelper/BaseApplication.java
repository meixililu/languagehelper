package com.messi.languagehelper;

import java.util.HashMap;

import android.app.Application;
import android.content.Context;

import com.avos.avoscloud.AVOSCloud;
import com.messi.languagehelper.dao.DaoMaster;
import com.messi.languagehelper.dao.DaoMaster.OpenHelper;
import com.messi.languagehelper.dao.DaoSession;
import com.messi.languagehelper.db.LHContract;

public class BaseApplication extends Application {

	public static HashMap<String, Object> dataMap = new HashMap<String, Object>();
	private static DaoMaster daoMaster;  
    private static DaoSession daoSession; 
    public static BaseApplication mInstance;
    
    @Override  
    public void onCreate() {  
        super.onCreate();  
        if(mInstance == null)  
            mInstance = this; 
        AVOSCloud.initialize(this, "3fg5ql3r45i3apx2is4j9on5q5rf6kapxce51t5bc0ffw2y4", "twhlgs6nvdt7z7sfaw76ujbmaw7l12gb8v6sdyjw1nzk9b1a");
    }  
    
	/** 
     * 取得DaoMaster 
     * @param context 
     * @return 
     */  
    public static DaoMaster getDaoMaster(Context context) {  
        if (daoMaster == null) {  
            OpenHelper helper = new DaoMaster.DevOpenHelper(context,LHContract.DATABASE_NAME, null);  
            daoMaster = new DaoMaster(helper.getWritableDatabase());  
        }  
        return daoMaster;  
    }  
      
    /** 
     * 取得DaoSession 
     * @param context 
     * @return 
     */  
    public static DaoSession getDaoSession(Context context) {  
        if (daoSession == null) {  
            if (daoMaster == null) {  
                daoMaster = getDaoMaster(context);  
            }  
            daoSession = daoMaster.newSession();  
        }  
        return daoSession;  
    }  

}
