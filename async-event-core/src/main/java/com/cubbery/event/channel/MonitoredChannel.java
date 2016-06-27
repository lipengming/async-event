/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.channel;

import com.cubbery.event.handler.EventHandler;
import com.cubbery.event.monitor.ChannelStatistics;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MonitoredChannel extends MemoryChannel {

    private ChannelStatistics statistics;

    public MonitoredChannel(int capacity,ChannelStatistics statistics) {
        super(capacity);
        this.statistics = statistics;
        this.statistics.setChannelCapacity(capacity);
        this.statistics.setChannelSize(capacity);
    }

    @Override
    public boolean offer(Object event,Set<EventHandler> handlers) {
        boolean isOk = false;
        try {
            this.statistics.incrementEventPutAttemptCount(handlers.size());
            isOk = super.offer(event,handlers);
        } finally {
            if(isOk) {
                this.statistics.addToEventPutSuccessCount(handlers.size());
                this.statistics.setChannelSize(getSize());
            }
        }
        return isOk;
    }

    @Override
    public ChannelData poll(long timeout, TimeUnit unit) {
        boolean isOk = false;
        this.statistics.incrementEventTakeAttemptCount();
        try {
            ChannelData obj = super.poll(timeout, unit);
            isOk = (obj != null);
            return obj;
        } finally {
            if(isOk) {
                this.statistics.addToEventTakeSuccessCount(1);
            }
            this.statistics.setChannelSize(getSize());
        }
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public boolean checkInit() {
        return super.checkInit();
    }
}
