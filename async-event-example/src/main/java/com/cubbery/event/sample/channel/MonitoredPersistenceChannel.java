/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.sample.channel;

import com.cubbery.event.EventStorage;
import com.cubbery.event.channel.ChannelData;
import com.cubbery.event.channel.PersistentChannel;
import com.cubbery.event.handler.EventHandler;
import com.cubbery.event.monitor.ChannelStatistics;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * <b>创建人</b>：   <a href="mailto:cubber.zh@gmail.com">百墨</a> <br>
 * <b>该类在基本管道的基础上，实现了复合的管道类型。</b>
 * @version 1.0.0   <br>
 */
public class MonitoredPersistenceChannel extends PersistentChannel {
    private final ChannelStatistics channelStatistics;

    public MonitoredPersistenceChannel(int capacity, EventStorage storage, ChannelStatistics channelStatistics) {
        super(capacity, storage);
        this.channelStatistics = channelStatistics;
        this.channelStatistics.setChannelCapacity(capacity);
        this.channelStatistics.setChannelSize(capacity);
    }


    @Override
    public boolean offer(Object event,Set<EventHandler> handlers) {
        boolean isOk = false;
        try {
            this.channelStatistics.incrementEventPutAttemptCount(handlers.size());
            isOk = super.offer(event,handlers);
        } finally {
            if(isOk) {
                this.channelStatistics.addToEventPutSuccessCount(handlers.size());
            }
            this.channelStatistics.setChannelSize(getSize());
        }
        return isOk;
    }

    protected boolean offer(Set<ChannelData> data) {
        boolean isOk = false;
        try {
            this.channelStatistics.incrementEventPutAttemptCount(data.size());
            isOk = super.offer(data);
        } finally {
            if(isOk) {
                this.channelStatistics.addToEventPutSuccessCount(data.size());
            }
            this.channelStatistics.setChannelSize(getSize());
        }
        return isOk;
    }

    @Override
    public ChannelData poll(long timeout, TimeUnit unit) {
        this.channelStatistics.incrementEventTakeAttemptCount();
        ChannelData obj = null;
        try {
            obj = super.poll(timeout, unit);
            return obj;
        } finally {
            if(obj != null) {
                this.channelStatistics.addToEventTakeSuccessCount(1);
            }
            this.channelStatistics.setChannelSize(getSize());
        }
    }
}
