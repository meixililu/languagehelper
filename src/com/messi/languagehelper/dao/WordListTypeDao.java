package com.messi.languagehelper.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.messi.languagehelper.dao.WordListType;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "WORD_LIST_TYPE".
*/
public class WordListTypeDao extends AbstractDao<WordListType, Long> {

    public static final String TABLENAME = "WORD_LIST_TYPE";

    /**
     * Properties of entity WordListType.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Type_id = new Property(1, String.class, "type_id", false, "TYPE_ID");
        public final static Property Course_num = new Property(2, String.class, "course_num", false, "COURSE_NUM");
        public final static Property Title = new Property(3, String.class, "title", false, "TITLE");
        public final static Property Word_num = new Property(4, String.class, "word_num", false, "WORD_NUM");
        public final static Property ListJson = new Property(5, String.class, "listJson", false, "LIST_JSON");
        public final static Property Img_url = new Property(6, String.class, "img_url", false, "IMG_URL");
        public final static Property Backup1 = new Property(7, String.class, "backup1", false, "BACKUP1");
        public final static Property Backup2 = new Property(8, String.class, "backup2", false, "BACKUP2");
        public final static Property Backup3 = new Property(9, String.class, "backup3", false, "BACKUP3");
    };


    public WordListTypeDao(DaoConfig config) {
        super(config);
    }
    
    public WordListTypeDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"WORD_LIST_TYPE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"TYPE_ID\" TEXT," + // 1: type_id
                "\"COURSE_NUM\" TEXT," + // 2: course_num
                "\"TITLE\" TEXT," + // 3: title
                "\"WORD_NUM\" TEXT," + // 4: word_num
                "\"LIST_JSON\" TEXT," + // 5: listJson
                "\"IMG_URL\" TEXT," + // 6: img_url
                "\"BACKUP1\" TEXT," + // 7: backup1
                "\"BACKUP2\" TEXT," + // 8: backup2
                "\"BACKUP3\" TEXT);"); // 9: backup3
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"WORD_LIST_TYPE\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, WordListType entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String type_id = entity.getType_id();
        if (type_id != null) {
            stmt.bindString(2, type_id);
        }
 
        String course_num = entity.getCourse_num();
        if (course_num != null) {
            stmt.bindString(3, course_num);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(4, title);
        }
 
        String word_num = entity.getWord_num();
        if (word_num != null) {
            stmt.bindString(5, word_num);
        }
 
        String listJson = entity.getListJson();
        if (listJson != null) {
            stmt.bindString(6, listJson);
        }
 
        String img_url = entity.getImg_url();
        if (img_url != null) {
            stmt.bindString(7, img_url);
        }
 
        String backup1 = entity.getBackup1();
        if (backup1 != null) {
            stmt.bindString(8, backup1);
        }
 
        String backup2 = entity.getBackup2();
        if (backup2 != null) {
            stmt.bindString(9, backup2);
        }
 
        String backup3 = entity.getBackup3();
        if (backup3 != null) {
            stmt.bindString(10, backup3);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public WordListType readEntity(Cursor cursor, int offset) {
        WordListType entity = new WordListType( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // type_id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // course_num
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // title
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // word_num
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // listJson
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // img_url
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // backup1
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // backup2
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9) // backup3
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, WordListType entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setType_id(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setCourse_num(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTitle(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setWord_num(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setListJson(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setImg_url(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setBackup1(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setBackup2(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setBackup3(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(WordListType entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(WordListType entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}