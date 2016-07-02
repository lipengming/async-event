/**
 * Copyright (c) 2016, www.jd.com. All rights reserved.
 */
package freamwork.core.stat;

/**
 * <b>类描述</b>：   <br>
 * <b>创建人</b>：   <a href="mailto:cubber.zh@gmail.com">百墨</a> <br>
 * <b>创建时间</b>： 2016/4/15 - 10:42  <br>
 *
 * @version 1.0.0   <br>
 */
public class NumberUtil  {
    public static double roundTo(double val, int places) {
        double factor = Math.pow(10, places);
        return ((int) ((val * factor) + 0.5)) / factor;
    }
}
