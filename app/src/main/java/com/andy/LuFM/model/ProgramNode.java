package com.andy.LuFM.model;

import com.andy.LuFM.Utils.TimeKit;
import com.andy.LuFM.helper.ChannelHelper;
import com.andy.LuFM.test.MediaCenter;

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

    public int getCurrPlayStatus() {
        long time = System.currentTimeMillis() / 1000;
        long startTime = getAbsoluteStartTime();
        long endTime = getAbsoluteEndTime();
        if (this.channelType != 0) {
            return PAST_PROGRAM;
        }
        if (startTime <= time && endTime > time) {
            return LIVE_PROGRAM;
        }
        if (startTime > time) {
            return RESERVE_PROGRAM;
        }
        return endTime < time ? PAST_PROGRAM : PAST_PROGRAM;
    }

    public long getAbsoluteStartTime() {
        if (this.absoluteStartTime >= 0) {
            return this.absoluteStartTime;
        }
        int time = startTime();
        if (time == -1) {
            this.startTime = "00:00";
            this.broadcastStartTime = 0;
            this.absoluteStartTime = 0;
        } else {
            this.absoluteStartTime = getAbsoluteBroadcastTime((long) time);
        }
        return this.absoluteStartTime;
    }

    public long getAbsoluteEndTime() {
        if (this.absoluteEndTime >= 0) {
            return this.absoluteEndTime;
        }
        if (endTime() == -1) {
            this.broadcastEndTime = getDuration();
            this.absoluteEndTime = (long) this.broadcastEndTime;
            if (this.channelType == LIVE_PROGRAM) {
                return (long) this.broadcastEndTime;
            }
        }
        return getAbsoluteBroadcastTime((long) this.broadcastEndTime);
    }

    private long getAbsoluteBroadcastTime(long offsetTime) {
        return absoluteBaseTime() + offsetTime;
    }

    private long absoluteBaseTime() {
        long time = ((System.currentTimeMillis() / 1000) / 60) * 60;
        int currDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        time -= (long) TimeKit.absoluteTimeToRelative(time);
        if (currDayOfWeek != this.dayOfWeek) {
            if (currDayOfWeek == 7) {
                if (this.dayOfWeek == LIVE_PROGRAM) {
                    return time + 86400;
                }
            } else if (currDayOfWeek == LIVE_PROGRAM && this.dayOfWeek == 7) {
                return time - 86400;
            }
            if (currDayOfWeek < this.dayOfWeek) {
                time += (long) (((this.dayOfWeek - currDayOfWeek) * 24) * 3600);
            } else {
                time -= (long) (((currDayOfWeek - this.dayOfWeek) * 24) * 3600);
            }
        }
        return time;
    }

    public int getDuration() {
        if (this.duration > 0.0d) {
            return (int) this.duration;
        }
        this.duration = (double) (endTime() - startTime());
        return (int) this.duration;
    }

    public int endTime() {
        if (this.broadcastEndTime == -1 && this.endTime != null) {
            try {
                String[] times = Pattern.compile("\\D+").split(this.endTime);
                if (times.length >= RESERVE_PROGRAM) {
                    this.broadcastEndTime = (Integer.parseInt(times[0]) * 3600) + (Integer.parseInt(times[LIVE_PROGRAM]) * 60);
                }
            } catch (Exception e) {
            }
        }
        if (this.broadcastEndTime < startTime() && this.endTime != null) {
            this.broadcastEndTime += 86400;
        }
        return this.broadcastEndTime;
    }

    public int startTime() {
        if (-1 == this.broadcastStartTime && this.startTime != null) {
            try {
                String[] times = Pattern.compile("\\D+").split(this.startTime);
                if (times.length >= RESERVE_PROGRAM) {
                    this.broadcastStartTime = (Integer.parseInt(times[0]) * 3600) + (Integer.parseInt(times[LIVE_PROGRAM]) * 60);
                }
            } catch (Exception e) {
            }
        }
        return this.broadcastStartTime;
    }


    public String getLowBitrateSource() {
        if (this.mLowBitrateSource != null && !this.mLowBitrateSource.equalsIgnoreCase("")) {
            return this.mLowBitrateSource;
        }
        int bit = 24;
        if (this.lstBitrate != null && this.lstBitrate.size() > 0) {
            bit = ((Integer) this.lstBitrate.get(0)).intValue();
        }
        if (this.channelType == 0 || this.mLiveInVirtual) {
            if (getCurrPlayStatus() == LIVE_PROGRAM) {
                this.mLowBitrateSource = MediaCenter.getInstance().getPlayUrls(MediaCenter.LIVE_CHANNEL_PLAY, String.valueOf(this.resId), bit, this.channelId);
            } else {
                this.mLowBitrateSource = MediaCenter.getInstance().getReplayUrls(String.valueOf(this.resId), bit, buildTimeParam(getAbsoluteStartTime()), buildTimeParam(getAbsoluteEndTime()));
            }
        } else if (this.channelType == LIVE_PROGRAM) {
            String filePath = "";
            if (this.lstAudioPath != null && this.lstAudioPath.size() > 0) {
                filePath = (String) this.lstAudioPath.get(0);
            }
            this.mLowBitrateSource = MediaCenter.getInstance().getPlayUrls(MediaCenter.VIRUTAL_CHANNEL, filePath, 24, this.channelId);
        }
        return this.mLowBitrateSource;
    }

    private String buildTimeParam(long time) {
        String year = TimeKit.getYear(time);
        String month = TimeKit.getMonth(time);
        return new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf("" + year)).append("M").toString())).append(month).toString())).append("D").toString())).append(TimeKit.getDayofMonth(time)).toString())).append("h").toString())).append(TimeKit.getHour(time)).toString())).append("m").toString())).append(TimeKit.getMinute(time)).toString())).append("s").toString())).append("0").toString();
    }

}
