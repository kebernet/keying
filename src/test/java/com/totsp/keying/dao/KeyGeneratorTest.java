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
package com.totsp.keying.dao;

import com.totsp.keying.impl.TestLowerBean;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: Robert
 * Date: 12/20/13
 * Time: 12:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class KeyGeneratorTest {

    @Test
    public void testCompute() throws Exception {
        TestLowerBean bean = new TestLowerBean();
        bean.setName("ROBERT");
        assertEquals("robert", KeyGenerator.compute(bean));
    }

    @Test
    public void testKey() throws Exception {
        TestLowerBean bean = new TestLowerBean();
        bean.setName("ROBERT");
        KeyGenerator.key(bean);
        assertEquals("robert", bean.getId());
    }
}
