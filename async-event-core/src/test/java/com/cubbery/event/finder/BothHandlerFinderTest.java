package com.cubbery.event.finder;

import com.cubbery.event.ISubscribe;
import com.cubbery.event.Subscriber;
import com.cubbery.event.handler.EventHandler;
import com.cubbery.event.event.SimpleEvent;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.*;

public class BothHandlerFinderTest {

    @Test
    public void testFindAllHandlers_listener() throws Exception {
        Sub listener = new Sub();
        Map<Class<? >, Set<EventHandler>> map = new BothHandlerFinder().findAllHandlers(listener);
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

    @Test
    public void testFindAllHandlers_anno() throws Exception {
        AnnoSub annoSub = new AnnoSub();
        Map<Class<? >, Set<EventHandler>> map = new BothHandlerFinder().findAllHandlers(annoSub);
        assertTrue(map.containsKey(SimpleEvent.class));

        Collection<EventHandler> handlers = map.get(SimpleEvent.class);
        assertTrue(handlers.size() == 1);
        Iterator<EventHandler> iterator = handlers.iterator();
        assertTrue(iterator.hasNext());
        EventHandler handler = iterator.next();
        try {
            assertTrue(handler.equals(new EventHandler(annoSub,annoSub.getClass().getMethod(ISubscribe.methodName,SimpleEvent.class))));
        } catch (NoSuchMethodException e) {
            fail();
        }
    }

    @Test
    public void testFindAllHandlers_both() throws Exception {
        BothSub bothSub = new BothSub();
        Map<Class<? >, Set<EventHandler>> map = new BothHandlerFinder().findAllHandlers(bothSub);
        assertTrue(map.containsKey(SimpleEvent.class));

        Collection<EventHandler> handlers = map.get(SimpleEvent.class);
        assertTrue(handlers.size() == 2);
        assertTrue(map.get(SimpleEvent.class).contains(new EventHandler(bothSub,bothSub.getClass().getMethod(ISubscribe.methodName,SimpleEvent.class))));

        Method method = null;
        for(Method m : BothSub.class.getMethods()) {
            if(m.isAnnotationPresent(Subscriber.class)) {
                method = m;
                break;
            }
        }
        if(method != null) {
            assertTrue(map.get(SimpleEvent.class).contains(new EventHandler(bothSub, method)));
        } else {
            fail();
        }
    }

    @Test
    public void testFindAllHandlers_both2() throws NoSuchMethodException {
        BothSub2 bothSub = new BothSub2();
        Map<Class<? >, Set<EventHandler>> map = new BothHandlerFinder().findAllHandlers(bothSub);
        assertTrue(map.containsKey(SimpleEvent.class));

        Collection<EventHandler> handlers = map.get(SimpleEvent.class);
        assertTrue(handlers.size() == 1);
        assertTrue(map.get(SimpleEvent.class).contains(new EventHandler(bothSub,bothSub.getClass().getMethod(ISubscribe.methodName,SimpleEvent.class))));
    }
}

class BothSub implements ISubscribe<SimpleEvent> {

    @Override
    public void handler(SimpleEvent event) {

    }

    @Subscriber
    public void handler0(SimpleEvent event) {

    }
}

class BothSub2 implements ISubscribe<SimpleEvent> {

    @Subscriber
    @Override
    public void handler(SimpleEvent event) {

    }
}