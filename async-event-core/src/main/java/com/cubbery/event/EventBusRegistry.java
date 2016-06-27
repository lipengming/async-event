/**
 * Copyright (c) 2016,www.cubbery.com. All rights reserved.
 */
package com.cubbery.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>类描述</b>：   <br>
 * <b>创建人</b>：   <a href="mailto:cubber.zh@gmail.com">百墨</a> <br>
 * <b>创建时间</b>： 2016/5/12 - 16:16  <br>
 *
 * @version 1.0.0   <br>
 */
class EventBusRegistry {
    private final static Logger LOG = LoggerFactory.getLogger("Event-Bus-All");
    private final static List<EventBus> allBus = new ArrayList<EventBus>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    destroyAll();
                } catch (Throwable e) {
                }
            }
        },"EventBusShutDownHook"));
    }

    public static void report(EventBus eventBus) {
        allBus.add(eventBus);
    }

    public static void destroyAll() {
        for(EventBus bus : allBus) {
            if(bus != null) {
                bus.stop();
                LOG.info("Event Bus Stopped!");
            }
        }
    }
}
