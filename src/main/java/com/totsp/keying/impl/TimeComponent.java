package com.totsp.keying.impl;

/**
 *
 */
public class TimeComponent<T> implements NonDeterministicComponent<T> {

    private final boolean inverse;

    public TimeComponent(boolean inverse) {
        this.inverse = inverse;
    }

    @Override
    public String getComponent(T object) {
        String s;
        if(inverse){
            s = Long.toHexString(Long.MAX_VALUE - System.currentTimeMillis());
        } else{
            s = Long.toHexString(System.currentTimeMillis());
        }
        return "00000000".substring(s.length() - 8) + s;
    }
}
