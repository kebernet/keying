package com.totsp.keying.impl;

import com.totsp.keying.reflect.Setter;

/**
 *
 */
public class Generator<T> {

    public final Component<T>[] components;
    private final Setter<T> setter;

    public Generator(Component<T>[] components, Setter<T> setter) {
        this.components = components;
        this.setter = setter;
    }

    public void key(T object){
        setter.setId(object, compute(object));
    }

    public boolean keyed(T object){
        return setter.keyed(object);
    }
    public String compute(T object){
        StringBuilder sb = new StringBuilder();
        for(int i =0; i < components.length; i++){
            if(i != 0){
                sb = sb.append(setter.strategy.separator());
            }
            sb = sb.append(components[i].getComponent(object));
        }
        return sb.toString();
    }
}
