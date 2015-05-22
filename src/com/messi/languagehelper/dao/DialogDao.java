package com.messi.languagehelper.dao;

import java.util.List;
import com.messi.languagehelper.dao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table DIALOG_DAO.
 */
public class DialogDao {

    private Long id;
    private String did;
    private String type;
    private Integer visit_times;
    private Integer speak_speed;
    private String score;
    private String iscollected;
    private String backup1;
    private String backup2;
    private String backup3;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient DialogDaoDao myDao;

    private List<Role> roleList;

    public DialogDao() {
    }

    public DialogDao(Long id) {
        this.id = id;
    }

    public DialogDao(Long id, String did, String type, Integer visit_times, Integer speak_speed, String score, String iscollected, String backup1, String backup2, String backup3) {
        this.id = id;
        this.did = did;
        this.type = type;
        this.visit_times = visit_times;
        this.speak_speed = speak_speed;
        this.score = score;
        this.iscollected = iscollected;
        this.backup1 = backup1;
        this.backup2 = backup2;
        this.backup3 = backup3;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDialogDaoDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getVisit_times() {
        return visit_times;
    }

    public void setVisit_times(Integer visit_times) {
        this.visit_times = visit_times;
    }

    public Integer getSpeak_speed() {
        return speak_speed;
    }

    public void setSpeak_speed(Integer speak_speed) {
        this.speak_speed = speak_speed;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getIscollected() {
        return iscollected;
    }

    public void setIscollected(String iscollected) {
        this.iscollected = iscollected;
    }

    public String getBackup1() {
        return backup1;
    }

    public void setBackup1(String backup1) {
        this.backup1 = backup1;
    }

    public String getBackup2() {
        return backup2;
    }

    public void setBackup2(String backup2) {
        this.backup2 = backup2;
    }

    public String getBackup3() {
        return backup3;
    }

    public void setBackup3(String backup3) {
        this.backup3 = backup3;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<Role> getRoleList() {
        if (roleList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            RoleDao targetDao = daoSession.getRoleDao();
            List<Role> roleListNew = targetDao._queryDialogDao_RoleList(id);
            synchronized (this) {
                if(roleList == null) {
                    roleList = roleListNew;
                }
            }
        }
        return roleList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetRoleList() {
        roleList = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}