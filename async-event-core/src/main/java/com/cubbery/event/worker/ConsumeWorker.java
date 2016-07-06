/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.worker;

import com.cubbery.event.channel.ChannelData;
import com.cubbery.event.handler.EventHandler;

/**
 * 从queue中取出的event可以使用该worker来消费，否则不能
 */
public class ConsumeWorker extends AbstractWorker implements Runnable {
    private EventHandler handler;
    private ChannelData event;//从queue中取出的event

    public ConsumeWorker(EventHandler handler, ChannelData event) {
        this.handler = handler;
        this.event = event;
    }

    @Override
    public void run() {
        try {
            handler.handleEvent(event);
        } catch (Throwable throwable) {
            //对于非持久化的消息，异常会从handler穿透到这里，所以catch处理之
            LOG.error("Error Consume Event {} ",event.getData());
        }
    }
}
