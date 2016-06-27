package com.cubbery.event.finder;

import com.cubbery.event.ISubscribe;
import com.cubbery.event.Subscriber;
import com.cubbery.event.event.SimpleEvent;
import com.cubbery.event.handler.EventHandler;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class AnnotatedHandlerFinderTest {

    @Test
    public void testFindAllHandlers() throws Exception {
        AnnoSub listener = new AnnoSub();
        Map<Class<? >, Set<EventHandler>> map = new AnnotatedHandlerFinder().findAllHandlers(listener);
        assertTrue(map.containsKey(SimpleEvent.class));

        Collection<EventHandler> handlers = map.get(SimpleEvent.class);
        assertTrue(handlers.size() == 1);
        Iterator<EventHandler> iterator = handlers.iterator();
        assertTrue(iterator.hasNext());
        EventHandler handler = iterator.next();
        try {
            assertTrue(handler.equals(new EventHandler(listener,listener.getClass().getMethod(ISubscribe.methodName,SimpleEvent.class))));
        } catch (NoSuchMethodException e) {
            fail();
        }
    }
}

class AnnoSub {

    @Subscriber
    public void handler(SimpleEvent event) {

    }
}