/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class Threads {
    private final static Logger LOG = LoggerFactory.getLogger(Threads.class);
    /**
     * sleep等待,单位为毫秒,忽略InterruptedException
     *
     * @param millis
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.interrupted();
            return;
        }
    }

    /**
     * sleep等待,忽略InterruptedException
     *
     * @param duration
     * @param unit
     */
    public static void sleep(long duration, TimeUnit unit) {
        try {
            Thread.sleep(unit.toMillis(duration));
        } catch (InterruptedException e) {
            Thread.interrupted();
            return;
        }
    }

    /**
     * 两阶段关闭:
     * 第一阶段调用 shutdown 拒绝传入任务。
     * 第二阶段调用 shutdownNow（如有必要）取消所有遗留的任务。
     *
     * @param pool
     */
    public static void shutDownAndAwaitTerminal(ExecutorService pool) {
        if(pool == null) {
            return;
        }
        //Disable new tasks from being submitted
        pool.shutdown();
        try {
            //Wait a while（10s） for existing tasks to terminate
            if(!pool.awaitTermination(10,TimeUnit.SECONDS)) {
                // Cancel currently executing tasks
                pool.shutdownNow();
                // Wait a while for tasks to respond to being cancelled
                if(!pool.awaitTermination(10,TimeUnit.SECONDS)) {
                    LOG.error("Pool did not terminate! ");
                }
            }
        } catch (InterruptedException e) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}
