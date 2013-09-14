/**
 *    Copyright 2013 Robert Cooper
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.totsp.keying.impl;

import com.totsp.keying.dao.KeyGenerator;
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

    @Test
    public void testKey(){
        TestPropertyBean bean = new TestPropertyBean();
        bean.setFirstName("Robert");
        bean.setLastName("Cooper");
        KeyGenerator.key(bean);
        assertEquals("Cooper,Robert", bean.getId());
    }


}
