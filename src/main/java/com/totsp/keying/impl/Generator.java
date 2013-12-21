/**
 *    Copyright 2013 Robert Cooper
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.totsp.keying.impl;

import com.totsp.keying.reflect.KeyException;
import com.totsp.keying.reflect.Setter;

import java.util.Arrays;

/**
 *
 */
public class Generator<T> {

    public final Component<T>[] components;
    private final Setter<T> setter;
    private final boolean lowerCase;

    public Generator(Component<T>[] components, Setter<T> setter, boolean lowerCase) {
        this.components = Arrays.copyOf(components, components.length);
        this.setter = setter;
        this.lowerCase = lowerCase;
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
        return lowerCase ? sb.toString().toLowerCase() : sb.toString();
    }

    public void checkDeterministic(){
        for(Component<T> component: components){
            if(component instanceof NonDeterministicComponent){
                throw new KeyException(component.getClass().getCanonicalName() +" isn't a deterministic component.");
            }
        }
    }

    public boolean isDeterministic(){
        for(Component<T> component: components){
            if(component instanceof NonDeterministicComponent){
               return false;
            }
        }
        return true;
    }
}
