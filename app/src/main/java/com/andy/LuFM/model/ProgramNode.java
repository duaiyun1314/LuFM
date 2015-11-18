package com.andy.LuFM.model;

/**
 * Created by wanglu on 15/11/16.
 */
public class ProgramNode extends Node{
    public int channelId;
    private String channelName;
    public int channelRatingStar;

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
