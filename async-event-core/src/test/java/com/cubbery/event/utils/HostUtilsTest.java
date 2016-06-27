package com.cubbery.event.utils;

import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class HostUtilsTest {

    @Test
    public void testAboutThisJvm() throws Exception {

    }

//    @Test
    public void testIpAddress() throws Exception {
        assertEquals(HostUtils.getLocalHost(),"10.9.45.12");
    }
}