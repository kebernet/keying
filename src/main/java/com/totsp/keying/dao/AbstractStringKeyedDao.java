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

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.memcache.MemcacheServiceException;
import com.google.apphosting.api.ApiProxy;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.LoadResult;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.cmd.Query;
import com.totsp.keying.reflect.Reader;
import com.totsp.keying.util.RetryHandler;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * An abstract DAO class you can extend to work with keyed classes.
 */
public class AbstractStringKeyedDao<T> implements StringKeyedDao<T> {
    private final Function<T, T> KEY = new Function<T, T>() {
        @Override
        public T apply(@javax.annotation.Nullable T t) {
            return KeyGenerator.key(t);
        }
    };
    private final Class<T> clazz;
    private static int ERROR_TRY_NUM = 3;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    protected RetryHandler retryHandler = RetryHandler.Builder
            .retryTimes(3)
            .every(250, TimeUnit.MILLISECONDS)
            .withBackoffStrategy(RetryHandler.Builder.EXPONENTIAL)
            .forExceptions(ApiProxy.RPCFailedException.class, MemcacheServiceException.class)
            .build();

    /**
     * The factory must be injected by the implementing class
     */
    public AbstractStringKeyedDao(Class<T> clazz, Boolean useLowerCase) {
        this.clazz = clazz;
        Reader.convertToLowerCase = useLowerCase;
    }

    public AbstractStringKeyedDao(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * save or update entity in datastore entity must be of a type registered with the injected objectify factory
     *
     * @param entity must not be null
     * @return the Key of the saved object
     */
    @Override
    public <R extends T> Key<R> save(final R entity) {
        return retryHandler.executeRuntime(new Callable<Key<R>>() {
            @Override
            public Key<R> call() throws Exception {
                R value = KeyGenerator.key(entity);
                Key<R> savedKey = null;
                savedKey = ofy().save().entity(entity).now();

                return savedKey;
            }
        });
    }

    /**
     * save or update entities in datastore entities must be of a type registered with the injected objectify factory
     *
     * @param entities
     * @return a map of the saved entities mapped to their datastore keys
     */
    @Override
    public <R extends T> Map<Key<R>, R> saveAll(final Iterable<R> entities) {
        return retryHandler.executeRuntime(new Callable<Map<Key<R>, R>>() {
            @Override
            public Map<Key<R>, R> call() throws Exception {
                Iterable<R> vals = (Iterable<R>) Iterables.transform(entities, KEY);
                return ofy().save().entities(entities).now();
            }
        });
    }

    /**
     * get object of type clazz that is stored in the datastore under the param id clazz must be of a type registered
     * with the injected objectify factory
     *
     * @param id
     * @return the object of type clazz that matches on the id
     * @throws EntityNotFoundException thrown if no entity object could be found
     */
    @Override
    public T findById(final String id) throws NotFoundException {
        try {
            return this.retryHandler.execute(new Callable<T>() {
                    @Override
                    public T call() throws Exception {
                        return ofy().load().key(Key.create(clazz, id)).safeGet();
                    }
                });
        } catch(NotFoundException e){
            throw e;
        } catch(Exception e){
            throw new RuntimeException(e);
        }


    }


    /**
     * get object of type clazz that is stored in the datastore under the param id clazz must be of a type registered
     * with the injected objectify factory
     *
     * @param id
     * @return the object of type clazz that matches on the id
     * @throws EntityNotFoundException thrown if no entity object could be found
     * @author Tomas de Priede
     */
    @Override
    public LoadResult<T> findAsync(String id) throws EntityNotFoundException {
        return ofy().load().key(Key.create(clazz, id));
    }

    /**
     * /**
     * get entities from datastore that match against the passed in collection of ids
     *
     * @param ids the set of String or Long ids matching against those entities to be retrieved from the datastore
     * @return all entities that match on the collection of ids. no error is thrown for entities not found in datastore.
     */
    @Override
    public Map<String, T> findByIds(final Iterable <String> ids) {
        return this.retryHandler.executeRuntime(new Callable<Map<String, T>>() {
            @Override
            public Map<String, T> call() throws Exception {
                return ofy().load().type(clazz).ids(ids);
            }
        });
    }


    /**
     * get entities from datastore that match against the passed in collection of keys
     *
     * @param keys the set of keys matching against those entities to be retrieved from the datastore
     * @return all entities that match on the collection of keys. no error is thrown for entities not found in
     *         datastore.
     */
    @Override
    public Map<Key<T>, T> findByKeys(final Iterable<Key<T>> keys) {
        return this.retryHandler.executeRuntime(new Callable<Map<Key<T>, T>>() {
            @Override
            public Map<Key<T>, T> call() throws Exception {
                return ofy().load().keys(keys);
            }
        });
    }

    /**
     * delete object of type clazz that is stored in the datastore under the param id clazz must be of a type registered
     * with the injected objectify factory
     *
     * @param id
     */
    @Override
    public void delete(final String id) {
        this.retryHandler.executeRuntime(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                ofy().delete().type(clazz).id(id).now();;
                return Void.TYPE;
            }
        });
    }

    /**
     * delete entities from datastore that match against the passed in collection entities must be of a type registered
     * with the injected objectify factory
     *
     * @param entities
     */
    @Override
    public void deleteAll(Iterable<T> entities) {
        final Iterable<T> finalEntities = Iterables.transform(entities, KEY);;
        this.retryHandler.executeRuntime(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return ofy().delete().entities(finalEntities).now();
            }
        });
    }

    /**
     * delete entities from datastore that match against the passed in collection keys must be of a type string with the
     * injected objectify factory
     * <p/>
     * the keys to delete
     */
    @Override
    public void deleteEntitiesByKeys(Iterable<String> stringKeys) {
        ofy().delete().type(clazz).ids(stringKeys).now();
    }

    @Override
    public Integer getCount(int limit) {
        Integer count = ofy().load().type(clazz).limit(limit).count();
        if (count == limit) {
            int pageSize = 1000;
            Query query = ofy().load().type(clazz).limit(pageSize);
            String cursor = null;
            count = 0;
            do {
                if (Strings.isNullOrEmpty(cursor)) {
                    query = query.startAt(Cursor.fromWebSafeString(cursor));
                }
                QueryResultIterator iterator = query.keys().iterator();
                String newCursor = null;
                int pageCount = 0;
                while (iterator.hasNext()) {
                    pageCount++;
                    iterator.next();
                }
                count += pageCount;
                if (pageCount == pageSize) {
                    Cursor c = iterator.getCursor();
                    if (c != null) {
                        newCursor = c.toWebSafeString();
                    }
                }
                if (newCursor != null && !newCursor.equals(cursor)) {
                    cursor = newCursor;
                } else {
                    cursor = null;
                }
            } while (cursor != null);
        }
        return count;
    }


    protected void beforeOperation() {

    }

    protected void afterOperation() {

    }

    protected Objectify ofy() {
        return OfyService.ofy();
    }

    protected void sleep(int timeInMillSec) {
        try {
            Thread.sleep(timeInMillSec);
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, "Thread was interrupted in UserAppsScanTask.");
        }
    }


}
