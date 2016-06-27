/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event;

import com.cubbery.event.event.SimpleEvent;
import com.cubbery.event.event.Offline;
import com.cubbery.event.event.RetryEvent;
import com.cubbery.event.retry.Lease;

import java.util.List;

/**
 * 事件持久化标准协议
 */
public interface EventStorage {

    /**
     * 插入一条事件消息
     *
     * @param event  插入成功后，返回主键ID
     */
    void insertEvent(List<SimpleEvent> event) ;

    /**
     * 单条更新成死信状态
     *
     * @param id
     * @return
     */
    int markAsDead(long id);

    /**
     * 单条更新成成功状态
     *
     * @param id
     * @return
     */
    int markAsSuccess(long id);

    /**
     * 单条更新成重试状态
     *
     * @param id
     * @return
     */
    int markAsRetry(long id);

    /**
     * 批量跟新为待重试状态
     *
     * @return
     */
    int batchMarkAsRetry();

    /**
     * 批量标记超过最大重试上限的事件为死信
     *
     * @param maxRetryCount     最大重试上限
     * @return
     */
    int batchMarkAsDead(int maxRetryCount);

    /**
     * 查询一个单位（100-1000左右）的待重试事件
     *
     * @return
     */
    List<RetryEvent> selectRetryEvents(int maxRetryCount);

    /**
     * 查询租约信息
     *
     * @return
     */
    Lease selectLease();

    /**
     * 续租约
     *
     * @param masterInfo
     * @param oldVersion
     * @return
     */
    int updateLease(String masterInfo, long oldVersion,long period);

    /**
     * 尝试插入一则数据，如果插入失败（主键异常，那么忽略）
     */
    void initLease(long period);

    /**
     * 插入一则下线消息到下线表
     *
     * @param masterInfo
     * @return
     */
    int confirmOffline(Offline masterInfo);

    /**
     * 查询最近下线记录
     *
     * @return
     */
    Offline getLastOffline();

    public enum DataSourceType {
        MYSQL,ORACLE;
    }
}
