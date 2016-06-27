package com.cubbery.event;

import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class EventBusSpringTest {
    @Test
    public void ipStrToListTest_single() {
        String ipStr = "127.0.0.1";
        List<String> ipList = EventBusSpring.ipStrToList(ipStr);
        assertEquals(ipList.get(0),ipStr);
    }

    @Test
    public void ipStrToListTest_mutip() {
        String ipStr = "127.0.0.1|127.0.0.2|127.0.0.4|10.9.45.12";
        List<String> ipList = EventBusSpring.ipStrToList(ipStr);
        assertEquals(ipList.get(0),"127.0.0.1");
        assertEquals(ipList.get(1),"127.0.0.2");
        assertEquals(ipList.get(2),"127.0.0.4");
        assertEquals(ipList.get(3),"10.9.45.12");
    }

    @Test
    public void ipStrToListTest_unvali() {
        String ipStr = "127.0.0|0.0.0.2|127.0.0.4";
        List<String> ipList = EventBusSpring.ipStrToList(ipStr);
        //assertEquals(ipList.get(0),"127.0.0");
        assertEquals(ipList.get(0),"0.0.0.2");
        assertEquals(ipList.get(1),"127.0.0.4");
    }
}