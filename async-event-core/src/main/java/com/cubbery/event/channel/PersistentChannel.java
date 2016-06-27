/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.channel;

import com.cubbery.event.EventStorage;
import com.cubbery.event.event.SimpleEvent;
import com.cubbery.event.handler.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PersistentChannel extends MemoryChannel {
    private final Logger LOGGER = LoggerFactory.getLogger("Persistent-Channel");

    private EventStorage storage;

    public PersistentChannel(int capacity, final EventStorage storage) {
        super(capacity);
        this.storage = storage;
    }

    @Override
    public boolean offer(Object event,Set<EventHandler> handlers) {
        try {
            List<SimpleEvent> eventList = new ArrayList<SimpleEvent>(handlers.size());
            for(EventHandler handler : handlers) {
                SimpleEvent simpleEvent = getCreator().create(event, handler);
                eventList.add(simpleEvent);
            }
            storage.insertEvent(eventList);

            Set<ChannelData> dataList = new HashSet<ChannelData>(eventList.size());
            for (SimpleEvent e : eventList) {
                dataList.add(new ChannelData(event,e.getId(),e.getEventHandler()));
            }
            super.offer(dataList);
            return true;
        } catch (Exception e) {
            //1、数据库记录成功，但是队列入失败。重试服务处理此类问题。
            //2、数据库和队列对入失败,没有影响。
            LOGGER.warn("Offer operator wrong! event = {} " + event,e);
        }
        return false;
    }

    @Override
    public ChannelData poll(long timeout, TimeUnit unit) {
        //对于这种不需要持久化了，直接使用mem实现
        return super.poll(timeout, unit);
    }

    @Override
    public boolean checkInit() {
        return super.checkInit() && storage != null ;
    }

    @Override
    public EventStorage getStorage() {
        return this.storage;
    }


    @Override
    public void setStorage(EventStorage storage) {
        this.storage = storage;
    }
}
