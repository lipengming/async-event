/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.event;

import java.io.Serializable;
import java.util.Date;

public class Offline implements Serializable {
    private long id;
    private String master;
    private Date modifiedTime;
    private Date createdTime;
    private Date now;

    public Offline() {
    }

    public Offline(String master) {
        this.master = master;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
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

    public long getInterval() {
        return this.getCreatedTime().getTime() - this.getNow().getTime();
    }
}
