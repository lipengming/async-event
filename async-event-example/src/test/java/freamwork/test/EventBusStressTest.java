/**
 * Copyright (c) 2016, www.jd.com. All rights reserved.
 */
package freamwork.test;

import com.cubbery.event.EventBus;
import freamwork.simple.core.ConcurrentTask;
import freamwork.simple.task.Task;

import static freamwork.test.EventPool.get;

/**
 * <b>类描述</b>：   <br>
 * <b>创建人</b>：   <a href="mailto:cubber.zh@gmail.com">百墨</a> <br>
 * <b>创建时间</b>： 2016/4/15 - 9:57  <br>
 *
 * @version 1.0.0   <br>
 */
public class EventBusStressTest extends Task<EventBus> implements ConcurrentTask {

    public EventBusStressTest(EventBus eventBus) {
        super(eventBus);
    }

    @Override
    public void run() {
        this.target.publish(get());
    }
}
