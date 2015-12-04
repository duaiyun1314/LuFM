package com.andy.LuFM.model;

import java.util.regex.Pattern;

/**
 * Created by Andy.Wang on 2015/12/4.
 */
public class RecommendPlayingItemNode extends Node {
    public int channelId;
    public String channelName;
    public int channelType;
    public String endTime;
    public String programName;
    public int programid;
    public int ratingStar;
    private int relativeEndTime;
    private int relativeStartTime;
    public int resId;
    public String startTime;
    public String thumb;

    public RecommendPlayingItemNode() {
        this.ratingStar = -1;
        this.relativeStartTime = -1;
        this.relativeEndTime = -1;
        this.nodeName = "recommendplayingitem";
    }

    public int startTime() {
        if (-1 == this.relativeStartTime) {
            try {
                String[] times = Pattern.compile("\\D+").split(this.startTime);
                if (times.length >= 2) {
                    this.relativeStartTime = (Integer.parseInt(times[0]) * 3600) + (Integer.parseInt(times[1]) * 60);
                }
            } catch (Exception e) {
            }
        }
        return this.relativeStartTime;
    }

    public int endTime() {
        if (-1 == this.relativeEndTime) {
            try {
                String[] times = Pattern.compile("\\D+").split(this.endTime);
                if (times.length >= 2) {
                    this.relativeEndTime = (Integer.parseInt(times[0]) * 3600) + (Integer.parseInt(times[1]) * 60);
                }
            } catch (Exception e) {
            }
            if (this.relativeEndTime < startTime()) {
                this.relativeEndTime += 86400;
            }
        }
        return this.relativeEndTime;
    }

    public String getUpdateTime() {
        String startTime;
        String endTime;
        if (this.startTime == null) {
            startTime = "00:00";
        } else {
            startTime = trimTime(this.startTime);
        }
        if (this.endTime == null) {
            endTime = "00:00";
        } else {
            endTime = trimTime(this.endTime);
        }
        return "\u76f4\u64ad\u65f6\u95f4:" + startTime + "-" + endTime;
    }

    private String trimTime(String time) {
        return time.substring(0, time.lastIndexOf(":"));
    }

}
