/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.sample.conf;

import com.cubbery.event.sample.event.BothSub;
import com.cubbery.event.sample.event.EventA;
import com.cubbery.event.sample.event.EventAny;
import com.cubbery.event.EventBus;
import com.cubbery.event.sample.event.ListenerSub;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfDemo {
    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        InputStream is = ConfDemo.class.getResourceAsStream("/conf/conf.properties");
        properties.load(is);
        final EventBus eventBus = new EventBus(properties);

        //注册消费者
        eventBus.register(new ListenerSub());
        eventBus.register(new BothSub());
        //启动事件总线
        eventBus.start();

        //发送事件消息（需要启动后才能发送）
        eventBus.publish(new EventAny());
        eventBus.publish(new EventA());
        //停止事件总线
        eventBus.stop();
    }
}
