/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.channel;

import com.cubbery.event.Channel;
import com.cubbery.event.EventStorage;
import com.cubbery.event.conf.Configurable;
import com.cubbery.event.conf.ConfigureKeys;
import com.cubbery.event.conf.Context;
import com.cubbery.event.EventCreator;
import com.cubbery.event.event.impl.DefaultEventCreator;

import java.util.concurrent.TimeUnit;

abstract class AbstractChannel implements Channel,Configurable {
    protected TimeUnit timeUnit ;
    protected long expire ;
    protected EventCreator eventCreator;

    protected AbstractChannel() {
        timeUnit = TimeUnit.MILLISECONDS;
        expire = 10;
        eventCreator = new DefaultEventCreator();
    }

    @Override
    public EventStorage getStorage() {
        return null;
    }

    @Override
    public void setStorage(EventStorage storage) {
    }

    @Override
    public EventCreator getCreator() {
        return this.eventCreator;
    }

    @Override
    public void setCreator(EventCreator creator) {
        this.eventCreator = creator;
    }

    @Override
    public void configure(Context context) {
        expire = context.getLong(ConfigureKeys.CHANNEL_OFFER_EXPIRE,expire);
        timeUnit = context.getTimeUnit(ConfigureKeys.BUS_TIME_UNIT,timeUnit);
    }
}