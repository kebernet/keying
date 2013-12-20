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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nullable;
import java.util.ArrayList;

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
        OfyService.factory().register(TestEntity.class);
        HELPER.setUp();

    }

    @After
    public void tearDown(){
        HELPER.tearDown();
    }

    @Test
    public void testSaveAndIdSymmetric() throws Exception {
        TestEntityDao instance = new TestEntityDao();
        TestEntity entity = new TestEntity();
        entity.setName("testSave");
        instance.save(entity);
        assertNotNull(entity.getId());
        TestEntity fetched = instance.findById(entity.getId());
        assertEquals(entity, fetched);
    }

    @Test
    public void testSaveAllAndFindByIdsSymmtric() throws Exception {
        ArrayList<TestEntity> test = new ArrayList<TestEntity>(100);
        for(int i=0; i < 100; i++){
            TestEntity e = new TestEntity();
            e.setName("Test "+i);
            test.add(e);
        }
        new TestEntityDao().saveAll(test);
        Iterable<String> ids = transform(test, new Function<TestEntity, String>(){

            @Nullable
            @Override
            public String apply(@Nullable TestEntity testEntity) {
                return testEntity.getId();
            }
        });
        ArrayList<TestEntity> results = new ArrayList<TestEntity>(100);
        addAll(results, new TestEntityDao().findByIds(ids).values());
        assertEquals(test, results);

    }


    public void testFindAsync() throws Exception {

    }


    public void testFindByKeys() throws Exception {

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




    static class TestEntityDao extends AbstractStringKeyedDao<TestEntity> {
        private boolean beforeCalled;
        private boolean afterCalled;
        public TestEntityDao() {
            super(TestEntity.class);
        }

        @Override
        protected void beforeOperation() {
            this.beforeCalled = true;
        }

        @Override
        protected void afterOperation() {
            this.afterCalled = true;
        }
    }
}
