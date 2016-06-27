/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.worker;

import com.cubbery.event.EventBus;
import com.cubbery.event.channel.ChannelData;
import com.cubbery.event.event.SimpleEvent;
import com.cubbery.event.handler.EventHandler;

/**
 * 此时由于从数据库中加载的event都泛化成了simpleEvent，所以需要先做一次还原。
 */
public class RetryWorker extends AbstractWorker implements Runnable {
    private final EventBus eventBus;
    private final EventHandler handler;
    private final SimpleEvent simpleEvent;//从queue中取出的event

    public RetryWorker(EventBus eventBus, EventHandler handler, SimpleEvent event) {
        this.eventBus = eventBus;
        this.handler = handler;
        this.simpleEvent = event;
    }


    @Override
    public void run() {
        Class<?> clazz = eventBus.getEventClassByType(simpleEvent.getType());
        Object obj = eventBus.getChannel().getCreator().reCreate(simpleEvent,clazz);
        if(obj != null) {
            handler.handleEvent(new ChannelData(obj,simpleEvent.getId(),handler));
        } else {
            markAsDead(this.handler,this.simpleEvent.getId());
        }
    }
}
