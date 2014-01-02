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

import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Map;

/**
 * A base interface for a DAO.
 */
public interface Dao<T extends Serializable, K extends Serializable> {

    /**
     * save or update entity in datastore entity must be of a type registered with the injected objectify factory
     *
     * @param entity must not be null
     * @return the Key of the saved object
     */
    <R extends T> Key<R> save(@Nonnull R entity);

    /**
     * save or update entities in datastore entities must be of a type registered with the injected objectify factory
     *
     * @param entities entities to save
     * @return a map of the saved entities mapped to their datastore keys
     */
    <R extends T> Map<Key<R>, R> save(@Nonnull Iterable<R> entities);

    /**
     * get object of type clazz that is stored in the datastore under the param id clazz must be of a type registered
     * with the injected objectify factory
     *
     * @param id Name/ID of the entity to find.
     * @return the object of type clazz that matches on the id
     * @throws com.google.appengine.api.datastore.EntityNotFoundException thrown if no entity object could be found
     */
    T findById(@Nonnull K id) throws NotFoundException;

    /**
     * get entities from datastore that match against the passed in collection of ids
     *
     * @param ids the set of String or Long ids matching against those entities to be retrieved from the datastore
     * @return all entities that match on the collection of ids. no error is thrown for entities not found in datastore.
     */
    Map<String, T> findByIds(@Nonnull Iterable<K> ids);

    /**
     * Returns count of entities of T
     *
     * @return count of entities.
     */
    Integer getCount(int limit);

    /**
     * delete object of type clazz that is stored in the datastore under the param id clazz must be of a type registered
     * with the injected objectify factory
     *
     * @param id Name/ID of the entity to delete.
     */
    void delete(@Nonnull K id);

    /**
     * delete entities from datastore that match against the passed in collection entities must be of a type registered
     * with the injected objectify factory
     *
     * @param entities entities to delete.
     */
    void deleteAll(@Nonnull Iterable<T> entities);

}
