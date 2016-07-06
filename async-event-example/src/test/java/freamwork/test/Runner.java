/**
 * Copyright (c) 2016, www.jd.com. All rights reserved.
 */
package freamwork.test;

import com.cubbery.event.EventBus;
import freamwork.simple.core.ConcurrentTest;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * <b>类描述</b>：   <br>
 * <b>创建人</b>：   <a href="mailto:cubber.zh@gmail.com">百墨</a> <br>
 * <b>创建时间</b>： 2016/4/15 - 10:17  <br>
 *
 * @version 1.0.0   <br>
 */
public class Runner {
    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml","spring-orac.xml");
        final EventBus eventBus = applicationContext.getBean("eventBus",EventBus.class);
        eventBus.register(new EventSub());

        eventBus.start();

        EventPool.init(100);
        /*
        ===: =======
        sum: 40,676,685,409
        min: 14,026,225
        max: 755,596,717
        avg: 40,676,685
        err: 0
        ===: =======
        */
        //new ConcurrentTest(10,100,new EventBusStressTest(eventBus)).start();//10个并发,100次重复

        //Thread.sleep(10000);

        /*
        ===: =======
        sum: 39,964,314,762
        min: 13,577,526
        max: 947,670,239
        avg: 39,964,314
        err: 0
        ===: =======
        */
        new ConcurrentTest(10,100,new EventBusStressTest(eventBus)).start();//10个并发,100次重复

        eventBus.stop();
        applicationContext.stop();
        applicationContext.close();
        System.exit(1);
        //性能瓶颈主要在数据库
    }
}
