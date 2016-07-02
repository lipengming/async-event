/**
 * Copyright (c) 2016, www.jd.com. All rights reserved.
 */
package freamwork.test;

import freamwork.simple.core.ConcurrentTask;
import freamwork.simple.core.ConcurrentTest;
import freamwork.simple.task.Task;

import static com.cubbery.event.utils.security.DesCrypter.encrypt;
import static freamwork.simple.common.UniqNumUtil.getUniqNum;

/**
 * <b>类描述</b>：   <br>
 * <b>创建人</b>：   <a href="mailto:cubber.zh@gmail.com">百墨</a> <br>
 * <b>创建时间</b>： 2016/4/18 - 15:25  <br>
 *
 * @version 1.0.0   <br>
 */
public class EncryptRunner {
    public static void main(String[] args) {
        /*
        ===: =======
        sum: 4,274,342,478
        min: 23,399
        max: 420,821,638
        avg: 4,274,342
        err: 0
        ===: =======
        */
        new ConcurrentTest(10,100,new EncryptTest(null)).start();//10个并发,100次重复
        /*
        ===: =======
        sum: 4,270,944,970
        min: 10,263
        max: 399,123,212
        avg: 427,094
        err: 0
        ===: =======
        */
        new ConcurrentTest(10,1000,new EncryptTest(null)).start();//10个并发,1000次重复
        //结论：单线程数一定的时候，随着重复次数的增加，平均耗时显著提升
    }
}

class EncryptTest extends Task implements ConcurrentTask {

    public EncryptTest(Object o) {
        super(o);
    }

    @Override
    public void run() {
        encrypt(getUniqNum(10));
    }
}
