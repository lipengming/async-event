/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.conf;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * 配置信息，相当于properties
 */
public class Context {
    private Map<String, String> parameters;

    public Context() {
        parameters = Collections.synchronizedMap(new HashMap<String, String>());
    }

    public Context(Map<String, String> parameters) {
        this();
        this.putAll(parameters);
    }

    public Context(Properties properties) {
        this();
        Class<ConfigureKeys> clazz = ConfigureKeys.class;
        Field[] fs = clazz.getFields();
        for(Field f : fs) {
            try {
                String fValue = (String) f.get(clazz);
                this.put(fValue, (String) properties.get(fValue));
            } catch (IllegalAccessException e) {
                //
            }
        }
    }

    public void putAll(Map<String, String> map) {
        parameters.putAll(map);
    }

    public void put(String key, String value) {
        parameters.put(key, value);
    }

    public boolean containsKey(String key) {
        return parameters.containsKey(key);
    }

    public Integer getInt(String key, Integer defaultValue) {
        String value = get(key);
        if(value != null) {
            return Integer.parseInt(value.trim());
        }
        return defaultValue;
    }

    public Integer getInt(String key) {
        return getInt(key, null);
    }

    public Long getLong(String key, Long defaultValue) {
        String value = get(key);
        if(value != null) {
            return Long.parseLong(value.trim());
        }
        return defaultValue;
    }

    public TimeUnit getTimeUnit(String key,TimeUnit defaultValue) {
        String result = parameters.get(key);
        if(result != null) {
            TimeUnit unit = TimeUnit.valueOf(result);
            if(unit != null) {
                return unit;
            }
        }
        return defaultValue;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        return Boolean.getBoolean(value);
    }

    public TimeUnit getTimeUnit(String key) {
        return getTimeUnit(key,null);
    }

    public Long getLong(String key) {
        return getLong(key,null);
    }

    public String getString(String key, String defaultValue) {
        return get(key, defaultValue);
    }

    public String getString(String key) {
        return get(key);
    }

    private String get(String key) {
        return get(key, null);
    }

    private String get(String key, String defaultValue) {
        String result = parameters.get(key);
        if(result != null) {
            return result;
        }
        return defaultValue;
    }
}
