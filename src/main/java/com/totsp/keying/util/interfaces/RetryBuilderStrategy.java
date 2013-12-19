package com.totsp.keying.util.interfaces;

import com.totsp.keying.util.RetryHandler;

import javax.annotation.Nonnull;

/**
 * Masking interface for specifying the backoff strategy for the builder.
 */
public interface RetryBuilderStrategy {
    /**
     * Specifies the backoff strategy used for timing.
     *
     * @see com.totsp.keying.util.RetryHandler.Builder#LINEAR
     * @see com.totsp.keying.util.RetryHandler.Builder#EXPONENTIAL
     * @see com.totsp.keying.util.RetryHandler.Builder#FIXED
     * @param strategy The strategy used to compute the backoff time.
     *
     * @return interface for specifying the matching logic for exceptions.
     */
    public RetryBuilderPredicate withBackoffStrategy(@Nonnull RetryHandler.Builder.Strategy strategy);
}
