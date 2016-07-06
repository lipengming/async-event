/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Leader Node Selection With Lease.(time unit：milliseconds)
 */
final class LeaseTask implements Runnable {
    private final static Logger LOG = LoggerFactory.getLogger("Lease-Task");

    private RetryService retryService;
    private boolean leaseStop;

    public LeaseTask(RetryService retryService,boolean leaseStop) {
        this.retryService = retryService;
        this.leaseStop = leaseStop;
    }

    @Override
    public void run() {
        do {
            try {
                //1、查询Lease表，获取Lease信息
                Lease leaseEntity = retryService.getLeaseDao().selectLease();
                LOG.info("Read Lease Master Info ..." + leaseEntity.getMaster());
                long interval = leaseEntity.getNow().getTime() - leaseEntity.getModifiedTime().getTime();
                long milliseconds = leaseEntity.getPeriod() * 1000;
                long wait = waitTime(interval,milliseconds);
                if(wait > 0) {
                    LOG.info("Lease Wait for ..." + wait);
                    Thread.sleep(wait);
                    continue;
                }
                //试着去抢占，拿到锁
                int row = retryService.getLeaseDao().updateLease(retryService.getName(),leaseEntity.getVersion(),retryService.getLeasePeriod());
                LOG.info("Compete Lease Master ..." + row);
                retryService.setMaster(row == 1);//不关心前一次的状态，不用cas。按照最后最新的数据去覆盖。
            } catch (Throwable e) {
                LOG.error("Lease is terminal ！",e);
                retryService.setMaster(false);//处理异常无条件设置为普通节点。
                sleep();//此时被打断，不再care,进入下一个循环。
            }
        } while (!leaseStop);
    }

    private long waitTime(long interval,long period) {
        if(retryService.isMaster()) {
            return period - interval - retryService.getPriority() * 1000;
        }
        return period - interval;
    }

    private void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.interrupted();
            LOG.error("Lease is Interrupted ！", e);
        }
    }

    public synchronized void stop() {
        LOG.info("Try To Lease Service ！");
        leaseStop = true;
        retryService.setMaster(false);
        LOG.info("Stop Retry Service Success ！");
    }
}
