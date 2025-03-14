package com.swwx.charm.event.lib.consumer;

import com.alibaba.fastjson.JSON;
import com.swwx.charm.event.lib.consumer.exception.IllegalEventTypeException;
import com.swwx.charm.event.lib.dao.EventDAO;
import com.swwx.charm.event.lib.entity.Event;
import com.swwx.charm.event.lib.entity.EventMessage;
import com.swwx.charm.event.lib.type.EventStatus;
import com.swwx.charm.event.lib.util.QueueNameUtils;
import com.swwx.charm.mq.support.spring.AbstractMQConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Transactional(rollbackFor = Throwable.class)
public abstract class MQAckConsumer<T> extends AbstractMQConsumer {

    @Autowired
    private EventDAO eventDAO;

    public void consumerOneMessage(String data) throws Exception {
        EventMessage message = JSON.parseObject(data, EventMessage.class);
        switch (message.getEventType()) {
            case ACK:
                executeAckMessage(message);
                break;
            default:
                throw new IllegalEventTypeException(data);
        }
    }

    public String getReceiveQueueName() {
        return QueueNameUtils.getAckQueueName(getEventName());
    }

    public abstract String getEventName();

    /**
     * 获取接收事件消息Class
     * @return
     */
    protected abstract Class<T> getEventMessageClass();

    /**
     * 具体业务方法
     * @param message
     * @return
     */
    protected abstract void execute(T message) throws Exception;

    private void executeAckMessage(EventMessage message) throws Exception {
        Event event = eventDAO.findByEventNo(message.getEventNo());
        if (event == null) {
            return;
        }

        if (event.getEventStatus().equals(EventStatus.ACKED)) {
            return;
        }

        execute(convert(message.getData()));

        event.setAckNo(message.getAckNo());
        event.setAckSource(message.getSource());
        event.setAckContent(convertString(message.getData()));
        event.setReceiveTime(new Date());
        event.setEventStatus(EventStatus.ACKED);
        eventDAO.save(event);

    }

    private T convert(Object data) throws Exception {
        if (data == null) {
            return null;
        }

        if (data instanceof String){
            return JSON.parseObject(data.toString(), getEventMessageClass());
        } else {
            return JSON.parseObject(JSON.toJSONString(data), getEventMessageClass());
        }

    }

    private String convertString(Object data) throws Exception {
        if (data == null) {
            return null;
        }
        return JSON.toJSONString(data);
    }
}
