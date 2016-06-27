/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.channel;

import com.cubbery.event.handler.EventHandler;

import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class MemoryChannel extends AbstractChannel {

    private LinkedBlockingQueue<ChannelData> queue;
    private Semaphore queueRemaining;

    public MemoryChannel(int capacity) {
        super();
        queueRemaining = new Semaphore(capacity);
        this.queue = new LinkedBlockingQueue<ChannelData>(capacity);
    }

    @Override
    public boolean offer(Object event,Set<EventHandler> handlers) {
        try {
            if(queueRemaining.tryAcquire(handlers.size(),expire,timeUnit)) {
                for(EventHandler handler : handlers) {
                    if(!queue.offer(new ChannelData(event,0,handler))) {
                        throw new RuntimeException("Queue add failed, this shouldn't be able to happen");
                    }
                }
                return true;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
    }

    protected boolean offer(Set<ChannelData> data) {
        try {
            if(queueRemaining.tryAcquire(data.size(),expire,timeUnit)) {
                for(ChannelData ds : data) {
                    if(!queue.offer(ds)) {
                        throw new RuntimeException("Queue add failed, this shouldn't be able to happen");
                    }
                }
                return true;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return false;
    }

    @Override
    public ChannelData poll(long timeout, TimeUnit unit) {
        try {
            ChannelData data = queue.poll(timeout, unit);
            if(null != data) {
                queueRemaining.release();
            }
            return data;
        } catch (InterruptedException e) {
            Thread.interrupted();
            return null;
        }
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public boolean checkInit() {
        return queue != null && queue.isEmpty();
    }

    protected int getSize() {
        return queue.size() ;
    }
}
