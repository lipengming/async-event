package com.cubbery.event.event;

import com.cubbery.event.handler.EventHandler;
import com.google.gson.Gson;
import com.cubbery.event.event.impl.EncryptEventCreator;
import com.cubbery.event.utils.security.DesCrypter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.testng.Assert.*;

public class SimpleEventTest {

    Sub sub;
    Method method;
    Person person;

    @BeforeClass
    public void before() {
        person = new Person();person.setAge(111);person.setName("cubbery");
        sub = new Sub();
        try {
            method = Sub.class.getMethod("handler",Person.class);
        } catch (NoSuchMethodException e) {
            fail();
        }
    }

    @Test
    public void testCreate() throws Exception {
        SimpleEvent simple = new EncryptEventCreator().create(person,new EventHandler(sub,method));

        assertEquals(simple.getData(), DesCrypter.encrypt(new Gson().toJson(person)));
        assertEquals(simple.getExpression(),sub.getClass().getCanonicalName() + "#" + method.getName());
        assertEquals(simple.getType(),Person.class.getCanonicalName());

    }

    @Test
    public void testReCreate() throws Exception {
        SimpleEvent simple = new SimpleEvent();
        simple.setData(DesCrypter.encrypt(new Gson().toJson(person)));
        Object obj  = new EncryptEventCreator().reCreate(simple, Person.class);
        assertTrue(obj instanceof Person);
        Person p = (Person) obj;
        assertEquals(p.getAge(),person.getAge());
        assertEquals(p.getName(),person.getName());
    }

    @Test
    public void testReCreate_Empty(){
        SimpleEvent simple = new SimpleEvent();
        simple.setData(DesCrypter.encrypt(new Gson().toJson(new Person())));
        Object obj  = new EncryptEventCreator().reCreate(simple, Person.class);
        assertTrue(obj != null);
        assertTrue(obj instanceof Person);
    }

    @Test
    public void testReCreate_NULL() throws Exception {
        SimpleEvent simple = new SimpleEvent();
        simple.setData(null);
        Object obj  = new EncryptEventCreator().reCreate(simple,Person.class);
        assertTrue(obj != null);
        assertTrue(obj instanceof Person);
    }

}