package com.totsp.keying.impl;

import com.totsp.keying.dao.KeyGenerator;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class TimeComponentTest {
    @Test
    public void testGetComponent() throws Exception {
        TimeComponent<TestTimeBean> timeComponent = new TimeComponent<TestTimeBean>(false);
        long start = System.currentTimeMillis()-1;
        TestTimeBean bean = new TestTimeBean();
        String value = timeComponent.getComponent(bean);
        long end = System.currentTimeMillis();
        String starStr = "00000000".substring(Long.toHexString(start).length() - 8) + Long.toHexString(start);
        String endStr = "00000000".substring(Long.toHexString(end).length() - 8) + Long.toHexString(end);
        assertTrue(value.compareTo(starStr) >= 0);
        assertTrue(value.compareTo(endStr) <= 0);
        System.out.println("Time code "+value);
    }

    @Test
    public void testSingleStrategy() {
        TestTimeBean bean = new TestTimeBean();
        long start = System.currentTimeMillis() -1;
        KeyGenerator.key(bean);
        long end = System.currentTimeMillis();
        //You can't just use a time, so we have to pull out the time component.
        System.out.println("Time code "+bean.id.substring(0, bean.id.indexOf(":")));
        String starStr = "00000000".substring(Long.toHexString(start).length() - 8) + Long.toHexString(start);
        String endStr = "00000000".substring(Long.toHexString(end).length() - 8) + Long.toHexString(end);
        assertTrue(bean.id.substring(0, bean.id.indexOf(":")).compareTo(starStr) >= 0);
        assertTrue(bean.id.substring(0, bean.id.indexOf(":")).compareTo(endStr) <= 0);

    }

    @Test
    public void testGetInverseComponent() throws Exception {
        TimeComponent<TestInverseTimeBean> timeComponent = new TimeComponent<TestInverseTimeBean>(true);
        long start = Long.MAX_VALUE - System.currentTimeMillis() -1;
        TestInverseTimeBean bean = new TestInverseTimeBean();
        String value = timeComponent.getComponent(bean);
        long end = Long.MAX_VALUE - System.currentTimeMillis();
        System.out.println("Time code "+value);
        String starStr = "00000000".substring(Long.toHexString(start).length() - 8) + Long.toHexString(start);
        String endStr = "00000000".substring(Long.toHexString(end).length() - 8) + Long.toHexString(end);
        System.out.println(starStr);
        System.out.println(endStr);
        assertTrue(value.compareTo(starStr) >= 0);
        assertTrue(value.compareTo(endStr) <= 0);

    }

    @Test
    public void testGetInverseStrategy() throws Exception {
        TimeComponent<TestInverseTimeBean> timeComponent = new TimeComponent<TestInverseTimeBean>(true);
        long start = Long.MAX_VALUE-  System.currentTimeMillis() - 100;
        TestInverseTimeBean bean = new TestInverseTimeBean();
        KeyGenerator.key(bean);
        long end = Long.MAX_VALUE - System.currentTimeMillis() + 100;
        System.out.println("Time code "+bean.getId());
        String starStr = "00000000".substring(Long.toHexString(start).length() - 8) + Long.toHexString(start);
        String endStr = "00000000".substring(Long.toHexString(end).length() - 8) + Long.toHexString(end);
        assertTrue(bean.getId().substring(0, bean.getId().indexOf(":")).compareTo(starStr) >= 0);
        assertTrue(bean.getId().substring(0, bean.getId().indexOf(":")).compareTo(endStr) <= 0);

    }
}
