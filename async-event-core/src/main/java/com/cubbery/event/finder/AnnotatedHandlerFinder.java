/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.finder;

import com.cubbery.event.Subscriber;
import com.cubbery.event.handler.EventHandler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.cubbery.event.utils.MapUtils.put;

/**注解方式订阅者查找服务**/
public class AnnotatedHandlerFinder extends AbstractHandlerFinder {

    @Override
    public Map<Class<?>, Set<EventHandler>> findAllHandlers(Object listener) {
        Map<Class<?>, Set<EventHandler>> methodsInAnno = new HashMap<Class<?>, Set<EventHandler>>();
        Class clazz = listener.getClass();
        while (clazz != null) {
            for (Method method : clazz.getMethods()) {
                Subscriber annotation = method.getAnnotation(Subscriber.class);
                if (annotation != null) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length != 1) {
                        throw new IllegalArgumentException( "Method " + method + " has @Subscribe annotation, but requires " + parameterTypes.length + " arguments.  Event handler methods " + "must require a single argument.");
                    }
                    Class<?> eventType = parameterTypes[0];
                    EventHandler handler = makeHandler(listener, method);
                    put(methodsInAnno,eventType, handler);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return methodsInAnno;
    }
}
