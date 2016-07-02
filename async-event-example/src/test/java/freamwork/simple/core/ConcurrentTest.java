/**
 * Copyright (c) 2015, cubbery.com. All rights reserved.
 */
package freamwork.simple.core;

import com.cubbery.event.utils.Formatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentTest {
    private CountDownLatch startSignal = new CountDownLatch(1);//开始阀门
    private CountDownLatch doneSignal = null;//结束阀门

    //响应时间
    private CopyOnWriteArrayList<Long> list = new CopyOnWriteArrayList<Long>();

    private AtomicInteger err = new AtomicInteger();//原子递增
    private ConcurrentTask task = null;
    private int numPerThread = 1;//每个线程执行次数

    public ConcurrentTest(int concurrent,int numPerThread,ConcurrentTask task){
        this.task = task;
        this.numPerThread = numPerThread;

        if(task == null){
            System.out.println("task can not null");
            System.exit(1);
        }

        doneSignal = new CountDownLatch(concurrent);//并发数（线程数）
    }

    /**
     * @throws ClassNotFoundException
     */
    public void start(){
        //创建线程，并将所有线程等待在阀门处
        runThread();
        //打开阀门
        startSignal.countDown();//递减锁存器的计数，如果计数到达零，则释放所有等待的线程
        try {
            doneSignal.await();//等待所有线程都执行完毕
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //计算执行时间
        getExeTime();
    }

    /**
     * 初始化所有线程，并在阀门处等待
     */
    private void runThread() {
        long len = doneSignal.getCount();//线程数
        //每个任务的线程数
        for (int i = 0; i < len; i++) {
            new Thread(new MyThreadRun(numPerThread,task) {
                @Override
                void before() {
                    try {
                        startSignal.await();//使当前线程在锁存器倒计数至零之前一直等待
                    } catch (InterruptedException e) {
                        err.getAndIncrement();//相当于err++
                    }
                }

                @Override
                void after() {
                    long end = (System.nanoTime() - startTime);
                    list.add(end);
                }

                @Override
                long getStartTime() {
                    return System.nanoTime();
                }

                @Override
                void finish() {
                    doneSignal.countDown();
                }
            },"ConcurrentTest_" + i).start();
        }

    }

    /**
     * 计算平均响应时间
     */
    private void getExeTime() {
        int size = list.size();
        List<Long> _list = new ArrayList<Long>(size);
        _list.addAll(list);
        Collections.sort(_list);
        long min = _list.get(0);
        long max = _list.get(size-1);
        long sum = 0L;
        for (Long t : _list) {
            sum += t;
        }
        long avg = sum/size;

        System.out.println("===: =======");
        System.out.println("sum: " + Formatter.formatNS(sum));
        System.out.println("min: " + Formatter.formatNS(min));
        System.out.println("max: " + Formatter.formatNS(max));
        System.out.println("avg: " + Formatter.formatNS(avg));
        System.out.println("err: " + err.get());
        System.out.println("===: =======");
    }

    abstract class MyThreadRun implements Runnable{
        int replay;
        ConcurrentTask task;
        public Long startTime;

        MyThreadRun(int replay,ConcurrentTask task){
            this.replay = replay;
            this.task = task;
        }

        @Override
        public void run() {
            do{
                try{
                    before();
                    startTime = getStartTime();
                    task.run();
                    after();
                }catch (Throwable t){
                    t.printStackTrace();
                }
            }while (--replay > 0);

            finish();
        }

        abstract void before();
        abstract void after();
        abstract long getStartTime();
        abstract void finish();
    }
}
