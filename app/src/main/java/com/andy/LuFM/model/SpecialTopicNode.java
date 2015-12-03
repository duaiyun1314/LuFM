package com.andy.LuFM.model;

import com.andy.LuFM.Utils.TimeKit;
import com.andy.LuFM.data.InfoManager;

import java.util.List;
import java.util.Map;

/**
 * Created by wanglu on 15/11/19.
 */
public class SpecialTopicNode extends Node {
    private long _updateTime;
    public int categoryId;
    public int channelStar;
    public String create_time;
    public String desc;
    public int id;
    private transient List<ChannelNode> mLstChannels;
    public String sub_title;
    public String thumb;
    public String title;
    public String update_time;

    public SpecialTopicNode() {
        this.channelStar = -99;
        this._updateTime = 0;
        this.nodeName = "specialtopic";
    }

    public void updatePartialInfo(SpecialTopicNode node) {
        this.id = node.id;
        this.title = node.title;
        this.sub_title = node.sub_title;
        this.desc = node.desc;
        this.create_time = node.create_time;
        this.update_time = node.update_time;
        this.thumb = node.thumb;
        this.categoryId = node.categoryId;
        this._updateTime = node._updateTime;
    }

    public void setChannels(List<ChannelNode> lstChannels) {
        this.mLstChannels = lstChannels;
    }

    public List<ChannelNode> getlstChannels() {
        return this.mLstChannels;
    }

    public int getApiId() {
        return this.id - 1000001;
    }

    public String getKey() {
        return String.valueOf(this.id);
    }

    public long getUpdateTime() {
        if (this.update_time == null) {
            return 0;
        }
        if (this._updateTime > 0) {
            return this._updateTime;
        }
        this._updateTime = TimeKit.dateToMS(this.update_time);
        return this._updateTime;
    }

    public void onNodeUpdated(Object obj, Map<String, String> map, String type) {
        if (type.equalsIgnoreCase(InfoManager.INodeEventListener.ADD_SPECIAL_TOPIC)) {
            SpecialTopicNode node = (SpecialTopicNode) obj;
            if (node.id == this.id) {
                updatePartialInfo(node);
            }
        }
    }


}
