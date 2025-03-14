package com.swwx.charm.event.lib.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import com.swwx.charm.commons.lang.base.BasicTO;
import com.swwx.charm.event.lib.type.EventStatus;

@Entity
@DynamicUpdate(true)
public class Event extends BasicTO {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @GeneratedValue
    @Id
    private Long id;

    /**
     * 事件No
     */
    @Column(unique = true)
    private String eventNo;

    /**
     * 来源
     */
    private String source;

    /**
     * ack来源
     */
    private String ackSource;

    /**
     * 发送方事件内容
     */
    private String content;

    /**
     * ack方事件内容
     */
    private String ackContent;

    /**
     * 事件名称
     */
    private String eventName;

    /**
     *发送次数 
     */
    @Generated(GenerationTime.INSERT)
    @Column(insertable = false)
    private Integer sendCnt;

    /**
     * 事件状态
     */
    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;

    /**
     * ACKNO
     */
    @Column(unique = true)
    private String ackNo;

    /**
     * 接收时间
     */
    private Date receiveTime;

    /**
     * 发送时间
     */
    private Date lastSendTime;

    /**
     * 下次发送时间
     */
    @Generated(GenerationTime.INSERT)
    @Column(insertable = false)
    private Date nextSendTime;

    /**
     * 创建时间
     */
    @Generated(GenerationTime.ALWAYS)
    @Column(insertable = false, updatable = false)
    private Date createTime;

    /**
     * 修改时间
     */
    @Generated(GenerationTime.ALWAYS)
    @Column(insertable = false, updatable = false)
    private Date lastUpdateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventNo() {
        return eventNo;
    }

    public void setEventNo(String eventNo) {
        this.eventNo = eventNo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAckContent() {
        return ackContent;
    }

    public void setAckContent(String ackContent) {
        this.ackContent = ackContent;
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getAckNo() {
        return ackNo;
    }

    public void setAckNo(String ackNo) {
        this.ackNo = ackNo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Integer getSendCnt() {
        return sendCnt;
    }

    public void setSendCnt(Integer sendCnt) {
        this.sendCnt = sendCnt;
    }

    public EventStatus getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }

    public Date getNextSendTime() {
        return nextSendTime;
    }

    public void setNextSendTime(Date nextSendTime) {
        this.nextSendTime = nextSendTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getLastSendTime() {
        return lastSendTime;
    }

    public void setLastSendTime(Date lastSendTime) {
        this.lastSendTime = lastSendTime;
    }

    public String getAckSource() {
        return ackSource;
    }

    public void setAckSource(String ackSource) {
        this.ackSource = ackSource;
    }
}
