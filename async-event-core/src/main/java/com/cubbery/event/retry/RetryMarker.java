/**
 * Copyright (c) 2016, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A Daemon Thread For ：Batch Mark Consume State To Retry State & Batch Mark Retry State To Dead State.
 */
class RetryMarker extends TimerTask {
    private final static Logger LOG = LoggerFactory.getLogger("Retry_Marker");
    private final static long period = 10 * 60 * 1000;// 10 min
    private Timer timer;
    private RetryService retryService;

    public RetryMarker(RetryService retryService) {
        this.retryService = retryService;
        timer = new Timer(true);
    }

    @Override
    public void run() {
        try {
            int rows = retryService.getLeaseDao().batchMarkAsRetry();
            LOG.info("Execute Batch Mark to Retry Success ! Row = " + rows);
            int num = retryService.getLeaseDao().batchMarkAsDead(retryService.getMaxRetryCount());
            LOG.info("Execute Batch Mark to Dead Success ! MaxRetry = " + retryService.getMaxRetryCount() + " Row = " + num);
        } catch (Exception e) {
            LOG.info("Retry Batch Mark Error!");
        }
    }

    public synchronized void start() {
        timer.purge();
        timer.scheduleAtFixedRate(this,0,period);
    }

    public synchronized void stop() {
        timer.cancel();
    }

}
