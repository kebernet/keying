package com.totsp.keying.impl;

import com.totsp.keying.dao.KeyGenerator;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class UUIDComponentTest {

    public static final String UUID_REGEX="[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";


    @Test
    public void testGetComponent() {
        UUIDComponent comp = new UUIDComponent();
        TestUUIDBean bean = new TestUUIDBean();
        assertTrue(comp.getComponent(bean).matches(UUID_REGEX));
    }

    @Test
    public void testSingleStrategy() {
        TestUUIDBean bean = new TestUUIDBean();
        KeyGenerator.key(bean);
        assertTrue(bean.id.matches(UUID_REGEX));
    }
}