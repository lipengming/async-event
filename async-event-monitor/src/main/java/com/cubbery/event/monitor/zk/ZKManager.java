/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.monitor.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZKManager {
    private final static Logger _LOG = LoggerFactory.getLogger(ZKManager.class);

    private ZooKeeper zk;
    private List<ACL> acl = new ArrayList<ACL>();
    private final String zkConnectString;
    private String userName = "async_event";
    private String password = "async_event";
    private final int zkSessionTimeout;//5s

    public ZKManager(String zkConnectString) {
        this(zkConnectString,5 * 1000);
    }

    public ZKManager(String zkConnectString,int zkSessionTimeout) {
        this.zkConnectString = zkConnectString;
        this.zkSessionTimeout = zkSessionTimeout;
        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            if (this.checkZookeeperState() == false) {
                createZookeeper(countDownLatch);
            }
            countDownLatch.countDown();
        } catch (Exception e) {
            _LOG.error("Create Zk Manager Err!",e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 重连zookeeper
     *
     * @throws Exception
     */
    public synchronized void reConnection() throws Exception {
        if (this.zk != null) {
            this.zk.close();
            this.zk = null;
            this.connect();
        }
    }

    public boolean checkZookeeperState() throws Exception {
        return zk != null && zk.getState() == ZooKeeper.States.CONNECTED;
    }

    public void close() throws InterruptedException {
        _LOG.info("关闭zookeeper连接");
        this.zk.close();
    }

    public void updateConf(String zkPath,String content) throws Exception {
        if(zk.exists(zkPath, false) == null){
            ZkTools.createPath(zk, zkPath, CreateMode.PERSISTENT, acl);
        }
        zk.setData(zkPath,content.getBytes(),-1);
    }

    private void connect() throws Exception {
        CountDownLatch connectionLatch = new CountDownLatch(1);
        createZookeeper(connectionLatch);
        connectionLatch.await();
    }

    private void createZookeeper(final CountDownLatch connectionLatch) throws Exception {
        zk = new ZooKeeper(zkConnectString, zkSessionTimeout,
                new Watcher() {
                    public void process(WatchedEvent event) {
                        sessionEvent(connectionLatch, event);
                    }
                });
        String authString = userName + ":" + password;
        zk.addAuthInfo("digest", authString.getBytes());
        acl.clear();
        acl.add(new ACL(ZooDefs.Perms.ALL, new Id("digest", DigestAuthenticationProvider.generateDigest(authString))));
        acl.add(new ACL(ZooDefs.Perms.READ, ZooDefs.Ids.ANYONE_ID_UNSAFE));
    }

    private void sessionEvent(CountDownLatch connectionLatch, WatchedEvent event) {
        if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
            _LOG.info("收到ZK连接成功事件！");
            connectionLatch.countDown();
        } else if (event.getState() == Watcher.Event.KeeperState.Expired) {
            _LOG.error("会话超时，等待重新建立ZK连接...");
            try {
                reConnection();
            } catch (Exception e) {
                _LOG.error(e.getMessage(), e);
            }
        }
        // Disconnected：Zookeeper会自动处理Disconnected状态重连
    }
}
