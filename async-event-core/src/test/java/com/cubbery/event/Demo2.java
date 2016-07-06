/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event;

import com.cubbery.event.finder.ListenerHandlerFinder;
import com.cubbery.event.utils.Threads;
import com.cubbery.event.channel.MemoryChannel;
import com.cubbery.event.event.SimpleEvent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Demo2 {
    private static volatile boolean end = false;

    public static void main(String[] args) {
        final EventBus eventBus = new EventBus(new MemoryChannel(1024),new ListenerHandlerFinder());
        eventBus.register(new InterSub());
        eventBus.register(new InterSub2());
        eventBus.start();

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i < 10 && !end; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (int a = 0; a < 100; a++) {
                        eventBus.publish(new SimpleEvent());
                    }
                }
            }).start();
        }
        countDownLatch.countDown();
        Threads.sleep(1000);
        end = true;
        eventBus.stop();
    }
}

class InterSub implements ISubscribe<SimpleEvent> {
    AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void handler(SimpleEvent event) {
        System.out.println(System.currentTimeMillis() + " ---1---> " + counter.incrementAndGet());
    }
}

class InterSub2 implements ISubscribe<SimpleEvent> {
    AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void handler(SimpleEvent event) {
        System.out.println(System.currentTimeMillis() + " ---2---> " + counter.incrementAndGet());
    }
}