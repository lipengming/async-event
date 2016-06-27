/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.retry;

import java.io.Serializable;
import java.util.Date;

public class Lease implements Serializable {
    //主键
    private long id;
    //修改时间（master最后续租期时间）
    private Date modifiedTime;
    //创建时间
    private Date createdTime;
    //数据库当前时间(用作查询，不做存储)
    private Date now;
    //lease 周期
    private long period;
    //master 信息
    private String master;
    //version 用于加乐观锁
    private long version;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Date modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getNow() {
        return now;
    }

    public void setNow(Date now) {
        this.now = now;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public String getMaster() {
        return master;
    }

    public Lease setMaster(String master) {
        this.master = master;
        return this;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
