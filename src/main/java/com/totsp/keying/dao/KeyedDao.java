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

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.LoadResult;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Map;

/**
 *
 */
public interface KeyedDao<T extends Serializable, K extends Serializable> extends Dao<T, K> {
    public static final char LAST_UNICODE_CHARACTER = '\ufffd';

    /**
     * get object of type clazz that is stored in the datastore under the param id clazz must be of a type registered
     * with the injected objectify factory
     *
     * @param id ID/Name of the entity to find.
     * @return the object of type clazz that matches on the id
     * @throws EntityNotFoundException thrown if no entity object could be found
     * @author Tomas de Priede
     */
    public LoadResult<T> findAsync(@Nonnull K id) throws EntityNotFoundException;

    /**
     * get entities from datastore that match against the passed in collection of keys
     *
     * @param keys the set of keys matching against those entities to be retrieved from the datastore
     * @return all entities that match on the collection of keys. no error is thrown for entities not found in
     *         datastore.
     */
    public <R extends T> Map<Key<R>, R> findByKeys(@Nonnull Iterable<Key<R>> keys);

    /**
     * delete entities from datastore that match against the passed in collection keys must be of a type string with the
     * injected objectify factory
     *
     * @param keys the keys to delete
     */
    public void deleteEntitiesByKeys(@Nonnull Iterable<K> keys);




}
