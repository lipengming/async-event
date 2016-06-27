/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event;

import com.cubbery.event.handler.EventHandler;

import java.util.Map;
import java.util.Set;

/**
 * 事件处理器发现者
 * 用于发现通过实现监听器或者注解方式的事件处理者接口
 * @version 1.0.0  <br>
 *
 */
public interface HandlerFinder {
    /**
     * 查询（发现）订阅方法
     *
     * @param listener  监听者（订阅者）
     * @return
     */
    Map<Class<?>, Set<EventHandler>> findAllHandlers(Object listener);

    /**
     * 配置存储
     *
     * @param storage
     * @return
     */
    HandlerFinder setStorage(EventStorage storage);
}
