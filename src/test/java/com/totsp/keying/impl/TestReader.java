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
