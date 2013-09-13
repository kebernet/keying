package com.totsp.keying.impl;

import com.totsp.keying.definition.KeySegment;
import com.totsp.keying.definition.KeyStrategy;

/**
 *
 */
public class TestPropertyBean {

    private String id;
    private String firstName;
    private String lastName;
    private int age = 39;

    @KeyStrategy(value={KeySegment.PROPERTY, KeySegment.PROPERTY},
            properties={"lastName", "firstName"}, separator=",")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void getBadProperty(){
        throw new UnsupportedOperationException();
    }
}
