package com.andy.LuFM.model;

import com.andy.LuFM.Utils.TimeKit;

/**
 * Created by wanglu on 15/11/16.
 */
public class RecommendItemNode extends Node {
    public int sectionId;
    public String belongName;
    public String briefName;
    public String name;
    private String smallThumb;
    private String mediumThumb;
    private String largeThumb;
    public String update_time;
    public int ratingStar;
    public int mCategoryId;
    public Node mNode;
    public int categoryPos;
    private long mUpdateTime;

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
