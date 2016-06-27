/**
 * Copyright (c) 2016,www.cubbery.com. All rights reserved.
 */
package com.cubbery.event;

import com.cubbery.event.event.SimpleEvent;
import com.cubbery.event.handler.EventHandler;

/**
 * <b>类描述</b>：   <br>
 * <b>创建人</b>：   <a href="mailto:cubber.zh@gmail.com">百墨</a> <br>
 * <b>创建时间</b>： 2016/4/18 - 15:34  <br>
 *
 * @version 1.0.0   <br>
 */
public interface EventCreator {

    /**
     * 将从队列中取出的数据格式化成统一的数据结构，便于保存和后续统一使用
     *
     * @param e 任意obj或者包装结构
     * @return
     */
    SimpleEvent create(Object e,EventHandler handler);

    /**
     * 由统一格式，反解成event的格式
     *
     * @param event     统一格式
     * @param clazz     目标对象类型
     * @return          反解失败返回null
     */
    Object reCreate(SimpleEvent event,Class<?> clazz);
}
