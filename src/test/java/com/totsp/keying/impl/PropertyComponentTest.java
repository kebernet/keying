package com.totsp.keying.impl;

import com.totsp.keying.reflect.KeyException;
import com.totsp.keying.reflect.Reader;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class PropertyComponentTest {

    @Test
    public void testPropertyRead(){
        TestPropertyBean bean = new TestPropertyBean();
        bean.setFirstName("Robert");
        bean.setLastName("Cooper");
        Reader<TestPropertyBean> reader = new Reader<TestPropertyBean>(TestPropertyBean.class, "firstName");
        String read = reader.read(bean);
        assertEquals("Robert", read);
        PropertyComponent<TestPropertyBean> component = new PropertyComponent<TestPropertyBean>(reader);
        read = component.getComponent(bean);
        assertEquals("Robert", read);

    }

    @Test
    public void testFieldRead(){
        TestPropertyBean bean = new TestPropertyBean();
        bean.setFirstName("Robert");
        bean.setLastName("Cooper");
        Reader<TestPropertyBean> reader = new Reader<TestPropertyBean>(TestPropertyBean.class, "age");
        String read = reader.read(bean);
        assertEquals("39", read);
        PropertyComponent<TestPropertyBean> component = new PropertyComponent<TestPropertyBean>(reader);
        read = component.getComponent(bean);
        assertEquals("39", read);
    }

    @Test(expected = KeyException.class)
    public void testMissing(){
        TestPropertyBean bean = new TestPropertyBean();
        bean.setFirstName("Robert");
        bean.setLastName("Cooper");
        Reader<TestPropertyBean> reader = new Reader<TestPropertyBean>(TestPropertyBean.class, "pet");
    }

    @Test(expected = KeyException.class)
    public void testBad(){
        TestPropertyBean bean = new TestPropertyBean();
        bean.setFirstName("Robert");
        bean.setLastName("Cooper");
        Reader<TestPropertyBean> reader = new Reader<TestPropertyBean>(TestPropertyBean.class, "badProperty");
        String read = reader.read(bean);
    }


}
