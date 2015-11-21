package com.andy.LuFM.model;

import com.andy.LuFM.Utils.TimeKit;

/**
 * Created by wanglu on 15/11/16.
 */
public class RecommendItemNode extends Node {
    public String belongName;
    public String briefName;
    public int categoryPos;
    public String desc;
    public String id;
    public transient boolean isAds;
    public boolean isweb;
    private String largeThumb;
    //public transient AdvertisementItemNode mAdNode;
    public String mAttributesPath;
    public int mCategoryId;
    public transient int mClickCnt;
    public transient Node mNode;
    private long mUpdateTime;
    private String mediumThumb;
    public String name;
    public int ratingStar;
    public int redirect;
    public int redirectToVirtualChannels;
    public int sectionId;
    private transient long showLinkTime;
    private String smallThumb;
    public transient int time;
    public String update_time;

    public RecommendItemNode() {
        this.id = "";
        this.name = "";
        this.briefName = "";
        this.desc = "";
        this.isweb = false;
        this.redirectToVirtualChannels = 0;
        this.mUpdateTime = 0;
        this.sectionId = 0;
        this.belongName = "";
        this.ratingStar = -1;
        this.isAds = false;
        //this.mAdNode = null;
        this.categoryPos = 1;
        this.mClickCnt = 0;
        this.redirect = 0;
        this.nodeName = "recommenditem";
    }

    public String getLargeThumb() {
        return largeThumb;
    }

    public void setLargeThumb(String largeThumb) {
        this.largeThumb = largeThumb;
    }

    public String getMediumThumb() {
        return mediumThumb;
    }

    public void setMediumThumb(String mediumThumb) {
        this.mediumThumb = mediumThumb;
    }

    public String getSmallThumb() {
        return smallThumb;
    }

    public void setSmallThumb(String smallThumb) {
        this.smallThumb = smallThumb;
    }

    public boolean noThumb() {
        return this.smallThumb == null && this.mediumThumb == null && this.largeThumb == null;
    }

    public Node getDetail() {
        return this.mNode;
    }

    public void setDetail(Node node) {
        if (node != null) {
            this.mNode = node;
            this.mNode.parent = this;
        }
    }

    public long getUpdateTime() {
        if (this.update_time == null) {
            return 0;
        }
        if (this.mUpdateTime > 0) {
            return this.mUpdateTime;
        }
        this.mUpdateTime = TimeKit.dateToMS(this.update_time);
        return this.mUpdateTime;
    }
}
