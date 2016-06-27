/**
 * Copyright (c) 2016, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.monitor.log;

import com.cubbery.event.Statistics;
import com.cubbery.event.monitor.MonitorServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Timer;

public final class LogMonitor extends MonitorServer {
    private final static Logger LOG = LoggerFactory.getLogger("Log_Monitor");
    private final static long period = 10 * 60 * 1000;// 10 min
    private Timer timer;

    public LogMonitor(List<Statistics> samples) {
        super(samples);
        this.timer = new Timer(true);
    }

    public synchronized void startUp() {
        timer.purge();
        timer.scheduleAtFixedRate(new Task(),0,period);
    }

    public synchronized void close() {
        timer.cancel();
    }

    class Task extends java.util.TimerTask {

        @Override
        public void run() {
            //遍历所有样本，依次上报
            for(Map.Entry<Statistics.Type,Statistics> entry : samples.entrySet()) {
                switch (entry.getKey()) {
                    case CHANNEL:
                        LOG.info("==Channel==" + entry.getValue().toString());
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
