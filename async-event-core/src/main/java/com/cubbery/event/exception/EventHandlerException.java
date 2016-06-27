/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.exception;

/**时间消费异常**/
public class EventHandlerException extends RuntimeException {
    public EventHandlerException(String message) {
        super(message);
    }

    public EventHandlerException(Throwable cause) {
        super(cause);
    }
}
