package com.totsp.keying.impl;

import com.totsp.keying.definition.KeySegment;
import com.totsp.keying.definition.KeyStrategy;

/**
 *
 */
public class TestTimeBean {

    @KeyStrategy(value={KeySegment.TIME, KeySegment.UUID})
    public String id;
}
