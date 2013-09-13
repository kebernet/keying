package com.totsp.keying.impl;

import com.totsp.keying.definition.KeySegment;
import com.totsp.keying.definition.KeyStrategy;

/**
 *
 */
public class TestBean {

    @KeyStrategy(value={KeySegment.UUID})
    public String id;


}
