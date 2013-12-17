package com.totsp.keying.impl;

import com.totsp.keying.reflect.Reader;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertFalse;

/**
 * Created with IntelliJ IDEA.
 * User: BradGross
 * Date: 12/17/13
 * Time: 3:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestReader {

    private class ClassToBeRead {
        String fieldToRead = "README";
    }

    @Test
    public void testReaderDefaultsToMaintainCase() {
        Reader reader = new Reader(ClassToBeRead.class, "fieldToRead");
        reader.convertToLowerCase = Boolean.FALSE;
        String readField = reader.read(new ClassToBeRead());
        assertFalse(readField.equals("readme"));
        assertTrue(readField.equals("README"));
    }

    @Test
    public void testReadeConvertsToLowerCase() {
        Reader reader = new Reader(ClassToBeRead.class, "fieldToRead");
        reader.convertToLowerCase = Boolean.TRUE;
        String readField = reader.read(new ClassToBeRead());
        assertTrue(readField.equals("readme"));
        assertFalse(readField.equals("README"));
    }


}
