package com.messi.languagehelper.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.messi.languagehelper.util.LogUtil;

public class DBOpenHelper extends SQLiteOpenHelper {

	// If you change the database schema, you must increment the database
	// version.
	public static final int DATABASE_VERSION = 2;
	public static final String DATABASE_NAME = "Languagehelper.db";
	public static final String TABLE_NAME_ENTRIES = "languagehelper";

	private static final String TEXT_TYPE = " TEXT";
	private static final String NUMBER_TYPE = " INTEGER";
	private static final String COMMA_SEP = ",";
	private static final String SQL_CREATE_ENTRIES = "CREATE TABLE "
			+ LHContract.FeedEntry.TABLE_NAME + " (" + LHContract.FeedEntry.COLUMN_NAME_RECORD_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ LHContract.FeedEntry.COLUMN_NAME_ENGLISH + TEXT_TYPE + COMMA_SEP
			+ LHContract.FeedEntry.COLUMN_NAME_CHINESE + TEXT_TYPE + COMMA_SEP
			
			+ LHContract.FeedEntry.COLUMN_NAME_RESULTAUDIOPATH + TEXT_TYPE + COMMA_SEP
			+ LHContract.FeedEntry.COLUMN_NAME_QUESTIONAUDIOPATH + TEXT_TYPE + COMMA_SEP
			+ LHContract.FeedEntry.COLUMN_NAME_QUESTIONVOICEID + TEXT_TYPE + COMMA_SEP
			+ LHContract.FeedEntry.COLUMN_NAME_RESULTVOICEID + TEXT_TYPE + COMMA_SEP
			+ LHContract.FeedEntry.COLUMN_NAME_ISCOLLECTED + TEXT_TYPE + COMMA_SEP
			+ LHContract.FeedEntry.COLUMN_NAME_VISIT_TIMES + NUMBER_TYPE + COMMA_SEP
			+ LHContract.FeedEntry.COLUMN_NAME_SPEAK_SPEED + NUMBER_TYPE + COMMA_SEP
			
			+ LHContract.FeedEntry.COLUMN_NAME_BACKUP1 + TEXT_TYPE + COMMA_SEP
			+ LHContract.FeedEntry.COLUMN_NAME_BACKUP2 + TEXT_TYPE + COMMA_SEP
			+ LHContract.FeedEntry.COLUMN_NAME_BACKUP3 + TEXT_TYPE + " )";

	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + LHContract.FeedEntry.TABLE_NAME;

	private String CREATE_TEMP_RECORD = "alter table record rename to _temp_record";
    private String INSERT_DATA = "insert into record select *,'','','','','1',0,50,'','','' from _temp_record";
    private String DROP_BOOK = "drop table _temp_record";
	
	public DBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		LogUtil.DefalutLog("SQL_CREATE_ENTRIES:"+SQL_CREATE_ENTRIES);
		db.execSQL(SQL_CREATE_ENTRIES);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 switch (oldVersion) {
		  case 1:
		   db.execSQL(CREATE_TEMP_RECORD);
		   db.execSQL(SQL_CREATE_ENTRIES);
		   db.execSQL(INSERT_DATA);
		   db.execSQL(DROP_BOOK);
		   break;
		  }
	}

	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

}
