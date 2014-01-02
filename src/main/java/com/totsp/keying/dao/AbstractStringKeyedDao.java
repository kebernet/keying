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
import com.googlecode.objectify.Key;
import com.googlecode.objectify.LoadResult;
import com.googlecode.objectify.NotFoundException;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;


/**
 * An abstract DAO class you can extend to work with keyed classes.
 */
public class AbstractStringKeyedDao<T extends Serializable> extends AbstractKeyedDao<T, String> implements StringKeyedDao<T> {

    /**
     * The factory must be injected by the implementing class
     */
    public AbstractStringKeyedDao(@Nonnull Class<T> clazz) {
        super(clazz);
    }

    /**
     * get object of type clazz that is stored in the datastore under the param id clazz must be of a type registered
     * with the injected objectify factory
     *
     * @param id Name/ID field to find.
     * @return the object of type clazz that matches on the id
     * @throws NotFoundException thrown if no entity object could be found
     */
    @Override
    public T findById(@Nonnull final String id) throws NotFoundException {
        checkNotNull(id);
        beforeOperation();
        try {
            return this.retryHandler.execute(new Callable<T>() {
                @Override
                public T call() throws Exception {
                    return preReturnHook.apply(
                            ofy().load().key(Key.create(clazz, id)).now()
                    );
                }
            });
        } catch(NotFoundException e){
            throw e;
        } catch(Exception e){
            throw new RuntimeException(e);
        } finally {
            afterOperation();
        }
    }

    /**
     * save or update entity in datastore entity must be of a type registered with the injected objectify factory
     *
     * @param entity must not be null
     * @return the Key of the saved object
     */
    @Override
    public <R extends T> Key<R> save(@Nonnull final R entity) {
        checkNotNull(entity);
        beforeOperation();
        try {
            //Only retry saves if the entity has a key or the key is
            //deterministic to try and avoid dupes.
            if(KeyGenerator.hasKey(entity)){
                return retryHandler.executeRuntime(new Callable<Key<R>>() {
                    @Override
                    public Key<R> call() throws Exception {
                        return saveImpl(entity);
                    }
                });
            } else {
                return saveImpl(entity);
            }
        } finally {
            afterOperation();
        }
    }

    @SuppressWarnings("unchecked")
    private <R extends T> Key<R> saveImpl(R entity){
        R value = KeyGenerator.key(entity);
        value = (R) preSaveHook.apply(value);
        return  ofy().save().entity(value).now();
    }

    /**
     * save or update entities in datastore entities must be of a type registered with the injected objectify factory
     *
     * @param entities Iterable of entities to save.
     * @return a map of the saved entities mapped to their datastore keys
     */
    @Override
    public <R extends T> Map<Key<R>, R> save(@Nonnull final Iterable<R> entities) {
        checkNotNull(entities);
        beforeOperation();
        try{
            //Only retry saves if the entity has a key or the key is
            //deterministic to try and avoid dupes.
            if(KeyGenerator.haveKeys(entities)){
                return retryHandler.executeRuntime(new Callable<Map<Key<R>, R>>() {
                    @Override
                    public Map<Key<R>, R> call() throws Exception {
                        return saveAllImpl(entities);
                    }
                });
            } else {
                return saveAllImpl(entities);
            }
        } finally {
            afterOperation();
        }
    }

    @SuppressWarnings("unchecked")
    private <R extends T> Map<Key<R>, R> saveAllImpl(final Iterable<R> entities){
        @SuppressWarnings("unchecked")
        Iterable<R> values = transform(entities, (Function<? super R,? extends R>) KeyGenerator.KEYING_FUNCTION);
        values = (Iterable<R>) transform(values, preSaveHook);
        return ofy().save().entities(values).now();
    }



    /**
     * get object of type clazz that is stored in the datastore under the param id clazz must be of a type registered
     * with the injected objectify factory
     *
     * @param id Name/ID field of the entities to find.
     * @return the object of type clazz that matches on the id
     * @throws NotFoundException thrown if no entity object could be found
     */
    @Override
    public LoadResult<T> findAsync(@Nonnull String id) {
       checkNotNull(id);
       beforeOperation();
       try {
        return ofy().load().key(Key.create(clazz, id));
       } finally {
           afterOperation();
       }
    }

    /**
     * /**
     * get entities from datastore that match against the passed in collection of ids
     *
     * @param ids the set of String or Long ids matching against those entities to be retrieved from the datastore
     * @return all entities that match on the collection of ids. no error is thrown for entities not found in datastore.
     */
    @Override
    public Map<String, T> findByIds(@Nonnull final Iterable <String> ids) {
        checkNotNull(ids);
        beforeOperation();
        try {
        return this.retryHandler.executeRuntime(new Callable<Map<String, T>>() {
                @Override
                public Map<String, T> call() throws Exception {
                    Map<String, T> result =  ofy().load().type(clazz).ids(ids);
                    applyPreReturnHook(result.values());
                    return result;
                }
            });
        } finally {
            afterOperation();
        }
    }


    /**
     * get entities from datastore that match against the passed in collection of keys
     *
     * @param keys the set of keys matching against those entities to be retrieved from the datastore
     * @return all entities that match on the collection of keys. no error is thrown for entities not found in
     *         datastore.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <R extends T> Map<Key<R>, R> findByKeys(@Nonnull final Iterable<Key<R>> keys) {
        checkNotNull(keys);
        beforeOperation();
        try {
            return this.retryHandler.executeRuntime(new Callable<Map<Key<R>, R>>() {
                @Override
                public Map<Key<R>, R> call() throws Exception {
                    Map<Key<R>, R> result = ofy().load().keys(keys);
                    applyPreReturnHook((Iterable<T>) result.values());
                    return result;
                }
            });
        } finally {
            afterOperation();
        }
    }

    /**
     * delete object of type clazz that is stored in the datastore under the param id clazz must be of a type registered
     * with the injected objectify factory
     *
     * @param id Name/ID of the entity to delete
     */
    @Override
    public void delete(@Nonnull final String id) {
        checkNotNull(id);
        beforeOperation();
        try {
            this.retryHandler.executeRuntime(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    ofy().delete().type(clazz).id(id).now();
                    return Void.TYPE;
                }
            });
        } finally {
            afterOperation();
        }
    }

    /**
     * delete entities from datastore that match against the passed in collection entities must be of a type registered
     * with the injected objectify factory
     *
     * @param entities Iterable of entities to delete.
     */
    @Override
    public void deleteAll(@Nonnull final Iterable<T> entities) {
        checkNotNull(entities);
        beforeOperation();
        try {
            this.retryHandler.executeRuntime(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    @SuppressWarnings("unchecked")
                    Iterable<T> finalEntities = transform(entities, (Function<? super T,? extends T>) KeyGenerator.KEYING_FUNCTION);
                    finalEntities = transform(finalEntities, preSaveHook);
                    return ofy().delete().entities(finalEntities).now();
                }
            });
        } finally {
            afterOperation();
        }
    }

    /**
     * delete entities from datastore that match against the passed in collection keys must be of a type string with the
     * injected objectify factory
     * <p/>
     * @param stringKeys the keys to delete
     */
    @Override
    public void deleteEntitiesByKeys(@Nonnull Iterable<String> stringKeys) {
        checkNotNull(stringKeys);
        beforeOperation();
        try {
            ofy().delete().type(clazz).ids(stringKeys).now();
        } finally {
            afterOperation();
        }
    }
}
