package com.swwx.charm.event.lib.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import com.swwx.charm.commons.lang.utils.LogPortal;
import com.swwx.charm.event.lib.entity.Event;
import com.swwx.charm.event.lib.service.EventService;
import com.swwx.charm.zookeeper.exception.GetLockFailedException;
import com.swwx.charm.zookeeper.exception.ReleaseLockFailedException;
import com.swwx.charm.zookeeper.lock.DistributedLock;

@Transactional(rollbackFor = Throwable.class)
public abstract class AbstractEventTask {

    @Autowired
    private EventService eventService;

    protected abstract Integer getExpiredTime();

    protected abstract String getEventName();

    protected abstract DistributedLock getDistributedLock();

    @Scheduled(cron = "30 */1 * * * ?")
    public void execute() {
        boolean isLock = false;
        String locakName = this.getClass().getName();
        try {
            isLock = getDistributedLock().getLock(locakName);
            if (isLock) {
                doExecute();
            }

        } catch(GetLockFailedException e) {
            LogPortal.error(e.getMessage(), e);
        } finally {
            try {
                if (isLock) {
                    getDistributedLock().releaseLock(locakName);
                }
            } catch(ReleaseLockFailedException e) {
                LogPortal.error(e.getMessage(), e);
            }
        }
    }

    private void doExecute() {
        List<Event> list = eventService.getNeedSendEventByPageSize(getEventName(),500);
        for (Event event: list) {
            eventService.sendEvent(event, getExpiredTime());
        }
    }

}
