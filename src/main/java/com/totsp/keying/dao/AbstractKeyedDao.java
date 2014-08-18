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
import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.memcache.MemcacheServiceException;
import com.google.apphosting.api.ApiProxy;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.cmd.Query;
import com.totsp.keying.util.RetryHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterators.addAll;
import static com.google.common.collect.Iterators.transform;

/**
 * An abstract class with functions for keyed DAOs.
 */
public abstract class AbstractKeyedDao<T extends Serializable, K extends Serializable> implements KeyedDao<T, K> {

    private static int ERROR_TRY_NUM = 3;
    private static final int ERROR_BACKOFF_MILLIS = 250;
    protected Logger logger = Logger.getLogger(this.getClass().getName());
    /**
     * The retryHandler that will be used to check exceptions.
     */
    @SuppressWarnings("unchecked")
    protected RetryHandler retryHandler = RetryHandler.Builder
            .retryTimes(ERROR_TRY_NUM)
            .every(ERROR_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
            .withBackoffStrategy(RetryHandler.Builder.EXPONENTIAL)
            .forExceptions(ApiProxy.RPCFailedException.class, MemcacheServiceException.class,
                    DatastoreTimeoutException.class, DatastoreFailureException.class, ConcurrentModificationException.class)
            .build();
    /**
     * The class type this DAO is for.
     */
    protected final Class<T> clazz;

    /**
     * A pass-through function that will be called on all entities before the are saved.
     */
    protected Function<T, T> preSaveHook = new Function<T, T>() {
        @Nullable
        @Override
        public T apply(@Nullable T t) {
            return t;
        }
    };

    /**
     * A pass-throughg function that will be called on all entities before they are
     * returned from finders.
     */
    protected Function<T, T> preReturnHook = new Function<T, T>() {
        @Nullable
        @Override
        public T apply(@Nullable T t) {
            return t;
        }
    };

    /**
     * Constructor for a KeyedDay for type clazz.
     * @param clazz class type this DAO is for.
     */
    protected AbstractKeyedDao(@Nonnull Class<T> clazz) {
        checkNotNull(clazz);
        logger.fine("Create "+this.getClass().getCanonicalName()+" for "+clazz.getCanonicalName());
        this.clazz = clazz;
    }

    /**
     * Called before any operation.
     */
    protected void beforeOperation() {

    }

    /**
     * Called after any operation.
     */
    protected void afterOperation() {

    }

    /**
     * Applies the preReturnHook function to a collection of values.
     * @param values  values to apply the preReturnHook too.
     */
    protected void applyPreReturnHook(@Nonnull Iterable<T> values){
        for(T t: values){
            preReturnHook.apply(t);
        }
    }

    /**
     * Uses the query to build a Page of results.
     * @param query query to use.
     * @param pageSize size of the page to fetch
     * @param cursor web safe ofy cursor, or null for the first one.
     * @return a Page of entities.
     */
    @SuppressWarnings("unchecked")
    protected Page<T> fetchPage(@Nonnull Query query, int pageSize, @Nullable String cursor){
        query = query.limit(pageSize);
        if (!Strings.isNullOrEmpty(cursor)) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }

        QueryResultIterator<T> iterator = query.iterator();
        ArrayList<T> list = new ArrayList<>(pageSize);
        String newCursor = null;
        addAll(list, transform(iterator, preReturnHook));

        if (list.size() == pageSize) {
            Cursor c = iterator.getCursor();
            if (c != null) {
                String webSafeCursor = c.toWebSafeString();
                if(webSafeCursor!=null && !webSafeCursor.equals(cursor)){
                    newCursor = webSafeCursor;
                }
            }
        }

        return new Page<>(list, newCursor);
    }

    /**
     * Applies the parameters necessary for a "startsWith" operatio to the query.
     * @param q query to append.
     * @param property property to filter.
     * @param pattern pattern to search for.
     * @return the query mutated with the new filters.
     */
    protected Query startsWith(Query q, @Nonnull String property, @Nonnull String pattern){
        checkNotNull(property);
        checkNotNull(pattern);
        return q.filter(property+" >=", pattern)
                .filter(property+" <=", pattern + LAST_UNICODE_CHARACTER);
    }

    /**
     * An overridable method to return a configured Objectify instance.
     * @return  a configured Objectify instance.
     */
    protected Objectify ofy() {
        return OfyService.ofy();
    }

    /**
     * Counts the number of entities keys in the datastore for this type up to
     * the limit.
     * @param limit maximun number of entities to count.
     * @return count of entites.
     */
    @Override
    public Integer getCount(int limit) {
        beforeOperation();
        try {
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
        } finally {
            afterOperation();
        }
    }


}
