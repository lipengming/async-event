/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.utils;

import com.cubbery.event.handler.EventHandler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapUtils {

    public static void put(final Map<Class<?>, Set<EventHandler>> map,final Class<?> clazz,final EventHandler handler) {
        if(!map.containsKey(clazz)) {
            map.put(clazz,new HashSet<EventHandler>());//初始化
        }
        map.get(clazz).add(handler);
    }

    public static void put(final Map<Class<?>, Set<EventHandler>> map,final Class<?> clazz,final Set<EventHandler> handler) {
        if(!map.containsKey(clazz)) {
            map.put(clazz,new HashSet<EventHandler>());//初始化
        }
        map.get(clazz).addAll(handler);
    }

    public static void putAll(final Map<Class<?>, Set<EventHandler>> map,final Map<Class<?>, Set<EventHandler>> map0) {
        Set<Class<?>> keys = map0.keySet();
        for(Class<?> clazz : keys) {
            put(map,clazz,map0.get(clazz));
        }
    }

}
