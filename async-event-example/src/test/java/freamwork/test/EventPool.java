/**
 * Copyright (c) 2016, www.jd.com. All rights reserved.
 */
package freamwork.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static freamwork.simple.common.UniqNumUtil.getUniqNum;

/**
 * <b>类描述</b>：   <br>
 * <b>创建人</b>：   <a href="mailto:cubber.zh@gmail.com">百墨</a> <br>
 * <b>创建时间</b>： 2016/4/15 - 10:05  <br>
 *
 * @version 1.0.0   <br>
 */
public class EventPool {
    private static List<Event> eventPool;
    private static int size;

    public static void init(int size) {
        EventPool.size = size;
        eventPool = new ArrayList<Event>(size);

        for (int a = 0; a < size; a++) {
            Event event = new Event();
            event.setE1(getUniqNum(10));
            event.setE2(getUniqNum(10));
            event.setE3(getUniqNum(10));
            event.setE4(getUniqNum(10));
            event.setE5(getUniqNum(10));
            event.setE6(getUniqNum(10));
            event.setE7(getUniqNum(10));
            event.setE8(getUniqNum(10));

            SubEvent subEvent = new SubEvent();
            subEvent.setF1(getUniqNum(11));
            subEvent.setF2(getUniqNum(11));
            subEvent.setF4(getUniqNum(11));
            subEvent.setF3(getUniqNum(11));
            subEvent.setF5(getUniqNum(11));
            subEvent.setF6(getUniqNum(11));
            subEvent.setF7(getUniqNum(11));
            subEvent.setF8(getUniqNum(11));
            subEvent.setF9(getUniqNum(11));

            event.setSubEvent(subEvent);
            eventPool.add(event);
        }
    }

    public static Event get() {
        return eventPool.get(new Random().nextInt(size));
    }

}
