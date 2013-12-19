package com.totsp.keying.util.interfaces;

import com.totsp.keying.util.RetryHandler;

/**
 * Masking interface for the final build.
 */
public interface RetryBuilderBuild {
    /**
     * Builds a RetryHandler.
     * @return instance with desired configuration.
     */
    RetryHandler build();
}
