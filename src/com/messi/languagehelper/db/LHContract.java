package com.messi.languagehelper.db;

import android.provider.BaseColumns;

public class LHContract {
	
	public static abstract class FeedEntry implements BaseColumns {
	    public static final String TABLE_NAME = "record";
	    public static final String COLUMN_NAME_RECORD_ID = "recordid";
	    public static final String COLUMN_NAME_ENGLISH = "english";
	    public static final String COLUMN_NAME_CHINESE = "chinese";
	    public static final String COLUMN_NAME_RESULTAUDIOPATH = "resultAudioPath";
	    public static final String COLUMN_NAME_QUESTIONAUDIOPATH = "questionAudioPath";
	    public static final String COLUMN_NAME_QUESTIONVOICEID = "questionVoiceId";
	    public static final String COLUMN_NAME_RESULTVOICEID = "resultVoiceId";
	    public static final String COLUMN_NAME_ISCOLLECTED = "iscollected";
	    public static final String COLUMN_NAME_VISIT_TIMES = "visit_times";
	    public static final String COLUMN_NAME_SPEAK_SPEED = "speak_speed";
	    public static final String COLUMN_NAME_BACKUP1 = "backup1";
	    public static final String COLUMN_NAME_BACKUP2 = "backup2";
	    public static final String COLUMN_NAME_BACKUP3 = "backup3";
		public static final String COLUMN_NAME_NULLABLE = "null";
	}
	
	private LHContract(){ } 

}
