/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * <b>创建人</b>：   <a href="mailto:cubber.zh@gmail.com">百墨</a> <br>
 * <b>修改人</b>：   <br>
 * <b>创建时间</b>： 2016/3/29 - 10:05  <br>
 *
 * @version 1.0.0   <br>
 */
public class HostUtils {
    private static final Logger logger = LoggerFactory.getLogger(HostUtils.class);

    public static final String LOCALHOST = "127.0.0.1";

    public static final String ANYHOST = "0.0.0.0";

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    private static volatile InetAddress LOCAL_ADDRESS = null;

    /**
     * 获取当前机器的标识: ip_pid_random
     *
     * @return
     */
    public static String aboutThisJvm() {
        StringBuffer sb = new StringBuffer();
        sb.append(getLocalHost());
        // get name representing the running Java virtual machine.
        String name = ManagementFactory.getRuntimeMXBean().getName();
        // get pid
        sb.append("_").append(name.split("@")[0]);
        sb.append("_").append(new Random().nextInt(100));
        return sb.toString();
    }

    /**
     * 如果获取网络地址失败，返回本地回环地址：127.0.0.1
     *
     * @return  本地地址
     */
    public static String getLocalHost() {
        InetAddress address = getLocalAddress();
        return address == null ? LOCALHOST : address.getHostAddress();
    }

    public static boolean isValidNetAddress(String ipAddress) {
        return (ipAddress != null
                && ! ANYHOST.equals(ipAddress)
                && ! LOCALHOST.equals(ipAddress)
                && IP_PATTERN.matcher(ipAddress).matches());
    }

    public static boolean isValidIpAddress(String ipAddress) {
        return (ipAddress != null && IP_PATTERN.matcher(ipAddress).matches());
    }

    /**
     * 遍历本地网卡，返回第一个合理的IP。
     *
     * @return 本地网卡IP
     */
    private static InetAddress getLocalAddress() {
        if (LOCAL_ADDRESS != null)
            return LOCAL_ADDRESS;
        InetAddress localAddress = getLocalAddress0();
        LOCAL_ADDRESS = localAddress;
        return localAddress;
    }

    private static InetAddress getLocalAddress0() {
        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                return localAddress;
            }
        } catch (Throwable e) {
            logger.warn("Failed to retrieving ip address, " + e.getMessage(), e);
        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    try {
                        NetworkInterface network = interfaces.nextElement();
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        if (addresses != null) {
                            while (addresses.hasMoreElements()) {
                                try {
                                    InetAddress address = addresses.nextElement();
                                    if (isValidAddress(address)) {
                                        return address;
                                    }
                                } catch (Throwable e) {
                                    logger.warn("Failed to retrieving ip address, " + e.getMessage(), e);
                                }
                            }
                        }
                    } catch (Throwable e) {
                        logger.warn("Failed to retrieving ip address, " + e.getMessage(), e);
                    }
                }
            }
        } catch (Throwable e) {
            logger.warn("Failed to retrieving ip address, " + e.getMessage(), e);
        }
        logger.error("Could not get local host ip address, will use 127.0.0.1 instead.");
        return localAddress;
    }

    private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }
        String name = address.getHostAddress();
        return isValidNetAddress(name);
    }
}
