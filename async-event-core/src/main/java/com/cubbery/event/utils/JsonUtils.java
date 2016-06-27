/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtils {
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    private JsonUtils() {
    }

    public static <T> String serialize(T obj) {
        try {
            return gson.toJson(obj);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T deSerialize(String json, Class<T> classOfT) {
        try {
            return gson.fromJson(json, classOfT);
        } catch (Exception e) {
            return null;
        }
    }
}
