package com.totsp.keying.impl;

import java.util.UUID;

/**
 *
 */
public class UUIDComponent<T> implements NonDeterministicComponent<T> {

    @Override
    public String getComponent(T object) {
        return UUID.randomUUID().toString();
    }
}
