/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.event;

public class DeadEvent extends SimpleEvent {
    public DeadEvent() {
        setStatus(EventState.DEAD);
    }
}
