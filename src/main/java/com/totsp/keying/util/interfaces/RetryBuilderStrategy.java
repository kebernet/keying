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
