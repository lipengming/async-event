/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event;

import org.testng.annotations.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class SemaphoreTest {

    @Test
    public void testSem() {
        int size = 3;
        Semaphore semaphore = new Semaphore(size);
        assertTrue(semaphore.tryAcquire(3));
        //1
        semaphore.release();
        try {
            assertTrue(!semaphore.tryAcquire(2,1, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            fail();
        }
        semaphore.release();
        try {
            assertTrue(semaphore.tryAcquire(2,1, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            fail();
        }
    }
}
