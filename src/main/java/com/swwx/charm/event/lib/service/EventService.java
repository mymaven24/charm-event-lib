package com.swwx.charm.event.lib.service;

import java.util.List;

import com.swwx.charm.event.lib.entity.Event;
import com.swwx.charm.event.lib.entity.EventForm;

/**
 * @author shixuelin
 */
public interface EventService {

    /**
     * 添加事件
     * @param form
     */
    Event addEvent(EventForm form);

    /**
     * 添加事件并立即发送
     * @param form
     * @return
     */
    Event addEventAndSendImmediately(EventForm form);

    List<Event> getNeedSendEvent(String eventName);

    void sendEvent(Event event, Integer expiredTimes);

    List<Event> getNeedSendEventByPageSize(String eventName,int pageSize);
}
