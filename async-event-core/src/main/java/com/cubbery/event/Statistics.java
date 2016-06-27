/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <b>创建人</b>：   <a href="mailto:cubber.zh@gmail.com">百墨</a> <br>
 * @version 1.0.0   <br>
 * <b>统计信息采集样本。统计信息汇聚在此处，上报到其他需求方</b>
 */
public abstract class Statistics {
    private static final Logger logger = LoggerFactory.getLogger(Statistics.class);
    private static final String COUNTER_GROUP_START_TIME = "start.time";
    private static final String COUNTER_GROUP_STOP_TIME = "stop.time";

    private final Type type;
    private final String name;
    private final Map<String, AtomicLong> counterMap;

    private AtomicLong startTime;
    private AtomicLong stopTime;

    public Statistics(Type type, String name, String... attrs) {
        this.type = type;
        this.name = name;
        Map<String, AtomicLong> counterInitMap = new HashMap<String, AtomicLong>();
        // Initialize the counters
        for (String attribute : attrs) {
            counterInitMap.put(attribute, new AtomicLong(0L));
        }
        this.counterMap = Collections.unmodifiableMap(counterInitMap);
        startTime = new AtomicLong(0L);
        stopTime = new AtomicLong(0L);
        start();
    }

    public void start() {
        stopTime.set(0L);
        for (String counter : counterMap.keySet()) {
            counterMap.get(counter).set(0L);
        }
        startTime.set(System.currentTimeMillis());
        logger.info("Component type: " + type + ", name: " + name + " started");
    }

    public void stop() {
        stopTime.set(System.currentTimeMillis());
        logger.info("Component type: " + type + ", name: " + name + " stopped !");
        final String typePrefix = type.name().toLowerCase(Locale.ENGLISH);

        logger.info("Shutdown Metric for type: " + type + ", " + "name: " + name + ". "
                + typePrefix + "." + COUNTER_GROUP_START_TIME + " == " + startTime);

        logger.info("Shutdown Metric for type: " + type + ", " + "name: " + name + ". "
                + typePrefix + "." + COUNTER_GROUP_STOP_TIME + " == " + stopTime);

        final List<String> mapKeys = new ArrayList<String>(counterMap.keySet());
        Collections.sort(mapKeys);
        for (final String counterMapKey : mapKeys) {
            final long counterMapValue = get(counterMapKey);
            logger.info("Shutdown Metric for type: " + type + ", " + "name: " + name + ". " + counterMapKey + " == " + counterMapValue);
        }
    }

    protected long get(String counter) {
        return counterMap.get(counter).get();
    }

    protected void set(String counter, long value) {
        counterMap.get(counter).set(value);
    }

    protected long addAndGet(String counter, long delta) {
        return counterMap.get(counter).addAndGet(delta);
    }

    protected long increment(String counter) {
        return counterMap.get(counter).incrementAndGet();
    }

    public String getTypeDesc() {
        return type.name();
    }

    public Type getType() {
        return type;
    }

    public long getStartTime() {
        return startTime.get();
    }

    public long getStopTime() {
        return stopTime.get();
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder("{\"name\":\"").append(type.name())
                .append("\",\"startTime\":\"").append(getStartTime())
                .append("\",\"stopTime\":\"").append(getStopTime()).append("\",\"data\":{");
        boolean first = true;
        Iterator<String> counterIterator = counterMap.keySet().iterator();
        while (counterIterator.hasNext()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            String counterName = counterIterator.next();
            sb.append("\"" + counterName.replace(".","")).append("\":\"").append(get(counterName)).append("\"");
        }
        sb.append("}}");

        return sb.toString();
    }

    public static enum Type {
        CHANNEL;
    }
}
