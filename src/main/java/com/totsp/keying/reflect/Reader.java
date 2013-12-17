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

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class Reader<T> {
    private static final Logger LOGGER = Logger.getLogger(Reader.class.getCanonicalName());
    private final Accessor<T> accessor;
    public static Boolean convertToLowerCase = false;

    public Reader(Class<T> type, String name){
        Accessor<T> accessor = null;
        name = name.trim();
        try {
            for(PropertyDescriptor pd : Introspector.getBeanInfo(type).getPropertyDescriptors()){
                if(pd.getName().equals(name) && pd.getReadMethod() != null){
                    pd.getReadMethod().setAccessible(true);
                    accessor = new PropertyAccessor<T>(pd, type);
                    break;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to introspect type: "+type.getCanonicalName(), e);
        }
        if(accessor == null){
            for(Field field : type.getDeclaredFields()){
                if(field.getName().equals(name)){
                    accessor = new FieldAccesor<T>(field);
                    break;
                }
            }
        }
        if(accessor == null){
            throw new KeyException("Failed to find a readable field or property for "+name+" on "+type.getCanonicalName());
        }
        this.accessor = accessor;
    }

    public String read(T object){
        return this.accessor.read(object);
    }

    private static interface Accessor<T> {
        String read(T target);
    }

    private static class FieldAccesor<T> implements Accessor<T> {

        private final Field field;

        private FieldAccesor(Field field) {
            field.setAccessible(true);
            this.field = field;
        }

        @Override
        public String read(T target) {
            try {
                if(convertToLowerCase){
                    return new StringBuilder().append(this.field.get(target)).toString().toLowerCase();
                } else {
                    return new StringBuilder().append(this.field.get(target)).toString();
                }
            } catch (IllegalAccessException e) {
                throw new KeyException("Failed to read from "+field.getName()+" on "+target, e);
            }
        }
    }

    private static class PropertyAccessor<T> implements Accessor<T>{
        private final PropertyDescriptor descriptor;
        private final Class<T> type;

        private PropertyAccessor(PropertyDescriptor descriptor, Class<T> type) {
            this.type = type;
            if(descriptor.getReadMethod() == null){
                throw new KeyException("Cannot write to property "+descriptor.getName()+" on type "+type.getCanonicalName());
            }
            descriptor.getWriteMethod().setAccessible(true);
            this.descriptor = descriptor;
        }

        @Override
        public String read(T target) {
            try {
                return new StringBuilder().append(descriptor.getReadMethod().invoke(target)).toString();
            } catch (Exception e) {
                throw new KeyException("Could not read property "+descriptor.getName()+" on "+target, e);
            }
        }
    }


}
