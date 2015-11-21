package com.andy.LuFM.model;

import com.andy.LuFM.helper.ChannelHelper;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by wanglu on 15/11/16.
 */
public class ProgramNode extends Node {
    public static final int LIVE_PROGRAM = 1;
    public static final int PAST_PROGRAM = 3;
    public static final int RESERVE_PROGRAM = 2;
    private long _createTime;
    private long _updateTime;
    private long absoluteEndTime;
    private long absoluteStartTime;
    public boolean available;
    private int broadcastEndTime;
    private int broadcastStartTime;
    private int categoryId;
    public int channelId;
    private String channelName;
    public int channelRatingStar;
    public int channelType;
    public String createTime;
    public int dayOfWeek;
    //public Download downloadInfo;
    public double duration;
    public String endTime;
    public int groupId;
    public int id;
    public boolean isDownloadProgram;
    public List<String> lstAudioPath;
    public List<Integer> lstBitrate;
    //public List<BroadcasterNode> lstBroadcaster;
    private int mAvailableUrlIndex;
    private String mHighBitrateSource;
    public boolean mLiveInVirtual;
    private String mLowBitrateSource;
    private int mSetting;
    public String mShareSourceUrl;
    public transient Map<Integer, Integer> mapLinkInfo;
    public int resId;
    public int sequence;
    public String startTime;
    public String title;
    public int uniqueId;
    public String updateTime;

    public ProgramNode() {
        this.available = true;
        this.groupId = 0;
        this.channelRatingStar = -1;
        this.categoryId = -1;
        this.broadcastStartTime = -1;
        this.broadcastEndTime = -1;
        this._createTime = 0;
        this._updateTime = 0;
        this.isDownloadProgram = false;
        this.mSetting = -1;
        this.absoluteStartTime = -1;
        this.absoluteEndTime = -1;
        this.mLiveInVirtual = false;
        this.mAvailableUrlIndex = -1;
        this.nodeName = "program";
    }

    public String getChannelName() {
        if (this.channelName == null || this.channelName.equalsIgnoreCase("")) {
           /* ChannelNode node;
            if (this.mLiveInVirtual) {
                node = ChannelHelper.getInstance().getChannel(this.channelId, LIVE_PROGRAM);
                if (node != null) {
                    this.channelName = node.title;
                }
            } else {
                node = ChannelHelper.getInstance().getChannel(this.channelId, this.channelType);
                if (node != null) {
                    this.channelName = node.title;
                }
            }*/
        }
        return this.channelName;
    }

    public void setChannelName(String cName) {
        this.channelName = cName;
    }


    public void setAbsoluteStartTime(long time) {
        this.absoluteStartTime = time;
    }

    public void setAbsoluteEndTime(long time) {
        this.absoluteEndTime = time;
    }


    public void setCategoryId(int id) {
        if (id != -1) {
            this.categoryId = id;
        }
    }

    public int getCategoryId() {
        if (this.isDownloadProgram) {
            return 71;
        }
        if (this.categoryId != -1) {
            return this.categoryId;
        }
        ChannelNode node;
        if (this.mLiveInVirtual) {
            node = ChannelHelper.getInstance().getChannel(this.channelId, LIVE_PROGRAM);
            if (node == null) {
                return -1;
            }
            this.categoryId = node.categoryId;
            return node.categoryId;
        }
        node = ChannelHelper.getInstance().getChannel(this.channelId, this.channelType);
        if (node == null) {
            return -1;
        }
        this.categoryId = node.categoryId;
        return node.categoryId;
    }

}
