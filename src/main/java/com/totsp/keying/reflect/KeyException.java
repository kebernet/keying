package com.totsp.keying.reflect;

/**
 * Created with IntelliJ IDEA.
 * User: keber_000
 * Date: 8/29/13
 * Time: 8:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class KeyException  extends RuntimeException {

    public KeyException(String message, Throwable t){
        super(message, t);
    }


    public KeyException(String message){
        super(message);
    }
}
