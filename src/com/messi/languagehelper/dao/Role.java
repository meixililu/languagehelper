package com.messi.languagehelper.dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table ROLE.
 */
public class Role {

    private Long id;
    private String roleEn;
    private String roleCh;
    private String roleId;
    private String rolePath;
    private String isShow;
    private String userSpeak;
    private String userSpeakId;
    private String userSpeakPath;
    private String score;
    private String type;
    private String backup1;
    private Long dialogId;

    public Role() {
    }

    public Role(Long id) {
        this.id = id;
    }

    public Role(Long id, String roleEn, String roleCh, String roleId, String rolePath, String isShow, String userSpeak, String userSpeakId, String userSpeakPath, String score, String type, String backup1, Long dialogId) {
        this.id = id;
        this.roleEn = roleEn;
        this.roleCh = roleCh;
        this.roleId = roleId;
        this.rolePath = rolePath;
        this.isShow = isShow;
        this.userSpeak = userSpeak;
        this.userSpeakId = userSpeakId;
        this.userSpeakPath = userSpeakPath;
        this.score = score;
        this.type = type;
        this.backup1 = backup1;
        this.dialogId = dialogId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoleEn() {
        return roleEn;
    }

    public void setRoleEn(String roleEn) {
        this.roleEn = roleEn;
    }

    public String getRoleCh() {
        return roleCh;
    }

    public void setRoleCh(String roleCh) {
        this.roleCh = roleCh;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRolePath() {
        return rolePath;
    }

    public void setRolePath(String rolePath) {
        this.rolePath = rolePath;
    }

    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String isShow) {
        this.isShow = isShow;
    }

    public String getUserSpeak() {
        return userSpeak;
    }

    public void setUserSpeak(String userSpeak) {
        this.userSpeak = userSpeak;
    }

    public String getUserSpeakId() {
        return userSpeakId;
    }

    public void setUserSpeakId(String userSpeakId) {
        this.userSpeakId = userSpeakId;
    }

    public String getUserSpeakPath() {
        return userSpeakPath;
    }

    public void setUserSpeakPath(String userSpeakPath) {
        this.userSpeakPath = userSpeakPath;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBackup1() {
        return backup1;
    }

    public void setBackup1(String backup1) {
        this.backup1 = backup1;
    }

    public Long getDialogId() {
        return dialogId;
    }

    public void setDialogId(Long dialogId) {
        this.dialogId = dialogId;
    }

}