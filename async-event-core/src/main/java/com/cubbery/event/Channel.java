/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event;

import com.cubbery.event.channel.ChannelData;
import com.cubbery.event.handler.EventHandler;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 事件通道接口
 */
public interface Channel {

    /**
     * put in to
     *
     * @param event
     * @return
     */
    boolean offer(Object event,Set<EventHandler> handlers);

    /**
     * remove and get one from the q
     *
     * @param timeout
     * @param unit
     * @return
     */
    ChannelData poll(long timeout, TimeUnit unit);

    /**
     * is empty when queue.size() == 0
     *
     * @return
     */
    boolean isEmpty();

    /**
     * get storage if this is persistence
     *
     * @return
     */
    EventStorage getStorage();

    /**
     * put a storage if necessary
     *
     * @param storage
     */
    void setStorage(EventStorage storage);

    /**
     * check init is ok
     */
    boolean checkInit();

    /**
     * get a creator for event
     *
     * @return
     */
    EventCreator getCreator();

    /**
     * set a creator for event
     *
     * @param creator
     */
    void setCreator(EventCreator creator);
}
