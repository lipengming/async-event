/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.sample.event;

import com.cubbery.event.ISubscribe;

import java.util.concurrent.atomic.AtomicInteger;

public class ListenerSub implements ISubscribe<EventA> {

    @Override
    public void handler(EventA event) {
        System.out.println(event.getName() + "=====handler=====" + counter.incrementAndGet());
    }

    public static final AtomicInteger counter = new AtomicInteger(0);

}
