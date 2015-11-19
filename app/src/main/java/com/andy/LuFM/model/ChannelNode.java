package com.andy.LuFM.model;

import android.text.TextUtils;

import com.andy.LuFM.Utils.TimeKit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wanglu on 15/11/19.
 */
public class ChannelNode extends Node {


    public static final int LIVE_CHANNEL = 0;
    public static final int VIRTUAL_CHANNEL = 1;
    public int audienceCnt;
    public boolean autoPlay;
    public int categoryId;
    public int channelId;
    public int channelType;
    public String desc;
    public float freq;
    public int groupId;
    private String largeThumb;
    public String latest_program;
    // public List<BroadcasterNode> lstAuthors;
    //  public List<BroadcasterNode> lstBroadcaster;
    //public transient List<UserInfo> lstPodcasters;
    private transient boolean mAutoPlay;
    public transient int mLoadedProgramId;
    public transient int mLoadedProgramSize;
    public transient List<Integer> mLoadingSize;
    public transient List<Integer> mLoadingStart;
    public String mSourceUrl;
    private transient long mUpdateTime;
    public long mViewTime;
    public transient Map<Integer, Integer> mapLinkInfo;
    private String mediumThumb;
    public int programCnt;
    public int ratingStar;
    public boolean recordEnable;
    public int resId;
    private String thumb;
    public String title;
    public String update_time;
    public transient long viewTime;

    public ChannelNode() {
        this.mViewTime = 0;
        this.ratingStar = -1;
        this.mUpdateTime = 0;
        this.viewTime = 0;
        this.mAutoPlay = false;
        this.mLoadedProgramSize = LIVE_CHANNEL;
        this.nodeName = "channel";
    }


    public ChannelNode clone() {
        ChannelNode node = new ChannelNode();
        node.channelId = this.channelId;
        node.categoryId = this.categoryId;
        node.title = this.title;
        node.desc = this.desc;
        node.groupId = this.groupId;
        node.thumb = this.thumb;
        node.mediumThumb = this.mediumThumb;
        node.largeThumb = this.largeThumb;
        node.autoPlay = this.autoPlay;
        node.recordEnable = this.recordEnable;
        node.channelType = this.channelType;
        node.resId = this.resId;
        node.audienceCnt = this.audienceCnt;
        node.mapLinkInfo = this.mapLinkInfo;
        node.mLoadingSize = this.mLoadingSize;
        node.mLoadingStart = this.mLoadingStart;
        node.mSourceUrl = this.mSourceUrl;
        node.freq = this.freq;
        node.latest_program = this.latest_program;
        node.update_time = this.update_time;
        node.mViewTime = this.mViewTime;
        node.programCnt = this.programCnt;
        node.ratingStar = this.ratingStar;
        return node;
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


    public void addLoadingStart(int start) {
        if (this.mLoadingStart == null) {
            this.mLoadingStart = new ArrayList();
        }
        this.mLoadingStart.add(Integer.valueOf(start));
    }

    public void addLoadingSize(int size) {
        if (this.mLoadingSize == null) {
            this.mLoadingSize = new ArrayList();
        }
        this.mLoadingSize.add(Integer.valueOf(size));
    }

    public boolean hasLoadingStart(int start) {
        if (this.mLoadingStart == null) {
            return false;
        }
        for (int i = LIVE_CHANNEL; i < this.mLoadingStart.size(); i += VIRTUAL_CHANNEL) {
            if (((Integer) this.mLoadingStart.get(i)).intValue() == start) {
                return true;
            }
        }
        return false;
    }

    public boolean hasLoadingSize(int size) {
        if (this.mLoadingSize == null) {
            return false;
        }
        for (int i = LIVE_CHANNEL; i < this.mLoadingSize.size(); i += VIRTUAL_CHANNEL) {
            if (((Integer) this.mLoadingSize.get(i)).intValue() == size) {
                return true;
            }
        }
        return false;
    }


    public boolean isLiveChannel() {
        if (this.channelType == 0) {
            return true;
        }
        return false;
    }


    public void setSmallThumb(String thumb) {
        this.thumb = thumb;
    }

    public void setMediumThumb(String thumb) {
        this.mediumThumb = thumb;
    }

    public void setLargeThumb(String thumb) {
        this.largeThumb = thumb;
    }

    public boolean noThumb() {
        return this.thumb == null && this.mediumThumb == null && this.largeThumb == null;
    }


    public String getApproximativeThumb(int targetWidth, int targetHeight, boolean scaled) {
        int size = Math.max(targetWidth, targetHeight);
        int medium = 600;
        int small = 300;
        if (size >= medium) {
            if (!isEmpty(this.largeThumb)) {
                return this.largeThumb;
            }
            if (isEmpty(this.mediumThumb)) {
                return this.thumb;
            }
            return this.mediumThumb;
        } else if (size <= small) {
            return this.thumb;
        } else {
            if (isEmpty(this.mediumThumb)) {
                return this.thumb;
            }
            return this.mediumThumb;
        }
    }

    private static boolean isEmpty(String text) {
        return TextUtils.isEmpty(text);
    }


}
