/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.sample;

import com.cubbery.event.sample.event.BothSub;
import com.cubbery.event.sample.event.EventA;
import com.cubbery.event.sample.event.EventAny;
import com.cubbery.event.sample.event.ListenerSub;
import com.cubbery.event.EventBus;
import com.cubbery.event.channel.MemoryChannel;
import com.cubbery.event.event.SimpleEvent;

public class SampleWithListenerSub {
    public static void main(String[] args) {
        //实例化事件总线，使用内存队列
        final EventBus eventBus = new EventBus(new MemoryChannel(1024));
        //注册消费者
        eventBus.register(new ListenerSub());
        eventBus.register(new BothSub());
        //启动事件总线
        eventBus.start();

        //发送事件消息（需要启动后才能发送）
        try {
            eventBus.publish(new SimpleEvent());
        } catch (Exception e) {

        }
        eventBus.publish(new EventAny());
        eventBus.publish(new EventA());
        //停止事件总线
        eventBus.stop();
    }
}
