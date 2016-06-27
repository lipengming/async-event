/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event;

import com.cubbery.event.channel.ChannelData;
import com.cubbery.event.worker.ConsumeWorker;
import com.cubbery.event.utils.ThreadFactories;
import com.cubbery.event.utils.Validation;

import java.util.concurrent.TimeUnit;

/**
 * 事件派发者，程序逻辑处理器，使用守护线程。
 */
final class Dispatcher {
    private final EventBus eventBus;
    private volatile boolean running;

    protected Dispatcher(EventBus eventBus) {
        this.eventBus = eventBus;
        this.running = false;
    }

    public synchronized Dispatcher start() {
        if(!this.running) {
            this.running = true;
            //使用守护线程是由于:(对于持久化消息，有重试进程定时mark事件成重试状态)
            new ThreadFactories(true,"Dispatcher").newThread(new DispatcherTask()).start();
        }
        return this;
    }

    public synchronized void stop() {
        this.running = false;
    }

    class DispatcherTask implements Runnable {
        private final ThreadLocal<Integer> counter = new ThreadLocal<Integer>(){
            @Override
            protected Integer initialValue() {
                return 0;//Integer cache 127 ~ -127
            }
        };

        @Override
        public void run() {
            Channel channel = eventBus.getChannel();
            while (running) {
                //避免线程无法shutDown,所以给定一个超时时间，而不是一直阻塞
                ChannelData event = selfRegulation(channel,channel.poll(10, TimeUnit.MILLISECONDS));
                if(event != null) {
                    eventBus.getConsumeExecutor().submit(new ConsumeWorker(event.getHandler(), event));
                }
            }
        }

        private ChannelData selfRegulation(Channel channel,ChannelData event) {
            //常驻线程，避免线程无法shutDown,阻塞进程关闭。所以给定一个超时时间，而不是一直阻塞
            try {
                Validation.checkNotNull(event, "Event cannot be null.");
                Validation.checkNotNull(event.getData(), "Event cannot be null.");
                return event;
            } catch (Exception e) {
                if(counter.get() > 2) {
                    //当2个周期没有拿到数据，那么wait 1min
                    counter.set(0);
                    return channel.poll(60,TimeUnit.SECONDS);
                } else {
                    ChannelData eventData = channel.poll(10, TimeUnit.MILLISECONDS);
                    counter.set(counter.get() + 1);
                    return selfRegulation(channel,eventData);
                }
            }
        }
    }
}
