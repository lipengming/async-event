/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.monitor;

import com.cubbery.event.Statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class MonitorServer {
    /**样本集合**/
    protected final Map<Statistics.Type,Statistics> samples = new HashMap<Statistics.Type, Statistics>();

    protected MonitorServer() {
    }

    protected MonitorServer(List<Statistics> samples) {
        if(null == samples || samples.size() < 1) return;
        for(Statistics statistics : samples) {
            this.samples.put(statistics.getType(),statistics);
        }
    }

    /**
     * close
     */
    public abstract void close();

    /**
     * startUp
     */
    public abstract void startUp();

    public Map<Statistics.Type, Statistics> getSamples() {
        return samples;
    }

    public void setSamples(Map<Statistics.Type, Statistics> samples) {
        this.samples.putAll(samples);
    }
}
