/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.channel;

import com.cubbery.event.handler.EventHandler;

/**
 * 
 *事件处理单元
 *用于封装一个事件和该事件对应的处理者,作为一个事件处理单元   
 *
 */
public class ChannelData {
    /** event **/
    private Object data;
    /** persistent primary key **/
    private long id;
    /** handler for event **/
    private EventHandler handler;


    public ChannelData(Object data) {
        this.data = data;
    }

    public ChannelData(Object data, EventHandler handler) {
        this.data = data;
        this.handler = handler;
    }

    public ChannelData(Object data, long id, EventHandler handler) {
        this.data = data;
        this.id = id;
        this.handler = handler;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public EventHandler getHandler() {
        return handler;
    }

    public void setHandler(EventHandler handler) {
        this.handler = handler;
    }
}
