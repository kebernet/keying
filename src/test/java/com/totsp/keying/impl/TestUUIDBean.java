package com.totsp.keying.impl;

import com.totsp.keying.definition.KeySegment;
import com.totsp.keying.definition.KeyStrategy;

/**
 *
 */
public class TestUUIDBean {

    @KeyStrategy(KeySegment.UUID)
    public String id;


}
