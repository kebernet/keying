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

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.base.Function;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.NotFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Map;

import static com.google.common.collect.Iterables.addAll;
import static com.google.common.collect.Iterables.transform;
import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: Robert
 * Date: 12/20/13
 * Time: 11:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractStringKeyedDaoTest {

    public static final LocalServiceTestHelper HELPER = new LocalServiceTestHelper(
            new LocalDatastoreServiceTestConfig());

    @Before
    public void setUp(){
        OfyService.factory().register(NodeterministicEntity.class);
        OfyService.factory().register(DeterministicEntity.class);
        HELPER.setUp();

    }

    @After
    public void tearDown(){
        HELPER.tearDown();
    }

    @Test
    public void testSaveAndIdSymmetric() throws Exception {
        TestEntityDao instance = new TestEntityDao();
        NodeterministicEntity entity = new NodeterministicEntity();
        entity.setName("testSave");
        instance.save(entity);
        checkBeforeAndAfter(instance);
        instance.reset();
        assertNotNull(entity.getId());
        NodeterministicEntity fetched = instance.findById(entity.getId());
        assertEquals(entity, fetched);
        checkBeforeAndAfter(instance);
    }

    @Test
    public void testSaveAllAndFindByIdsSymmetric() throws Exception {
        ArrayList<NodeterministicEntity> test = new ArrayList<NodeterministicEntity>(100);
        for(int i=0; i < 100; i++){
            NodeterministicEntity e = new NodeterministicEntity();
            e.setName("Test "+i);
            test.add(e);
        }
        TestEntityDao dao = new TestEntityDao();
        dao.save(test);
        checkBeforeAndAfter(dao);
        Iterable<String> ids = transform(test, new Function<NodeterministicEntity, String>(){

            @Nullable
            @Override
            public String apply(@Nullable NodeterministicEntity testEntity) {
                return testEntity.getId();
            }
        });
        ArrayList<NodeterministicEntity> results = new ArrayList<NodeterministicEntity>(100);
        TestEntityDao dao2 =  new TestEntityDao();
        addAll(results, dao2.findByIds(ids).values());
        assertEquals(test, results);
        checkBeforeAndAfter(dao2);

    }


    public void testFindAsync() throws Exception {

    }


    @Test
    public void testFindByKeysAndSaveAllSymmetric() throws Exception {
        ArrayList<NodeterministicEntity> test = new ArrayList<NodeterministicEntity>(100);
        for(int i=0; i < 100; i++){
            NodeterministicEntity e = new NodeterministicEntity();
            e.setName("Test "+i);
            test.add(e);
        }
        TestEntityDao dao = new TestEntityDao();
        Map<Key<NodeterministicEntity>, NodeterministicEntity> results = dao.save(test);
        checkBeforeAndAfter(dao);
        dao.reset();
        ArrayList<NodeterministicEntity> initialResults = new ArrayList<NodeterministicEntity>(results.values());
        assertEquals(test, initialResults);
        ArrayList<NodeterministicEntity> found = new ArrayList<NodeterministicEntity>(dao.findByKeys(results.keySet()).values());
        checkBeforeAndAfter(dao);
        dao.reset();
        assertEquals(test, found);
    }

    @Test(expected = NotFoundException.class)
    public void testThrowsNFE() throws Exception {
        ArrayList<DeterministicEntity> test = new ArrayList<DeterministicEntity>(100);
        for(int i=0; i < 100; i++){
            DeterministicEntity e = new DeterministicEntity();
            e.setFirstName("Foo");
            e.setLastName(""+i);
            test.add(e);
        }
        TestDetEnDap dao = new TestDetEnDap();
        dao.save(test);
        assertNotNull(dao.findById("Foo:1"));
        dao.findById("Foo:101");
    }

    public void testDelete() throws Exception {

    }

    public void testDeleteAll() throws Exception {

    }

    public void testDeleteEntitiesByKeys() throws Exception {

    }

    public void testGetCount() throws Exception {

    }

    private void checkBeforeAndAfter(TestEntityDao dao){
        assertTrue(dao.beforeCalled);
        assertTrue(dao.afterCalled);
    }
    static class TestDetEnDap extends AbstractStringKeyedDao<DeterministicEntity> {

        /**
         * The factory must be injected by the implementing class
         */
        public TestDetEnDap() {
            super(DeterministicEntity.class);
        }
    }

    static class TestEntityDao extends AbstractStringKeyedDao<NodeterministicEntity> {
        private boolean beforeCalled;
        private boolean afterCalled;
        public TestEntityDao() {
            super(NodeterministicEntity.class);
        }

        @Override
        protected void beforeOperation() {
            this.beforeCalled = true;
        }

        @Override
        protected void afterOperation() {
            this.afterCalled = true;
        }

        public void reset(){
            this.beforeCalled = false;
            this.afterCalled = false;
        }
    }
}
