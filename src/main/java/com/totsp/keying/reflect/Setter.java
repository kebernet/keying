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
package com.totsp.keying.reflect;

import com.totsp.keying.definition.KeyStrategy;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

/**
 *
 */
public class Setter<T> {
    private final Mutator<T> mutator;
    public final KeyStrategy strategy;

    public Setter(Class<T> type){
        boolean found = false;
        Mutator<T> mutator = null;
        KeyStrategy strategy = null;
        for(Field field : type.getDeclaredFields()){
            if((strategy = field.getAnnotation(KeyStrategy.class)) != null){
                found = true;
                mutator = new FieldMutator<T>(field);
                break;
            }
        }
        if(!found){
            try {
                for(PropertyDescriptor pd : Introspector.getBeanInfo(type).getPropertyDescriptors()){
                    if(pd.getReadMethod() != null && (strategy = pd.getReadMethod().getAnnotation(KeyStrategy.class)) != null && pd.getWriteMethod() != null){
                        mutator = new PropertyMutator<T>(pd);
                    } else if(pd.getWriteMethod() != null && (strategy = pd.getWriteMethod().getAnnotation(KeyStrategy.class)) != null){
                        mutator = new PropertyMutator<T>(pd);

                    }
                }
            } catch (IntrospectionException e) {
                throw new KeyException("Unabled to introspect "+type.getCanonicalName(), e);
            }
        }
        if(mutator == null || strategy == null){
            throw new KeyException("Failed to find a KeyStrategy annotation on "+type.getCanonicalName());
        }
        this.mutator = mutator;
        this.strategy = strategy;
    }

    public void setId(T target, String id){
        this.mutator.set(target, id);
    }

    public boolean keyed(T target){
        return this.mutator.keyed(target);
    }

    private static interface Mutator<T> {
        void set(T target, String value);
        boolean keyed(T target);
    }

    private static class FieldMutator<T> implements Mutator<T>{
        private final Field field;

        private FieldMutator(Field field) {
            this.field = field;
        }

        public void set(T target, String value){
            this.field.setAccessible(true);
            try {
                this.field.set(target, value);
            } catch (IllegalAccessException e) {
                throw new KeyException("Unable to key on field "+this.field.getName(), e);
            }
        }

        @Override
        public boolean keyed(T target) {
            try {
                return field.get(target) != null;
            } catch (IllegalAccessException e) {
                throw new KeyException("Couldn't read field "+field.getName()+" on "+field.getDeclaringClass().getCanonicalName());
            }
        }
    }

    private static class PropertyMutator<T> implements Mutator<T> {
        private final PropertyDescriptor prop;

        private PropertyMutator(PropertyDescriptor prop) {
            this.prop = prop;
        }

        @Override
        public void set(T target, String value) {
            prop.getWriteMethod().setAccessible(true);
            try {
                this.prop.getWriteMethod().invoke(target, value);
            } catch (Exception e) {
                throw new KeyException("Unable set key on property"+prop.getName(), e);
            }
        }

        @Override
        public boolean keyed(T target) {
            try {
                return prop.getReadMethod().invoke(target) != null;
            } catch (Exception e) {
                throw new KeyException("Unabled to read property "+prop.getName()+" on "+prop.getWriteMethod().getDeclaringClass().getCanonicalName());
            }
        }
    }
}
