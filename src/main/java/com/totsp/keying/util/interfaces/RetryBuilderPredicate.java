package com.totsp.keying.util.interfaces;

import com.google.common.base.Predicate;

import javax.annotation.Nonnull;

/**
 * Masking interface for specifying matching logic.
 */
public interface RetryBuilderPredicate {

    /**
     * Specifies the classes of Exceptions that should trigger a retry. Any exceptions
     * not matching these will be thrown through to the call point
     * @param exceptionClasses Array of exception types to retry on.
     * @return interface for performing the final build.
     */
    RetryBuilderBuild forExceptions(@Nonnull Class<? extends Exception>... exceptionClasses);

    /**
     * Specifies a predicate that matches exceptions thrown by the call that should
     * trigger a retry. Any exceptions not matching will the thrown through to the
     * call point.
     * @param predicate A predicate matching exceptions that should be retried.
     * @return interface for performing the final build
     */
    RetryBuilderBuild matching(Predicate<? extends Exception> predicate);
}
