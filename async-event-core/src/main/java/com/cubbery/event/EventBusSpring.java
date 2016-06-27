/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event;

import com.cubbery.event.utils.HostUtils;
import com.cubbery.event.event.impl.EncryptEventCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring辅助配置类：辅助处理：<br>
 *     <ul>
 *         <ol>黑名单字符串处理</ol>
 *         <ol>订阅者处理</ol>
 *         <ol>指定加解密</ol>
 *     </ul>
 * <b>创建人</b>：   <a href="mailto:cubber.zh@gmail.com">百墨</a> <br>
 * <b>创建时间</b>： 2016/3/4 - 10:24  <br>
 * @version 1.0.0   <br>
 */
public class EventBusSpring {
    private EventBus eventBus;
    private List<Object> subscribers;
    private List<String> blackIps;

    public EventBusSpring(EventBus eventBus) {
        this(eventBus,new ArrayList<Object>(0));
    }

    public EventBusSpring(EventBus eventBus, List<Object> subscribers) {
        this(eventBus, subscribers,new ArrayList<String>(0));
    }

    public EventBusSpring(EventBus eventBus, List<Object> subscribers, String blackIpStr) {
        this.eventBus = eventBus;
        this.subscribers = subscribers;
        this.blackIps = ipStrToList(blackIpStr);
    }

    public EventBusSpring(EventBus eventBus, List<Object> subscribers, String blackIpStr,boolean needEncryptEvent) {
        this.eventBus = eventBus;
        this.subscribers = subscribers;
        this.blackIps = ipStrToList(blackIpStr);
        if (needEncryptEvent) {
            this.eventBus.getChannel().setCreator(new EncryptEventCreator());
        }
    }

    public EventBusSpring(EventBus eventBus, List<Object> subscribers,List<String> blackIps) {
        this.eventBus = eventBus;
        this.subscribers = subscribers;
        this.blackIps = blackIps;
    }

    public synchronized void start() {
        for(Object obj : subscribers) {
            this.eventBus.register(obj);
        }
        this.eventBus.setBlackIps(blackIps);
        this.eventBus.start();
    }

    public synchronized void stop() {
        this.eventBus.stop();
    }

    public synchronized void setSubscribers(List<Object> subscribers) {
        this.subscribers = subscribers;
    }

    public static List<String> ipStrToList(String blackIpStr) {
        List<String> ips = new ArrayList<String>();
        if(blackIpStr != null && !blackIpStr.equals("")) {
            String[] arr = blackIpStr.split("\\|");
            for(String ip : arr) {
                if(HostUtils.isValidIpAddress(ip)) {
                    ips.add(ip);
                }
            }
        }
        return ips;
    }
}
