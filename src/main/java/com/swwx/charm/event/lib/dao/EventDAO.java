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

    @Query("select e from Event e where e.eventNo = :eventNo and e.generateTimeIndex=:generateTimeIndex")
    Event findByEventNo(@Param("eventNo") String eventNo,@Param("generateTimeIndex") Integer generateTimeIndex);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Event e where e.id = :id and e.generateTimeIndex=:generateTimeIndex")
    Event lockEvent(@Param("id") Long id,@Param("generateTimeIndex")Integer generateTimeIndex);

    @Query("select e from Event e where e.generateTimeIndex in (:timeIndexList) and e.eventStatus = 'WAIT_ACK' and e.eventName = :eventName and e.nextSendTime < now()")
    List<Event> findNeedSendEvent(@Param("eventName") String eventName,@Param("timeIndexList")List<Integer>timeIndexList);

    @Query(nativeQuery=true,value = "select * from t_event_new e where e.generate_time_index in (:timeIndexList) and e.event_status = 'WAIT_ACK' and e.event_name = :eventName and e.next_send_time < now() limit :pageSize")
    List<Event> findNeedSendEventByPageSize(@Param("eventName") String eventName,@Param("pageSize")int pageSize,@Param("timeIndexList")List<Integer>timeIndexList);

    @Modifying
    @Query("update Event e set e.nextSendTime =:nextSendTime, e.sendCnt = e.sendCnt + 1, e.lastSendTime = :lastSendTime where e.id = :id and e.generateTimeIndex=:generateTimeIndex and e.eventStatus='WAIT_ACK'")
    void updateEventByWaitAck(@Param("id") Long id, @Param("nextSendTime") Date nextSendTime, @Param("lastSendTime") Date lastSendTime,@Param("generateTimeIndex") Integer generateTimeIndex);
}
