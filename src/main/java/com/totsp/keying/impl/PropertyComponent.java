package com.totsp.keying.impl;

import com.totsp.keying.reflect.Reader;

/**
 *
 */
public class PropertyComponent<T> implements Component<T> {
    private final Reader<T> reader;

    public PropertyComponent(Reader<T> reader) {
        this.reader = reader;
    }

    @Override
    public String getComponent(T object) {
        return this.reader.read(object);
    }
}
