/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.sample;

import com.cubbery.event.EventBus;
import com.cubbery.event.channel.MonitoredChannel;
import com.cubbery.event.monitor.ChannelStatistics;
import com.cubbery.event.sample.event.EventA;
import com.cubbery.event.sample.event.ListenerSub;
import com.cubbery.event.utils.Threads;

import java.util.concurrent.atomic.AtomicBoolean;

public class SampleWithMonitoredChannel {
    public static void main(String[] args) {
        final ChannelStatistics cs = new ChannelStatistics("channel_monitor");
        final EventBus eventBus = new EventBus(new MonitoredChannel(1024,cs));
        eventBus.register(new ListenerSub());
        eventBus.start();

        final AtomicBoolean isEnd = new AtomicBoolean(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isEnd.get()) {
                    eventBus.publish(new EventA("lee",10));
                }
            }
        }).start();

        Threads.sleep(1000);
        isEnd.set(true);
        eventBus.stop();
        System.out.println("===End====" + cs);

        //===End====CHANNEL:channel_monitor{channel.event.put.success=239598, channel.current.size=0, channel.capacity=1024, channel.event.take.attempt=239625, channel.event.take.success=239625, channel.event.put.attempt=541960}
        //对于并发较高时，管道太小会导致发布事件大量失败！
    }
}
