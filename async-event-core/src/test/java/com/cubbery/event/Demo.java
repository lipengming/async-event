/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event;

import com.cubbery.event.channel.MemoryChannel;
import com.cubbery.event.event.SimpleEvent;
import com.cubbery.event.utils.Threads;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Demo {
    private static volatile boolean end = false;

    public static void main(String[] args) throws InterruptedException {
        final EventBus eventBus = new EventBus(new MemoryChannel(1024));
        eventBus.register(new AnnotationSub());
        eventBus.register(new AnnotationSub2());
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

class AnnotationSub {
    AtomicInteger counter = new AtomicInteger(0);

    @Subscriber
    public void handler(SimpleEvent event) {
        System.out.println(System.currentTimeMillis() + " ---1---> " + counter.incrementAndGet());
    }
}

class AnnotationSub2 {
    AtomicInteger counter = new AtomicInteger(0);

    @Subscriber
    public void handler(SimpleEvent event) {
        System.out.println(System.currentTimeMillis() + " ----2--> " + counter.incrementAndGet());
    }
}

