/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.worker;

import com.cubbery.event.handler.EventHandler;
import com.cubbery.event.handler.PersistenceEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractWorker {
    protected final Logger LOG = LoggerFactory.getLogger("Consume_Worker");

    protected void markAsDead(EventHandler handler,long id) {
        if(handler instanceof PersistenceEventHandler && id > 0) {
            PersistenceEventHandler  pHandler = (PersistenceEventHandler) handler;
            pHandler.getStorage().markAsDead(id);
        }
    }
}
