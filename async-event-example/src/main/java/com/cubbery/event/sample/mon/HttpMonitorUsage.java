/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.sample.mon;

import com.cubbery.event.sample.event.BothSub;
import com.cubbery.event.EventBus;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * <b>创建人</b>：   <a href="mailto:cubber.zh@gmail.com">百墨</a> <br>
 * <b>修改人</b>：   <br>
 * <b>创建时间</b>： 2016/3/28 - 18:06  <br>
 *
 * @version 1.0.0   <br>
 */
public class HttpMonitorUsage {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/mon/spring-http.xml","spring-orac.xml");
        final EventBus eventBus = applicationContext.getBean("eventBus",EventBus.class);
        eventBus.register(new BothSub());

        eventBus.start();

       /* final CountDownLatch countDownLatch = new CountDownLatch(1);
        for (int i = 0; i < 10 ; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        countDownLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for (int a = 0; a < 10 ; a++) {
                        int v = a % 3;
                        if(a % 3 != 0 ) {
                            Threads.sleep(1000 * v);
                        }
                        eventBus.publish(new EventA());//发送事件消息
                    }
                }
            }).start();
        }
        countDownLatch.countDown();*/
    }
}
