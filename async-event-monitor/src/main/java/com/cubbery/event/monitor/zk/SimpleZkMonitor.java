/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.monitor.zk;

import com.cubbery.event.Statistics;
import com.cubbery.event.monitor.MonitorServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.cubbery.event.utils.HostUtils.aboutThisJvm;

/**
 * <b>创建人</b>：   <a href="mailto:cubber.zh@gmail.com">百墨</a> <br>
 * <b>修改人</b>：   <br>
 * <b>创建时间</b>： 2016/3/28 - 15:27  <br>
 * @version 1.0.0   <br>
 *
 * 路径示例:    async_event_[app]_[statisticType]_[node(ip+pid)]
 *
 */
public class SimpleZkMonitor extends MonitorServer {
    private final static Logger _LOG = LoggerFactory.getLogger(SimpleZkMonitor.class);

    private final Map<Statistics.Type,String> _ZK_PATH = new HashMap<Statistics.Type, String>();
    private final String appName ;
    private final int internal;
    private final ZKManager zkManager;
    private final Timer timer;
    private volatile boolean isRunning = false;

    public SimpleZkMonitor(String appName,String zkConnectString) {
        this(appName,zkConnectString,null);
    }

    public SimpleZkMonitor(String appName,String zkConnectString,List<Statistics> samples) {
        this(appName,zkConnectString,5,samples);
    }

    public SimpleZkMonitor(String appName,String zkConnectString,int internal,List<Statistics> samples) {
        super(samples);
        this.internal = internal;
        this.appName = appName;
        this.timer = new Timer(true);//Daemon
        this.zkManager = new ZKManager(zkConnectString);
    }

    @Override
    public void close() {
        isRunning = false;
        try {
            zkManager.close();
            timer.cancel();
        } catch (Exception e) {
            _LOG.info("Try to close zk monitor Error!",e);
        }
    }

    @Override
    public synchronized void startUp() {
        if(isRunning == true) {
            return;
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                report2Zk();
            }
        },0,internal);
    }

    private void report2Zk() {
        //遍历所有样本，依次上报
        for(Map.Entry<Statistics.Type,Statistics> entry : samples.entrySet()) {
            switch (entry.getKey()) {
                case CHANNEL:
                    reportChannel(entry.getValue());
                    break;
                default:
                    break;
            }
        }
    }

    private void reportChannel(Statistics value) {
        String zkPath = zkPath(Statistics.Type.CHANNEL);
        String content = value.toString();
        //write to zk
        try {
            zkManager.updateConf(zkPath,content);
        } catch (Exception e) {
            _LOG.info("Report to zk Error!",e);
        }
    }

    private String zkPath(Statistics.Type type) {
        if(!_ZK_PATH.containsKey(type)) {
            StringBuilder sb = new StringBuilder();
            sb.append("/async_event/").append(appName);
            sb.append("/").append(type.name());
            sb.append("/").append(aboutThisJvm());
            _ZK_PATH.put(type,sb.toString());
        }
        return _ZK_PATH.get(type);
    }
}


