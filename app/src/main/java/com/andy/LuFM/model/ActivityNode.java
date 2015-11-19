package com.andy.LuFM.model;

import java.util.List;

/**
 * Created by wanglu on 15/11/19.
 */
public class ActivityNode extends Node {
    public int categoryId;
    public int channelId;
    public List<String> clickTracking;
    public String contentUrl;
    public String desc;
    public boolean hasShared;
    public int id;
    public List<String> imageTracking;
    public String infoTitle;
    public String infoUrl;
    public String name;
    public String network;
    public boolean putUserInfo;
    public String titleIconUrl;
    public String type;
    public int updatetime;
    public boolean useLocalWebview;

    public ActivityNode() {
        this.name = "\u6d3b\u52a8";
        this.network = "";
        this.useLocalWebview = false;
        this.putUserInfo = true;
        this.hasShared = true;
        this.nodeName = "activity";
    }

}
