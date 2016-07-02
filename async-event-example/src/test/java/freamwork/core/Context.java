/**
 * Copyright (c) 2015, cubbery.com. All rights reserved.
 */
package freamwork.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class Context implements ApplicationContextAware {
    public static ApplicationContext applicationContext;

    public static <T> T  getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
