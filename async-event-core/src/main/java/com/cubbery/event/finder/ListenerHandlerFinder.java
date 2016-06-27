/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.finder;

import com.cubbery.event.ISubscribe;
import com.cubbery.event.handler.EventHandler;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.cubbery.event.utils.MapUtils.put;

/**接口方式订阅者查找服务**/
public class ListenerHandlerFinder extends AbstractHandlerFinder {

    @Override
    public Map<Class<?>, Set<EventHandler>> findAllHandlers(Object listener) {
        Map<Class<?>, Set<EventHandler>> methodsInListener = new HashMap<Class<?>, Set<EventHandler>>();
        Class clazz = listener.getClass();
        Type[] types = clazz.getGenericInterfaces();
        for(Type type : types) {
            if(type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                if(!parameterizedType.getRawType().equals(ISubscribe.class)) {
                    continue;
                }
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                if(typeArguments.length == 1) {
                    try {
                        Class<?> eventType = ((Class)typeArguments[0]);
                        Method method = clazz.getMethod(ISubscribe.methodName,eventType);
                        EventHandler handler = makeHandler(listener, method);
                        put(methodsInListener,eventType, handler);
                    } catch (Exception e) {
                        throw new IllegalArgumentException( "Method has not implements ISubscribe ");
                    }
                }
            }
        }
        return methodsInListener;
    }
}
