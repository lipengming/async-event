/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.sample.spring;

import com.cubbery.event.sample.event.AnnotationSub;
import com.cubbery.event.sample.event.BothSub;
import com.cubbery.event.EventBus;
import com.cubbery.event.channel.PersistentChannel;
import com.cubbery.event.finder.BothHandlerFinder;
import com.cubbery.event.retry.RetryService;
import com.cubbery.event.sample.event.EventA;
import com.cubbery.event.utils.Threads;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.CountDownLatch;

public class MutipDemo {
    private static volatile boolean end = false;

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-orac.xml");

        //使用持久化的通道，保证数据不丢
        final EventBus eventBus = new EventBus(
                //使用spring配置的持久化队列
                new PersistentChannel(1024, (com.cubbery.event.EventStorage) applicationContext.getBean("storage")),
                new BothHandlerFinder()//指定只能使用接口方式定义消费者
        );
        //启用重试服务
        RetryService retryService = new RetryService(eventBus);
        eventBus.setRetryService(retryService);

        //注册消费者
        eventBus.register(new AnnotationSub());//注册消费者
        eventBus.register(new BothSub());//注册消费者
        //启动事件总线
        eventBus.start();

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i < 10 && !end; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (int a = 0; a < 100 && !end; a++) {
                        try {
                            eventBus.publish(new EventA());//发送事件消息
                        }catch (Exception e) {

                        }
                    }
                }
            }).start();
        }
        countDownLatch.countDown();
        Threads.sleep(10 * 1000);
        end = true;
        //停止事件总线
        eventBus.stop();
    }
}