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
package com.totsp.keying.util;

import org.junit.Test;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: Robert
 * Date: 12/19/13
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class RetryHandlerTest {


    @Test
    public void testExponential(){
        long base = 2;
        assertEquals(2, RetryHandler.Builder.EXPONENTIAL.compute(base, 0));
        assertEquals(4, RetryHandler.Builder.EXPONENTIAL.compute(base, 1));
        assertEquals(8, RetryHandler.Builder.EXPONENTIAL.compute(base, 2));
        assertEquals(16, RetryHandler.Builder.EXPONENTIAL.compute(base, 3));
        base = 3;
        assertEquals(3, RetryHandler.Builder.EXPONENTIAL.compute(base, 0));
        assertEquals(9, RetryHandler.Builder.EXPONENTIAL.compute(base, 1));
        assertEquals(27, RetryHandler.Builder.EXPONENTIAL.compute(base, 2));

    }

    @Test
    public void testLinear(){
        long base = 2;
        assertEquals(2, RetryHandler.Builder.LINEAR.compute(base, 0));
        assertEquals(4, RetryHandler.Builder.LINEAR.compute(base, 1));
        assertEquals(6, RetryHandler.Builder.LINEAR.compute(base, 2));
        assertEquals(8, RetryHandler.Builder.LINEAR.compute(base, 3));
        base = 3;
        assertEquals(3, RetryHandler.Builder.LINEAR.compute(base, 0));
        assertEquals(6, RetryHandler.Builder.LINEAR.compute(base, 1));
        assertEquals(9, RetryHandler.Builder.LINEAR.compute(base, 2));
    }


    @Test
    public void testSimpleSuccess() throws Exception {

        RetryHandler handler = RetryHandler.Builder.retryTimes(3)
                .every(500, TimeUnit.MILLISECONDS)
                .withBackoffStrategy(RetryHandler.Builder.LINEAR)
                .forExceptions(IOException.class)
                .build();

        TestCallable callable = new TestCallable(1, IOException.class);

        assertEquals("Done.", handler.execute(callable));

    }

    @Test
    public void testRetrySuccess() throws Exception {

        RetryHandler handler = RetryHandler.Builder.retryTimes(3)
                .every(500, TimeUnit.MILLISECONDS)
                .withBackoffStrategy(RetryHandler.Builder.LINEAR)
                .forExceptions(IOException.class)
                .build();

        TestCallable callable = new TestCallable(3, IOException.class);

        assertEquals("Done.", handler.execute(callable));
        assertEquals(3, callable.called);


    }

    @Test(expected = IOException.class)
    public void testRetryFailure() throws Exception {

        RetryHandler handler = RetryHandler.Builder.retryTimes(3)
                .every(500, TimeUnit.MILLISECONDS)
                .withBackoffStrategy(RetryHandler.Builder.LINEAR)
                .forExceptions(IOException.class)
                .build();

        TestCallable callable = new TestCallable(5, IOException.class);
        handler.execute(callable);
        fail();
    }

    @Test(expected = IOException.class)
    public void testRetryFailureSubclass() throws Exception {

        RetryHandler handler = RetryHandler.Builder.retryTimes(3)
                .every(500, TimeUnit.MILLISECONDS)
                .withBackoffStrategy(RetryHandler.Builder.LINEAR)
                .forExceptions(IOException.class)
                .build();

        TestCallable callable = new TestCallable(5, SocketTimeoutException.class);
        handler.execute(callable);
        fail();
    }

    @Test(expected = NullPointerException.class)
    public void testRetryFailureDifferentType() throws Exception {

        RetryHandler handler = RetryHandler.Builder.retryTimes(3)
                .every(500, TimeUnit.MILLISECONDS)
                .withBackoffStrategy(RetryHandler.Builder.LINEAR)
                .forExceptions(IOException.class)
                .build();

        TestCallable callable = new TestCallable(5, NullPointerException.class);
        handler.execute(callable);
        fail();
    }

    private class TestCallable implements Callable<String>{

        int called = 0;
        final int succeedOn;
        final Class<? extends Exception> throwThis;

        private TestCallable(int succeedOn, Class<? extends Exception> throwThis) {
            this.succeedOn = succeedOn;
            this.throwThis = throwThis;
        }


        @Override
        public String call() throws Exception {
            called++;
            if(called == succeedOn){
                return "Done.";
            } else {
                throw throwThis.newInstance();
            }
        }
    }
}
