# async-event
进程内部异步事件调用组件

## 解决什么问题：

+   加速服务处理效率。提供进程级别的事件发布和异步处理能力。
+   服务解耦。观察者和发布者之间互不干涉，解耦关系。
+   事件驱动。提供一对多的对象关系。
+   最终一致性。低延时，最终一致。


## 总体设计

![总体设计](/doc/frame.png)


## Usage:

        //实例化事件总线，使用内存队列
        final EventBus eventBus = new EventBus(new MemoryChannel(1024));
        //注册消费者
        eventBus.register(new ListenerSub());
        eventBus.register(new BothSub());
        //启动事件总线
        eventBus.start();

        //发送事件消息（需要启动后才能发送）
        eventBus.publish(new SimpleEvent());
        eventBus.publish(new EventAny());
        eventBus.publish(new EventA());
        //停止事件总线
        eventBus.stop();