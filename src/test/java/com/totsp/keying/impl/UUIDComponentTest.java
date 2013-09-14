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