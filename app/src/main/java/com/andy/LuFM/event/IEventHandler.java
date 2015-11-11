package com.andy.LuFM.event;

/**
 * Created by Andy.Wang on 2015/11/11.
 */
public interface IEventHandler {
    void OnEvent(Object target, EventType type, Object params);
}
