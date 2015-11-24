package com.andy.LuFM.model;

import com.andy.LuFM.data.InfoManager;

import java.util.List;

/**
 * Created by wanglu on 15/11/24.
 */
public class UserInfo extends Node {
    public long fansNumber;
    public boolean isBlocked = false;
    public boolean isPodcaster = false;
    public long lastestUpdateTime;
    public int level = 0;
    private transient List<ChannelNode> mLstChannelNodes;
    private transient List<ProgramNode> mLstProgramNodes;
    public int podcasterId;
    public String podcasterName;
    public SnsInfo snsInfo = new SnsInfo();
    //public String userId = InfoManager.getInstance().getDeviceId();
    public String userId = null;
    public String userKey = "";
    public String userType = "normal";

    public List<ChannelNode> getChannelNodes() {
        return this.mLstChannelNodes;
    }

    public void setChannelNodes(List<ChannelNode> lstNodes) {
        this.mLstChannelNodes = lstNodes;
    }

    public List<ProgramNode> getProgramNodes() {
        return this.mLstProgramNodes;
    }

    public void setProgramNodes(List<ProgramNode> lstNodes) {
        this.mLstProgramNodes = lstNodes;
    }

    public boolean isBlocked() {
        return this.isBlocked;
    }

    public void setBlocked(boolean flag) {
        this.isBlocked = flag;
    }

    public int getLevel() {
        return this.level;
    }

   /* public String getUid() {
        return new StringBuilder(String.valueOf(this.snsInfo.sns_site)).append(this.snsInfo.sns_id).toString();
    }*/

    public void swapUserInfo(UserInfo info) {
        if (info != null) {
            String temp = this.userId;
            this.userId = info.userId;
            info.userId = temp;
            temp = this.userKey;
            this.userKey = info.userKey;
            info.userKey = temp;
            int tempAge = this.snsInfo.age;
            this.snsInfo.age = info.snsInfo.age;
            info.snsInfo.age = tempAge;
            temp = this.snsInfo.signature;
            this.snsInfo.signature = info.snsInfo.signature;
            info.snsInfo.signature = temp;
            temp = this.snsInfo.sns_account;
            this.snsInfo.sns_account = info.snsInfo.sns_account;
            info.snsInfo.sns_account = temp;
            temp = this.snsInfo.sns_avatar;
            this.snsInfo.sns_avatar = info.snsInfo.sns_avatar;
            info.snsInfo.sns_avatar = temp;
            temp = this.snsInfo.sns_gender;
            this.snsInfo.sns_gender = info.snsInfo.sns_gender;
            info.snsInfo.sns_gender = temp;
            temp = this.snsInfo.sns_id;
            this.snsInfo.sns_id = info.snsInfo.sns_id;
            info.snsInfo.sns_id = temp;
            temp = this.snsInfo.sns_name;
            this.snsInfo.sns_name = info.snsInfo.sns_name;
            info.snsInfo.sns_name = temp;
            temp = this.snsInfo.sns_site;
            this.snsInfo.sns_site = info.snsInfo.sns_site;
            info.snsInfo.sns_site = temp;
            temp = this.snsInfo.source;
            this.snsInfo.source = info.snsInfo.source;
            info.snsInfo.source = temp;
            boolean tempBlock = this.isBlocked;
            this.isBlocked = info.isBlocked;
            info.isBlocked = tempBlock;
            int tempLevel = this.level;
            this.level = info.level;
            info.level = tempLevel;
            boolean isPodcaster = this.isPodcaster;
            this.isPodcaster = info.isPodcaster;
            info.isPodcaster = isPodcaster;
            int podcasterId = this.podcasterId;
            this.podcasterId = info.podcasterId;
            info.podcasterId = podcasterId;
        }
    }

    public void updateUserInfo(UserInfo info) {
        if (info != null) {
            // this.userId = info.userId;
            this.userKey = info.userKey;
            this.podcasterName = info.podcasterName;
            this.fansNumber = info.fansNumber;
            this.snsInfo.age = info.snsInfo.age;
            this.snsInfo.signature = info.snsInfo.signature;
            this.snsInfo.sns_account = info.snsInfo.sns_account;
            this.snsInfo.sns_avatar = info.snsInfo.sns_avatar;
            this.snsInfo.sns_gender = info.snsInfo.sns_gender;
            this.snsInfo.sns_id = info.snsInfo.sns_id;
            this.snsInfo.sns_name = info.snsInfo.sns_name;
            this.snsInfo.sns_site = info.snsInfo.sns_site;
            this.snsInfo.source = info.snsInfo.source;
            this.snsInfo.desc = info.snsInfo.desc;
            this.isBlocked = info.isBlocked;
            this.level = info.level;
            this.isPodcaster = info.isPodcaster;
            this.podcasterId = info.podcasterId;
        }
    }

}
