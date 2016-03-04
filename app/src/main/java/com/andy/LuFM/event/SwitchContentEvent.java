package com.andy.LuFM.event;

/**
 * 切换fragment视图事件
 */
public class SwitchContentEvent {
    public static final String SWITCH_TYPE_CHANNEL_DETAIL = "channeldetail";
    public static final String SWITCH_TYPE_SPECIAL_TOPIC = "specialtopic";
    public static final String SWITCH_TYPE_CATEGORY_DETAIL = "category_detail";
    public String type;
    public Object params;
}
