/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.retry;

import com.cubbery.event.EventBus;
import com.cubbery.event.EventStorage;
import com.cubbery.event.conf.Configurable;
import com.cubbery.event.conf.ConfigureKeys;
import com.cubbery.event.event.Offline;
import com.cubbery.event.event.RetryEvent;
import com.cubbery.event.handler.EventHandler;
import com.cubbery.event.utils.Threads;
import com.cubbery.event.conf.Context;
import com.cubbery.event.utils.ThreadFactories;
import com.cubbery.event.worker.RetryWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.cubbery.event.utils.HostUtils.aboutThisJvm;

/**
 * <b>类描述</b>： 重试服务，管理重试标记、重试分发线程、重试队列等<br>
 * <b>创建人</b>： <a href="mailto:cubber.zh@gmail.com">百墨</a> <br>
 * <b>创建时间</b>：9:46 2016/2/25 <br>
 * @version 1.0.0 <br>
 */
public class RetryService implements Configurable {
    private final static Logger LOG = LoggerFactory.getLogger("Retry-Service");

    //master优先权,单位为秒（s）
    private long priority;
    //重试线程数
    private int retryTaskCount;
    //lease 周期,单位为秒（s）
    private long leasePeriod;
    //是否开启下线二次确认
    private boolean makeSureOffline;
    //【开启二次确认后】新master上线等待时间
    private int masterWaitCount;
    //每条消息最大重试次数
    private int maxRetryCount;

    //Lease 持久化引用
    private EventBus eventBus;
    //当前JVM是否为master
    private AtomicBoolean isMaster;
    //是否启动重试服务
    private AtomicBoolean started;
    //重试服务名称
    private String name;
    //lease 线程
    private LeaseTask leaseTask;
    //重试分发 线程
    private ScheduledExecutorService retryDispatchService;
    //重试线程池
    private ExecutorService retryService;
    //重试批量标记线程
    private RetryMarker retryMarker;

    public RetryService(EventBus eventBus) {
        this(eventBus,30,3,0,120,6);//默认重试线程数为3，master优先权为30个时间单位（1/4lease期）（s）
    }

    public RetryService(EventBus eventBus,long priority,int retryTaskCount,int masterWaitCount,long leasePeriod,int maxRetryCount) {
        this.priority = priority;
        this.retryTaskCount = retryTaskCount;
        this.maxRetryCount = maxRetryCount;
        if(masterWaitCount > 0) {
            this.makeSureOffline = true;
            this.masterWaitCount = masterWaitCount;
        } else {
            this.makeSureOffline = false;
            this.masterWaitCount = 0;
        }
        this.leasePeriod = leasePeriod;

        this.eventBus = eventBus;
        this.isMaster = new AtomicBoolean(false);
        this.started = new AtomicBoolean(false);
        this.name = aboutThisJvm();
        retryMarker = new RetryMarker(this);
    }

    public synchronized void start() {
        if(started.get()) {
            LOG.warn("Retry Service is started!");
            return;
        }
        if(eventBus.isBlackNode(this.getName())) {
            LOG.warn("This Node is in black list! Never Be A Retry Node!");
            started.set(false);
            return;
        }
        LOG.info("Try to Start Retry Service ！");
        init();
        startLease();
        startRetry(false);
        started.compareAndSet(false, true);
        LOG.info("Retry Service Started ！");
    }

    public synchronized void stop() {
        LOG.info("Try To Stop Retry Service ！");
        this.setMaster(false);
        leaseTask.stop();
        started.set(false);
        LOG.info("Try To Stop Retry Service ！");
    }

    private synchronized void init() {
        //init Lease
        try {
            eventBus.getStorage().initLease(leasePeriod);
        } catch (Exception e) {
            LOG.warn("Init Lease Error! Ignore the DuplicateKeyException ！");
        }
        //init ...
    }

    public synchronized void setMaster(boolean isNewMaster) {
        if(this.isMaster() && isNewMaster) {
            LOG.info("Current Node Is Master Now ！");
            return;//保留现在的状态
        }
        //下线标识
        final boolean confirmOfflineIfNecessary = this.isMaster() && !isNewMaster;
        //上线标识
        final boolean waitOnlineIfNecessary = !this.isMaster() && isNewMaster;

        this.isMaster.set(isNewMaster);//先改，然后启动
        if(this.isMaster()) {
            startRetry(waitOnlineIfNecessary);
        } else {
            stopRetry(confirmOfflineIfNecessary);
        }
    }

    private void startRetry(boolean waitOnlineIfNecessary) {
        LOG.info("Try to Start Inner Retry Service ！");
        //先读取是否可以上线
        int waitCount = 0;
        while(makeSureOffline && waitOnlineIfNecessary && (++waitCount) < masterWaitCount) {
            if(!beforeHaveOffLine()) {
                LOG.info("Wait for Pre-Master Release the Lease...");
                Threads.sleep(10000);//10s
            } else {
                break;//已经下线，那么当前节点升级为master
            }
        }
        //在老的master发送了下线通知，或者超时未收到的情况下（存在风险）,新的master上线
        if(started.get() && isMaster()) {
            LOG.info("Start to Retry ！");
            //懒初始化，配置加载
            if(retryService == null) {
                //add comments by liuc 
                //3个线程是否会重复处理同一个消息（虽然幂等）,no!
                //用于消费重试事件单元的线程，不是筛选重试事件的线程，所以不存在重复消费问题这里只是把重试事件线程和普通消费线程分开
                //用户业务线程，使用常驻线程
                this.retryService = Executors.newFixedThreadPool(this.retryTaskCount,new ThreadFactories("Retry-Consumer"));
            }
            
            //重试服务筛选线程，只能保持一个线程操作，用于选出需要重试的事件（标记为重试的）
            if(retryDispatchService == null) {
                //程序分发线程，使用守护线程
                retryDispatchService = Executors.newScheduledThreadPool(1,new ThreadFactories(true,"Retry-Dispatcher"));
            }

            //重试数据加载默认20min间隔处理一次。初始完成后，delay为10min
            retryDispatchService.scheduleWithFixedDelay(new RetryTask(), 10, 10, TimeUnit.MINUTES);
            
            //启动批量标记，（add comments by liuc
            //注意*：这个逻辑是为了避免极端情况，比如在内存中还没来得及消费，就掉电或者进程DOWN了，
            //导致持久化消息既没置为重试，也还是等待消费状态）,可能你又要问，为何不重试的时候把重试状态和待消费状态的都查出来处理呢？
            //这样可能会发生重复消费，比如内存中正在消费，重试服务又启动了，又选出了这个待消费的消息，导致重复消费
            retryMarker.start();
        }
        LOG.info("Inner Retry Service Started ！");
    }

    private boolean beforeHaveOffLine() {
        Offline offline = this.getLeaseDao().getLastOffline();
        if(offline != null) {
            long interval = offline.getInterval();
            //小于半个周期为有效的下线通知
            return (interval < halfPeriod());
        }
        return false;
    }

    private void stopRetry(boolean confirmOfflineIfNecessary) {
        LOG.info("Try To Stop Inner Retry Service ！");
        //停止批量标记
        retryMarker.stop();

        //两阶段关闭，第一阶段调用 shutdown 拒绝传入任务，然后调用 shutdownNow（如有必要）取消所有遗留的任务。
        Threads.shutDownAndAwaitTerminal(retryDispatchService);
        Threads.shutDownAndAwaitTerminal(retryService);

        //发送下线确认
        if(confirmOfflineIfNecessary) {
            this.eventBus.getStorage().confirmOffline(new Offline(name));
        }
        LOG.info("Stop Inner Retry Service Success ！");
    }

    private void startLease() {
        LOG.info("Try To Start Lease Service ！");
        if(started.get()) return;
        if(leaseTask == null) {
            leaseTask = new LeaseTask(this, false);
        }
        Thread leaseThread  = new ThreadFactories("Lease").newThread(leaseTask);
        leaseThread.start();
        LOG.info("Start Lease Service Success ！");
    }

    public boolean isMaster() {
        return this.isMaster.get();
    }

    public long getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }

    public void setRetryTaskCount(int retryTaskCount) {
        this.retryTaskCount = retryTaskCount;
    }

    public EventStorage getLeaseDao() {
        return eventBus.getStorage();
    }

    class RetryTask implements Runnable {
        @Override
        public void run() {
            List<RetryEvent> retries = eventBus.getStorage().selectRetryEvents(maxRetryCount);
            int size = retries.size();
            LOG.info("Read {} items to retry!",size);
            if(size < 1) return;

            for (int a = 0; (a < size && started.get()); a++ ) {
               try {
                    RetryEvent entity = retries.get(a);
                    consumeEvent(entity);
                } catch (Throwable throwable) {
                    LOG.error("Consumer Error!",throwable);
                    break;
                }
            }
        }

        private void consumeEvent(RetryEvent entity) {
            //当handler的表达式为空的时候，默认全部相关的handler都重试
            Set<EventHandler> handlers = eventBus.getHandlerClassByType(entity.getType(),entity.getExpression());
            if(handlers == null || handlers.isEmpty()) return;
            for(EventHandler handler : handlers) {
                retryService.submit(new RetryWorker(eventBus,handler, entity));
            }
        }
    }

    private long halfPeriod() {
        return this.leasePeriod * 1000 / 2;
    }

    @Override
    public void configure(Context context) {
        this.priority = context.getLong(ConfigureKeys.RETRY_MASTER_PRIORITY,this.priority);
        this.retryTaskCount = context.getInt(ConfigureKeys.RETRY_PARALLEL_COUNT,this.retryTaskCount);
        this.leasePeriod = context.getLong(ConfigureKeys.RETRY_LEASE_PERIOD,this.leasePeriod);
        this.maxRetryCount = context.getInt(ConfigureKeys.EVENT_MAX_RETRY_COUNT,this.maxRetryCount);

        this.makeSureOffline = context.getBoolean(ConfigureKeys.RETRY_MAKESURE_OFFLINE,this.makeSureOffline);
        this.masterWaitCount = context.getInt(ConfigureKeys.RETRY_MASTER_WAIT,this.masterWaitCount);
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    public long getLeasePeriod() {
        return leasePeriod;
    }
}
