package com.swwx.charm.event.lib.entity;

import com.swwx.charm.commons.lang.base.BasicTO;

public class EventForm extends BasicTO {

    private static final long serialVersionUID = 1L;

    /**
     * 来源
     */
    private String source;

    /**
     * 事件内容
     */
    private Object data;

    /**
     * 事件名称
     */
    private String eventName;

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
