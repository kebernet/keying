package com.totsp.keying.util;

import com.google.common.base.Predicate;
import com.totsp.keying.util.interfaces.RetryBuilderBuild;
import com.totsp.keying.util.interfaces.RetryBuilderPredicate;
import com.totsp.keying.util.interfaces.RetryBuilderStrategy;
import com.totsp.keying.util.interfaces.RetryBuilderTime;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The RetryHandler is a utility class for doing backoffs and retries on calls given certain
 * "temporary failure" exceptions.
 *
 * Sample usage:
 *
 *  RetryHandler handler = RetryHandler.Builder.retryTimes(3)
 *    .every(500, TimeUnit.MILLISECONDS)
 *    .withBackoffStrategy(RetryHandler.Builder.LINEAR)
 *    .forExceptions(IOException.class)
 *    .build();
 *
 *    String result = handler.execute( new Callable&lt;String&gt(){
 *        public String call() throws Exception {
 *            return makeMyWebRequest();
 *        }
 *    }
 *
 *    Retry 3 times backing of linearly (500, 1000, 1500) milliseconds whenever the
 */
@ThreadSafe
public class RetryHandler {
    private static final Logger LOGGER = Logger.getLogger(RetryHandler.class.getCanonicalName());
    private final TimeUnit unit;
    private final long time;
    private final Predicate<Exception> predicate;
    private final int maxTries;
    private final Builder.Strategy strategy;

    RetryHandler(long time, TimeUnit unit, int maxTries, Builder.Strategy backoffStrategy,  Predicate<? extends Exception> predicate) {
        this.unit = unit;
        this.time = time;
        this.predicate = (Predicate<Exception>) predicate;
        this.maxTries = maxTries;
        this.strategy = backoffStrategy;
    }


    /**
     * Executes the callable and throws any exceptions that aren't retry-able.
     * @param callable The callable to execute.
     * @param <T> Return type from the callable.
     * @return the value returned on the first successful call.
     * @throws Exception Any exception thrown from the call.
     */
    public <T> T execute(Callable<T> callable) throws Exception {
        return execute(1, callable);
    }

    /**
     * Executes the callable, and wraps any non-Runtime exceptions in
     * a RuntimeException.
     * @param callable The callable to execute.
     * @param <T> Return type from the callable.
     * @return the value returned on the first successful call.
     * @throws Exception Any exception thrown from the call.
     */
    public <T> T executeRuntime(Callable<T> callable) {
        try {
            return execute(callable);
        } catch(RuntimeException e){
            throw e;
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    private <T> T execute(int tryNumber, Callable<T> callable) throws Exception{
        LOGGER.finest(" executing try " + tryNumber);
        try {
            return callable.call();
        } catch(Exception e){
            LOGGER.log(Level.FINE, "Caught during "+callable.getClass().getCanonicalName(), e);
            if(tryNumber < maxTries && predicate.apply(e)){
                sleepAttempt(tryNumber -1);
                return execute(++tryNumber, callable);
            } else {
                throw e;
            }
        }
    }

    private void sleepAttempt(int tryNumber) {
        long realTime = this.strategy.compute(this.time, tryNumber);
        try {
            LOGGER.finest("Sleeping " + realTime + " " + unit);
            Thread.sleep(unit.toMillis(realTime));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * A Builder for RetryHandlers. Begin with RetryHandler.Builder.retryTimes()...
     *
     */
    public static class Builder implements RetryBuilderTime, RetryBuilderStrategy, RetryBuilderPredicate, RetryBuilderBuild {
        private int maxTries;
        private long time;
        private TimeUnit unit;
        private Strategy strategy;
        private Predicate<? extends Exception> predicate;

        private Builder(){

        }

        /**
         * Begins a builder by specifying the number of times a call should be retried.
         * @param maxTries max retry count
         * @return Interface for specifying the base backoff time.
         */
        public static RetryBuilderTime retryTimes(int maxTries){
            Builder builder = new Builder();
            builder.maxTries = maxTries;
            return builder;
        }

        /**
         * Specifies the base time to back off.
         * @param time long unit of time.
         * @param units The TimeUnit the long value represents.
         * @return  interface for specifying the the backoff strategy.
         */
        @Override
        public RetryBuilderStrategy every(long time, @Nonnull TimeUnit units){
            checkNotNull(units, "No TimeUnit provided.");
            this.time = time;
            this.unit = units;
            return this;
        }

        /**
         * Specifies the backoff strategy used for timing.
         *
         * @see Builder#LINEAR
         * @see Builder#EXPONENTIAL
         * @see Builder#FIXED
         * @param strategy The strategy used to compute the backoff time.
         *
         * @return interface for specifying the matching logic for exceptions.
         */
        @Override
        public RetryBuilderPredicate withBackoffStrategy(@Nonnull Strategy strategy){
            checkNotNull(strategy, "No backoff strategy provided.");
            this.strategy = strategy;
            return this;
        }

        /**
         * Specifies the classes of Exceptions that should trigger a retry. Any exceptions
         * not matching these will be thrown through to the call point
         * @param exceptionClasses Array of exception types to retry on.
         * @return interface for performing the final build.
         */
        @Override
        public RetryBuilderBuild forExceptions(@Nonnull final Class<? extends Exception>... exceptionClasses ){
            checkNotNull(exceptionClasses, "No exception classes specified");
            checkArgument(exceptionClasses.length > 0, "No exception classes specified.");
            this.predicate = new Predicate<Exception>() {
                @Override
                public boolean apply(@Nullable Exception thrown) {
                    for(Class<? extends Throwable> clazz : exceptionClasses){
                        if(thrown.getClass().isAssignableFrom(clazz)){
                            return true;
                        }
                    }
                    return false;
                }
            };
            return this;
        }

        /**
         * Specifies a predicate that matches exceptions thrown by the call that should
         * trigger a retry. Any exceptions not matching will the thrown through to the
         * call point.
         * @param predicate A predicate matching exceptions that should be retried.
         * @return interface for performing the final build
         */
        @Override
        public RetryBuilderBuild matching(@Nonnull Predicate<? extends Exception> predicate) {
            checkNotNull(predicate, "No predicate provided.");
            this.predicate = predicate;
            return this;
        }

        /** Increases the base time linearly with each subsequent call
         * 5 seconds, 10 seconds, 15 seconds, etc.
         */
        public static final Strategy LINEAR = new Strategy() {
            @Override
            public long compute(long base, int factor) {
                return base * (factor + 1);
            }
        };

        /**
         * Increases the base time exponentially with each subsequent call
         * 5 seconds, 25 seconds, 125 seconds, 625 seconds, etc.
         */
        public static final Strategy EXPONENTIAL = new Strategy() {
            @Override
            public long compute(long base, int factor) {
                long value = base;
                for(int i=0; i < factor; i ++){
                    value *= base;
                }
                return value;
            }
        };

        /**
         * Does not increase the delay between subsequent calls at all
         * 5 seconds, 5 seconds, 5 seconds, etc.
         */
        public static final Strategy FIXED  = new Strategy() {
            @Override
            public long compute(long base, int factor) {
                return base;
            }
        };

        /**
         * Builds a RetryHandler.
         * @return instance with desired configuration.
         */
        @Override
        public RetryHandler build() {
            return new RetryHandler(this.time, this.unit, this.maxTries, this.strategy, this.predicate);
        }


        public static interface Strategy {
            long compute(long base, int step);
        }
    }




}
