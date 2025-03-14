package com.swwx.charm.event.lib.entity;

import java.util.Date;

import com.swwx.charm.commons.lang.base.BasicTO;
import com.swwx.charm.event.lib.type.EventType;

public class EventMessage extends BasicTO {

    private static final long serialVersionUID = 1L;

    /**
     * 事件NO
     */
    private String eventNo;

    /**
     * ACK NO
     */
    private String ackNo;

    /**
     * 事件名称
     */
    private String eventName;

    /**
     * 事件类型
     */
    private EventType eventType;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 来源
     */
    private String source;

    /**
     * 消息体
     */
    private Object data;

    public String getAckNo() {
        return ackNo;
    }

    public void setAckNo(String ackNo) {
        this.ackNo = ackNo;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getEventNo() {
        return eventNo;
    }

    public void setEventNo(String eventNo) {
        this.eventNo = eventNo;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
