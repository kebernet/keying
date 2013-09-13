package com.totsp.keying.impl;

import com.totsp.keying.definition.KeySegment;
import com.totsp.keying.definition.KeyStrategy;

/**
 *
 */
public class TestInverseTimeBean {

    private String id;

    @KeyStrategy(value={KeySegment.INVERSE_TIME, KeySegment.UUID})
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
