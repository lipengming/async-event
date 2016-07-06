/**
 * Copyright (c) 2016,www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.event.impl;

import com.cubbery.event.EventCreator;
import com.cubbery.event.channel.ChannelData;
import com.cubbery.event.event.SimpleEvent;
import com.cubbery.event.handler.EventHandler;
import com.cubbery.event.utils.JsonUtils;

/**
 * <b>类描述</b>：   默认事件构造器<br>
 * <b>创建人</b>：   <a href="mailto:cubber.zh@gmail.com">百墨</a> <br>
 * <b>创建时间</b>： 2016/4/18 - 15:38  <br>
 *
 * @version 1.0.0   <br>
 */
public class DefaultEventCreator implements EventCreator {
    @Override
    public SimpleEvent create(Object e, EventHandler handler) {
        if(e != null ) {
            SimpleEvent simpleEvent = new SimpleEvent();
            if(e instanceof ChannelData) {
                simpleEvent.setId(((ChannelData)e).getId());
            }
            simpleEvent.setData(JsonUtils.serialize(e));
            simpleEvent.setType(e.getClass().getCanonicalName());
            if(handler != null) {
                simpleEvent.setExpression(handler.expression());
                simpleEvent.setEventHandler(handler);
            }
            return simpleEvent;
        }
        return null;
    }

    @Override
    public Object reCreate(SimpleEvent event, Class<?> clazz) {
        if(clazz == null) return null;
        if(!SimpleEvent.class.equals(clazz)) {
            //还原事件类型
            if(event.getData() == null) {
                try {
                    return clazz.newInstance();
                } catch (InstantiationException e) {
                    //
                } catch (IllegalAccessException e) {
                    //
                }
                return null;
            }
            return JsonUtils.deSerialize(event.getData(), clazz);
        }
        return event;
    }
}
