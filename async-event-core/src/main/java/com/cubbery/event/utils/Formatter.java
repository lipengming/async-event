/**
 * Copyright (c) 2016,www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.utils;

/**
 * <b>类描述</b>：   <br>
 * <b>创建人</b>：   <a href="mailto:cubber.zh@gmail.com">百墨</a> <br>
 * <b>创建时间</b>： 2016/4/15 - 9:53  <br>
 *
 * @version 1.0.0   <br>
 */
public final class Formatter {

    /**
     * 把纳秒的输出增加千分位，方便人工读数.例如：1234567 => 1,234,567
     *
     * @param ns    1234567
     * @return      1,234,567
     */
    public static String formatNS(long ns) {
        String src = String.valueOf(ns);
        int len = src.length();
        int count = len / 3;
        int first = len % 3;
        if (count < 1 || (count == 1 && first == 0)) {
            return src;
        }
        if (first == 0) {
            first = 3;
            count--;
        }
        StringBuilder sb = new StringBuilder(len + count);
        for (int i = 0; i < len; i++) {
            sb.append(src.charAt(i));
            if ((i+1) == first) {
                sb.append(',');
            } else if (i > first && ((i+1-first)%3) == 0 && (i+1) < len) {
                sb.append(',');
            }
        }
        String fmt = sb.toString();
        //assert fmt.length() == (len+count);
        return fmt;
    }
}
