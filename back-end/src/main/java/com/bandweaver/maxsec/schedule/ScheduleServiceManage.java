package com.bandweaver.maxsec.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ScheduleServiceManage {
    private ConcurrentHashMap<String, ScheduledExecutorService> scheduleServiceMap = new ConcurrentHashMap<>();

    public void put(String key, ScheduledExecutorService scheduleService) {
        scheduleServiceMap.put(key, scheduleService);
        log.info("add scheduleService:" + key);
        log.info("map size:" + scheduleServiceMap.size());
    }

    public ScheduledExecutorService get(String key) {
        log.info("get scheduleService:" + key);
        return scheduleServiceMap.get(key);
    }

    public void remove(String key) {
        scheduleServiceMap.remove(key);
        log.info("remove scheduleService:" + key);
    }

    public boolean containsKey(String key) {
        return scheduleServiceMap.containsKey(key);
    }

    public void shutdown(String key) {
        log.info("shutdown scheduleService:" + key);
        ScheduledExecutorService scheduler = scheduleServiceMap.get(key);
        if (scheduler != null) {
            try {
                scheduler.shutdown();
                log.info("shutdown scheduleService:" + key);
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    // 超时的时候向线程池中所有的线程发出中断(interrupted)。
                    scheduler.shutdownNow();
                    log.info("shutdown awaitTermination scheduleService:" + key);
                }
            } catch (InterruptedException e) {
                // awaitTermination方法被中断的时候也中止线程池中全部的线程的执行。
                log.info("shutdown InterruptedException scheduleService:" + key);
                scheduler.shutdownNow();
            } finally {
                remove(key);
            }
        }
    }
}
