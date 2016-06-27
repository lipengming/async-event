/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadFactories implements ThreadFactory {
    private final AtomicInteger threadId ;
    private final StringBuilder prefix ;
    private final boolean isDaemon;

    public ThreadFactories(String name) {
        this(false,name);
    }

    public ThreadFactories(boolean isDaemon, String name) {
        this.prefix = new StringBuilder("Async_Event_").append(name).append("_");
        this.threadId = new AtomicInteger(0);
        this.isDaemon = isDaemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        //mark as user thread ,the jvm exit on all of the thread are demon!
        thread.setDaemon(isDaemon);
        thread.setName(prefix.toString() + threadId.incrementAndGet());
        return thread;
    }
}
