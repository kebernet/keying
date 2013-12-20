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

import com.googlecode.objectify.annotation.Id;
import com.totsp.keying.definition.KeySegment;
import com.totsp.keying.definition.KeyStrategy;

/**
 * Created with IntelliJ IDEA.
 * User: Robert
 * Date: 12/20/13
 * Time: 12:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestLowerBean {

    @Id
    private String id;

    private String name;

    @KeyStrategy(value={KeySegment.PROPERTY}, properties = "name", lowerCase = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
