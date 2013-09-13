package com.totsp.keying.dao;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.memcache.MemcacheServiceException;
import com.google.appengine.labs.repackaged.com.google.common.base.Function;
import com.google.appengine.labs.repackaged.com.google.common.collect.Iterables;
import com.google.common.base.Strings;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.LoadResult;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.cmd.Query;

import javax.annotation.Nullable;
import java.util.Map;

import static com.totsp.keying.dao.OfyService.ofy;

/**
 * An abstract DAO class you can extend to work with keyed classes.
 */
public class AbstractStringKeyedDao<T> implements StringKeyedDao<T> {

    private final Class<T> clazz;
    /**
     * The factory must be injected by the implementing class
     */
    public AbstractStringKeyedDao(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * save or update entity in datastore entity must be of a type registered with the injected objectify factory
     *
     * @param entity
     *            must not be null
     * @return the Key of the saved object
     */
    @Override
    public Key<T> save(T entity) {
        entity = KeyGenerator.key(entity);
        return ofy().save().entity(entity).now();
    }

    /**
     * save or update entities in datastore entities must be of a type registered with the injected objectify factory
     *
     * @param entities
     * @return a map of the saved entities mapped to their datastore keys
     */
    @Override
    public Map<Key<T>, T> saveAll(Iterable<T> entities) {
        entities = Iterables.transform(entities, new Function<T, T>() {
            @Override
            public T apply(@javax.annotation.Nullable T t) {
                return KeyGenerator.key(t);
            }
        });
        return  ofy().save().entities(entities).now();
    }

    /**
     * get object of type clazz that is stored in the datastore under the param id clazz must be of a type registered
     * with the injected objectify factory
     *
     * @param id
     * @return the object of type clazz that matches on the id
     * @throws EntityNotFoundException
     *             thrown if no entity object could be found
     */
    @Override
    public T findById(String id) throws NotFoundException {
        return this.findById(id, false);
    }

    private T findById(String id, boolean hasRetried) throws NotFoundException {
        T result = null;
        try{
            result = ofy().load().key(Key.create(clazz,id)).safeGet();
        }catch(MemcacheServiceException mse){
            if(!hasRetried){
                ofy().clear();
                findById(id, true);
            } else {
                throw new RuntimeException("Memcache Service is currently unavailable.");
            }

        }
        return result;
    }

    /**
     *
     * get object of type clazz that is stored in the datastore under the param id clazz must be of a type registered
     * with the injected objectify factory
     *
     * @param id
     * @return the object of type clazz that matches on the id
     * @throws EntityNotFoundException
     *             thrown if no entity object could be found
     * @author Tomas de Priede
     */
    @Override
    public LoadResult<T> findAsync(String id) throws EntityNotFoundException {
        return ofy().load().key(Key.create(clazz,id));
    }

    /**

    /**
     * get entities from datastore that match against the passed in collection of ids
     *
     * @param ids
     *            the set of String or Long ids matching against those entities to be retrieved from the datastore
     * @return all entities that match on the collection of ids. no error is thrown for entities not found in datastore.
     */
    @Override
    public Map<String, T> findByIds(Iterable<String> ids) {
        return ofy().load().type(clazz).ids(ids);
    }


    /**
     * get entities from datastore that match against the passed in collection of keys
     *
     * @param keys
     *            the set of keys matching against those entities to be retrieved from the datastore
     * @return all entities that match on the collection of keys. no error is thrown for entities not found in
     *         datastore.
     */
    @Override
    public Map<Key<T>, T> findByKeys(Iterable<Key<T>> keys) {
        return ofy().load().keys(keys);
    }

    /**
     * delete object of type clazz that is stored in the datastore under the param id clazz must be of a type registered
     * with the injected objectify factory
     * @param id
     */
    @Override
    public void delete(String id) {
        ofy().delete().type(clazz).id(id).now();
    }

    /**
     * delete entities from datastore that match against the passed in collection entities must be of a type registered
     * with the injected objectify factory
     *
     * @param entities
     */
    @Override
    public void deleteAll(Iterable<T> entities) {
        entities = Iterables.transform(entities, new Function<T, T>() {
            @Nullable
            @Override
            public T apply(@Nullable T t) {
                return KeyGenerator.key(t);
            }
        });
        ofy().delete().entities(entities).now();
    }

    /**
     * delete entities from datastore that match against the passed in collection keys must be of a type string with the
     * injected objectify factory
     *
     *            the keys to delete
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


    protected void beforeOperation(){

    }

    protected void afterOperation(){

    }


}
