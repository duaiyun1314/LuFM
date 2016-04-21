package com.andy.LuFM.event;

/**
 * 切换fragment视图事件
 */
public class SwitchContentEvent {
    public static final String SWITCH_TYPE_CHANNEL_DETAIL = "channeldetail";//频道program列表
    public static final String SWITCH_TYPE_SPECIAL_TOPIC = "specialtopic";//点播频道列表
    public static final String SWITCH_TYPE_CATEGORY_DETAIL = "category_detail";//类别列表
    public static final String SWITCH_TYPE_CHANNEL_LIST = "channellist";//直播频道列表
    public static final String SWITCH_TYPE_REGION_LIST = "regionlist";//省市列表
    public String type;
    public Object params;
}
