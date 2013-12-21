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
package com.totsp.keying.dao;

import com.google.common.base.Function;
import com.totsp.keying.definition.KeySegment;
import com.totsp.keying.impl.Component;
import com.totsp.keying.impl.Generator;
import com.totsp.keying.impl.PropertyComponent;
import com.totsp.keying.impl.TimeComponent;
import com.totsp.keying.impl.UUIDComponent;
import com.totsp.keying.reflect.KeyException;
import com.totsp.keying.reflect.Reader;
import com.totsp.keying.reflect.Setter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A class with static methods for computing/applying KeyStrategies to objects.
 */
public class KeyGenerator {

    private static final Map<Class, Generator> GENERATORS = new ConcurrentHashMap<>();

    /**
     * A function implementation to key a value.
     */
    public static final Function<? extends Serializable,? extends Serializable> KEYING_FUNCTION = new Function<Serializable,Serializable>() {
        @Nullable
        @Override
        public Serializable apply(@Nullable Serializable o) {
            return o == null ? null : key(o);
        }
    };

    /**
     * Gets or constructs a Generator implementation for the type of o
     * @param o Object to inspect.
     * @param <T> Type to lookup.
     * @return a Generator implementation for type T.
     */
    @SuppressWarnings("unchecked")
    private static <T> Generator<T> get(T o){
        Generator<T> generator = GENERATORS.get(o.getClass());
        if(generator == null){
            Setter<T> t = new Setter<>((Class<T>) o.getClass());
            ArrayList<Component<T>> components = new ArrayList<>(t.strategy.value().length);
            int propertyIndex = 0;
            Class<T> type = (Class<T>) o.getClass();
            for(KeySegment segment : t.strategy.value()){
                switch(segment){
                    case PROPERTY:
                        if(t.strategy.properties().length < propertyIndex){
                            throw new KeyException("Expected "+(propertyIndex + 1)+" properties but found only "+ Arrays.asList(t.strategy.properties()));
                        }
                        components.add(new PropertyComponent<>(new Reader<>(type, t.strategy.properties()[propertyIndex])));
                        propertyIndex++;
                        if(t.strategy.properties().length < propertyIndex){
                            throw new KeyException("Expected "+ (propertyIndex +1) +" properties but found an extra "+(t.strategy.properties().length -1 -propertyIndex)+" "+ Arrays.asList(t.strategy.properties()));
                        }
                        break;
                    case UUID:
                        components.add(new UUIDComponent<T>());
                        break;
                    case TIME:
                        components.add(new TimeComponent<T>(false));
                        if(t.strategy.value().length == 1){
                            throw new KeyException(o.getClass().getCanonicalName()+" cannot a a key value of only a time.");
                        }
                        break;
                    case INVERSE_TIME:
                        components.add(new TimeComponent<T>(true));
                        if(t.strategy.value().length == 1){
                            throw new KeyException(o.getClass().getCanonicalName()+" cannot a a key value of only a time.");
                        }
                        break;
                    default:
                        throw new KeyException("Unknown segment type "+segment);
                }
            }
            generator = new Generator<>(components.toArray(new Component[components.size()]), t, t.strategy.lowerCase());
            GENERATORS.put(o.getClass(), generator);
        }
        return generator;
    }

    /**
     * Appplies a key to the object if unkeyed and returns it.
     * @param o Object to key
     * @param <T> Type being keyed
     * @return the object passed in mutated with a key where needed.
     */
    public static <T> T key(@Nonnull T o){
        checkNotNull(o);
        Generator<T> gen = get(o);
        if(!gen.keyed(o)){
            get(o).key(o);
        }
        return o;
    }

    /**
     * Determines if the object either has a key, or has a deterministic key.
     * This indicates that a save is an idempotent operation.
     * @param o Object to check
     * @param <T> Type to check.
     * @return That it's key is determinable.
     */
    public static <T> boolean hasKey(@Nonnull T o){
        checkNotNull(o);
        Generator<T> gen = get(o);
        return gen.keyed(o) || gen.isDeterministic();
    }

    /**
     * The same as hasKey() but for an Iterable.
     * @param values Objects to check
     * @param <T> Type to check
     * @return if all the objects have determinable keys.
     */
    public static <T> boolean haveKeys(@Nonnull Iterable<T> values){
        checkNotNull(values);
        for(T t: values){
            if(!hasKey(t)){
                return false;
            }
        }
        return true;
    }

    /**
     * Computes the key for an object (useful for queries) if the object has a
     * deterministic key.
     * @param o Object to key
     * @param <T> Type to key
     * @return Computed key
     * @throws KeyException if the object doesn't have a deterministic key.
     */
    public static <T> String compute(T o){
       Generator<T> generator = get(o);
       generator.checkDeterministic();
       return generator.compute(o);
    }
}
