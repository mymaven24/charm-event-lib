package com.swwx.charm.event.lib.consumer;

import com.alibaba.fastjson.JSON;
import com.swwx.charm.commons.lang.utils.CharmStringUtils;
import com.swwx.charm.commons.lang.utils.LogPortal;
import com.swwx.charm.event.lib.consumer.exception.IllegalEventNameException;
import com.swwx.charm.event.lib.consumer.exception.IllegalEventTypeException;
import com.swwx.charm.event.lib.dao.EventDAO;
import com.swwx.charm.event.lib.entity.Event;
import com.swwx.charm.event.lib.entity.EventMessage;
import com.swwx.charm.event.lib.type.EventStatus;
import com.swwx.charm.event.lib.type.EventType;
import com.swwx.charm.event.lib.util.DateUtil;
import com.swwx.charm.event.lib.util.QueueNameUtils;
import com.swwx.charm.mq.support.spring.AbstractMQConsumer;
import com.swwx.charm.mq.support.spring.MQProvider;
import com.swwx.charm.zookeeper.exception.GetLockFailedException;
import com.swwx.charm.zookeeper.exception.ReleaseLockFailedException;
import com.swwx.charm.zookeeper.lock.DistributedLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * MQ消息消费者
 * @author shixuelin
 * @param <T> 接收消息对象
 */
@Service
public abstract class MQConsumer<T> extends AbstractMQConsumer {

    @Autowired
    private EventDAO eventDAO;

    @Autowired
    private MQProvider mqProvider;

    public String getReceiveQueueName() {
        return QueueNameUtils.getQueueName(getEventName());
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
    protected abstract Object execute(T message) throws Exception;

    /**
     * 获取ack来源
     * @return
     */
    protected abstract String getAckSource();

    /**
     * 获取锁
     * @return
     */
    protected abstract DistributedLock getDistributedLock();

    /**
     * 获取ack事件失效事件
     * @return
     */
    public abstract int getAckEventExpiredTime();

    public void consumerOneMessage(String data) throws Exception {
        EventMessage message = JSON.parseObject(data, EventMessage.class);
        switch (message.getEventType()) {
            case SEND:
                if (!message.getEventName().equals(getEventName())) {
                    String errorMsg =
                        String.format("expect eventName: %s actutal eventName: %s data: %s", getEventName(),
                            message.getEventName(), data);
                    throw new IllegalEventNameException(errorMsg);
                }
                executeSendMessage(message);
                break;
            default:
                throw new IllegalEventTypeException(data);
        }
    }

    private void executeSendMessage(EventMessage message) throws Exception {
        boolean isLock = false;
        String lockName = this.getClass().getSimpleName() + "-" + message.getEventNo();
        try {
            isLock = getDistributedLock().getLock(lockName);
            if (!isLock) {
                return;
            }
            Integer generatorTimeIndex =Integer.parseInt(message.getEventNo().substring(0,8));
            Event event = eventDAO.findByEventNo(message.getEventNo(),generatorTimeIndex);
            if (event != null && event.getEventStatus().equals(EventStatus.ACKED)) {
                ack(event.getAckContent(), message.getEventNo(), event.getAckNo());
                return;
            }
            Object ack = execute(convert(message.getData()));
            Date currentDate = new Date();
            Integer currentGeneratorTimeIndex = DateUtil.dateToInteger(currentDate);
            String ackNo =currentGeneratorTimeIndex+CharmStringUtils.generateUUID();
            event = new Event();
            event.setAckNo(ackNo);
            event.setContent(convertString(message.getData()));
            event.setEventNo(message.getEventNo());
            event.setEventStatus(EventStatus.ACKED);
            event.setSource(message.getSource());
            event.setEventName(message.getEventName());
            event.setLastSendTime(message.getSendTime());
            event.setAckContent(convertString(ack));
            event.setReceiveTime(currentDate);
            event.setAckSource(getAckSource());
            event.setGenerateTimeIndex(currentGeneratorTimeIndex);
            eventDAO.save(event);
            ack(ack, message.getEventNo(), ackNo);
        } catch(GetLockFailedException e) {
            LogPortal.error(e.getMessage(), e);
        } finally {
            try {
                if (isLock) {
                    getDistributedLock().releaseLock(lockName);
                }
            } catch(ReleaseLockFailedException e) {
                LogPortal.error(e.getMessage(), e);
            }
        }
    }

    private void ack(Object data, String eventNo, String ackNo) throws Exception {
        EventMessage ackMessage = new EventMessage();
        ackMessage.setData(data);
        ackMessage.setSendTime(new Date());
        ackMessage.setEventNo(eventNo);
        ackMessage.setSource(this.getAckSource());
        ackMessage.setEventType(EventType.ACK);
        ackMessage.setAckNo(ackNo);
        ackMessage.setEventName(getEventName());
        String ackData = JSON.toJSONString(ackMessage);
        LogPortal.info("ackData:{}",ackData);
        mqProvider.productMessage(QueueNameUtils.getAckQueueName(getEventName()), getAckEventExpiredTime(),ackData);
    }

    private T convert(Object data) throws Exception {
        return JSON.parseObject(JSON.toJSONString(data), getEventMessageClass());
    }

    private String convertString(Object data) throws Exception {
        if (data == null) {
            return "";
        }
        return JSON.toJSONString(data);
    }
}
