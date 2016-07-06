/**
 * Copyright (c) 2015, cubbery.com. All rights reserved.
 */
package freamwork.simple.task;

public abstract class Task<T> {
    protected T target;
    public Task(T t) {
        this.target = t;
    }
}
