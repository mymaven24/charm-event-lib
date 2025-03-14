package com.swwx.charm.event.lib.service;

import com.alibaba.fastjson.JSON;
import com.swwx.charm.commons.lang.utils.CharmStringUtils;
import com.swwx.charm.commons.lang.utils.LogPortal;
import com.swwx.charm.event.lib.dao.EventDAO;
import com.swwx.charm.event.lib.entity.Event;
import com.swwx.charm.event.lib.entity.EventForm;
import com.swwx.charm.event.lib.entity.EventMessage;
import com.swwx.charm.event.lib.type.EventStatus;
import com.swwx.charm.event.lib.type.EventType;
import com.swwx.charm.event.lib.util.QueueNameUtils;
import com.swwx.charm.mq.support.spring.MQProvider;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author shixuelin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class EventServiceImpl implements EventService {

    @Autowired
    private EventDAO eventDAO;

    @Autowired
    private MQProvider mqProvider;

    public Event addEvent(EventForm form) {
        Event event = new Event();
        event.setEventNo(CharmStringUtils.generateUUID());
        event.setEventName(form.getEventName());
        event.setContent(form.getData() == null ? null : JSON.toJSONString(form.getData()));
        event.setSource(form.getSource());
        event.setEventStatus(EventStatus.WAIT_ACK);
        return eventDAO.save(event);
    }

    public Event addEventAndSendImmediately(EventForm form) {
        Event event = addEvent(form);
        sendEvent(event, 0);
        return event;
    }

    public List<Event> getNeedSendEvent(String eventName) {
        return eventDAO.findNeedSendEvent(eventName);
    }

    public void sendEvent(Event event, Integer expiredTimes) {
        event = eventDAO.lockEvent(event.getId());
        if (event.getEventStatus().equals(EventStatus.ACKED)) {
            return;
        }
        try {
            EventMessage message = genEventMessage(event);
            mqProvider.productMessage(QueueNameUtils.getQueueName(event.getEventName()), expiredTimes, JSON.toJSONString(message));
        } catch(Exception e) {
            LogPortal.error(e.getMessage(), e);
        }
        eventDAO.updateEventByWaitAck(event.getId(), getNextSendTime(event.getSendCnt()), new Date());
    }

    @Override
    public List<Event> getNeedSendEventByPageSize(String eventName, int pageSize) {
        return eventDAO.findNeedSendEventByPageSize(eventName,pageSize);
    }

    private EventMessage genEventMessage(Event event) {
        EventMessage message = new EventMessage();
        message.setData(StringUtils.isNotBlank(event.getContent()) ? JSON.parse(event.getContent()) : null);
        message.setEventName(event.getEventName());
        message.setEventNo(event.getEventNo());
        message.setEventType(EventType.SEND);
        message.setSource(event.getSource());
        message.setSendTime(new Date());
        return message;
    }

    private static Date getNextSendTime(Integer sendCnt) {
        double cnt = 1;
        for (int i = 1; i < sendCnt; i++) {
            cnt = cnt * 1.5;
            if (cnt > 60 * 60 * 24 * 3) {
                break;
            }
        }
        return DateUtils.addSeconds(new Date(), Long.valueOf(Math.round(cnt)).intValue());
    }
}
