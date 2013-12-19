package com.totsp.keying.util.interfaces;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

/**
 * Masking interface for specifying the time component of the builder.
 */
public interface RetryBuilderTime {

    /**
     * Specifies the base time to back off.
     * @param time long unit of time.
     * @param units The TimeUnit the long value represents.
     * @return  interface for specifying the the backoff strategy.
     */
    public RetryBuilderStrategy every(long time, @Nonnull TimeUnit units);
}
