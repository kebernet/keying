package com.totsp.keying.impl;

import com.totsp.keying.definition.KeySegment;
import com.totsp.keying.definition.KeyStrategy;

/**
 * Created with IntelliJ IDEA.
 * User: keber_000
 * Date: 8/29/13
 * Time: 9:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class TimeInverseBean {

    private String id;

    @KeyStrategy(value={KeySegment.INVERSE_TIME})
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
