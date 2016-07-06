/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.sample;

import com.cubbery.event.EventBus;
import com.cubbery.event.channel.MemoryChannel;
import com.cubbery.event.event.SimpleEvent;
import com.cubbery.event.sample.event.AnnotationSub;
import com.cubbery.event.sample.event.BothSub;
import com.cubbery.event.sample.event.EventA;
import com.cubbery.event.sample.event.EventAny;

public class SampleWithAnnotaionSub {
    public static void main(String[] args) {
        final EventBus eventBus = new EventBus(new MemoryChannel(1024));
        eventBus.register(new AnnotationSub());
        eventBus.register(new BothSub());
        eventBus.start();

        try {
            eventBus.publish(new SimpleEvent());
        } catch (Exception e) {

        }
        eventBus.publish(new EventAny());
        eventBus.publish(new EventA());
        eventBus.stop();
    }
}
