/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.finder;

import com.cubbery.event.EventStorage;
import com.cubbery.event.HandlerFinder;
import com.cubbery.event.handler.EventHandler;
import com.cubbery.event.handler.PersistenceEventHandler;

import java.lang.reflect.Method;

abstract class AbstractHandlerFinder implements HandlerFinder {
    private EventStorage storage;

    protected EventHandler makeHandler(Object listener, Method method) {
        if(getStorage() != null) {
            return new PersistenceEventHandler(listener, method,getStorage());
        }
        return new EventHandler(listener, method);
    }

    public EventStorage getStorage() {
        return storage;
    }

    public AbstractHandlerFinder setStorage(EventStorage storage) {
        this.storage = storage;
        return this;
    }
}
