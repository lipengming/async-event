/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event;

/**
 * 接口方式的订阅者
 * @param <T>
 */
public interface ISubscribe<T> {
    /**订阅者方法名**/
    String methodName = "handler";

    /**
     * 订阅者逻辑处理
     *
     * @param event
     */
    void handler(T event);
}
