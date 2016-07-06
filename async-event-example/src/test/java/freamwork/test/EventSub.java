/**
 * Copyright (c) 2016, www.jd.com. All rights reserved.
 */
package freamwork.test;

import com.cubbery.event.ISubscribe;

/**
 * <b>类描述</b>：   <br>
 * <b>创建人</b>：   <a href="mailto:cubber.zh@gmail.com">百墨</a> <br>
 * <b>创建时间</b>： 2016/4/15 - 10:20  <br>
 *
 * @version 1.0.0   <br>
 */
public class EventSub implements ISubscribe<Event> {
    @Override
    public void handler(Event event) {
        try {
            Thread.sleep(0,100);//100 ns
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
