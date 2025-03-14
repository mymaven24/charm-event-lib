package com.swwx.charm.event.lib.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.swwx.charm.event.lib.entity.Event;
import com.swwx.charm.event.lib.type.EventStatus;

@Transactional(rollbackFor = Throwable.class)
public interface EventDAO extends CrudRepository<Event, Long> {

    @Query("select e from Event e where e.eventNo = :eventNo")
    Event findByEventNo(@Param("eventNo") String eventNo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Event e where e.id = :id")
    Event lockEvent(@Param("id") Long id);

    @Query("select e from Event e where e.eventStatus = 'WAIT_ACK' and e.eventName = :eventName and e.nextSendTime < now()")
    List<Event> findNeedSendEvent(@Param("eventName") String eventName);

    @Modifying
    @Query("update Event e set e.nextSendTime =:nextSendTime, e.sendCnt = e.sendCnt + 1, e.lastSendTime = :lastSendTime where e.id = :id")
    void updateEvent(@Param("id") Long id, @Param("nextSendTime") Date nextSendTime,
        @Param("lastSendTime") Date lastSendTime);

    @Modifying
    @Query("update Event e set e.ackContent = :ackContent, e.ackNo = :ackNo, e.eventStatus = :eventStatus where id = :id")
    void ack(@Param("id") Long id, @Param("ackNo") String ackNo, @Param("ackContent") String ackContent, @Param("eventStatus") EventStatus eventStatus);


    @Query(nativeQuery=true,value = "select * from t_event e where e.event_status = 'WAIT_ACK' and e.event_name = :eventName and e.next_send_time < now() limit :pageSize")
    List<Event> findNeedSendEventByPageSize(@Param("eventName") String eventName,@Param("pageSize")int pageSize);

    @Modifying
    @Query("update Event e set e.nextSendTime =:nextSendTime, e.sendCnt = e.sendCnt + 1, e.lastSendTime = :lastSendTime where e.id = :id and e.eventStatus='WAIT_ACK'")
    void updateEventByWaitAck(@Param("id") Long id, @Param("nextSendTime") Date nextSendTime, @Param("lastSendTime") Date lastSendTime);
}
