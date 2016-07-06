/**
 * Copyright (c) 2016, www.jd.com. All rights reserved.
 */
package freamwork.simple.common;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * <b>类描述</b>：   <br>
 * <b>创建人</b>：   <a href="mailto:cubber.zh@gmail.com">百墨</a> <br>
 * <b>创建时间</b>： 2016/4/15 - 10:00  <br>
 *
 * @version 1.0.0   <br>
 */
public class UniqNumUtil {
    private static AtomicInteger num = new AtomicInteger(0);

    public static String getUniqNum(int length) {
        String cur = String.valueOf(System.nanoTime());
        return cur.substring(cur.length() - length) + num.getAndIncrement();
    }
}
