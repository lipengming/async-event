/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.conf;

/**
 * 对于实现了该接口的所有组件，都可配置，在所有组件注册到bus后，bus读取配置文件，下发到所有组件。配置生效。
 */
public interface Configurable {
    /**
     * 可配置组件，配置下发
     *
     * @param context
     */
    void configure(Context context);
}
