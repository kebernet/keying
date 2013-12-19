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
