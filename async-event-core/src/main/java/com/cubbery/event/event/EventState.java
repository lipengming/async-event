/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.event;

public interface EventState {

    int SUCCESS = 0;//受理成功
    int CONSUME = 1;//待消费
    int RETRY = 2;//重试消息
    int DEAD = 3;//死信消息

}
