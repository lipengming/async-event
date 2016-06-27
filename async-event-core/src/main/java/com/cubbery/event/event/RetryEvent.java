/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.event;

public class RetryEvent extends SimpleEvent {
    public RetryEvent() {
        setStatus(EventState.RETRY);
    }

}
